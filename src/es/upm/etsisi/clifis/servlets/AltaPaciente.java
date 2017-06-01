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
import es.upm.etsisi.clifis.gestores.GestorPacientes;
import es.upm.etsisi.clifis.model.Paciente;
import es.upm.etsisi.clifis.model.PacienteBuilder;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/process_alta_paciente")
public class AltaPaciente extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String dni = req.getParameter("dni");
        String nombre = req.getParameter("nombre");
        String apellidos = req.getParameter("apellidos");
        String aseguradora = req.getParameter("aseguradora");
        String id = req.getParameter("id_paciente");
        GestorPacientes gestor = new GestorPacientes();
        String operacion = req.getParameter("operacion");
        int num;

        Paciente paciente = new PacienteBuilder()
                .setDni(dni)
                .setApellidos(apellidos)
                .setNombre(nombre)
                .setAseguradora(aseguradora)
                .build();
        String URLDestino = "/alta_paciente.jsp";
        String fallo="";
        try {
            if(operacion != null){
                if(operacion.equals("Alta")){
                    System.out.println("-----dando de alta al paciente:----- " +paciente.getNombre() );
                    gestor.altaPaciente(paciente);
                }else if(operacion.equals("Modificar")){
                    try {
                        num = Integer.parseInt(id);
                        paciente.setId(num);
                        URLDestino = "/modificacion_paciente.jsp";
                        req.setAttribute("id_paciente",req.getAttribute("id_paciente"));
                        System.out.println("--------modificar-------");
                        gestor.modificarPaciente(paciente);
                    }catch(SQLException e){
                        fallo = "DNI duplicado";
                    }catch (GestorException e) {
                       fallo = e.getMessage();
                    }catch(DBManager2Exception e) {
                        fallo = "Error de conexion con la Base de datos";
                    }
                    req.setAttribute("fallo",fallo);

                }
            }
        }catch (GestorException e) {
            req.setAttribute("gestorException",e);
        }
        req.setAttribute("paciente",paciente);
    //    req.setAttribute("fallo",fallo);
        RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher(URLDestino);
        dispatcher.forward(req, resp);
    }
}
