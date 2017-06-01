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
import es.upm.etsisi.clifis.model.Consulta;
import es.upm.etsisi.clifis.model.ConsultaBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@WebServlet("/process_alta_consulta")
public class AltaConsulta extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger("es.upm.etsisi.clifis.servlets.AltaConsulta");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String dia = req.getParameter("diaSemana");
        String operacion = req.getParameter("operacion");
        int idSala = GestorFechas.getIntFromParameter("idSala", req);

        GestorMedicos gestorMedicos = new GestorMedicos();
        GestorEspecialidades gestorEspecialidades = (GestorEspecialidades) this.getServletContext().getAttribute("gestor_especialidades");
        GestorConsultas gestorConsultas = (GestorConsultas) this.getServletContext().getAttribute("gestor_consultas");
        Consulta consulta= null;
        try {
            consulta = new ConsultaBuilder()
                    .setId(idSala)
                    .setNumSala(GestorFechas.getIntFromParameter("numSala", req))
                    .setHoraInicio(this.getTimeFromParameter("horaIni", req))
                    .setHoraFin(this.getTimeFromParameter("horaFin", req))
                    .setEspecialidad(gestorEspecialidades.getEspecialidadFromId(GestorFechas.getIntFromParameter("especialidades", req)))
                    .setDuracion(GestorFechas.getIntFromParameter("duracion", req))
                    .setMedico(gestorMedicos.getMedicoById(GestorFechas.getIntFromParameter("medico", req)))
                    .setDiaSemana(dia)
                    .build();
        } catch (GestorException e) {
            e.printStackTrace();
        }

        String URLDestino = "/alta_consulta.jsp";
        LOG.trace("Recibida consulta '{}' para dar de alta.", consulta.getNumSala());
        try {
            if (operacion != null) {
                if (operacion.equals("Dar de Alta")) {
                    gestorConsultas.altaConsulta(consulta);
                } else if (operacion.equals("Modificar Consulta")) {
                    URLDestino = "/adm_consulta.jsp";
                    gestorConsultas.modificarConsulta(consulta);
                }
                req.setAttribute("consulta", consulta);
            } else {
                throw new GestorException("No se ha recibido la operación a realizar (¿alta o modificación?).");
            }
        } catch (GestorException e) {
            req.setAttribute("gestorException", e);
            LOG.debug("Capturado un GestorException.", e);
        }

        RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher(URLDestino);
        dispatcher.forward(req, resp);
    }

    /**
     * Recupera el valor de un componente de un formulario y lo pasa a tipo <i>java.sql.Time</i> para devolverlo. Si la cadena
     * obtenida del formulario es <i>null</i> o no puede ser convertida (fallo en el formato) se devuelve <i>null</i>
     * pero no se eleva ninguna excepción.
     *
     * @param parameter Nombre del parámetro (identificador usado en el atributo <i>name</i> del <i>html</i>).
     * @param req Objeto que tiene los valores del formulario.
     * @return el objeto <i>java.sql.Time</i> convertido o <i>null</i> si ha habido algún problema con la conversión.
     */
    private Time getTimeFromParameter (String parameter, HttpServletRequest req) {

        Time value = null;
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

        try {
            value = new Time(formatter.parse(req.getParameter(parameter)).getTime());
        } catch (ParseException e) {
            LOG.debug("Ha fallado la conversión del parámetro '{}' a 'java.sql.Time'. Cadena recibida: '{}'", parameter, req.getParameter(parameter), e);
        }

        return value;
    }
}
