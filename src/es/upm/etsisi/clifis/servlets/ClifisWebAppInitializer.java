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
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ClifisWebAppInitializer implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext sc = servletContextEvent.getServletContext();
        sc.setAttribute("gestor_citas", new GestorCitas());
        sc.setAttribute("gestor_especialidades", new GestorEspecialidades());
        sc.setAttribute("gestor_medicos", new GestorMedicos());
        sc.setAttribute("gestor_pacientes", new GestorPacientes());
        sc.setAttribute("gestor_consultas", new GestorConsultas());
        sc.setAttribute("gestor_usuarios", new GestorUsuarios());
        sc.setAttribute("gestor_historiales", new GestorHistoriales());
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        ServletContext sc = servletContextEvent.getServletContext();
        sc.removeAttribute("gestor_citas");
        sc.removeAttribute("gestor_especialidades");
        sc.removeAttribute("gestor_medicos");
        sc.removeAttribute("gestor_pacientes");
        sc.removeAttribute("gestor_consultas");
        sc.removeAttribute("gestor_usuarios");
        sc.removeAttribute("gestor_historiales");
    }
}
