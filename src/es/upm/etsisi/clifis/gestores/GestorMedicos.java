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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona los médicos con respecto al gestor de bases de datos.
 */
public class GestorMedicos implements Serializable {

    Logger LOG = LoggerFactory.getLogger("es.upm.etsisi.clifis.gestores.GestorMedicos");

    public GestorMedicos(){
    }

    /**
     * Da de alta un medico.
     *
     * @param medico Contiene la información del médico a dar de alta.
     */
    public void altaMedico(Medico medico) throws GestorException {
        LOG.trace("Comprobar que le médico no viene en blanco.");
        if (medico == null) {
            LOG.debug("Se ha intentado dar de alta un médico que venía como 'null'.");
            throw new GestorException("Hay algún campo del médico que está vacío.");
        }

        String errores = this.validarMedico(medico);
        if (!errores.isEmpty()) {
            LOG.debug("Los datos de médico no son válidos para darlo de alta: {}", errores);
            throw new GestorException(errores);
        }

        // Si no hay excepciones es que el médico se puede dar de alta.

        try (Connection con = DBManager2.getConn()) {

            PreparedStatement pstm = con.prepareStatement(
                    "INSERT INTO Medico (" +
                            "Med_NumColegiado, Med_Nombre, Med_Apellidos, Med_Password) " +
                            "VALUES ( ?, ?, ?, ?)"
            );

            pstm.setInt(1, medico.getNumCol());
            pstm.setString(2, medico.getNombre());
            pstm.setString(3, medico.getApellidos());
            pstm.setString(4, medico.getPassword());
            pstm.execute();
            pstm.close();

            PreparedStatement pstm2 =
                    con.prepareStatement("INSERT INTO Medico_Especialidad (MedEsp_MedId, MedEsp_EspId) VALUES (? ,?)");

            int idMedico = 0;

            PreparedStatement pstm3 =
                    con.prepareStatement("SELECT Med_Id FROM Medico WHERE Med_NumColegiado= ?");

            pstm3.setInt(1, medico.getNumCol());
            ResultSet rs = pstm3.executeQuery();

            if(rs.next()) {
                idMedico = rs.getInt("Med_Id");
                LOG.trace("Voy a meter una especialidad asociada " + idMedico);
            }
            for (Especialidad especialidad: medico.getEspecialidades()) {
                pstm2.setInt(1, idMedico);
                pstm2.setInt(2, especialidad.getId());
                pstm2.execute();
            }

            pstm2.close();

        } catch (SQLException e) {
            LOG.debug("Controlada una SQLException." , e);
            if (e.getSQLState().startsWith("23")) {
                throw new GestorException("El medico no puede ser borrado. Hay otros elementos que tienen este médico asignado.");
            }
            throw new GestorException("Se ha producido un error. Seguro que el medico '" +
                    medico.getNombre() + "' es correcto?.");

        } catch (DBManager2Exception e) {
            LOG.debug("Controlada una DBManager2Exception." , e);
            throw new GestorException("Algo ha ido mal con las conexiones a la base de datos al borrar una consulta.");
        }
    }

    public void bajaMedico(Medico medico) throws GestorException {

        LOG.trace("Comprobar que medico no viene en blanco.");
        LOG.trace("Medico de id: " + medico.getId() );
        if (medico == null || medico.getId() == 0) {
            LOG.debug("Se ha intentado dar de baja un medico que venía como 'null'.");
            throw new GestorException("El medico estaba vacío o no era válido.");
        }

        try (Connection conex = DBManager2.getConn()){
            try {
                conex.setAutoCommit(false);
                PreparedStatement pstm0 = conex.prepareStatement(
                        "DELETE FROM Medico_Especialidad WHERE MedEsp_MedId=?");
                pstm0.setInt(1, medico.getId());
                pstm0.execute();

                PreparedStatement pstm = conex.prepareStatement(
                        "DELETE FROM Medico WHERE Med_Id = ?");
                pstm.setInt(1, medico.getId());
                pstm.execute();

                conex.commit();
            }catch (SQLException e) {
                conex.rollback();
                LOG.debug("Controlada una SQLException.", e);
                medico = this.getMedicoById(medico.getId());
                String descripcionError = "El médico " + medico.getNombre() + " " + medico.getApellidos() + " no puede ser borrado.";
                if (e.getErrorCode() == 1451)
                    descripcionError += " Tiene citas o consultas asociadas.";
                throw new GestorException(descripcionError);
            } finally {
                conex.setAutoCommit(true);
            }
        } catch (SQLException e) {
            LOG.debug("Controlada una SQLException.", e);
        } catch (DBManager2Exception e) {
            LOG.debug("Controlada una DBManager2Exception." , e);
            throw new GestorException("Algo ha ido mal con las conexiones a la base de datos al borrar una consulta.");
        }
    }

    public void modificarMedico(Medico medico) throws  GestorException{

        LOG.debug("La pass del médico es: "+ medico.getPassword());
        try (Connection conex = DBManager2.getConn()){

            boolean tieneConsultas = false;

            int consultaProblema=-1;
            String espProblema = "";
            GestorConsultas gc = new GestorConsultas();
            List<Consulta> consultas = gc.getConsultas();
            for (Consulta consulta: consultas) {
                if (consulta.getMedico().getId() == medico.getId()) {
                    consultaProblema = consulta.getNumSala();
                    espProblema = consulta.getEspecialidad().getNombre();
                    tieneConsultas = true;
                }
            }
            if(!tieneConsultas) {
                PreparedStatement pstm = conex.prepareStatement(
                        "UPDATE Medico" +
                                " SET Med_Nombre = ?, Med_Apellidos = ?, Med_NumColegiado = ? WHERE Med_Id = ?");

                pstm.setString(1, medico.getNombre());
                pstm.setString(2, medico.getApellidos());
                pstm.setInt(3, medico.getNumCol());
                pstm.setInt(4, medico.getId());
                pstm.execute();

                if (pstm.getUpdateCount() == 0)
                    throw new GestorException("Se ha introducido un medico que no existe: '" +
                            medico.getNombre() + "'.");
                pstm.close();

                PreparedStatement pstm0 = conex.prepareStatement(
                        "DELETE FROM Medico_Especialidad WHERE MedEsp_MedId=?");
                pstm0.setInt(1, medico.getId());
                pstm0.execute();

                PreparedStatement pstmA =
                        conex.prepareStatement("INSERT INTO Medico_Especialidad (MedEsp_MedId, MedEsp_EspId) VALUES (? ,?)");

                for (Especialidad especialidad: medico.getEspecialidades()) {
                    pstmA.setInt(1, medico.getId());
                    pstmA.setInt(2, especialidad.getId());
                    pstmA.execute();
                }
            } else {
                throw new GestorException("Las especialidad " + espProblema + " del médico no puede ser modificada porque la tiene asignada en una consulta en la sala " + consultaProblema + ".");
            }
        } catch (SQLException e) {
            throw new GestorException("Se ha producido un error. Seguro que el médico '" +
                    medico.getNombre() + "' es correcto?.");
        } catch (DBManager2Exception e) {
            LOG.debug("Controlada una DBManager2Exception." , e);
            throw new GestorException("Algo ha ido mal con las conexiones a la base de datos al borrar una consulta.");
        }

    }

    private String validarMedico(Medico medico) {
        String resultado = "";

        String nombre = medico.getNombre();
        if(nombre == null || nombre.isEmpty()) {
            resultado += "No puede darse de alta un médico sin el nombre. ";
        }

        String apellidos = medico.getApellidos();
        if(apellidos == null || apellidos.isEmpty()){
            resultado += "No puede darse de alta un médico sin apellidos. ";
        }

        int numColegiado = medico.getNumCol();
        if(numColegiado == 0) {
            resultado += "No puede darse de alta un médico sin su número de colegiado. ";
        }

        List<Especialidad> especialidades = medico.getEspecialidades();
        if(especialidades.get(0) == null) {
            resultado += "No puede darse de alta un médico sin especialidad. ";
        }

        String password = medico.getPassword();
        if(password == null || password.isEmpty()) {
            resultado += "No puede darse de alra un médico sin una contraseña.";
        }

        return resultado;
    }

    public List<Medico> getMedicos() throws GestorException {

        ArrayList<Medico> resultado = new ArrayList<>();

        try (Connection conex = DBManager2.getConn()){
            PreparedStatement ptstm = conex.prepareStatement(
                    "SELECT Med_Id, Med_Nombre, Med_Apellidos, Med_NumColegiado" +
                            " FROM Medico" +
                            " ORDER BY Med_Apellidos ASC");

            ResultSet rs = ptstm.executeQuery();

            while(rs.next()) {
                resultado.add(new MedicoBuilder()
                        .setId(rs.getInt("Med_Id"))
                        .setNombre(rs.getString("Med_Nombre"))
                        .setApellidos(rs.getString("Med_Apellidos"))
                        .setNumCol(rs.getInt("Med_NumColegiado"))
                        // .setPassword(rs.getString("Med_Password"))
                        .build());
            }

        } catch (SQLException e) {
            LOG.debug("Controlada una SQLException." , e);
            throw new GestorException("Algo ha ido mal con la base de datos al listar los médicos.");
        } catch (DBManager2Exception e) {
            LOG.debug("Controlada una DBManager2Exception." , e);
            throw new GestorException("Algo ha ido mal con las conexiones a la base de datos al listar los médicos.");
        }

        return resultado;
    }

    public Medico getMedicoById(int idMedico) throws GestorException {

        Medico resultado =null;

        try (Connection conex = DBManager2.getConn()){
            PreparedStatement pstm = conex.prepareStatement(
                    "SELECT Med_Id, Med_Nombre, Med_Apellidos, Med_NumColegiado" +//, Med_Password" +
                            " FROM Medico " +
                            " WHERE Med_Id= ?");

            pstm.setInt(1, idMedico);
            ResultSet rs = pstm.executeQuery();

            if(rs.next()) {

                resultado = (new MedicoBuilder()
                        .setId(rs.getInt("Med_Id"))
                        .setNombre(rs.getString("Med_Nombre"))
                        .setApellidos(rs.getString("Med_Apellidos"))
                        .setNumCol(rs.getInt("Med_NumColegiado"))
                        // .setPassword(rs.getString("Med_Password"))
                        .build());
            }

        } catch (SQLException e) {
            LOG.debug("Controlada una SQLException." , e);
            throw new GestorException("Algo ha ido mal con la base de datos al listar las especialidades.");
        } catch (DBManager2Exception e) {
            LOG.debug("Controlada una DBManager2Exception." , e);
            throw new GestorException("Algo ha ido mal con las conexiones a la base de datos al listar las especialidades.");
        }

        return resultado;
    }

    /**
     * Consulta los médicos que practican una determinada especialidad.
     *
     * @param especialidad Especialidad consultada. Su campo ID debe estar cargado.
     * @return Una lista de médicos que practican dicha especialidad. Si no hay ninguno, estará vacía.
     * @throws GestorException En caso de que vaya algo mal se eleva esta excepción con un mensaje indicativo.
     */
    public List<Medico> getMedicosByEspecialidad(Especialidad especialidad) throws GestorException {

        ArrayList<Medico> resultado = new ArrayList<>();

        try (Connection conex = DBManager2.getConn()){
            PreparedStatement ptstm = conex.prepareStatement(
                    "SELECT Med_Id, Med_Nombre, Med_Apellidos, Med_NumColegiado, Es_Id, Es_Nombre FROM (Medico_Especialidad) " +
                            "INNER JOIN Medico ON Medico.Med_Id = Medico_Especialidad.MedEsp_MedId " +
                            "INNER JOIN Especialidad ON Medico_Especialidad.MedEsp_EspId = Es_Id " +
                            "WHERE Es_Id=?"
            );

            ptstm.setInt(1, especialidad.getId());
            ResultSet rs = ptstm.executeQuery();

            while(rs.next()) {
                especialidad.setNombre(rs.getString("Es_Nombre"));
                ArrayList<Especialidad> especialidades = new ArrayList<>();
                especialidades.add(especialidad);

                resultado.add(new MedicoBuilder()
                        .setId(rs.getInt("Med_Id"))
                        .setNombre(rs.getString("Med_Nombre"))
                        .setApellidos(rs.getString("Med_Apellidos"))
                        .setNumCol(rs.getInt("Med_NumColegiado"))
                        .setEspecialidades(especialidades)
                        .build());
            }

        } catch (SQLException e) {
            LOG.debug("Controlada una SQLException." , e);
            throw new GestorException("Algo ha ido mal con la base de datos al listar los médicos.");
        } catch (DBManager2Exception e) {
            LOG.debug("Controlada una DBManager2Exception." , e);
            throw new GestorException("Algo ha ido mal con las conexiones a la base de datos al listar los médicos.");
        }

        return resultado;
    }

    /**
     * Método que nos cuenta los medicos que hay en la base de datos.
     * @return El número de médicos. Si hay error, devuelve -1.
     */
    public int getNumeroMedicos() {

        int resultado = -1;
        String SQL = "SELECT count(Med_Id) FROM Medico";

        try (Connection conex = DBManager2.getConn()) {
            PreparedStatement ptstm = conex.prepareStatement(SQL);

            ResultSet rs = ptstm.executeQuery();

            if (rs.next()) {
                resultado = rs.getInt(1);
            }
        } catch (SQLException e) {
            LOG.debug("Controlada una SQLException contando medicos (getNumeroMedicos()).", e);
        } catch (DBManager2Exception e) {
            LOG.debug("Controlada una DBManager2Exception  contando medicos (getNumeroMedicos()).", e);
        }

        return resultado;
    }


}