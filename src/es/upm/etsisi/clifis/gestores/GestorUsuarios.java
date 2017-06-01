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

import es.upm.etsisi.clifis.model.Gestor;
import es.upm.etsisi.clifis.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GestorUsuarios {

    Logger LOG = LoggerFactory.getLogger("es.upm.etsisi.clifis.gestores.GestorUsuarios");
    private GestorMedicos gestorMedicos;

    public GestorUsuarios(){
        this.gestorMedicos = new GestorMedicos();
    }

    /**
     * Metodo que comprueba si la contraseña introducida corresponde con el usuario que
     * pretende acceder a la aplicación.
     *
     * @param pwd Contiene un String con la contraseña introducida.
     * @return Devuelve un usuario si la contraseña introducida se corresponde a la que hay en la BBDD, null en otro caso.
     * @throws GestorException En el caso de que haya algun problema obteniendo el medico de la BBDD.
     */
    public Usuario checkPassword (int idUsuario, String pwd) throws GestorException {
        Usuario resultado = null;

        if (pwd == null || pwd.length() != 64) {
            throw new GestorException("El password no tiene el formato correcto (vacío o longitud distinta de 64 bytes).");
        }

        try (Connection conex = DBManager2.getConn()) {
            PreparedStatement pstm = null;
            if (idUsuario == 0) {
                pstm = conex.prepareStatement("SELECT Ges_Password FROM Gestor");
            } else {
                pstm = conex.prepareStatement("SELECT Med_Password FROM Medico WHERE Med_Id = ?");
                pstm.setInt(1, idUsuario );
            }

            ResultSet rs = pstm.executeQuery();

            if (rs.next()) {
                if (pwd.equals(rs.getString(1)))
                    if (idUsuario == 0)
                        resultado = new Gestor("Gestor");
                    else
                        resultado = this.gestorMedicos.getMedicoById(idUsuario);
            } else {
                throw new GestorException("No se ha recibido el password de la base de datos.");
            }

        } catch (SQLException e) {
            LOG.debug("Controlada una SQLException." , e);
            throw new GestorException("Algo ha ido mal con la base de datos al traer la contraseña de un médico.");
        } catch (DBManager2Exception e) {
            LOG.debug("Controlada una DBManager2Exception." , e);
            throw new GestorException("Algo ha ido mal con las conexiones al traer la contraseña de un médico.");
        }
        return resultado;
    }

    /**
     * Método que actualzia la contraseña de un usuario. El usuario se selecciona mediante su id, y directamente se pasa
     * la contraseña con el resumen SHA-256, es decir, no viaja en claro.
     * @param idUsuario Identificador del medico en la base de datos del que se desea cambiar su contraseña.
     * @param pwd Nueva contraseña ya cifrada mediante el resumen SHA-256. Pasarle por parámetro DigestUtils.sha256Hex(password)
     * @throws GestorException En el caso de que algo vaya mal.
     */
    public void updatePassword (int idUsuario, String pwd) throws GestorException {
        LOG.trace("Usuario que va a cambiar la contraseña de id: {}", idUsuario);

        try (Connection conex = DBManager2.getConn()) {
            PreparedStatement pstm;

            if (idUsuario == 0) {
                pstm = conex.prepareStatement(
                        "UPDATE Gestor SET Ges_Password = ?");
            } else {
                pstm = conex.prepareStatement(
                        "UPDATE Medico SET Med_Password = ? WHERE Med_Id = ?");
                pstm.setInt(2, idUsuario);
            }


            pstm.setString(1, pwd);
            pstm.executeUpdate();

        } catch (SQLException e) {
            LOG.debug("Capturada SQLException.", e);
            throw new GestorException("Capturada SQLException al actualizar la contraseña.");
        } catch (DBManager2Exception e) {
            LOG.debug("Controlada una DBManager2Exception." , e);
            throw new GestorException("Capturada DBManager2Exception al actualizar la contraseña.");
        }

    }
}
