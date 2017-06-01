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

import es.upm.etsisi.clifis.gestores.GestorCitas;
import es.upm.etsisi.clifis.gestores.GestorConsultas;
import es.upm.etsisi.clifis.gestores.GestorException;
import es.upm.etsisi.clifis.gestores.GestorFechas;
import es.upm.etsisi.clifis.model.Consulta;
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


@WebServlet(urlPatterns = {"/process_baja_consulta", "/process_modificar_consulta"})
public class AdmConsulta extends HttpServlet {

    private static Logger LOG = LoggerFactory.getLogger("es.upm.etsisi.clifis.servlets.AdmConsulta");
    private GestorConsultas gestorConsultas = null;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        this.gestorConsultas = (GestorConsultas) this.getServletContext().getAttribute("gestor_consultas");

        String URI = req.getRequestURI();
        LOG.trace("Petición de acción recibida en AltaCita: {}", URI);
        String URLDestino = "/adm_consulta.jsp";

        switch (URI) {
            case "/process_baja_consulta":
                this.bajaConsultas(req);
                break;
            case "/process_modificar_consulta":
                this.modificarConsulta(req);
                if (req.getAttribute("excepciones") == null)
                    URLDestino ="/alta_consulta.jsp";
                break;
        }
        RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher(URLDestino);
        dispatcher.forward(req, resp);
    }

    private void modificarConsulta (HttpServletRequest req) {

        Consulta consulta;
        try {
            consulta = this.gestorConsultas.getConsultaFromId(GestorFechas.getIntFromParameter("modificar_consulta", req));
            if (new GestorCitas().tieneCitas(consulta))
                throw new GestorException("No se puede modificar una consulta con citas asociadas.");

            req.setAttribute("consultaParaModificar", consulta);
        } catch (GestorException e) {
            LOG.debug("No se recibió el ID de la consulta a modificar o hubo algún problema.", e);
            ArrayList<GestorException> excepciones = new ArrayList<>(1);
            excepciones.add(e);
            req.setAttribute("excepciones", excepciones);
        }
    }

    private void bajaConsultas (HttpServletRequest req) {
        ArrayList<Consulta> consultas = new ArrayList<>();
        ArrayList<GestorException> execepciones = new ArrayList<>();
        String[] consultas_id = req.getParameterValues("baja_consulta");

        if (consultas_id == null || consultas_id.length == 0) {
            LOG.trace("No se ha recibido ninguna consulta. Se añade una con id 0 (indica vacía).");
            consultas_id = new String[]{"0"};
        }

        for (String consulta_id: consultas_id) {
            try {

                // Si el id es un String o cualquier cosa que no pueda ser pasada a entero, se pone a 0.
                int id;
                try {
                    id = Integer.parseInt(consulta_id);
                    LOG.trace("Se ha recibido la consulta '{}' para dar de baja.", id);
                } catch (Exception e) {
                    LOG.debug("No se ha podido pasar el String con id a tipo int. Se fuerza a que sea 0 (indica id no válido)." , e);
                    id = 0;
                }

                Consulta consulta_obj = gestorConsultas.getConsultaFromId(id);

                gestorConsultas.bajaConsulta(consulta_obj);
                consultas.add(consulta_obj);
            } catch (GestorException e) {
                execepciones.add(e);
                LOG.debug("Capturada una GestorException al dar de baja una consulta.", e);
            }
        }

        if (consultas.size() > 0)
            req.setAttribute("consultas", consultas);

        if (execepciones.size() > 0)
            req.setAttribute("excepciones", execepciones);
    }
}
