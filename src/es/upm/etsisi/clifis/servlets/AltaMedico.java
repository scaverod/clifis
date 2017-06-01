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
import es.upm.etsisi.clifis.model.Especialidad;
import es.upm.etsisi.clifis.model.Medico;
import es.upm.etsisi.clifis.model.MedicoBuilder;
import org.apache.commons.codec.digest.DigestUtils;
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

@WebServlet("/process_alta_medico")
public class AltaMedico extends HttpServlet{

    Logger LOG = LoggerFactory.getLogger("es.upm.etsisi.clifis.gestores.AltaMedico");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int numCol = this.getIntFromParameter("numCol", req);
        String nombre = req.getParameter("nombre");
        String apellidos = req.getParameter("apellidos");
        String password;
        if (req.getParameter("password") == null || req.getParameter("password").isEmpty())
            password = "";
        else
            password = DigestUtils.sha256Hex(req.getParameter("password"));

        ArrayList<Especialidad> especialidades = new ArrayList<>();
        String operacion = req.getParameter("operacion");

        GestorEspecialidades ge = (GestorEspecialidades) this.getServletContext().getAttribute("gestor_especialidades");
        String[] idEspecialidades = req.getParameterValues("especialidad");

        if (idEspecialidades == null) {
            LOG.debug("No ha llegado ninguna especilidad al servlet que los tiene que dar de alta al mÃ©dico.");
            idEspecialidades = new String[]{"0"};
        }

        for(String especialidad: idEspecialidades) {
            especialidades.add(ge.getEspecialidadFromId(Integer.parseInt(especialidad)));
        }

        Medico medico = new MedicoBuilder()
                .setNumCol(numCol)
                .setApellidos(apellidos)
                .setNombre(nombre)
                .setPassword(password)
                .setEspecialidades(especialidades)
                .build();

        GestorMedicos gm = (GestorMedicos)this.getServletContext().getAttribute("gestor_medicos");
        String URLDestino= "/alta_medico.jsp";
        try {
                if(operacion != null){
                    if(operacion.equals("Alta")){
                        gm.altaMedico(medico);
                        req.setAttribute("medico", medico);
                    }else if(operacion.equals("Modificar")){
                        try {
                            URLDestino = "/adm_medicos.jsp";
                            medico.setId(GestorFechas.getIntFromParameter("med_id", req));
                            gm.modificarMedico(medico);
                            req.setAttribute("medico", medico);
                        } catch (GestorException e) {
                            ArrayList<GestorException> exceptions = new ArrayList<>(1);
                            exceptions.add(e);
                            req.setAttribute("excepciones", exceptions);
                        }
                    }
                }
        } catch (GestorException e) {
            req.setAttribute("excepcion", e);
        }

        RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher(URLDestino);
        dispatcher.forward(req, resp);
    }

    /**
     * Recupera el valor de un componente de un formulario y lo pasa a tipo <i>int</i> para devolverlo. Si la cadena
     * obtenida del formulario es <i>null</i> o no puede ser convertida (fallo en el formato) se devuelve el valor 0
     * pero no se eleva ninguna excepciÃ³n.
     *
     * @param parameter Nombre del parÃ¡metro (identificador usado en el atributo <i>name</i> del <i>html</i>).
     * @param req Objeto que tiene los valores del formulario.
     * @return el entero convertido o 0 si ha habido algÃºn problema con la conversiÃ³n.
     */
    private int getIntFromParameter (String parameter, HttpServletRequest req) {
        int value = 0;
        try {
            value = Integer.parseInt(req.getParameter(parameter));
        } catch (NumberFormatException e) {
            LOG.debug("Ha fallado la conversiÃ³n del parÃ¡metro '{}' a entero. Cadena recibida: '{}'", parameter, req.getParameter(parameter), e);
        }

        return value;
    }
}