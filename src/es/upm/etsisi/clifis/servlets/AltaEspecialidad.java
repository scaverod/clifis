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

import es.upm.etsisi.clifis.gestores.GestorEspecialidades;
import es.upm.etsisi.clifis.gestores.GestorException;
import es.upm.etsisi.clifis.model.Especialidad;
import es.upm.etsisi.clifis.model.EspecialidadBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controlador qpara dar de alta una especialidad.
 */
@WebServlet("/process_alta_especialidad")
public class AltaEspecialidad extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger("es.upm.etsisi.clifis.servlets.AltaEspecialidad");

    /**
     * Recoge los datos que le llegan del formulario desde el que se llama al Servlet y crea
     * un nuevo objeto {@link Especialidad} con los datos que hay que meter en la BBDD.
     *
     * Coge el {@link GestorEspecialidades} de la variable de aplicación <i>gestor_especialidades</i> e intenta
     * que de de alta la nueva especialidad.
     *
     * Si el alta va bien, pone el objeto {@link Especialidad} en la variable de petición <i>especialidad</i>.
     * En caso de que el alta no vaya bien nos encontraremos con una excepción del tipo {@link GestorException}
     * que se pondrá como variable de petición con el identificador <i>gestorException</i>.
     *
     * Acto seguido, se hace un forward a <i>alta_especialidad.jsp</i> para que pinte el resultado y, nuevamente,
     * el formulario de alta para una especialidad.
     *
     * @param req Request del Servlet.
     * @param resp Response del Servlet.
     * @throws ServletException Excepción del Servlet.
     * @throws IOException Excepción del servlet.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Especialidad especialidad = new EspecialidadBuilder()
                .setNombre(req.getParameter("nombre"))
                .build();

        LOG.trace("Recibida especialidad '{}' para dar de alta.", especialidad.getNombre());
        try {
            ((GestorEspecialidades) this.getServletContext().getAttribute("gestor_especialidades")).altaEspecialidad(especialidad);
            req.setAttribute("especialidad", especialidad);
        } catch (GestorException e) {
            req.setAttribute("gestorException", e);
            LOG.debug("Capturado un GestorException.", e);
        }

        RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/alta_especialidad.jsp");
        dispatcher.forward(req, resp);
    }
}

