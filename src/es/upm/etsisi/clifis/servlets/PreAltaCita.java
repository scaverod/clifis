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

import es.upm.etsisi.clifis.gestores.*;
import es.upm.etsisi.clifis.model.*;
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
import java.util.List;

@WebServlet("/process_pre_alta_cita")
public class PreAltaCita extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger("es.upm.etsisi.clifis.servlets.PreAltaCita");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Buscamos el paciente
        GestorPacientes gp = (GestorPacientes) this.getServletContext().getAttribute("gestor_pacientes");

        String dni = req.getParameter("dni");
        if (dni == null || dni.isEmpty()) {
            // TODO: No se ha ingresado un DNI correcto, viene vacío.
        }

        Paciente paciente = null;
        try {
            paciente = gp.getPacienteFromDNI(dni);
        } catch (GestorException e) {
            e.printStackTrace();
        }
        req.setAttribute("paciente", paciente);

        //Buscamos la especialidad
        GestorEspecialidades ge  = (GestorEspecialidades) this.getServletContext().getAttribute("gestor_especialidades");
        int idEspecialidad = this.getIntFromParameter("especialidad",req);
        Especialidad especialidad = ge.getEspecialidadFromId(idEspecialidad);
        req.setAttribute("especialidad", especialidad);

        //Buscamos las consultas disponibles
        GestorConsultas gc = (GestorConsultas) this.getServletContext().getAttribute("gestor_consultas");
        List<Consulta> consultas = new ArrayList<>();
        try {
            consultas = gc.getConsultasFromEspecialidad(especialidad);
        } catch (GestorException e) {
            e.printStackTrace();
        }
        req.setAttribute("consultas", consultas);

        RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/alta_cita.jsp");
        dispatcher.forward(req, resp);

    }

    private int getIntFromParameter (String parameter, HttpServletRequest req) {
        int value = 0;
        try {
            value = Integer.parseInt(req.getParameter(parameter));
        } catch (NumberFormatException e) {
            LOG.debug("Ha fallado la conversión del parámetro '{}' a entero. Cadena recibida: '{}'", parameter, req.getParameter(parameter), e);
        }

        return value;
    }


}