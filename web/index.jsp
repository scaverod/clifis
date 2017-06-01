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

<%@ page import="es.upm.etsisi.clifis.gestores.GestorCitas" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="es.upm.etsisi.clifis.model.Cita" %>
<%@ page import="java.util.Iterator" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>

<%@include file="templates/clifis_head.jsp" %>

<jsp:include page="templates/clifis_cabecera.jsp" flush="true"/>

<jsp:include page="templates/clifis_menu.jsp" flush="true"/>

<script>
    function mostrar_ppal() {
        $('#titulo').html('Bienvenido a Clifis');
        $('#ppal').show();
        $('#login').hide();
    }

    function mostrar_login() {
        $('#titulo').html('Entrada al sistema...');
        $('#ppal').hide();
        $('#login').show();
    }

    $(document).ready(function () {
        $(function () {
            <c:if test="${sessionScope.usuario == null}">
            mostrar_login();
            </c:if>
            <c:if test="${sessionScope.usuario != null}">
            mostrar_ppal();
            </c:if>
        });
    });
</script>

<style>
    #estadisticas {
        padding: 10px 10px 10px 10px;
        border-radius: 10px;
        background-color: rgba(255, 255, 255, 0.75);
    }
</style>

<div class="container clifis_container">
    <clifis:titulo>
        <span id="titulo"></span>
    </clifis:titulo>

    <div class="row" id="ppal" style="display: none;">
        <div class="col-sm-12  clifis_scrollable">
            <p>
                &nbsp;
            </p>
            <div class="row">
                <div class="col-xs-8" style="height: 43%;">
                    <h4>Listado diario de citas: </h4>
                    <%
                        GestorCitas gestorCitas = new GestorCitas();
                        Timestamp fechaActual = new Timestamp(Calendar.getInstance().getTimeInMillis());
                        List <Cita> citas = gestorCitas.getCitasFromFecha(fechaActual);
                        if(citas.isEmpty()){
                    %>
                    <br/>
                    <p>
                        No existe ninguna por ahora.
                        </br>
                        <a href="alta_cita.jsp">Dar de alta.</a>
                    </p>
                    <%
                    }else{
                    %>
                    <table class="table tablesorter-bootstrap" id="tabla_listado_citas" style="height: 33%; font-size: small;">
                        <thead>
                        <tr>
                            <th>Sala</th>
                            <th>Hora</th>
                            <th>Paciente</th>
                            <th>Especialidad</th>
                            <th>Médico</th>
                        </tr>
                        </thead>
                        <tbody>
                        <%
                            Iterator <Cita> iterador  = citas.iterator();
                            while (iterador.hasNext()){
                                Cita cita = iterador.next();
                        %>
                        <tr>
                            <td style="text-align: center;"><%= cita.getConsulta().getNumSala()%></td>
                            <td style="text-align: center;"> <fmt:formatDate value="<%= cita.getFecha()%>" pattern="HH:mm"/> </td>
                            <td><%= cita.getPaciente().getNombre()%> <%= cita.getPaciente().getApellidos()%></td>
                            <td><%= cita.getEspecialidad().getNombre()%></td>
                            <td><%= cita.getMedico().getNombre()%> <%= cita.getMedico().getApellidos()%></td>
                        </tr>
                        <%
                                }
                            }
                        %>
                        </tbody>
                    </table>
                </div>

                <%-- ESTADISTICAS --%>
                <div class="col-xs-4" style="height: 43%;">
                    <h4>Datos generales de la clínica:</h4>
                    <div id="estadisticas">
                        <ul id="ul_estadisticas" class="list-group">
                            <b>Número de médicos</b>:
                            <li class="list-group-item" id="li_num_medicos">${applicationScope.gestor_medicos.numeroMedicos}</li>
                            <br/>
                            <b>Número de especialidades</b>:
                            <li class="list-group-item" id="li_num_especialidades">${applicationScope.gestor_especialidades.numeroEspecialidades}</li>
                            <br/>
                            <b>Número de pacientes</b>:
                            <li class="list-group-item" id="li_num_pacientes">${applicationScope.gestor_pacientes.numeroPacientes}</li>
                            <br/>
                            <b>Número de citas pendientes</b>:
                            <li class="list-group-item" id="li_num_citas_pendientes">${applicationScope.gestor_citas.citasPendientes}</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="row" id="login" style="margin-top: 20px;">
        <div class="row" style="height: 10%;">
            <div class="col-sm-6" style="margin-left: 10px;">
                <c:if test="${requestScope.gestorException == null}">
                    <p>
                        Bienvenido a la clínica <span class="text-primary">Clifis</span>. Introduce tu usuario y contraseña
                        para continuar, por favor.
                    </p>
                    <br />
                </c:if>

                <c:if test="${requestScope.gestorException != null}">
                    <label class="control-label" for="excepcion">No se ha podido iniciar sesión:</label>
                    <span class="list-group-item list-group-item-danger" id="excepcion">
                        <%--@elvariable id="gestorException" type="es.upm.etsisi.clifis.gestores.GestorException"--%>
                        ${gestorException.message}
                    </span>
                </c:if>
                <hr style="border-width: 3px; border-color: #6785aa !important;"/>
            </div>
        </div>

        <div class="row" style="height: 50%;">
            <div class="col-sm-6  clifis_scrollable">
                <form method="post" action="process_login" id="form_login">
                    <div class="form-group">
                        <!-- SELECT: Médico-->
                        <label class="form-group" for="usuario">Usuario:</label>
                        <select class="selectpicker" data-container="body" data-width="100%" data-live-search="true"
                                id="usuario" name="usuario">
                            <option value="0">Gestor</option>
                            <c:forEach items="${applicationScope.gestor_medicos.medicos}" var="medico" varStatus="loop">
                                <option value="${medico.id}">${medico.nombre} ${medico.apellidos}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="password">Password</label>
                        <input type="password" class="form-control" id="password" name="password" placeholder="Contraseña">
                    </div>
                    <div class="form-group" style="text-align: right; margin-top: 25px;">
                        <label for="enviar"></label>
                        <input type="submit" class="btn btn-basic" id="enviar" value="Iniciar Sesión" style="border-color: #3f3f3f;">
                    </div>
                </form>
            </div>
            <div class="col-sm-6">
                &nbsp;
            </div>
        </div>
    </div>
</div>

<jsp:include page="templates/clifis_pie.jsp" flush="true"/>

<%@include file="templates/clifis_end.jsp" %>

<script>
    $(document).ready(function () {
        $(function () {
            $("#tabla_listado_citas").tablesorter(
                {
                    theme: 'bootstrap',
                    headerTemplate: '{content} {icon}',
                    widgets: ['uitheme', 'zebra', 'scroller'],
                    widgetOptions: {
                        scroller_height: 300
                    },
                    dateFormat: "HH:ii",
                    headers: {
                        4: {sorter: "time"}
                    }
                }
            );
        });
    });
</script>