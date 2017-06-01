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
import es.upm.etsisi.clifis.gestores.GestorPacientes;
import es.upm.etsisi.clifis.model.Paciente;
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

@WebServlet(urlPatterns = "/process_modificar_paciente")
public class ModificacionPaciente extends HttpServlet {
    private GestorPacientes gestorPacientes = null;
    private static Logger LOG = LoggerFactory.getLogger("es.upm.etsisi.clifis.servlets.modificacionPaciente");
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String URLDestino = "/modificacion_paciente.jsp";
        this.gestorPacientes = (GestorPacientes)this.getServletContext().getAttribute("gestor_pacientes");
        this.modificarPaciente(req);
        if (req.getAttribute("excepciones") == null)
            URLDestino ="/alta_paciente.jsp";
        RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher(URLDestino);
        dispatcher.forward(req, resp);
    }

    private void modificarPaciente(HttpServletRequest req){
        Paciente paciente;
        try{
            paciente = this.gestorPacientes.getPacienteFromId(GestorFechas.getIntFromParameter("modificar_paciente",req));
            req.setAttribute("pacienteParaModificar",paciente);

        } catch (GestorException e) {
            LOG.debug("No se recibió el ID del paciente a modificar o hubo algún problema.", e);
            ArrayList<GestorException> excepciones = new ArrayList<>(1);
            excepciones.add(e);
            req.setAttribute("exception", excepciones);
        }
        //  req.setAttribute("pacienteParaModificar",paciente);



    }
}



