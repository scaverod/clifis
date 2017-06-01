/*
        Clifis v1.0b (c) 2017 Escuela Técnica Superior de Ingeniería de Sistemas Informáticos (UPM)

        This file is part of Clifis.

        Clifis is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        Clifis is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with Clifis.  If not, see <http://www.gnu.org/licenses/>.
*/

package es.upm.etsisi.clifis.gestores;

import es.upm.etsisi.clifis.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Serializable;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Gestiona las citas con respecto al gestor de bases de datos.
 */
public class GestorCitas implements Serializable {

    Logger LOG = LoggerFactory.getLogger("es.upm.etsisi.clifis.gestores.GestorCitas");

    public GestorCitas() {
    }

    /**
     * Da de alata una cita.
     *
     * @param cita Contiene la información de la cita a dar de alta.
     */
    public void altaCita(Cita cita) throws GestorException {

        try (Connection con = DBManager2.getConn()) {

            PreparedStatement pstm = con.prepareStatement("INSERT INTO Cita (" +
                    "Cita_IdPaciente,Cita_IdMedico,Cita_Fecha,Cita_IdEspecialidad,Cita_IdConsulta)" +
                    "VALUES(" +
                    "?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);

            pstm.setInt(1, cita.getPaciente().getId());
            pstm.setInt(2, cita.getMedico().getId());
            pstm.setTimestamp(3, cita.getFecha());
            pstm.setInt(4, cita.getEspecialidad().getId());
            pstm.setInt(5, cita.getConsulta().getId());

            pstm.executeUpdate();

            ResultSet rs = pstm.getGeneratedKeys();

            if (rs.next()) {
                cita.setId(rs.getInt(1));
            }

            pstm.close();

        } catch (SQLException e) {
            LOG.debug("Controlada una SQLException.", e);
            throw new GestorException("Error en la sentencia sql a la hora de insertar una cita");
        } catch (DBManager2Exception e) {
            LOG.debug("Controlada una DBManager2Exception.", e);
            throw new GestorException("Algo ha ido mal con las conexiones a la base de datos cuando" +
                    "se intenaba dar de alta un cita.");
        }
    }

    /**
     * Consulta las últimas citas que hay en la base de datos ordenadas
     * descendentemente por fecha.
     *
     * @return Las últimas citas según fecha (orden descendente).
     */
    public List<Cita> getCitasFromFecha(Timestamp fecha) throws GestorException {

        ArrayList<Cita> resultado = new ArrayList<>();
        GestorConsultas gestorConsultas = new GestorConsultas();
        GestorMedicos gestorMedicos = new GestorMedicos();
        GestorEspecialidades gestorEspecialidades = new GestorEspecialidades();
        GestorPacientes gestorPacientes = new GestorPacientes();


        try (Connection conex = DBManager2.getConn()) {
            PreparedStatement ptstm = conex.prepareStatement(
                    "SELECT  Cita_IdPaciente, Cita_IdConsulta, Cita_IdMedico, Cita_Fecha, Cita_Id, Cita_IdEspecialidad " +
                            "FROM Cita " +
                            "WHERE Cita_Fecha BETWEEN ? AND ? ORDER BY Cita_Fecha ASC;"
            );

            ptstm.setTimestamp(1, fecha);
            ptstm.setTimestamp(2, getFechaFin(fecha));

            ResultSet rs = ptstm.executeQuery();

            while (rs.next()) {
                resultado.add(new CitaBuilder()
                        .setId(rs.getInt("Cita_Id"))
                        .setPaciente(gestorPacientes.getPacienteFromId(rs.getInt("Cita_IdPaciente")))
                        .setMedico(gestorMedicos.getMedicoById(rs.getInt("Cita_IdMedico")))
                        .setFecha(rs.getTimestamp("Cita_Fecha"))
                        .setEspecialidad(gestorEspecialidades.getEspecialidadFromId(rs.getInt("Cita_IdEspecialidad")))
                        .setConsulta(gestorConsultas.getConsultaFromId(rs.getInt("Cita_IdConsulta")))
                        .build());
            }
        } catch (SQLException e) {
            LOG.debug("Controlada una SQLException.", e);
            throw new GestorException("Error en la sentencia sql a la hora de " +
                    "sacar las citas a partir de una fecha (getCitasFromFecha).");
        } catch (DBManager2Exception e) {
            LOG.debug("Controlada una DBManager2Exception.", e);
            throw new GestorException("Algo ha ido mal con las conexiones a la base de datos cuando" +
                    "se intenaba sacar las citas a partir de una fecha (getCitasFromFecha).");
        } catch (GestorException e) {
            LOG.debug("Controlada una GestorException.", e);
            throw new GestorException("Algo ha ido mal llamando a un gestos (Medicos, Consultas, Especialidades...) " +
                    "cuando se intentaba sacar las citas a partir de una fecha (getCitasFromFecha).");
        }
        return resultado;
    }

    public boolean tieneCitas(Consulta consulta) throws GestorException {
        boolean resultado = false;

        try (Connection conex = DBManager2.getConn()) {
            PreparedStatement pstm = conex.prepareStatement("SELECT COUNT(Cita_IdConsulta) AS citas FROM Cita WHERE Cita_IdConsulta = ?");
            pstm.setInt(1, consulta.getId());
            ResultSet rs = pstm.executeQuery();
            if (rs.next())
                resultado = rs.getInt("citas") > 0;

        } catch (SQLException e) {
            LOG.debug("Controlada una SQLException.", e);
            throw new GestorException("Error en la sentencia sql a la hora de " +
                    "sacar las citas a partir de una fecha (tieneCitas).");
        } catch (DBManager2Exception e) {
            LOG.debug("Controlada una DBManager2Exception.", e);
            throw new GestorException("Algo ha ido mal con las conexiones a la base de datos cuando" +
                    "se intenaba comprobar si una consulta tiene citas asociadas (tieneCitas).");
        }

        return resultado;
    }

    /**
     * Devuelve las citas de una consulta para un día concreto ordenadas por la hora.
     *
     * @param consulta La consulta que se quiere consultar
     * @param fecha    Con el día del que se quiere saber las citas de esa consulta.
     * @return 0 elementos si no hay citas ese día o las citas encontradas.
     */
    public List<Cita> getCitasDeConsulta(Consulta consulta, Timestamp fecha) {
        List<Cita> resultado = null;
        try {
            resultado = (this.getCitasFromFecha(fecha));
        } catch (GestorException e) {
            e.printStackTrace();
        }

        for (Cita cita : resultado) {
            if (cita.getConsulta().getId() != consulta.getId()) {
                resultado.remove(resultado);
            }
        }

        return resultado;
    }


    /**
     * Método que da una cita a un paciente determinado. Dado una consulta te da el primer hueco disponible que encuentre
     * e inserta una nueva cita para esa consulta y un paciente dado.
     *
     * @param consulta Contiene los datos de la consulta de  la que se quiere una cita.
     * @param paciente Datos del paciente que solicita una cita para una consulta determinada
     * @return Cita que acaba de crear con la hora de inicio que primero ha encontrado en la lista de cosnultas disponibles.
     */
    public Cita getSiguienteCita(Consulta consulta, Paciente paciente) {
        Cita resultado = null;

        Calendar siguienteInicioDeConsulta = GestorFechas.getSiguienteInicioDeConsulta(consulta);
        Calendar siguienteFinDeConsulta = GestorFechas.getSiguienteFinDeConsulta(consulta);

        try (Connection conex = DBManager2.getConn()) {
            // Seleciona las citas del siguiente día de esa consulta en orden ascendente de hora.
            // Esto se puede sustituir por this. getCitacs de consulta y quedarte con la última.
            String SQL = "SELECT Cita_Id, Cita_Fecha FROM Cita" +
                    " WHERE Cita_IdConsulta=? AND Cita_Fecha BETWEEN ? AND ? ORDER BY Cita_Fecha ASC";

            LOG.trace("SQL: {}", SQL);
            PreparedStatement pstm0 = conex.prepareStatement(SQL);

            pstm0.setInt(1, consulta.getId());
            pstm0.setTimestamp(2, new java.sql.Timestamp(siguienteInicioDeConsulta.getTimeInMillis()));
            pstm0.setTimestamp(3, new java.sql.Timestamp(siguienteFinDeConsulta.getTimeInMillis()));

            ResultSet citasRS = pstm0.executeQuery();

            Calendar fechaHoraDeCita = Calendar.getInstance();

            if (citasRS.last()) {
                // Si existen citas en esa consulta se calcula fechaHoraDeCita
                fechaHoraDeCita.setTime(citasRS.getTimestamp("Cita_Fecha"));
                fechaHoraDeCita.add(Calendar.MINUTE, consulta.getDuracion());
            } else {
                // Si no, se fija fechaHoraDeCita al inicio de la consulta.
                fechaHoraDeCita = siguienteInicioDeConsulta;
            }

            citasRS.close();

            // Insertar cita
            // TODO: Check que no se pase de la hora de fin de consultas.
            PreparedStatement pstm = conex.prepareStatement("INSERT INTO Cita (Cita_IdPaciente, Cita_IdMedico, " +
                    "Cita_IdEspecialidad, Cita_IdConsulta, Cita_Fecha) VALUES (?, ?, ?, ?, ?) ");

            LOG.trace("Siguiente fecha y hora: {} ", siguienteInicioDeConsulta.getTime());

            pstm.setInt(1, paciente.getId());
            pstm.setInt(2, consulta.getMedico().getId());
            pstm.setInt(3, consulta.getEspecialidad().getId());
            pstm.setInt(4, consulta.getId());
            pstm.setTimestamp(5, new java.sql.Timestamp(fechaHoraDeCita.getTimeInMillis()));

            pstm.execute();
            pstm.close();

        } catch (SQLException e) {
            // TODO: Gestionar esta excepción
            e.printStackTrace();
        } catch (DBManager2Exception e) {
            // TODO: Gestionar esta excepción
            e.printStackTrace();
        }

        return resultado;
    }

    /**
     * Método que te devuelve una lista con todas las posibles citas que un paciente puede seleccionar para ser atendido.
     *
     * @param fecha        del dia del que se piden todas las citas. Este parametro es obligatorio
     * @param medico       del que se quiere que se muestren las citas disponibles. Este parametro no es obligatorio.
     * @param especialidad de la que se quiere que se muestren las citas disponibles. Este parametro no es onligatorio.
     * @return Lista con las citas disponibles
     */
    public List<Cita> getCitasLibres(Timestamp fecha, Medico medico, Especialidad especialidad, List<Consulta> consultas)
            throws GestorException {
        //TODO: Para implementar este metodo:
        //1. Obtenemos las citas ocupadas.
        //2. Obtenemos todas las citas posibles (Tanto ocupadas como las que no)
        //3. Realizamos la resta entre todas las citas y las ocupadas y devolvemos la lista resultante.
        ArrayList<Cita> citasOcupadas = null;
        ArrayList<Cita> citasTotales = new ArrayList<>();
        List<Cita> resultado = new ArrayList<>();

        try {
            if (medico.getId() > 0 && especialidad.getId() > 0)
                citasOcupadas = getCitasFromFechaANDEspecialidadANDMedico(fecha, especialidad, medico);
            else if (medico.getId() > 0)
                citasOcupadas = getCitasFromFechaANDMedico(fecha, medico);
            else if (especialidad.getId() > 0)
                citasOcupadas = getCitasFromFechaANDEspecialidad(fecha, especialidad);
        } catch (GestorException e) {
            LOG.debug("Controlada una GestorException en getCitasLibres.", e);
            throw new GestorException("No se han podido recuperar las getCitasLibres.");
        }

        for (Consulta consulta : consultas) {
            consulta = new GestorConsultas().getConsultaFromId(consulta.getId());
            List<Cita> citasConsulta = getAllCitasFromConsulta(consulta, fecha);

            citasTotales.addAll(citasConsulta);

        }

        if (citasOcupadas.size() > 0) {
            for (Cita citaOcupada : citasOcupadas) {
                int i = 0;
                int consultaOcupada = citaOcupada.getConsulta().getId();
                long fechaOcupada = citaOcupada.getFecha().getTime();
                boolean encontrado = false;
                while (i < citasTotales.size() && !encontrado) {
                    long fechaPosible = citasTotales.get(i).getFecha().getTime();
                    int consultaPosible = citasTotales.get(i).getConsulta().getId();
                    encontrado = (fechaOcupada == fechaPosible && consultaOcupada == consultaPosible);
                    if (!encontrado)
                        i++;
                }

                if (i < citasTotales.size())
                    citasTotales.remove(i);
            }
        }

        resultado = citasTotales;

        return resultado;
    }

    /**
     * Metodo que devuelve todas las citas que tiene un médico para una fecha determinada
     *
     * @param fecha  contiene el dia del que quiere que se muetren las citas
     * @param medico Medico que contiene la id para obtener sus citas asociadas
     * @return Lista de las citas de un medico y un dia concreto pasados por parametros
     * @throws GestorException Si hay algun problema a la hora de obtener la lista.
     */
    public ArrayList<Cita> getCitasFromFechaANDMedico(Timestamp fecha, Medico medico) throws GestorException {
        ArrayList<Cita> resultado = new ArrayList<>();
        GestorConsultas gestorConsultas = new GestorConsultas();
        GestorMedicos gestorMedicos = new GestorMedicos();
        GestorEspecialidades gestorEspecialidades = new GestorEspecialidades();
        GestorPacientes gestorPacientes = new GestorPacientes();

        String sql = "SELECT  Cita_IdPaciente, Cita_IdConsulta, Cita_IdMedico, Cita_Fecha, Cita_Id, Cita_IdEspecialidad " +
                "FROM Cita " +
                "WHERE Cita_IdMedico = ? AND Cita_Fecha BETWEEN ? AND ? ORDER BY Cita_Fecha ASC;";


        try (Connection conex = DBManager2.getConn()) {
            PreparedStatement pstm = conex.prepareStatement(sql);

            pstm.setInt(1, medico.getId());
            pstm.setTimestamp(2, fecha);
            pstm.setTimestamp(3, getFechaFin(fecha));

            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                resultado.add(new CitaBuilder()
                        .setMedico(gestorMedicos.getMedicoById(rs.getInt("Cita_IdMedico")))
                        .setPaciente(gestorPacientes.getPacienteFromId(rs.getInt("Cita_IdPaciente")))
                        .setFecha(rs.getTimestamp("Cita_Fecha"))
                        .setEspecialidad(gestorEspecialidades.getEspecialidadFromId(rs.getInt("Cita_IdEspecialidad")))
                        .setConsulta(gestorConsultas.getConsultaFromId(rs.getInt("Cita_IdConsulta")))
                        .build());
            }
        } catch (SQLException e) {
            LOG.debug("Controlada una SQLException.", e);
            throw new GestorException("Error en la sentencia sql a la hora de " +
                    "sacar un listado de las citas de un medico en una fecha determinada (getCitasFromFechaANDMedico).");
        } catch (DBManager2Exception e) {
            LOG.debug("Controlada una DBManager2Exception.", e);
            throw new GestorException("Algo ha ido mal con las conexiones a la base de datos cuando" +
                    "se intenaba  sacar un listado de las citas de un medico en una fecha determinada (getCitasFromFechaANDMedico).");
        } catch (GestorException e) {
            LOG.debug("Controlada una GestorException.", e);
            throw new GestorException("Algo ha ido mal llamando a un gestos (Medicos, Consultas, Especialidades...) " +
                    "cuando se intentaba sacar las citas a partir de una fecha y un médico (getCitasFromFechaANDMedico).");
        }
        return resultado;
    }


    /**
     * Metodo que devuelve todas las citas de una especialidad  para una fecha determinada
     *
     * @param fecha        contiene el dia del que quiere que se muetren las citas
     * @param especialidad Especialidad que nos proporciona una id para buscar las citas asociadas a esa especialidad
     * @return Lista de las citas de una especialidad  y un dia concreto pasados por parametros
     * @throws GestorException Si hay algun problema a la hora de obtener la lista.
     */
    public ArrayList<Cita> getCitasFromFechaANDEspecialidad(Timestamp fecha, Especialidad especialidad) throws GestorException {
        ArrayList<Cita> resultado = new ArrayList<>();
        GestorConsultas gestorConsultas = new GestorConsultas();
        GestorMedicos gestorMedicos = new GestorMedicos();
        GestorEspecialidades gestorEspecialidades = new GestorEspecialidades();
        GestorPacientes gestorPacientes = new GestorPacientes();

        String sql = "SELECT  Cita_IdPaciente, Cita_IdConsulta, Cita_IdMedico, Cita_Fecha, Cita_Id, Cita_IdEspecialidad " +
                "FROM Cita " +
                "WHERE Cita_IdEspecialidad = ? AND Cita_Fecha BETWEEN ? AND ? ORDER BY Cita_Fecha ASC;";

        try (Connection conex = DBManager2.getConn()) {
            PreparedStatement ptstm = conex.prepareStatement(sql);

            ptstm.setInt(1, especialidad.getId());
            ptstm.setTimestamp(2, fecha);
            ptstm.setTimestamp(3, getFechaFin(fecha));

            ResultSet rs = ptstm.executeQuery();

            while (rs.next()) {
                resultado.add(new CitaBuilder()
                        .setPaciente(gestorPacientes.getPacienteFromId(rs.getInt("Cita_IdPaciente")))
                        .setMedico(gestorMedicos.getMedicoById(rs.getInt("Cita_IdMedico")))
                        .setFecha(rs.getTimestamp("Cita_Fecha"))
                        .setEspecialidad(gestorEspecialidades.getEspecialidadFromId(rs.getInt("Cita_IdEspecialidad")))
                        .setConsulta(gestorConsultas.getConsultaFromId(rs.getInt("Cita_IdConsulta")))
                        .build());
            }

        } catch (SQLException e) {
            LOG.debug("Controlada una SQLException.", e);
            throw new GestorException("Error en la sentencia sql a la hora de " +
                    "sacar un listado de las citas de una especialidad y una fecha determinada (getCitasFromFechaANDEspecialidad).");
        } catch (DBManager2Exception e) {
            LOG.debug("Controlada una DBManager2Exception.", e);
            throw new GestorException("Algo ha ido mal con las conexiones a la base de datos cuando" +
                    "se intenaba  sacar un listado de las citas de una especialiad en una fecha determinada (getCitasFromFechaANDEspecialidad).");
        } catch (GestorException e) {
            LOG.debug("Controlada una GestorException.", e);
            throw new GestorException("Algo ha ido mal llamando a un gestor (Medicos, Consultas, Especialidades...) " +
                    "cuando se intentaba sacar las citas a partir de una fecha y una especialidad (getCitasFromFechaANDEspecialidad).");
        }
        return resultado;
    }

    /**
     * Metodo que devuelve todas las citas de una especialidad  para una fecha determinada
     *
     * @param fecha        contiene el dia del que quiere que se muetren las citas
     * @param especialidad Especialidad que nos proporciona una id para buscar las citas asociadas a esa especialidad
     * @param medico       Medico que nos proporciona una id con la que acceder a sus citas
     * @return Lista de las citas de una especialidad  y un dia concreto pasados por parametros
     * @throws GestorException Si hay algun problema a la hora de obtener la lista.
     */
    public ArrayList<Cita> getCitasFromFechaANDEspecialidadANDMedico(Timestamp fecha, Especialidad especialidad, Medico medico) throws GestorException {
        ArrayList<Cita> resultado = new ArrayList<>();
        GestorConsultas gestorConsultas = new GestorConsultas();
        GestorMedicos gestorMedicos = new GestorMedicos();
        GestorEspecialidades gestorEspecialidades = new GestorEspecialidades();
        GestorPacientes gestorPacientes = new GestorPacientes();

        String sql = "SELECT  Cita_IdPaciente, Cita_IdConsulta, Cita_IdMedico, Cita_Fecha, Cita_Id, Cita_IdEspecialidad " +
                "FROM Cita " +
                "WHERE Cita_IdEspecialidad = ? AND Cita_IdMedico = ? AND Cita_Fecha BETWEEN ? AND ? ORDER BY Cita_Fecha ASC;";

        try (Connection conex = DBManager2.getConn()) {
            PreparedStatement ptstm = conex.prepareStatement(sql);

            ptstm.setInt(1, especialidad.getId());
            ptstm.setInt(2, medico.getId());
            ptstm.setTimestamp(3, fecha);
            ptstm.setTimestamp(4, getFechaFin(fecha));
            ResultSet rs = ptstm.executeQuery();

            while (rs.next()) {
                resultado.add(new CitaBuilder()
                        .setPaciente(gestorPacientes.getPacienteFromId(rs.getInt("Cita_IdPaciente")))
                        .setMedico(gestorMedicos.getMedicoById(rs.getInt("Cita_IdMedico")))
                        .setFecha(rs.getTimestamp("Cita_Fecha"))
                        .setEspecialidad(gestorEspecialidades.getEspecialidadFromId(rs.getInt("Cita_IdEspecialidad")))
                        .setConsulta(gestorConsultas.getConsultaFromId(rs.getInt("Cita_IdConsulta")))
                        .build());
            }
        } catch (GestorException e) {
            LOG.debug("Controlada una GestorException.", e);
            throw new GestorException("Algo ha ido mal llamando a un gestor (Medicos, Consultas, Especialidades...) " +
                    "cuando se intentaba sacar las citas a partir de una fecha, un medico  y una especialidad (getCitasFromFechaANDEspecialidadANDMedico).");
        } catch (SQLException e) {
            LOG.debug("Controlada una SQLException.", e);
            throw new GestorException("Error en la sentencia sql a la hora de " +
                    "sacar un listado de las citas de una especialidad, medico y una fecha determinada (getCitasFromFechaANDEspecialidadANDMedico).");
        } catch (DBManager2Exception e) {
            LOG.debug("Controlada una DBManager2Exception.", e);
            throw new GestorException("Algo ha ido mal con las conexiones a la base de datos cuando" +
                    "se intenaba  sacar un listado de las citas de una especialiad, un medico y  en una fecha determinada (getCitasFromFechaANDEspecialidadANDMedico).");
        }
        return resultado;
    }

    /**
     * Dada una consulta y una fecha me devuelve todas las citas posibles para esa consulta y ese día.
     *
     * @param consulta Consulta con especialidad, medico y, sobre todo, horario (inicio y fin) y duración de cada cita.
     * @param fecha    El día concreto para las citas.
     * @return Las citas posibles o una lista vacía si no puede ser.
     */
    public ArrayList<Cita> getAllCitasFromConsulta(Consulta consulta, Timestamp fecha) {

        ArrayList<Cita> resultado = new ArrayList<>();

        // Se pone la fecha y la hora de inicio.
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        SimpleDateFormat sdfF = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfH = new SimpleDateFormat("HH:mm");

        Date horaInicio = null;
        Date horaFin = null;

        Calendar cal = Calendar.getInstance();
        try {
            String fechaS = sdfF.format(new Date(fecha.getTime()));

            String horaS = sdfH.format(consulta.getHoraInicio());
            cal.setTime(sdf.parse(fechaS + " " + horaS));
            horaInicio = new Date(cal.getTimeInMillis());

            horaS = sdfH.format(consulta.getHoraFin());
            cal.setTime(sdf.parse(fechaS + " " + horaS));
            horaFin = new Date(cal.getTimeInMillis());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        Timestamp fechaCita = new Timestamp(horaInicio.getTime());

        while (horaInicio.compareTo(horaFin) < 0) {

            Cita cita = new CitaBuilder()
                    .setMedico(consulta.getMedico())
                    .setEspecialidad(consulta.getEspecialidad())
                    .setConsulta(consulta)
                    .setFecha(fechaCita)
                    .build();

            cal.setTimeInMillis(fechaCita.getTime());
            cal.add(Calendar.MINUTE, consulta.getDuracion());
            fechaCita = new Timestamp(cal.getTimeInMillis());
            horaInicio.setTime(cal.getTimeInMillis());

            resultado.add(cita);
        }
        return resultado;
    }

    /**
     * Metodo que a partir de una fecha de un dia te devuelve una fecha con el mismo dia pero a las 23:59
     *
     * @param fecha dia del que queremos obtener ese mismo dia a las 23:59
     * @return fecha, con el dia pasado por parametros y como hora las 23:59
     */
    private Timestamp getFechaFin(Timestamp fecha) {

        Calendar fechaFin = Calendar.getInstance();
        fechaFin.setTimeInMillis(fecha.getTime());
        fechaFin.set(Calendar.HOUR_OF_DAY, 23);
        fechaFin.set(Calendar.MINUTE, 59);

        return new Timestamp(fechaFin.getTimeInMillis());
    }

    /**
     * Metodo que elimina una cita de la BBDD
     * @param cita Contiene los datos de la cita que va a ser borrada de la base de datos.
     * @throws GestorException Si hay algun problema a la hora de borrar una cita.
     */
    public void bajaCita(Cita cita) throws GestorException {

        LOG.trace("Comprobar que la cita no viene en blanco.");
        if (cita == null || cita.getId() == 0) {
            LOG.debug("Se ha intentado dar de baja una cita que venía como 'null'.");
            throw new GestorException("La cita estaba vacía o no era válida.");
        }

        try (Connection conex = DBManager2.getConn()) {

            LOG.trace("DELETE FROM Cita WHERE Cita_Id = {}", cita.getId());
            PreparedStatement pstm = conex.prepareStatement(
                    "DELETE FROM Cita WHERE Cita_Id = ?");

            pstm.setInt(1, cita.getId());
            pstm.execute();

            pstm.close();

        } catch (SQLException e) {
            LOG.debug("Controlada una SQLException.", e);
            throw new GestorException("La cita no puede ser borrada.Error en el SQL o en la BBDD a la hora de invocar el método bajaCita (GestorCitas) ");
        } catch (DBManager2Exception e) {
            LOG.debug("Controlada una DBManager2Exception.", e);
            throw new GestorException("Algo ha ido mal con las conexiones a la base de datos al borrar una cita.(GestorCitas)");
        }
    }

    public Cita getCitaFromId(int idCita) throws GestorException {
        Cita resultado = null;
        GestorConsultas gestorConsultas = new GestorConsultas();
        GestorMedicos gestorMedicos = new GestorMedicos();
        GestorEspecialidades gestorEspecialidades = new GestorEspecialidades();
        GestorPacientes gestorPacientes = new GestorPacientes();

        String sql = "SELECT  Cita_IdPaciente, Cita_IdConsulta, Cita_IdMedico, Cita_Fecha, Cita_Id, Cita_IdEspecialidad " +
                "FROM Cita WHERE Cita_Id = ?";

        try (Connection conex = DBManager2.getConn()) {
            PreparedStatement ptstm = conex.prepareStatement(sql);

            ptstm.setInt(1, idCita);
            ResultSet rs = ptstm.executeQuery();

            if (rs.next()) {
                resultado = (new CitaBuilder()
                        .setId(rs.getInt("Cita_Id"))
                        .setPaciente(gestorPacientes.getPacienteFromId(rs.getInt("Cita_IdPaciente")))
                        .setMedico(gestorMedicos.getMedicoById(rs.getInt("Cita_IdMedico")))
                        .setFecha(rs.getTimestamp("Cita_Fecha"))
                        .setEspecialidad(gestorEspecialidades.getEspecialidadFromId(rs.getInt("Cita_IdEspecialidad")))
                        .setConsulta(gestorConsultas.getConsultaFromId(rs.getInt("Cita_IdConsulta")))
                        .build());
            }
        } catch (GestorException e) {
            LOG.debug("Controlada una GestorException.", e);
            throw new GestorException("Algo ha ido mal llamando a un gestor (Medicos, Consultas, Especialidades...) " +
                    "cuando se intentaba sacar una cita a partir de un id.");
        } catch (SQLException e) {
            LOG.debug("Controlada una SQLException.", e);
            throw new GestorException("Error en la sentencia sql a la hora de " +
                    "sacar una cita a través de su id.");
        } catch (DBManager2Exception e) {
            LOG.debug("Controlada una DBManager2Exception.", e);
            throw new GestorException("Algo ha ido mal con las conexiones a la base de datos cuando " +
                    "se intenaba obtener una cita de un id.");
        }
        return resultado;
    }

    /**
     * Devuelve el número de citas que hay de <i>ahora</i> en adelante.
     *
     * @return El número de citas pendientes. Si hay error, devuelve -1.
     */
    public int getCitasPendientes() {

        int resultado = -1;
        String SQL = "SELECT count(Cita_Id) FROM Cita WHERE Cita_Fecha >= CURRENT_DATE()";

        try (Connection conex = DBManager2.getConn()) {
            PreparedStatement ptstm = conex.prepareStatement(SQL);

            ResultSet rs = ptstm.executeQuery();

            if (rs.next()) {
                resultado = rs.getInt(1);
            }
        } catch (SQLException e) {
            LOG.debug("Controlada una SQLException contando citas (getCitasPendientes()).", e);
        } catch (DBManager2Exception e) {
            LOG.debug("Controlada una DBManager2Exception  contando citas (getCitasPendientes()).", e);
        }

        return resultado;
    }
}



