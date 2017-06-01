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

import com.google.gson.Gson;
import es.upm.etsisi.clifis.gestores.GestorException;
import es.upm.etsisi.clifis.gestores.GestorFechas;
import es.upm.etsisi.clifis.gestores.GestorHistoriales;
import es.upm.etsisi.clifis.gestores.GestorPacientes;
import es.upm.etsisi.clifis.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

@WebServlet(urlPatterns = {"/get_historial_paciente", "/process_nueva_entrada"})
public class AdmHistoriales extends HttpServlet {

    private GestorHistoriales gestorHistoriales = null;
    private static Logger LOG = LoggerFactory.getLogger("es.upm.etsisi.clifis.servlets.AdmHistoriales");
    private static final Gson GSON = new Gson();

    @Override
    public void init() throws ServletException {
        this.gestorHistoriales = (GestorHistoriales)this.getServletContext().getAttribute("gestor_historiales");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String URI = req.getRequestURI();
        LOG.trace("Petición de acción recibida en AdmHistoriales: {}", URI);
        PrintWriter out = resp.getWriter();

        switch (URI) {
            case "/get_historial_paciente":
                out.print(this.getHistorialPaciente(req));
                out.close();
                break;
            case "/process_nueva_entrada":
                System.out.println("btn: "+ req.getParameter("btn_enviar"));
                if (req.getParameter("btn_enviar").equals("Enviar")) {
                    this.process_nueva_entrada(req);
                } else if (req.getParameter("btn_enviar").equals("Modificar")) {
                    this.process_modificacion_entrada(req);
                }
                this.getServletContext().getRequestDispatcher("/adm_historiales.jsp").forward(req, resp);
                break;
        }
    }

    private void process_modificacion_entrada(HttpServletRequest req) {
        String comentario = "";
        try {
            int entradaId = GestorFechas.getIntFromParameter("modificar_entrada", req);
            Medico medico = (Medico)req.getSession().getAttribute("usuario");
            Paciente paciente = new GestorPacientes().getPacienteFromId(GestorFechas.getIntFromParameter("paciente_id", req));
            req.setAttribute("paciente", paciente);
            comentario = req.getParameter("nuevoComentario");

            String esp = req.getParameter("especialidad");

            Especialidad especialidad = new EspecialidadBuilder().setId(GestorFechas.getIntFromParameter("especialidad", req)).build();
            req.setAttribute("especialidad", especialidad);
            //if (especialidad.getId() == 0) {
            //    throw new GestorException("Dr./a. " + medico.getApellidos() + ": No ha indicado su especialidad.");
            //}

            Timestamp fecha = null;
            String nuevaFecha = req.getParameter("nuevaFecha");
            if (nuevaFecha!=null) {
                Calendar hoy = Calendar.getInstance();
                hoy.set(Calendar.HOUR_OF_DAY, 0);
                hoy.set(Calendar.MINUTE, 0);
                hoy.set(Calendar.SECOND, 0);
                hoy.set(Calendar.MILLISECOND, 0);
                fecha = new Timestamp(hoy.getTimeInMillis());
            }

            HistorialMedico historialMedico = new HistorialMedicoBuilder()
                    .setId(entradaId)
                    .setMedico(medico)
                    .setEspecialidad(especialidad)
                    .setFecha(fecha)
                    .setComentario(comentario)
                    .setPaciente(paciente)
                    .build();

            this.gestorHistoriales.modificarHistorial(historialMedico);
            req.setAttribute("entradaModificada", historialMedico);
        }catch (GestorException e) {
            LOG.debug("Error procesando modificación de entrada de historial.", e);
            req.setAttribute("excepcion", e);
            req.setAttribute("comentario", comentario);
        }
    }


    private void process_nueva_entrada(HttpServletRequest req) {
        String comentario = "";
        try {
            Medico medico = (Medico)req.getSession().getAttribute("usuario");
            Paciente paciente = new GestorPacientes().getPacienteFromId(GestorFechas.getIntFromParameter("paciente_id", req));
            req.setAttribute("paciente", paciente);
            comentario = req.getParameter("nuevoComentario");
            //req.setAttribute("comentarioNuevo", comentario);
            Especialidad especialidad = new EspecialidadBuilder().setId(GestorFechas.getIntFromParameter("especialidad", req)).build();
            req.setAttribute("especialidad", especialidad);
            if (especialidad.getId() == 0) {
                throw new GestorException("Dr./a. " + medico.getApellidos() + ": No ha indicado su especialidad.");
            }
            Calendar hoy = Calendar.getInstance();
            hoy.set(Calendar.HOUR_OF_DAY, 0);
            hoy.set(Calendar.MINUTE, 0);
            hoy.set(Calendar.SECOND, 0);
            hoy.set(Calendar.MILLISECOND, 0);
            Timestamp fecha = new Timestamp(hoy.getTimeInMillis());

            HistorialMedico historialMedico = new HistorialMedicoBuilder()
                    .setMedico(medico)
                    .setEspecialidad(especialidad)
                    .setFecha(fecha)
                    .setComentario(comentario.trim())
                    .setPaciente(paciente)
                    .build();

            this.gestorHistoriales.altaHistorial(historialMedico);
            req.setAttribute("entradaNuevaCreada", historialMedico);

        } catch (GestorException e) {
            LOG.debug("Error procesando nueva entrada de historial.", e);
            req.setAttribute("comentario", comentario);
            req.setAttribute("excepcion", e);
        }

    }

    private String getHistorialPaciente(HttpServletRequest req) {

        String resultado = "";
        int pacienteId = GestorFechas.getIntFromParameter("paciente_id", req);

        try {
            List<HistorialMedico> historialMedico = this.gestorHistoriales.getHistorial(pacienteId);
            resultado = GSON.toJson(historialMedico);
        } catch (GestorException e) {
            LOG.debug("Excepción en servlet consultando al gestor de historiales el historial de un paciente.", e);
            req.setAttribute("excepcion", e);
        }

        return resultado;
    }
}
