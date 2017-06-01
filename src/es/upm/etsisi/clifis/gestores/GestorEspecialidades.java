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

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import es.upm.etsisi.clifis.model.*;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gestiona las especialidades con respecto al gestor de bases de datos.
 */
public class GestorEspecialidades implements Serializable {


    Logger LOG = LoggerFactory.getLogger("es.upm.etsisi.clifis.gestores.GestorEspecialidades");

    public GestorEspecialidades() {
    }

    /**
     * Da de alta una especialidad.
     *
     * @param especialidad Contiene la información de la especialidad a dar de alta.
     */
    public void altaEspecialidad(Especialidad especialidad) throws GestorException {

        LOG.trace("Comprobar que la especialidad no viene en blanco.");
        if (especialidad == null || especialidad.getNombre().isEmpty()) {
            LOG.debug("Se ha intentado dar de alta una especialidad que venía como 'null'.");
            throw new GestorException("El nombre de la especialidad está vacío.");
        }

        try (Connection con = DBManager2.getConn()){

            LOG.trace("Comprobar que una especialidad muy similar no existe ya.");
            PreparedStatement pstm1 = con.prepareStatement(
                    "SELECT Es_Nombre FROM Especialidad " +
                                "WHERE Es_Nombre LIKE '" + especialidad.getNombre() + "'");
            ResultSet rs1 = pstm1.executeQuery();

            if (rs1.next())
                throw new GestorException("Ha sido encontrada la especialidad "
                + rs1.getString("Es_Nombre") + " en el sistema. No se puede introducir "
                + "más de una vez.");

            PreparedStatement pstm = con.prepareStatement("INSERT INTO Especialidad (Es_Nombre) VALUES (?)");
            pstm.setString(1, especialidad.getNombre());
            pstm.execute();
            pstm.close();

        } catch (SQLException e) {
            LOG.debug("Controlada una SQLException." , e);

            // Comprobar que la especialidad no es demasiado larga.
            if (e.getMessage().contains("Data too long"))
                throw new GestorException("El nombre de la especialidad no puede ser tan largo.");
            else
                throw new GestorException("Se ha producido un error, pero sólo se sabe que tiene " +
                        "que ver con la base de datos. ¿Estás borrando de manera correcta?.");

        } catch (DBManager2Exception e) {
            LOG.debug("Controlada un a DBManager2Exception.", e);
            throw new GestorException("Algo ha ido mal con las conexiones a la base de datos al insertar una especialidad.");
        }
    }

    /**
     * Consulta todas las especialidades que hay almacenadas en la base de datos.
     *
     * @return Las especialidades existentes en el sistema.
     */
    public List<Especialidad> getEspecialidades() throws GestorException {

        ArrayList<Especialidad> resultado = new ArrayList<>();

        try (Connection conex = DBManager2.getConn()){
            PreparedStatement ptstm = conex.prepareStatement(
                    "SELECT Es_Id, Es_Nombre" +
                        " FROM Especialidad " +
                            "ORDER BY Es_Nombre ASC;");

            ResultSet rs = ptstm.executeQuery();

            while(rs.next()) {
                String nombre = rs.getString("Es_Nombre");
                int id = rs.getInt("Es_Id");
                resultado.add(new EspecialidadBuilder()
                        .setNombre(nombre)
                        .setId(id)
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
     * Da de baja una especialidad.
     *
     * La especialidad no puede ser nula ni el ID ser 0 (valor por defecto si no se indica lo contrario
     * cuando se crea la especialidad).
     *
     * Según el {@link SQLException} que pueda dar el borrado, se eleva un {@link GestorException} para indicar
     * que ese ID no existe o que se trata de otro tipo de error.
     *
     * @param especialidad Contiene el ID de la especialidad a borrar.
     * @throws GestorException Si hay algún problema en la acción de borrado.
     */
    public void bajaEspecialidad(Especialidad especialidad) throws GestorException {

        LOG.trace("Comprobar que la especialidad no viene en blanco.");
        if (especialidad == null || especialidad.getId() == 0) {
            LOG.debug("Se ha intentado dar de baja una especialidad que venía como 'null'.");
            throw new GestorException("La especialidad estaba vacía o no era válida.");
        }

        try (Connection conex = DBManager2.getConn()) {
            int especialidad_id = especialidad.getId();

            PreparedStatement pstm = conex.prepareStatement(
                    "DELETE FROM Especialidad WHERE Es_Id = ?");

            pstm.setInt(1, especialidad_id);
            pstm.execute();

            if (pstm.getUpdateCount() == 0)
                throw new GestorException("Se ha introducido una especialidad que no existe: '" +
                        especialidad.getNombre() + "'.");

            pstm.close();

        } catch (MySQLIntegrityConstraintViolationException e) {
            LOG.debug("Se ha intentado borrar una especialidad que está asociada a un médico o una consulta.", e);
            throw new GestorException("La especialidad '" + especialidad.getNombre() + "' no puede ser borrada " +
                    "mientras siga asignada a un médico o una consulta.");

        } catch (SQLException e) {
            LOG.debug("Controlada una SQLException: (Código: {}).", e.getErrorCode(), e);
            if (e.getErrorCode()==1451)
                throw new GestorException("La especialidad '" + especialidad.getNombre() + "' no puede ser borrada " +
                        "mientras siga asignada a un médico o una consulta.");
            else
                throw new GestorException("Se ha producido un error. Seguro que la especialidad '" +
                        especialidad.getNombre() + "' es correcta?.");

        } catch (DBManager2Exception e) {
            LOG.debug("Controlada una DBManager2Exception." , e);
            throw new GestorException("Algo ha ido mal con las conexiones a la base de datos al borrar una especialidad.");
        }
    }

    public Especialidad getEspecialidadFromId (int id){

        Especialidad resultado= null;

        try (Connection conex = DBManager2.getConn()){
            String SQL = "SELECT Es_Nombre FROM Especialidad WHERE Especialidad.Es_Id = ?";

            PreparedStatement pstm = conex.prepareStatement(SQL);
            pstm.setInt(1, id);

            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                String nombre = rs.getString("Es_Nombre");

                LOG.trace("Especialidad (id, nombre): {}, {}.", id, nombre);

                resultado = new EspecialidadBuilder()
                        .setNombre(nombre)
                        .setId(id)
                        .build();
            } else {
                // TODO: No se ha encontrado la el id de la especialidad
            }

            if (rs.next()) {
                // TODO: Hay más de una especialidad con una misma id o es un SQL Injection :-?.
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DBManager2Exception e) {
            e.printStackTrace();
        }
        return resultado;
    }

    /**
     * Consulta las especialidades que practica un determinado médico.
     *
     * @param medico Médico consultado. Su campo ID debe estar cargado.
     *
     * @return Una lista de especialidades que practica dicho médico. Si no hay ninguna, estará vacía.
     * @throws GestorException En caso de que vaya algo mal se eleva esta excepción con un mensaje indicativo.
     */
    public List<Especialidad> getEspecialidadesByMedico(Medico medico) throws GestorException {

        ArrayList<Especialidad> resultado = new ArrayList<>();

        try (Connection conex = DBManager2.getConn()){
            PreparedStatement ptstm = conex.prepareStatement(
                    "SELECT Es_Id, Es_Nombre FROM (Medico_Especialidad) " +
                            "INNER JOIN Medico ON Medico.Med_Id = Medico_Especialidad.MedEsp_MedId " +
                            "INNER JOIN Especialidad ON Medico_Especialidad.MedEsp_EspId = Es_Id " +
                            "WHERE Med_Id=?"
            );

            ptstm.setInt(1, medico.getId());
            ResultSet rs = ptstm.executeQuery();

            while(rs.next()) {
                resultado.add(new EspecialidadBuilder()
                        .setId(rs.getInt("Es_Id"))
                        .setNombre(rs.getString("Es_Nombre"))
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
     * Método que nos cuenta las especialidades que hay en la base de datos.
     * @return El número de especialidades. Si hay error, devuelve -1.
     */
    public int getNumeroEspecialidades() {

        int resultado = -1;
        String SQL = "SELECT count(Es_Id) FROM Especialidad";

        try (Connection conex = DBManager2.getConn()) {
            PreparedStatement ptstm = conex.prepareStatement(SQL);

            ResultSet rs = ptstm.executeQuery();

            if (rs.next()) {
                resultado = rs.getInt(1);
            }
        } catch (SQLException e) {
            LOG.debug("Controlada una SQLException contando paciente (getNumeroEspecialidades()).", e);
        } catch (DBManager2Exception e) {
            LOG.debug("Controlada una DBManager2Exception  contando medicos (getNumeroEspecialidades()).", e);
        }

        return resultado;
    }
}
