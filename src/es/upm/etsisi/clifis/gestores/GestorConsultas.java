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
import java.util.*;


/**
 * Gestiona las las consultas con respecto al gestor de bases de datos.
 */
public class GestorConsultas implements Serializable {

    Logger LOG = LoggerFactory.getLogger("es.upm.etsisi.clifis.gestores.GestorConsultas");

    public GestorConsultas() {
    }

    /**
     * Da de alta una Consulta.
     *
     * @param consulta Contiene la información de la consulta a dar de alta.
     */
    public void altaConsulta(Consulta consulta) throws GestorException {

        LOG.trace("Comprobar que la consulta no viene en blanco.");
        if(consulta==null)
            throw new GestorException("No se a recibido ninguna consulta.");
        else if (consulta.getDiaSemana().isEmpty())
            throw new GestorException("No ha seleccionado el dia de la semana.");
        else if (consulta.getHoraFin()==null)
            throw new GestorException("No ha seleccionado la hora de finalizacion de la consulta.");
        else if (consulta.getHoraInicio()==null)
            throw new GestorException("No ha seleccionado la hora de inicio de la consulta.");
        else if (consulta.getDuracion() == 0)
            throw new GestorException("No ha seleccionado la duración de la consulta.");
        else if (consulta.getEspecialidad().getId() == 0)
            throw new GestorException("No ha seleccionado la especialidad de la consulta.");
        else if (consulta.getMedico().getId() == 0)
            throw new GestorException("No ha seleccionado el médico de la consulta.");
        else if (consulta.getNumSala() == 0)
            throw new GestorException("No ha seleccionado la sala de la consulta.");
        else if (consulta.getHoraInicio().compareTo(consulta.getHoraFin()) >= 0)
            throw new GestorException("Una consulta no puede acabar antes de que empiece ni a la misma hora.");
        else if (!this.medicoTieneEspecialidad(consulta)) {
            LOG.debug("Se ha enviado una consulta con un médico que no tiene esa especialidad.");
            throw new GestorException("El médico seleccionado no tiene la especialidad " + consulta.getEspecialidad().getNombre() + ".");
        }

        try (Connection con = DBManager2.getConn()){

            String SQL = "SELECT Con_Id, Con_NumSala, Con_DiaSemana, Con_HoraInicio, Con_HoraFin FROM Consulta " +
                    "WHERE Con_NumSala =" + consulta.getNumSala() + " " +
                    "AND Con_DiaSemana='" + consulta.getDiaSemana() + "' " +
                    "AND ('" + consulta.getHoraInicio() + "' BETWEEN Con_HoraInicio AND Con_HoraFin " +
                    "OR '" + consulta.getHoraFin() + "' BETWEEN Con_HoraInicio AND Con_HoraFin " +
                    "OR (Con_HoraInicio >= '" +consulta.getHoraInicio() + "' AND Con_HoraFin <= '" + consulta.getHoraFin() + "'))";

            LOG.trace("Selección de consultas yuxtapuestas: " + SQL);

            PreparedStatement pstm1 = con.prepareStatement(SQL);

            ResultSet rs1 = pstm1.executeQuery();

            if (rs1.next())
                throw new GestorException("Existe un conflicto de horarios en la consulta [" +
                        rs1.getString("Con_NumSala") + "], que ya tiene asignado los " +
                        rs1.getString("Con_DiaSemana") + " el horario de " + rs1.getString("Con_HoraInicio") +
                        " a " + rs1.getString("Con_HoraFin") + ". Revise los datos de alta de consulta.");

            SQL = "INSERT INTO Consulta (" +
                    "Con_NumSala,Con_HoraInicio,Con_HoraFin,Con_IdEspecialidad,Con_DuracionCita," +
                    "Con_IdMedico,Con_DiaSemana) " +
                    "VALUES (" +
                    "?,?,?,?,?,?,?)";

            LOG.trace("INSERT de alta consulta: " + SQL);

            PreparedStatement pstm = con.prepareStatement(SQL);
            pstm.setInt(1, consulta.getNumSala());
            pstm.setTime(2, consulta.getHoraInicio());
            pstm.setTime(3, consulta.getHoraFin());
            pstm.setInt(4, consulta.getEspecialidad().getId());
            pstm.setInt(5, consulta.getDuracion());
            pstm.setInt(6, consulta.getMedico().getId());
            pstm.setString(7, consulta.getDiaSemana());

            System.out.println("--> " + pstm);

            pstm.execute();
            pstm.close();

        } catch (SQLException e) {
            LOG.debug("Controlada una SQLException." , e);

            // Comprobar que la consulta no es demasiado larga.
            if (e.getMessage().contains("Data too long"))
                throw new GestorException("El nombre de la consulta no puede ser tan largo.");
            else
                throw new GestorException("Se ha producido un error, pero sólo se sabe que tiene " +
                        "que ver con la base de datos. ¿Estás insertando de manera correcta?.");

        } catch (DBManager2Exception e) {
            LOG.debug("Controlada un a DBManager2Exception.", e);
            throw new GestorException("Algo ha ido mal con las conexiones a la base de datos al insertar una consulta.");
        }
    }

    /**
     * Da de baja una consulta de la BBDD
     * @param consulta Contiene los datos de la consulta que se desea borrar
     * @throws GestorException En el caso de que se produzca algún problema a la hora de borrar una consulta
     */
    public void bajaConsulta(Consulta consulta) throws GestorException {

        LOG.trace("Comprobar que la consulta no viene en blanco.");
        LOG.trace("Consulta de id: " + consulta.getId() );
        if (consulta == null || consulta.getId() == 0) {
            LOG.debug("Se ha intentado dar de baja una consulta que venía como 'null'.");
            throw new GestorException("La consulta estaba vacía o no era válida.");
        }

        try (Connection conex = DBManager2.getConn()){
            int consulta_id = consulta.getId();
            LOG.trace("DELETE FROM Consulta WHERE Con_Id = {}",consulta_id);
            PreparedStatement pstm = conex.prepareStatement(
                    "DELETE FROM Consulta WHERE Con_Id = ?");

            pstm.setInt(1, consulta_id);
            pstm.execute();

            if (pstm.getUpdateCount() == 0)
                throw new GestorException("Se ha introducido una consulta que no existe: '" +
                        consulta.getNumSala() + "'.");

            pstm.close();

        } catch (SQLException e) {
            LOG.debug("Controlada una SQLException." , e);
            if (e.getSQLState().startsWith("23")) {
                throw new GestorException("La consulta no puede ser borrada. Hay otros elementos que tienen esta consulta asignada.");
            }
            throw new GestorException("Se ha producido un error. Seguro que la consulta '" +
                    consulta.getNumSala() + "' es correcta?.");

        } catch (DBManager2Exception e) {
            LOG.debug("Controlada una DBManager2Exception." , e);
            throw new GestorException("Algo ha ido mal con las conexiones a la base de datos al borrar una consulta.");
        }
    }

    /**
     * Método que devuelve todas las consultas de la BBDD con todos sus atributos completos.
     * @return Todas las consultas de la BBDD
     * @throws GestorException En el caso de que haya algún problema.
     */
    public List<Consulta> getConsultas() throws GestorException {

        ArrayList<Consulta> resultado = new ArrayList<>();
        GestorMedicos gestorMedicos = new GestorMedicos();
        GestorEspecialidades gestorEspecialidades = new GestorEspecialidades();

        try (Connection conex = DBManager2.getConn()){
            PreparedStatement ptstm = conex.prepareStatement(
                    "SELECT Con_Id, Con_NumSala, Con_HoraInicio, Con_HoraFin, Con_IdEspecialidad, Con_DuracionCita, Con_IdMedico, Con_DiaSemana" +
                            " FROM Consulta " +
                            "ORDER BY Con_NumSala ASC;");

            ResultSet rs = ptstm.executeQuery();

            while(rs.next()) {
                int id = rs.getInt("Con_Id");
                int numSala = rs.getInt("Con_NumSala");
                int duracion = rs.getInt("Con_DuracionCita");
                Time horaInicio = rs.getTime("Con_HoraInicio");
                Time horaFin = rs.getTime("Con_HoraFin");
                String diaSemana = rs.getString("Con_DiaSemana");
                resultado.add(new ConsultaBuilder()
                        .setId(id)
                        .setMedico(gestorMedicos.getMedicoById(rs.getInt("Con_IdMedico")))
                        .setEspecialidad(gestorEspecialidades.getEspecialidadFromId(rs.getInt("Con_IdEspecialidad")))
                        .setNumSala(numSala)
                        .setHoraFin(horaFin)
                        .setHoraInicio(horaInicio)
                        .setDiaSemana(diaSemana)
                        .setDuracion(duracion)
                        .build());
            }

        } catch (SQLException e) {
            LOG.debug("Controlada una SQLException." , e);
            throw new GestorException("Algo ha ido mal con la base de datos al listar las consultas.");
        } catch (DBManager2Exception e) {
            LOG.debug("Controlada una DBManager2Exception." , e);
            throw new GestorException("Algo ha ido mal con las conexiones a la base de datos al listar las consultas.");
        }

        return resultado;
    }

    /**
     * Metodo que nos proporciona todas las consultas a partir de una especialidad.
     * @param especialidad Contiene los datos de la especialidad que queremos obtener todas las consultas asociadas.
     * @return Lista con todas las consultas de una especialidad dada.
     * @throws GestorException En el caso de que haya algun problema para obtener el listado.
     */
    public List<Consulta> getConsultasFromEspecialidad(Especialidad especialidad) throws GestorException {

        ArrayList<Consulta> resultado = new ArrayList<>();
        GestorMedicos gestorMedicos = new GestorMedicos();
        GestorEspecialidades gestorEspecialidades = new GestorEspecialidades();

        try (Connection conex = DBManager2.getConn()){
            PreparedStatement pstm = conex.prepareStatement(
                    "SELECT Con_Id, Con_NumSala, Con_HoraInicio, Con_HoraFin, Con_IdEspecialidad, Con_DuracionCita, Con_IdMedico, Con_DiaSemana" +
                            " FROM Consulta " +
                            "WHERE Con_IdEspecialidad=? ORDER BY FIELD(Con_DiaSemana, 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo')");



            LOG.trace("Me piden consultas para la especialidad: {} ({})", especialidad.getNombre(), especialidad.getId());
            pstm.setInt(1, especialidad.getId());
            ResultSet rs = pstm.executeQuery();

            while(rs.next()) {
                resultado.add(new ConsultaBuilder()
                        .setId(rs.getInt("Con_Id"))
                        .setMedico(gestorMedicos.getMedicoById(rs.getInt("Con_IdMedico")))
                        .setDuracion(rs.getInt("Con_DuracionCita"))
                        .setEspecialidad(gestorEspecialidades.getEspecialidadFromId(especialidad.getId()))
                        .setNumSala(rs.getInt("Con_NumSala"))
                        .setHoraFin(rs.getTime("Con_HoraFin"))
                        .setHoraInicio(rs.getTime("Con_HoraInicio"))
                        .setDiaSemana(rs.getString("Con_DiaSemana"))
                        .build());
            }

        } catch (SQLException e) {
            LOG.debug("Controlada una SQLException." , e);
            throw new GestorException("Algo ha ido mal con la base de datos al coger las consultas por especialidad.");
        } catch (DBManager2Exception e) {
            LOG.debug("Controlada una DBManager2Exception." , e);
            throw new GestorException("Algo ha ido mal con las conexiones a la base de datos al coger las consultas por especialidad.");
        }

        return resultado;
    }

    /**
     * Metodo que nos proporciona  una  consultas de su identificador de la BBDD.
     * @param idConsulta Entero con el id de una cosnulta.
     * @return Lista con todas las consultas de una especialidad dada.
     * @throws GestorException En el caso de que haya algun problema para obtener el listado.
     */
    public Consulta getConsultaFromId(int idConsulta) throws GestorException {

        Consulta resultado = null;
        GestorMedicos gestorMedicos = new GestorMedicos();
        GestorEspecialidades gestorEspecialidades = new GestorEspecialidades();

        try (Connection conex = DBManager2.getConn()) {
            PreparedStatement pstm = conex.prepareStatement(
                    "SELECT Con_Id, Con_NumSala, Con_HoraInicio, Con_HoraFin, Con_IdEspecialidad, Con_DuracionCita, Con_IdMedico, Con_DiaSemana" +
                            " FROM Consulta " +
                            "WHERE Con_Id=?");

            pstm.setInt(1, idConsulta);
            LOG.trace("Me piden una consulta de id: {}", idConsulta);
            ResultSet rs = pstm.executeQuery();

            if (rs.next()) {
                resultado = new ConsultaBuilder()
                        .setId(idConsulta)
                        .setMedico(gestorMedicos.getMedicoById(rs.getInt("Con_IdMedico")))
                        .setDuracion(rs.getInt("Con_DuracionCita"))
                        .setEspecialidad(gestorEspecialidades.getEspecialidadFromId(rs.getInt("Con_IdEspecialidad")))
                        .setNumSala(rs.getInt("Con_NumSala"))
                        .setHoraFin(rs.getTime("Con_HoraFin"))
                        .setHoraInicio(rs.getTime("Con_HoraInicio"))
                        .setDiaSemana(rs.getString("Con_DiaSemana"))
                        .build();
            } else {
                LOG.debug("Se pedido generar un objeto consulta de ID {}, algo ha ido mal.", idConsulta);
                throw new GestorException("La consulta pedida no existe.");
            }
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
     * Metodo que nos proporciona todas las consultas a partir de una médico.
     * @param medico Contiene los datos del medico que queremos obtener todas las consultas asociadas.
     * @return Lista con todas las consultas de um médico dado.
     * @throws GestorException En el caso de que haya algun problema para obtener el listado.
     */
    public List<Consulta> getConsultasFromMedico(Medico medico) throws GestorException {

        ArrayList<Consulta> resultado = new ArrayList<>();
        GestorMedicos gestorMedicos = new GestorMedicos();
        GestorEspecialidades gestorEspecialidades = new GestorEspecialidades();

        try (Connection conex = DBManager2.getConn()){
            PreparedStatement pstm = conex.prepareStatement(
                    "SELECT Con_Id, Con_NumSala, Con_HoraInicio, Con_HoraFin, Con_IdEspecialidad, Con_DuracionCita, Con_IdMedico, Con_DiaSemana" +
                            " FROM Consulta " +
                            "WHERE Con_IdMedico=? ORDER BY FIELD(Con_DiaSemana, 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo')");

            LOG.trace("Me piden consultas para el medico de id: {} ", medico.getId());
            pstm.setInt(1, medico.getId());
            ResultSet rs = pstm.executeQuery();

            while(rs.next()) {

                resultado.add(new ConsultaBuilder()
                        .setId(rs.getInt("Con_Id"))
                        .setMedico(gestorMedicos.getMedicoById(medico.getId()))
                        .setDuracion(rs.getInt("Con_DuracionCita"))
                        .setEspecialidad(gestorEspecialidades.getEspecialidadFromId(rs.getInt("Con_IdEspecialidad")))
                        .setNumSala(rs.getInt("Con_NumSala"))
                        .setHoraFin(rs.getTime("Con_HoraFin"))
                        .setHoraInicio(rs.getTime("Con_HoraInicio"))
                        .setDiaSemana(rs.getString("Con_DiaSemana"))
                        .build());
            }

        } catch (SQLException e) {
            LOG.debug("Controlada una SQLException." , e);
            throw new GestorException("Algo ha ido mal con la base de datos al coger las consultas por medico.");
        } catch (DBManager2Exception e) {
            LOG.debug("Controlada una DBManager2Exception." , e);
            throw new GestorException("Algo ha ido mal con las conexiones a la base de datos al coger las consultas por medico.");
        }

        return resultado;
    }

    /**
     * Metodo que nos proporciona todas las consultas que estén relacionadas con un médico Y una especialidad.
     * @param medico Contiene los datos del medico que queremos obtener todas las consultas asociadas.
     * @param especialidad Contiene los datos de la especialidad que queremos obtener todas las consultas asociadas.
     * @return Lista con todas las consultas de um médico y una especialidad determinada.
     * @throws GestorException En el caso de que haya algun problema para obtener el listado.
     */
    public List<Consulta> getConsultasFromMedicoANDEspecialidad(Medico medico, Especialidad especialidad) throws GestorException {

        ArrayList<Consulta> resultado = new ArrayList<>();
        GestorMedicos gestorMedicos = new GestorMedicos();
        GestorEspecialidades gestorEspecialidades = new GestorEspecialidades();

        try (Connection conex = DBManager2.getConn()){
            PreparedStatement pstm = conex.prepareStatement(
                    "SELECT Con_Id, Con_NumSala, Con_HoraInicio, Con_HoraFin, Con_IdEspecialidad, Con_DuracionCita, Con_IdMedico, Con_DiaSemana" +
                            " FROM Consulta " +
                            "WHERE Con_IdMedico= ? AND Con_IdEspecialidad = ? ORDER BY FIELD(Con_DiaSemana, 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo')");

            LOG.trace("Me piden consultas para el medico de id: {} y la especialidad de id: {} ", medico.getId(), especialidad.getId());
            pstm.setInt(1, medico.getId());
            pstm.setInt(2, especialidad.getId());
            ResultSet rs = pstm.executeQuery();

            while(rs.next()) {

                resultado.add(new ConsultaBuilder()
                        .setId(rs.getInt("Con_Id"))
                        .setMedico(gestorMedicos.getMedicoById(medico.getId()))
                        .setDuracion(rs.getInt("Con_DuracionCita"))
                        .setEspecialidad(gestorEspecialidades.getEspecialidadFromId(especialidad.getId()))
                        .setNumSala(rs.getInt("Con_NumSala"))
                        .setHoraFin(rs.getTime("Con_HoraFin"))
                        .setHoraInicio(rs.getTime("Con_HoraInicio"))
                        .setDiaSemana(rs.getString("Con_DiaSemana"))
                        .build());
            }

        } catch (SQLException e) {
            LOG.debug("Controlada una SQLException." , e);
            throw new GestorException("Algo ha ido mal con la base de datos al coger las consultas por medico.");
        } catch (DBManager2Exception e) {
            LOG.debug("Controlada una DBManager2Exception." , e);
            throw new GestorException("Algo ha ido mal con las conexiones a la base de datos al coger las consultas por medico.");
        }

        return resultado;
    }

    /**
     * Modifica los datos de una consulta con los datos que trae la consulta del parámetro.
     *
     * @param consulta El id, es el de la consulta a modificar. El resto de información es lo que debe tener
     *                 asociado finalmente la consulta.
     */
    public void modificarConsulta(Consulta consulta) throws  GestorException{

        LOG.trace("Comprobar que la consulta no viene en blanco.");
        LOG.trace("Consulta de id: {}", consulta.getId() );
        if (consulta == null || consulta.getId() == 0) {
            LOG.debug("Se ha intentado modificar una consulta que venía como 'null'.");
            throw new GestorException("La consulta estaba vacía o no era válida.");
        }

        if (!this.medicoTieneEspecialidad(consulta)) {
            LOG.debug("Se ha enviado una consulta con un médico que no tiene esa especialidad.");
            throw new GestorException("El médico seleccionado no tiene la especialidad " + consulta.getEspecialidad().getNombre() + ".");
        }

        try (Connection conex = DBManager2.getConn()){

            PreparedStatement pstm = conex.prepareStatement(
                    "UPDATE Consulta" +
                            " SET Con_IdEspecialidad = ?, Con_DiaSemana = ?, Con_DuracionCita = ?, Con_HoraInicio = ?, Con_HoraFin = ?, Con_IdMedico = ?, Con_NumSala = ?" +
                            " WHERE Con_Id = ?");

            pstm.setInt(8, consulta.getId());
            pstm.setInt(1, consulta.getEspecialidad().getId());
            pstm.setString(2, consulta.getDiaSemana());
            pstm.setInt(3, consulta.getDuracion());
            pstm.setTimestamp(4, new Timestamp(consulta.getHoraInicio().getTime()));
            pstm.setTimestamp(5, new Timestamp(consulta.getHoraFin().getTime()));
            pstm.setInt(6, consulta.getMedico().getId());
            pstm.setInt(7, consulta.getNumSala());

            pstm.execute();

            if (pstm.getUpdateCount() == 0)
                throw new GestorException("Se ha introducido una consulta que no existe: '" +
                        consulta.getNumSala() + "'.");

            pstm.close();

        } catch (SQLException e) {
            LOG.debug("Controlada una SQLException." , e);
            if (e.getSQLState().startsWith("23")) {
                throw new GestorException("La consulta no puede ser borrada. Hay otros elementos que tienen esta consulta asignada.");
            }
            throw new GestorException("Se ha producido un error. Seguro que la consulta '" +
                    consulta.getNumSala() + "' es correcta?.");

        } catch (DBManager2Exception e) {
            LOG.debug("Controlada una DBManager2Exception." , e);
            throw new GestorException("Algo ha ido mal con las conexiones a la base de datos al borrar una consulta.");
        }

    }

    private boolean medicoTieneEspecialidad (Consulta consulta) throws GestorException {
        //TODO: Este método debería estar en GestorMedicos o Medico y recibir otro tipo de parámetros.
        boolean resultado = false;

        Especialidad espConsulta = consulta.getEspecialidad();
        Medico medico = consulta.getMedico();
        List<Especialidad> especialidadesDelMedico = new GestorEspecialidades().getEspecialidadesByMedico(medico);

        for(Especialidad especialidadDelMedico: especialidadesDelMedico) {
            if (especialidadDelMedico.getId() == espConsulta.getId() || resultado)
                resultado = true;
        }

        return resultado;
    }
}
