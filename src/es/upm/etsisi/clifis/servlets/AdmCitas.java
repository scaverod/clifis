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
import es.upm.etsisi.clifis.gestores.*;
import es.upm.etsisi.clifis.model.Cita;
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
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {"/process_fecha_citas", "/process_modificar_cita", "/process_eliminar_citas"})
public class AdmCitas extends HttpServlet {

    private GestorPacientes gestorPacientes = null;
    private GestorMedicos gestorMedicos = null;
    private GestorConsultas gestorConsultas = null;
    private GestorEspecialidades gestorEspecialidades = null;
    private GestorCitas gestorCitas = null;

    private static final Logger LOG = LoggerFactory.getLogger("es.upm.etsisi.clifis.servlets.AdmCitas");
    private static final Gson GSON = new Gson();

    @Override
    public void init() throws ServletException {
        this.gestorPacientes = (GestorPacientes) this.getServletContext().getAttribute("gestor_pacientes");
        this.gestorMedicos = (GestorMedicos) this.getServletContext().getAttribute("gestor_medicos");
        this.gestorConsultas = (GestorConsultas) this.getServletContext().getAttribute("gestor_consultas");
        this.gestorEspecialidades = (GestorEspecialidades) this.getServletContext().getAttribute("gestor_especialidades");
        this.gestorCitas = (GestorCitas) this.getServletContext().getAttribute("gestor_citas");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String URI = req.getRequestURI();
        LOG.trace("Petición de acción recibida en AdmCitas: {}", URI);

        switch (URI) {
            case "/process_fecha_citas":
                PrintWriter out = resp.getWriter();
                out.print(this.process_fecha_citas(req));
                out.close();
                break;
            case "/process_modificar_cita":
                this.process_modificar_cita(req);
                if (req.getAttribute("excepciones") != null)
                    this.getServletContext().getRequestDispatcher("/adm_citas.jsp").forward(req, resp);
                else
                    this.getServletContext().getRequestDispatcher("/alta_cita.jsp").forward(req, resp);
                break;
            case "/process_eliminar_citas":
                this.process_eliminar_citas(req);
                this.getServletContext().getRequestDispatcher("/adm_citas.jsp").forward(req, resp);
                break;
        }
    }

    private void process_modificar_cita(HttpServletRequest req) {
        Cita cita;

        try {
            int idCita = GestorFechas.getIntFromParameter("modificar_cita", req);
            if (idCita == 0) {
                LOG.debug("No venía ninguna cita para modificar: {}.", req.getParameter("modificar_cita"));
                throw new GestorException("No se recibió ninguna cita para modificar.");
            } else {
                cita = this.gestorCitas.getCitaFromId(idCita);
            }
            req.setAttribute("citaParaModificar", cita);
        } catch (GestorException e) {
            LOG.debug("No se recibió el id de la cita a modificar o hubo algún problema.", e);
            ArrayList<GestorException> execepciones = new ArrayList<>(1);
            execepciones.add(e);
            req.setAttribute("excepciones", execepciones);
        }

    }

    private void process_eliminar_citas(HttpServletRequest req) {
        // Recoger lista con citas a borrar. Si no se recibe ninguna, se crea una con id 0 para saber que no ha venido bien.
        String[] citasId = req.getParameterValues("baja_cita");
        if (citasId == null || citasId.length == 0) {
            LOG.trace("No se ha recibido ninguna cita. Se añade una con id 0 (indica vacía).");
            citasId = new String[]{"0"};
        }

        // Se tiene una lista con las citas borradas para ponerlas en el req. y que se muestren a poteriori.
        // En caso de que alguna cita no pueda ser borrada, se añade su excepción asociada en otra lista.
        ArrayList<Cita> citas = new ArrayList<>();
        ArrayList<GestorException> execepciones = new ArrayList<>();

        for (String citaId : citasId) {
            try {
                // Si el id es un String o cualquier cosa que no pueda ser pasada a entero, se pone a 0.
                int id;
                try {
                    id = Integer.parseInt(citaId);
                    LOG.trace("Se ha recibido la cita '{}' para dar de baja.", id);
                } catch (Exception e) {
                    LOG.debug("No se ha podido pasar el String con id a tipo int. Se fuerza a que sea 0 (indica id no válido).", e);
                    id = 0;
                }

                Cita cita = this.gestorCitas.getCitaFromId(id);
                this.gestorCitas.bajaCita(cita);
                citas.add(cita);
            } catch (GestorException e) {
                execepciones.add(e);
                LOG.debug("Capturada una GestorException al dar de baja una cita.", e);
            }
        }

        if (citas.size() > 0)
            req.setAttribute("citas", citas);

        if (execepciones.size() > 0)
            req.setAttribute("excepciones", execepciones);
    }

    /**
     * Consulta las citas que hay para una determinada fecha (día) y devuelve el listado
     * en formato JSON.
     *
     * @param req Debe contener un parámetro <i>fecha_citas</i> de un formulario que contenga
     *           la fecha en formato <i>dd/MM/yyyy</i>.
     * @returnJSON con la información de las citas resultantes.
     */
    private String process_fecha_citas(HttpServletRequest req) {
        String resultado = null;
        List<Cita> citas = null;

        LOG.debug("No se pudo convertir el la fecha recibida ({})", req.getParameter("fecha_citas"));
        try {
            Timestamp fecha = GestorFechas.getTimestampDateFromString(req.getParameter("fecha_citas"));
            LOG.trace("Fecha: {} ", fecha);
            citas = this.gestorCitas.getCitasFromFecha(fecha);
        } catch (GestorException e) {
            LOG.debug("No se pudo convertir el la fecha recibida ({})", req.getParameter("fecha_citas"));
        }

        resultado = GSON.toJson(citas);
        LOG.trace("Resultado (/process_fecha): {}", resultado);

        return resultado;
    }
}
