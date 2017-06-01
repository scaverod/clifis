<%--
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
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<c:if test="${sessionScope.usuario != null}">
    <div class="container" style="font-family: 'Raleway', sans-serif;" id="menu">
    <%-- <nav class="navbar navbar-default nav-justified"> --%>
    <nav class="navbar navbar-default">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#myNavbar">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">Menú:</a>
        </div>
        <div class="collapse navbar-collapse" id="myNavbar">
            <ul class="nav navbar-nav">

                <%-- Opciones para CITAS --%>
                <li class="dropdown">
                    <a class="dropdown-toggle" data-toggle="dropdown" href="#">Citas<span class="caret"></span></a>
                    <ul class="dropdown-menu">
                        <c:if test="${sessionScope.usuario.id == 0}">
                            <li><a href="../alta_cita.jsp">Nueva Cita</a></li>
                            <li><a href="../adm_citas.jsp">Administrar Citas</a></li>
                        </c:if>
                        <c:if test="${sessionScope.usuario.id != 0}">
                            <li><a href="../adm_citas.jsp">Listar Citas</a></li>
                        </c:if>
                    </ul>
                </li>

                <%-- Opciones para CONSULTAS --%>
                <li class="dropdown">
                    <a class="dropdown-toggle" data-toggle="dropdown" href="#">Consultas<span class="caret"></span></a>
                    <ul class="dropdown-menu">
                        <c:if test="${sessionScope.usuario.id == 0}">
                            <li><a href="../alta_consulta.jsp">Alta de Consulta</a></li>
                            <li><a href="../adm_consulta.jsp">Administración de Consultas</a></li>
                        </c:if>
                        <c:if test="${sessionScope.usuario.id != 0}">
                            <li><a href="../adm_consulta.jsp">Listado de Consultas</a></li>
                        </c:if>
                    </ul>
                </li>

                <%-- Opciones para HISTORIALES --%>
                <li class="dropdown">
                    <a class="dropdown-toggle" data-toggle="dropdown" href="#">Historiales<span class="caret"></span></a>
                    <ul class="dropdown-menu">
                        <c:if test="${sessionScope.usuario.id != 0}">
                            <li><a href="../adm_historiales.jsp">Administrar Historiales</a></li>
                        </c:if>
                        <c:if test="${sessionScope.usuario.id == 0}">
                            <li><a href="../adm_historiales.jsp">Ver Historiales</a></li>
                        </c:if>
                    </ul>
                </li>

                <%-- Opciones para PACIENTES --%>
                <li class="dropdown">
                    <a class="dropdown-toggle" data-toggle="dropdown" href="#">Pacientes<span class="caret"></span></a>
                    <ul class="dropdown-menu">
                        <li><a href="../alta_paciente.jsp">Dar de alta</a></li>
                        <li><a href="../listado_paciente.jsp">Listar Pacientes</a></li>
                        <li><a href="../modificacion_paciente.jsp">Modificación de Pacientes</a></li>
                    </ul>
                </li>

                <%-- Opciones para MÉDICOS --%>
                <li class="dropdown">
                    <a class="dropdown-toggle" data-toggle="dropdown" href="#">Médicos<span class="caret"></span></a>
                    <ul class="dropdown-menu">
                        <c:if test="${sessionScope.usuario.id == 0}">
                            <li><a href="../alta_medico.jsp">Dar de alta</a></li>
                        </c:if>
                            <li><a href="../listado_medicos.jsp">Listar</a></li>
                        <c:if test="${sessionScope.usuario.id == 0}">
                            <li><a href="../adm_medicos.jsp">Administración de médicos</a></li>
                        </c:if>
                    </ul>
                </li>

                <%-- Opciones para ESPECIALIDADES --%>
                <li class="dropdown">
                    <a class="dropdown-toggle" data-toggle="dropdown" href="#">Especialidades<span class="caret"></span></a>
                    <ul class="dropdown-menu">
                        <c:if test="${sessionScope.usuario.id == 0}">
                            <li><a href="../alta_especialidad.jsp">Alta de Especialidad</a></li>
                            <li><a href="../baja_especialidad.jsp">Baja de Especialidad</a></li>
                        </c:if>
                        <li><a href="../listado_especialidad.jsp">Listar todas las Especialidades</a></li>
                    </ul>
                </li>
            </ul>

            <%-- Opciones para USUARIO --%>
            <ul class="nav navbar-nav navbar-right">
                <li class="dropdown">
                    <a class="dropdown-toggle" data-toggle="dropdown" href="#">Usuario: ${sessionScope.usuario.nombre}<span class="caret"></span></a>
                    <ul class="dropdown-menu">
                        <li><a href="../adm_contrasenya.jsp">Cambiar Contraseña</a></li>
                        <form action="/process_logout" method="post" id="form_logout">
                            <input type="hidden" name="Para" value="que el form no esté vacío">
                        </form>
                            <li><a href="#" onclick="$('#form_logout').submit()">Desconectar</a></li>
                    </ul>
                </li>
            </ul>
        </div>
    </nav>
</div>
</c:if>

<c:if test="${sessionScope.usuario == null}">
    <div class="container" style="font-family: 'Raleway', sans-serif;" id="menu">
        <nav class="navbar navbar-default nav-justified">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#myNavbar">
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="#">Esperando a que entre un usuario para mostrar el menú de opciones...</a>
            </div>
        </nav>
    </div>
</c:if>