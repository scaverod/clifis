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

import es.upm.etsisi.clifis.model.HistorialMedico;
import es.upm.etsisi.clifis.model.HistorialMedicoBuilder;
import es.upm.etsisi.clifis.model.Paciente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Gestiona el historial de los pacientes con respecto al gestor de bases de datos.
 */
public class GestorHistoriales {

    Logger LOG = LoggerFactory.getLogger("es.upm.etsisi.clifis.gestores.GestorHistoriales");

    private GestorEspecialidades gestorEspecialidades;
    private GestorMedicos gestorMedicos;
    private GestorPacientes gestorPacientes;

    public GestorHistoriales(){
        this.gestorEspecialidades = new GestorEspecialidades();
        this.gestorMedicos = new GestorMedicos();
        this.gestorPacientes = new GestorPacientes();
    }

    /**
     * Metodo que da de alta en la BBDD un comentario que formara parte dle historial medico de un paciente concreto.
     * Además el historial medico será escrito por un médico con una especialidad concreta.
     * @param historial  Contiene los datos del comentario, médico, paciente, fecha y especialidad.
     * @throws GestorException En el caso de que haya algún problema a la hora de insertar la nueva historia.
     */
    public void altaHistorial(HistorialMedico historial) throws GestorException {

        try (Connection con = DBManager2.getConn()) {

            PreparedStatement pstm = con.prepareStatement("INSERT INTO HistorialMedico (" +
                    "H_IdMedico, H_IdEspecialidad, H_Fecha, H_Comentario, H_IdPaciente) " +
                    "VALUES (?,?,?,?,?);");

            pstm.setInt(1,historial.getMedico().getId());
            pstm.setInt(2,historial.getEspecialidad().getId());
            pstm.setTimestamp(3, historial.getFecha());
            pstm.setString(4, historial.getComentario());
            pstm.setInt(5,historial.getPaciente().getId());

            pstm.execute();

        } catch (SQLException e) {
            LOG.debug("Controlada una SQLException.", e);
            throw new GestorException("Error en la sentencia sql a la hora de " +
                    "introducir en la base de datos un nuevo comentario para el historial médico de un paciente.");
        } catch (DBManager2Exception e) {
            LOG.debug("Controlada una GestorException.", e);
            throw new GestorException("Algo ha ido mal llamando al gestor de historiales medicos.");
        }
    }

    /**
     * Elimina un Historial Medico.
     *
     * El Historial medico  no puede ser nulo ni el ID ser 0 (valor por defecto si no se indica lo contrario
     * cuando se crea el historial).
     *
     * Según el {@link SQLException} que pueda dar el borrado, se eleva un {@link GestorException} para indicar
     * que ese ID no existe o que se trata de otro tipo de error.
     *
     * @param historialMedico Contiene el ID del historial a borrar.
     * @throws GestorException Si hay algún problema en la acción de borrado.
     */
    public void bajaHistorial(HistorialMedico historialMedico) throws GestorException {

        LOG.trace("Comprobar que el historial no viene en blanco.");
        if (historialMedico == null || historialMedico.getId() == 0) {
            LOG.debug("Se ha intentado dar de baja un historial que venía como 'null'.");
            throw new GestorException("El historial estaba vacío o no era válido.");
        }

        try (Connection conex = DBManager2.getConn()) {


            PreparedStatement pstm = conex.prepareStatement(
                    "DELETE FROM HistorialMedico WHERE H_Id = ?");

            pstm.setInt(1, historialMedico.getId());
            pstm.execute();
            pstm.close();

        } catch (SQLException e) {
            LOG.debug("Controlada una SQLException." , e);
            throw new GestorException("Se ha producido un error. Seguro que el historial '" +
                    historialMedico.getId() + "' es correcto?.");

        } catch (DBManager2Exception e) {
            LOG.debug("Controlada una DBManager2Exception." , e);
            throw new GestorException("Algo ha ido mal con las conexiones a la base de datos al borrar un historial.");
        }
    }

    public List<HistorialMedico> getHistorial(Paciente paciente) throws GestorException {
        return this.getHistorial(paciente.getId());
    }

    public List<HistorialMedico> getHistorial(int pacienteId) throws GestorException {
        ArrayList<HistorialMedico> resultado = new ArrayList<>();

        if (pacienteId < 1) {
            LOG.debug("El paciente con id '{}' no es correcto, no sepuede obtener su historia.", pacienteId);
            throw new GestorException("El paciente pedido no es correcto.");
        }

        String SQL = "SELECT H_Id, H_IdMedico, H_IdEspecialidad, H_Fecha, H_Comentario, H_IdPaciente FROM HistorialMedico WHERE H_IdPaciente=?";

        try (Connection conex = DBManager2.getConn()){
            PreparedStatement ptstm = conex.prepareStatement(SQL);

            ptstm.setInt(1, pacienteId);
            ResultSet rs = ptstm.executeQuery();
            HistorialMedicoBuilder historialMedicoBuilder = new HistorialMedicoBuilder();

            while (rs.next()) {
                resultado.add(
                        historialMedicoBuilder
                                .setId(rs.getInt("H_Id"))
                                .setMedico(this.gestorMedicos.getMedicoById(rs.getInt("H_IdMedico")))
                                .setEspecialidad(this.gestorEspecialidades.getEspecialidadFromId(rs.getInt("H_IdEspecialidad")))
                                .setFecha(rs.getTimestamp("H_Fecha"))
                                .setComentario(rs.getString("H_comentario"))
                                .setPaciente(this.gestorPacientes.getPacienteFromId(rs.getInt("H_IdPaciente")))
                                .build()
                );
            }

            if (resultado.size() == 0){
                LOG.debug("El paciente con id '{}' no tiene historial, no sepuede obtener su historia.", pacienteId);
                throw new GestorException("El paciente no tiene historial.");
            }

        } catch (SQLException e) {
            LOG.debug("Controlada una SQLException." , e);
            throw new GestorException("Algo ha ido mal con la base de datos al obtener la histroia de un paciente.");
        } catch (DBManager2Exception e) {
            LOG.debug("Controlada una DBManager2Exception." , e);
            throw new GestorException("Algo ha ido mal con las conexiones a la base de datos al obtener la histroia de un paciente.");
        }

        return resultado;
    }

    /**
     * Método que modifica los datos de un historial medico. Siempre modifica el comentario.
     * También puede modificar la fecha en el caso que se desee.
     * @param historialMedico  Historial de donde obtenemos la id el cual vamos a modificar.
     * @throws GestorException  Si algo ha ido mal en la modificación.
     */
    public void modificarHistorial (HistorialMedico historialMedico) throws GestorException {
        if (historialMedico == null || historialMedico.getId() == 0) {
            LOG.debug("Se ha intentado modificar un historial que venía como 'null'.");
            throw new GestorException("El historial estaba vacío o no era válido.");
        }

        try (Connection conex = DBManager2.getConn()){
            PreparedStatement psmt;
            String SQL;

            if (historialMedico.getFecha() != null){

                SQL = "UPDATE HistorialMedico SET H_Comentario = ?, H_Fecha = ? WHERE H_Id = ?";
                psmt=conex.prepareStatement(SQL);
                psmt.setTimestamp(2, historialMedico.getFecha());
                psmt.setInt(3, historialMedico.getId() );
            }else {
                SQL = "UPDATE HistorialMedico SET H_Comentario = ? WHERE H_Id = ?";
                psmt=conex.prepareStatement(SQL);
                psmt.setInt(2, historialMedico.getId() );
            }

            psmt.setString(1, historialMedico.getComentario());

            psmt.executeUpdate();

        } catch (SQLException e) {
            LOG.debug("Controlada una SQLException." , e);
            throw new GestorException("Hay un problema en el sql. (modificarHistorial)");
        } catch (DBManager2Exception e) {
            LOG.debug("Controlada una DBManager2Exception." , e);
            throw new GestorException("Hay un problema en la conexion a la base de datos. (modificarHistorial)");
        }

    }
}
