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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@WebServlet(urlPatterns = {"/checkDNI", "/loadMedicos", "/loadEspecialidades", "/checkConsultas", "/loadCitas", "/deshacer_cita", "/process_alta_cita2"})
public class AltaCita extends HttpServlet {

    private GestorPacientes gestorPacientes = null;
    private GestorMedicos gestorMedicos = null;
    private GestorEspecialidades gestorEspecialidades = null;
    private GestorConsultas gestorConsultas = null;
    private GestorCitas gestorCitas = null;

    private static final Logger LOG = LoggerFactory.getLogger("es.upm.etsisi.clifis.servlets.AltaCita");
    private static final Gson GSON = new Gson();

    @Override
    public void init() throws ServletException {
        this.gestorPacientes = (GestorPacientes) this.getServletContext().getAttribute("gestor_pacientes");
        this.gestorMedicos = (GestorMedicos) this.getServletContext().getAttribute("gestor_medicos");
        this.gestorEspecialidades = (GestorEspecialidades) this.getServletContext().getAttribute("gestor_especialidades");
        this.gestorConsultas = (GestorConsultas) this.getServletContext().getAttribute("gestor_consultas");
        this.gestorCitas = (GestorCitas) this.getServletContext().getAttribute("gestor_citas");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String URI = req.getRequestURI();
        LOG.trace("Petición de acción recibida en AltaCita: {}", URI);
        String respuesta = null;
        RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/alta_cita.jsp");

        switch (URI) {
            case "/checkDNI":
                respuesta = this.checkDNI(req);
                break;
            case "/loadMedicos":
                respuesta = this.loadMedicos(req);
                break;
            case "/loadEspecialidades":
                respuesta = this.loadEspecialidades(req);
                break;
            case "/checkConsultas":
                respuesta = this.checkConsultas(req);
                break;
            case "/loadCitas":
                respuesta = this.loadCitas(req);
                break;
            case "/deshacer_cita":
                respuesta = this.deshacerCita(req);
                dispatcher.forward(req, resp);
                break;
            case "/process_alta_cita2":
                try {
                    respuesta = this.process_alta_cita2(req);
                } catch (GestorException e) {
                    req.setAttribute("gestorException", e);
                }
                dispatcher.forward(req, resp);
                break;
        }

        PrintWriter out = resp.getWriter();
        out.print(respuesta);
        out.close();
    }

    private String deshacerCita(HttpServletRequest req) {
        String resultado = req.getParameter("idCita");
        int idCita = GestorFechas.getIntFromParameter("idCita", req);
        if(idCita == 0) {
            LOG.debug("El id de cita no venía bien: {}.", req.getParameter("idCita"));
            req.setAttribute("gestorException", new GestorException("La cita no se puede deshacer."));
        } else {
            try {
                Cita cita = this.gestorCitas.getCitaFromId(idCita);
                this.gestorCitas.bajaCita(cita);
                req.setAttribute("cita_baja", cita);
                resultado = "" + cita.getId();
            } catch (GestorException e) {
                LOG.debug("No se ha podido deshacer la cita.", e);
                req.setAttribute("gestorException", e);
            }
        }

        return resultado;
    }


    private String process_alta_cita2 (HttpServletRequest req) throws GestorException {
        String resultado = "";
        int idConsulta;

        // Se recogen los datos del formulario.
        try {
            Paciente paciente = this.gestorPacientes.getPacienteFromDNI(req.getParameter("dni"));
            String[] aux = req.getParameter("horaCita").split(",");
            idConsulta = Integer.parseInt(aux[0]);
            Consulta consulta = this.gestorConsultas.getConsultaFromId(idConsulta);
            Medico medico = consulta.getMedico();
            Especialidad especialidad = consulta.getEspecialidad();

            Timestamp fecha = GestorFechas.getTimestampFromString(aux[1]);
            resultado += fecha + "\n";

            Cita cita = new CitaBuilder()
                    .setPaciente(paciente)
                    .setConsulta(consulta)
                    .setMedico(medico)
                    .setEspecialidad(especialidad)
                    .setFecha(fecha)
                    .build();

            this.gestorCitas.altaCita(cita);
            req.setAttribute("cita", cita);

            int idCitaMod = GestorFechas.getIntFromParameter("idCitaMod", req);
            if(idCitaMod != 0) {
                Cita citaMod = this.gestorCitas.getCitaFromId(idCitaMod);
                this.gestorCitas.bajaCita(citaMod);
                req.setAttribute("cita_modificada", citaMod);
            }

        } catch (NumberFormatException e){
            LOG.debug("El identificador de la consulta no venía bien para dar de alta una cita: {}", req.getParameter("horaCita").split(",")[0]);
            throw new GestorException("No se ha podido convertir la hora de la cita.");
        }

        return resultado;
    }

    /**
     * Comprueba las citas que hay disponibles para las consultas de un día dado según médico y/o especialidad.
     * Forma un JSON como respuesta con dicha información.
     *
     * El formato del JSON es:
     *      {
     *          "citasConsulta" :
     *              [
     *                  {
     *                      "txtConsulta" : "Texto que describe la consulta",
     *                      "idConsulta" : id,
     *                      "horas" : HH:mm
     *                 }
     *              ]
     *      }
     * @param req Los id de especialiadd, medico, fecha y consultas se recogen de aquí.
     * @return Un <i>String</i> JSON con la respuesta.
     */
    private String loadCitas (HttpServletRequest req) {
        String resultado;

        Especialidad especialidad = new EspecialidadBuilder().setId(GestorFechas.getIntFromParameter("especialidad_id", req)).build();
        Medico medico = new MedicoBuilder().setId(GestorFechas.getIntFromParameter("medico_id", req)).build();
        SimpleDateFormat sdfHoras = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdfFechas = new SimpleDateFormat("dd/MM/yyyy");
        List<Cita> citas;
        JsonObject resultadoJSON = new JsonObject();
        JsonArray citasJSON = new JsonArray();

        try {
            Timestamp fecha = GestorFechas.getTimestampDateFromString(req.getParameter("fecha"));
            for(String consulta_id: req.getParameterValues("consultas_id[]")) {

                List<Consulta> consultas = new ArrayList<>();
                Consulta consulta = this.gestorConsultas.getConsultaFromId(Integer.parseInt(consulta_id));
                Calendar calAux = Calendar.getInstance();
                calAux.setTimeInMillis(fecha.getTime());

                if (GestorFechas.getCalendarInt(consulta.getDiaSemana()) == calAux.get(Calendar.DAY_OF_WEEK)) {
                    consultas.add(consulta);

                    JsonObject citaJSON = new JsonObject();
                    JsonArray horasJSON = new JsonArray();

                    citas = this.gestorCitas.getCitasLibres(fecha, medico, especialidad, consultas);
                    String txtConsulta = "Consulta: " + consulta.getDiaSemana() + " (" + sdfFechas.format(fecha) + ")"
                            + " de " + sdfHoras.format(consulta.getHoraInicio()) + " a " + sdfHoras.format(consulta.getHoraFin())
                            + " (" + consulta.getMedico().getNombre() + ", " + consulta.getEspecialidad().getNombre() + ")";

                    citaJSON.addProperty("txtConsulta", txtConsulta);

                    citaJSON.addProperty("idConsulta", consulta_id);

                    for (Cita cita : citas) {
                        horasJSON.add(sdfHoras.format(cita.getFecha()));
                    }
                    citaJSON.add("horas", horasJSON);
                    citasJSON.add(citaJSON);
                }
            }
        } catch (GestorException e) {
            // TODO: Gestionar esta excepción (redireccionar a alguna web de error, por ejemplo). Corregir el texto!!!!!!
            LOG.debug("Se ha enviado una fecha no válida desde AltaCita/loadCitas: {}", req.getParameter("fecha"));
        }
        resultadoJSON.add("citasConsulta", citasJSON);
        resultado = GSON.toJson(resultadoJSON);
        return resultado;
    }

        /**
         * Comprueba las consultas que hay para un médico, una especialidad o ambas cosas y genera un
         * <i>String</i> con un JSON que tiene el siguiente formato:
         * {
         *     "idConsultas" : [id, id, ...],
         *     "dias" : [d, d, ...],
         *     "horas" : ["día de HH a HH (Nombre médico, especialidad)", "día de HH...", ...]
         * }
         * @param req Los id del medico y/o de la especialidad.
         * @return Un <i>String</i> JSON con la respuesta.
         */
    private String checkConsultas (HttpServletRequest req) {
        String resultado = null;

        // Coge la id de la especialidad. Si no viene como parámetro, pone un 0.
        int esp = GestorFechas.getIntFromParameter("especialidad_id", req);
        // Coge el id del médico. Si no viene como parámetro, pone un 0.
        int med = GestorFechas.getIntFromParameter("medico_id", req);

        Especialidad especialidad;
        Medico medico;
        List<Consulta> consultas = null;
        try {
            if(esp > 0 && med == 0) {
                especialidad = new EspecialidadBuilder().setId(esp).build();
                consultas = this.gestorConsultas.getConsultasFromEspecialidad(especialidad);
            } else if (esp == 0 && med > 0) {
                medico = new MedicoBuilder().setId(med).build();
                consultas = this.gestorConsultas.getConsultasFromMedico(medico);
            } else if (esp > 0 && med > 0) {
                especialidad = new EspecialidadBuilder().setId(esp).build();
                medico = new MedicoBuilder().setId(med).build();
                consultas = this.gestorConsultas.getConsultasFromMedicoANDEspecialidad(medico, especialidad);
            }

            // Se crea un objeto JSON para poderlo rellenar con la información.
            JsonObject resultadoJSON = new JsonObject();
            JsonArray diasJSON = new JsonArray();
            JsonArray horasJSON = new JsonArray();
            JsonArray consultasJSON = new JsonArray();

            for(Consulta consulta: consultas) {
                // Añade el día de la consulta al array 'dias' del JSON para ser leído en javascript (0 = Domingo y 6 = Sábado).
                diasJSON.add(GestorFechas.getCalendarInt(consulta.getDiaSemana()) - 1);
                // Añade el texto con la información de la cita al array 'horas' del JSON.
                horasJSON.add(consulta.getDiaSemana() + " de " +
                        consulta.getHoraInicio().toString().substring(0, 2) + " h." + " a " +
                        consulta.getHoraFin().toString().substring(0, 2) + "h." +
                        "(" + consulta.getMedico().getNombre() + ", " +
                        consulta.getEspecialidad().getNombre() + ").");
                consultasJSON.add(consulta.getId());

            }

            // Añade los arrays 'idConsultas', 'dias' y 'horas' al objeto JSON.
            resultadoJSON.add("idConsultas", consultasJSON);
            resultadoJSON.add("dias", diasJSON);
            resultadoJSON.add("horas", horasJSON);

            // Escupe el String al resultado con formato JSON.
            resultado = GSON.toJson(resultadoJSON);

            } catch (GestorException e) {
                LOG.debug("Problema al pedir las consultas con especialidad.", e);
            }

        return resultado;
    }

    /**
     * Coge de la BBDD las especialidades de un médico y
     * devuelve un array de objetos Especialidad como string JSON.
     *
     * Si el médico está vacío (su id es 0) devuelve todas las especialidades.
     *
     * @param req Para pedir el médico a la petición.
     * @return String JSON con las especialidades de un médico o todas las especialidades.
     */
    private String loadEspecialidades(HttpServletRequest req) {
        String resultado = null;
        List<Especialidad> especialidades = null;
        int medicoChanged = GestorFechas.getIntFromParameter("medico_id", req);

        if (medicoChanged == 0) {
            LOG.debug("El médico viene vacío al cargar la lista de especialidades. Se cargan todas las especialidades.");
            try {
                especialidades = this.gestorEspecialidades.getEspecialidades();
            } catch (GestorException e) {
                LOG.debug("Problema al pedir todas las especialidades.", e);
            }
        } else {
            LOG.trace("Recibida el médico: {}", medicoChanged);
            Medico medico = new MedicoBuilder()
                    .setId(medicoChanged)
                    .build();
            try {
                especialidades = this.gestorEspecialidades.getEspecialidadesByMedico(medico);
            } catch (GestorException e) {
                LOG.debug("Problema al pedir los médicos de una especialidad.", e);
            }
        }
        resultado = GSON.toJson(especialidades);
        LOG.trace("Resultado (/loadMedicos): {}", resultado);

        return resultado;
    }

    /**
     * Coge de la BBDD los médicos de una especialidad y
     * devuelve un array de objetos Médico como string JSON.
     *
     * Si la especialidad está vacía (su id es 0) devuelve todos los médicos.
     *
     * @param req Para pedir la especialidad a la petición.
     * @return String JSON con los médicos de una especialidad o todos los médicos.
     */
    private String loadMedicos(HttpServletRequest req) {
        String resultado = null;
        List<Medico> medicos = null;

        int especialidadChanged = GestorFechas.getIntFromParameter("especialidad_id", req);
        if (especialidadChanged == 0) {
            LOG.debug("La especialidad viene vacía al cargar la lista de médicos. Se cargan todos los médicos.");
            try {
                medicos = this.gestorMedicos.getMedicos();
            } catch (GestorException e) {
                LOG.debug("Problema al pedir todos los médicos.", e);
            }
        } else {
            LOG.trace("Recibida la especialidad: {}", especialidadChanged);
            Especialidad especialidad = new EspecialidadBuilder()
                    .setId(especialidadChanged)
                    .build();
            try {
                medicos = this.gestorMedicos.getMedicosByEspecialidad(especialidad);
            } catch (GestorException e) {
                LOG.debug("Problema al pedir los médicos de una especialidad.", e);
            }
        }
        resultado = GSON.toJson(medicos);
        LOG.trace("Resultado (/loadMedicos): {}", resultado);

        return resultado;
    }

    /**
     * Si existe en la BBDD un paciente con el dni indicado,
     * devuelve un objeto paciente como string JSON.
     *
     * @param req Para pedir el dni a la petición
     * @return String JSON con un paciente o null si no existe ningún paciente con dicho dni.
     */
    private String checkDNI(HttpServletRequest req) {
        String resultado = null;
        String dniChanged = req.getParameter("dniChanged");
        if(dniChanged == null || dniChanged.isEmpty()) {
            LOG.debug("El dni viene vacío: {}", dniChanged);
        } else {
            LOG.trace("Recibido el DNI: {}", dniChanged);
            try {
                Paciente paciente = this.gestorPacientes.getPacienteFromDNI(dniChanged);
                resultado = GSON.toJson(paciente);
            } catch (GestorException e) {
                LOG.debug("Problema al pedir los datos de un paciente por DNI.", e);
            }

            LOG.trace("Resultado (/checkDNI): {}", resultado);
        }
        return resultado;
    }
}