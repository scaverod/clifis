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
import es.upm.etsisi.clifis.gestores.GestorFechas;
import es.upm.etsisi.clifis.gestores.GestorMedicos;
import es.upm.etsisi.clifis.model.Medico;
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

@WebServlet(urlPatterns = { "/process_baja_medico", "/process_modificar_medico"})

public class AdmMedico extends HttpServlet{
    private GestorMedicos gestorMedicos = null;
    private static Logger LOG = LoggerFactory.getLogger("es.upm.etsisi.clifis.servlets.modificacionMedico");
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        this.gestorMedicos = (GestorMedicos) this.getServletContext().getAttribute("gestor_medicos");
        String URI = req.getRequestURI();
        String URLDestino = "/adm_medicos.jsp";
        switch (URI) {
            case "/process_baja_medico":
                this.bajaMedicos(req);
                break;
            case "/process_modificar_medico":
                this.modificarMedico(req);
                if (req.getAttribute("excepciones") == null)
                    URLDestino ="/alta_medico.jsp";
                break;
        }
        RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher(URLDestino);
        dispatcher.forward(req, resp);
    }

    private void bajaMedicos(HttpServletRequest req) {
        ArrayList<Medico> medicos = new ArrayList<>();
        ArrayList<GestorException> execepciones = new ArrayList<>();
        String[] medicos_id = req.getParameterValues("baja_medico");

        if (medicos_id == null || medicos_id.length == 0) {
            LOG.trace("No se ha recibido ningun medico. Se añade una con id 0 (indica vacía).");
            medicos_id = new String[]{"0"};
        }

        for (String medico_id: medicos_id) {
            try {

                // Si el id es un String o cualquier cosa que no pueda ser pasada a entero, se pone a 0.
                int id;
                try {
                    id = Integer.parseInt(medico_id);
                    LOG.trace("Se ha recibido el medico '{}' para dar de baja.", id);
                } catch (Exception e) {
                    LOG.debug("No se ha podido pasar el String con id a tipo int. Se fuerza a que sea 0 (indica id no válido)." , e);
                    id = 0;
                }

                Medico medico_obj = this.gestorMedicos.getMedicoById(id);

                /*Medico medico_obj = new MedicoBuilder()
                        .setId(id)
                        .setNombre(req.getParameter(medico_id))
                        .build();*/

                gestorMedicos.bajaMedico(medico_obj);
                medicos.add(medico_obj);
            } catch (GestorException e) {
                execepciones.add(e);
                LOG.debug("Capturada una GestorException al dar de baja una medico.", e);
            }
        }

        if (medicos.size() > 0)
            req.setAttribute("medicos", medicos);

        if (execepciones.size() > 0)
            req.setAttribute("excepciones", execepciones);
    }


    private void modificarMedico(HttpServletRequest req){
        Medico medico;
        try{
            medico = this.gestorMedicos.getMedicoById(GestorFechas.getIntFromParameter("modificar_medico",req));
            req.setAttribute("medicoParaModificar",medico);
        } catch (GestorException e) {
            LOG.debug("No se recibió el ID del medico a modificar o hubo algún problema.", e);
            ArrayList<GestorException> excepciones = new ArrayList<>(1);
            excepciones.add(e);
            req.setAttribute("excepciones", excepciones);
        }



    }

}