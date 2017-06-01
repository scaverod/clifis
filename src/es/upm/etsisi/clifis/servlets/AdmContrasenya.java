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

package es.upm.etsisi.clifis.servlets;

import es.upm.etsisi.clifis.gestores.GestorException;
import es.upm.etsisi.clifis.gestores.GestorUsuarios;
import es.upm.etsisi.clifis.model.Usuario;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(urlPatterns = {"/process_change_pwd"})
public class AdmContrasenya extends HttpServlet {

    private static Logger LOG = LoggerFactory.getLogger("es.upm.etsisi.clifis.servlets.AdmContraseña");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        GestorUsuarios gestorUsuarios = (GestorUsuarios) this.getServletContext().getAttribute("gestor_usuarios");
        HttpSession session = req.getSession();

        // Parece razonable que si rescato un ususario, el nombre de la variable sea usuario.
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        LOG.trace("Origen 1: {}", req.getParameter("origen"));
        String origen = ((req.getParameter("origen") == null ) ? "A" : req.getParameter("origen"));
        LOG.trace("Origen 2: {}", origen);


        if(origen.equals("A")){
            LOG.trace("Medico que va a cambiar la contraseña: {} ({})", usuario.getNombre(), usuario.getId());
            String password = req.getParameter("password");
            LOG.trace("Contraseña introducida: {}", password);
            if (password != null && !password.isEmpty()) {
                try {
                    if ((gestorUsuarios.checkPassword(usuario.getId(), DigestUtils.sha256Hex(password))) != null) {
                        req.setAttribute("origen", "B");
                        LOG.trace("Contraseña introducida correcta");
                    } else {
                        req.setAttribute("origen", "A");
                        LOG.trace("Contraseña introducida no coindice");
                    }
                } catch (GestorException e) {
                    LOG.debug("Algo fue mal cuando se comprobaba si la contraseña del medico es correcta.", e);
                }
            }

       } else if (origen.equals("B")) {
           String passwordN1 = req.getParameter("passwordN1");
           LOG.trace("Contraseña 1: {}", passwordN1);
           String passwordN2 = req.getParameter("passwordN2");
           LOG.trace("Contraseña 2: {}", passwordN2);
           if (passwordN1 ==  null || passwordN1.equals("") ||
                   passwordN2 == null || passwordN2.equals("") ||
                   !passwordN1.equals(passwordN2)){
               req.setAttribute("error", true);
               req.setAttribute("origen", "B");
           }else {
               req.setAttribute("error", false);
               try {
                   gestorUsuarios.updatePassword(usuario.getId(), DigestUtils.sha256Hex(passwordN1));
                   LOG.trace("Contraseña Actualziada");
                   req.setAttribute("origen", "C");
               } catch (GestorException e) {
                   LOG.debug("Algo fue mal cuando se actualizaba  la contraseña del medico.", e);
               }
           }
       }

        req.getRequestDispatcher("adm_contrasenya.jsp").forward(req,resp);
    }
}
