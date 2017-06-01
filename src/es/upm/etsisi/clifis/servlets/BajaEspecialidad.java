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
import java.util.ArrayList;

@WebServlet("/process_baja_especialidad")
public class BajaEspecialidad extends HttpServlet {

    private static Logger LOG = LoggerFactory.getLogger("es.upm.etsisi.clifis.servlets.BajaEspecialidad");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        GestorEspecialidades gestorEspecialidades =
                (GestorEspecialidades) this.getServletContext().getAttribute("gestor_especialidades");

        ArrayList<Especialidad> especialidades = new ArrayList<>();
        ArrayList<GestorException> execepciones = new ArrayList<>();

        String[] especialidades_id = req.getParameterValues("especialidad_id");
        if (especialidades_id == null) {
            LOG.trace("No se ha recibido ninguna especialidad. Se añade un a con id 0 (indica vacía).");
            especialidades_id = new String[]{"0"};
        }

        for (String especialidad_id: especialidades_id) {
            try {

                // Si el id es un String o cualquier cosa que no pueda ser pasada a entero, se pone a 0.
                int id;

                try {
                    id = Integer.parseInt(especialidad_id);
                    LOG.trace("Se ha recibido la especialidad '{}' para dar de baja'.", especialidad_id);
                } catch (Exception e) {
                    LOG.debug("No se ha podido pasar el String con id a tipo int. Se fuerza a que sea 0 (indica id no válido)." , e);
                    id = 0;
                }

                Especialidad especialidad_obj = new EspecialidadBuilder()
                        .setId(id)
                        .setNombre(req.getParameter(especialidad_id))
                        .build();

                gestorEspecialidades.bajaEspecialidad(especialidad_obj);
                especialidades.add(especialidad_obj);
            } catch (GestorException e) {
                execepciones.add(e);
                LOG.debug("Capturada una GestorException.", e);
            }
        }

        if (especialidades.size() > 0)
            req.setAttribute("especialidades", especialidades);

        if (execepciones.size() > 0)
            req.setAttribute("excepciones", execepciones);

        RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/baja_especialidad.jsp");
        dispatcher.forward(req, resp);
    }
}
