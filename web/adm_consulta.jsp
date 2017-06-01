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

<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>

<%@include file="templates/clifis_head.jsp" %>

<jsp:include page="templates/clifis_cabecera.jsp" flush="true"/>

<jsp:include page="templates/clifis_menu.jsp" flush="true"/>

<script>
    $(document).ready(function () {
        $(function () {
            $("#tabla_baja_consultas").tablesorter(
                {
                    theme: 'bootstrap',
                    headerTemplate: '{content} {icon}',
                    widgets: ['uitheme', 'zebra', 'scroller'],
                    widgetOptions: {
                        scroller_height: 300
                    },
                    dateFormat: "ddmmyyyy",
                    headers: {}
                }
            );

            $("#tabla_operaciones_consultas").tablesorter(
                {
                    theme: 'bootstrap',
                    headerTemplate: '',
                    widgets: ['uitheme', 'zebra'],
                    widgetOptions: {},
                    headers: {}
                }
            );

        });
    });

    <%-- Envía el formulario completo --%>
    function submit_eliminar() {
        $('#formulario').submit();
    }

    function submit_modificar() {
        $('#formulario')
            .attr("action", "/process_modificar_consulta")
            .submit();
    }
</script>

<div class="container clifis_container">
    <clifis:titulo>
        Administración de consultas
    </clifis:titulo>

    <c:if test="${requestScope.gestorException != null || requestScope.excepciones != null || requestScope.consultas != null || requestScope.consulta != null}">
        <div class="row">
            <div class="col-sm-12 clifis_scrollable" style="height: 12%">
                <c:if test="${requestScope.gestorException != null}">
                    <div class="row">
                        <div class="col-sm-12">
                            <label class="control-label" for="excepcion">No se ha podido modificar esta
                                consulta:</label>
                            <span class="list-group-item list-group-item-danger" id="excepcion">
                                    ${requestScope.gestorException.message}
                            </span>
                            <br/>
                        </div>
                    </div>
                </c:if>
                <c:if test="${requestScope.excepciones != null}">
                    <div class="row">
                        <div class="col-sm-12">
                            <c:forEach items="${requestScope.excepciones}" var="excepcion">
                                <label class="control-label" for="excepcion">No se ha podido dar de baja esta
                                    consulta:</label>
                                <span class="list-group-item list-group-item-danger" id="excepcion">
                                        ${excepcion.message}
                                </span>
                                <br/>
                            </c:forEach>
                        </div>
                    </div>
                </c:if>
                <c:if test="${requestScope.consultas != null}">
                    <div class="row">
                        <div class="col-sm-12">
                            <c:forEach items="${requestScope.consultas}" var="consulta">
                                <label class="control-label" for="borrada_consulta">Consulta dada de baja:</label>
                                <span class="list-group-item list-group-item-success" id="borrada_consulta">
                                    Sala número ${consulta.numSala}. ${consulta.diaSemana} de
                                    <fmt:formatDate value="${consulta.horaInicio}" pattern="HH:mm"/> a <fmt:formatDate value="${consulta.horaFin}" pattern="HH:mm"/>.
                                    Dr/a. ${consulta.medico.nombre} ${consulta.medico.apellidos} (${consulta.especialidad.nombre}).
                                </span>
                                <br/>
                            </c:forEach>
                        </div>
                    </div>
                </c:if>
                    <%--@elvariable id="consulta" type="es.upm.etsisi.clifis.model.Consulta"--%>
                <c:if test="${requestScope.consulta != null}">
                    <div class="row">
                        <div class="col-sm-12">
                            <label class="control-label" for="borrada_consulta">Consulta modificada:</label>
                            <span class="list-group-item list-group-item-success" id="consulta_modificada">
                                Sala número ${consulta.numSala}. ${consulta.diaSemana} de
                                <fmt:formatDate value="${consulta.horaInicio}" pattern="HH:mm"/> a <fmt:formatDate value="${consulta.horaFin}" pattern="HH:mm"/>.
                                Dr/a. ${consulta.medico.nombre} ${consulta.medico.apellidos} (${consulta.especialidad.nombre}).
                            </span>
                            <br/>
                        </div>
                    </div>
                </c:if>
            </div>
        </div>
        <hr style="border-width: 3px; border-color: #6785aa !important; margin-left: 25px; margin-right: 25px;"/>
    </c:if>

    <div class="row">
        <form method="post" action="process_baja_consulta" id="formulario">
            <label class="control-label" for="tabla_baja_consultas">Puedes seleccionar consultas para modificar y/o
                eliminar:</label>
            <c:if test="${requestScope.excepciones != null || requestScope.consultas != null}">
            <div class="col-sm-12" style="height: 44%">
                </c:if>
                <c:if test="${requestScope.excepciones == null && requestScope.consultas == null}">
                <div class="col-sm-12 clifis_scrollable">
                    </c:if>
                    <c:if test="${empty applicationScope.gestor_consultas.consultas}">
                        <br/>
                        <p>
                            No exsite ninguna consulta en el sistema. Prueba a dar alguna
                            de <a href="alta_consulta.jsp">alta</a> antes... :)
                        </p>
                    </c:if>

                    <table class="table tablesorter-bootstrap" id="tabla_baja_consultas">
                        <thead>
                        <tr>
                            <th>Nº Sala</th>
                            <th>Hora Inicio</th>
                            <th>Hora Fin</th>
                            <th>Especialidad</th>
                            <th>Duración</th>
                            <th>Médico</th>
                            <th class="sorter-false">Día Semana</th>
                            <c:if test="${sessionScope.usuario.id == 0}">
                                <th class="sorter-false">Modificar</th>
                                <th class="sorter-false">Eliminar</th>
                            </c:if>
                        </tr>
                        </thead>
                        <tbody>
                        <style>
                            td {
                                text-align: center;
                            }
                        </style>
                        <c:forEach items="${applicationScope.gestor_consultas.consultas}" var="consulta"
                                   varStatus="loop">
                            <tr>
                                <td>${consulta.numSala}</td>
                                <td><fmt:formatDate value="${consulta.horaInicio}" pattern="HH:mm" /></td>
                                <td><fmt:formatDate value="${consulta.horaFin}" pattern="HH:mm" /></td>
                                <td style="text-align: left;">${consulta.especialidad.nombre}</td>
                                <td>${consulta.duracion}</td>
                                <td style="text-align: left;">${consulta.medico.nombre} ${consulta.medico.apellidos}</td>
                                <td style="text-align: left;">${consulta.diaSemana}</td>
                                <c:if test="${sessionScope.usuario.id == 0}">
                                    <td>
                                        <label><input type="radio" name="modificar_consulta" value="${consulta.id}"></label>
                                    </td>
                                    <td>
                                        <label><input type="checkbox" name="baja_consulta" value="${consulta.id}"></label>
                                    </td>
                                </c:if>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                    <c:if test="${sessionScope.usuario.id == 0}">
                        <table class="table tablesorter-bootstrap" id="tabla_operaciones_consultas"
                               style="margin-top: 15px;">
                            <thead style="display: none;">
                            <tr>
                                <th></th>
                                <th></th>
                                <th></th>
                                <th></th>
                                <th></th>
                                <th></th>
                                <th></th>
                                <th></th>
                                <th></th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td colspan="7" style="text-align: right; font-weight: bold; vertical-align: middle;">
                                    Operaciones sobre consultas:
                                </td>
                                <td>
                                    <input type="button" value="Modificar" id="btn_modificar" class="btn btn-warning"
                                           onclick="submit_modificar();"/>
                                </td>
                                <td>
                                    <input type="button" value="Eliminar" id="btn_eliminar" class="btn btn-danger"
                                           onclick="submit_eliminar();"/>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </c:if>
                </div>
        </form>
    </div>
</div>

<jsp:include page="templates/clifis_pie.jsp" flush="true"/>

<%@include file="templates/clifis_end.jsp" %>
