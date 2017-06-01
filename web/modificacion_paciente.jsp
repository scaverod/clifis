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
            $("#tabla_modificar_paciente").tablesorter(
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

            $("#tabla_operaciones_pacientes").tablesorter(
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


    function submit_modificar() {
        $('#formulario')
            .attr("action", "/process_modificar_paciente")
            .submit();
    }
</script>

<div class="container clifis_container">
    <clifis:titulo>
        Modificación del paciente:
    </clifis:titulo>

    <c:if test="${requestScope.fallo!= null || requestScope.pacientes != null || requestScope.paciente != null}">
        <div class="row">
            <div class="col-sm-12 clifis_scrollable" style="height: 12%">
                <c:if test="${requestScope.fallo != null}">
                    <div class="row">
                        <div class="col-sm-12">
                            <c:forEach items="${requestScope.fallo}" var="excepcion">
                                <label class="control-label" for="excepcion">Error, no se ha podido realizar la operación</label>
                                <span class="list-group-item list-group-item-danger" id="excepcion">
                                        ${fallo}
                                </span>
                                <br/>
                            </c:forEach>
                        </div>
                    </div>
                </c:if>

                <c:if test="${requestScope.paciente != null && (requestScope.fallo =='' || requestScope.fallo == null)}">
                    <div class="row">
                        <div class="col-sm-12">
                            <label class="control-label" for="paciente_modificado">Paciente modificado:</label>
                            <span class="list-group-item list-group-item-success" id="paciente_modificado">
                                Sr/a.  ${paciente.nombre}  ${paciente.apellidos}  con la aseguradora:  ${paciente.aseguradora}  y DNI:  ${paciente.dni}.
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
        <form method="post" action="process_modificar_paciente" id="formulario">
            <label class="control-label" for="tabla_modificar_paciente">Puedes seleccionar un paciente para modificarlo:</label>
            <c:if test="${requestScope.exception != null || requestScope.pacientes != null}">
            <div class="col-sm-12" style="height: 44%">
                </c:if>
                <c:if test="${requestScope.exception == null && requestScope.pacientes == null}">
                <div class="col-sm-12 clifis_scrollable">
                    </c:if>


                    <c:forEach items="${requestScope.exception}" var="excepcion">
                        <label class="control-label" for="excepcion">No se ha podido modificar este paciente:</label>
                        <span class="list-group-item list-group-item-danger" id="excepcion">
                                ${exception.message}
                        </span>
                        <br/>
                    </c:forEach>
                    <c:if test="${empty applicationScope.gestor_pacientes.pacientes}">
                        <br/>
                        <p>
                            No exsite ningun paciente en el sistema. Prueba a dar alguno
                            de <a href="alta_paciente.jsp">alta</a> antes... :)
                        </p>
                    </c:if>
                    <table class="table tablesorter-bootstrap" id="tabla_modificar_paciente">
                        <thead>
                        <tr>
                            <th>Nombre</th>
                            <th>Apellidos</th>
                            <th>Aseguradora</th>
                            <th>DNI</th>
                            <th class="sorter-false">Modificar</th>
                        </tr>
                        </thead>
                        <tbody>
                        <style>
                            td {
                                text-align: center;
                            }
                        </style>
                        <c:forEach items="${applicationScope.gestor_pacientes.pacientes}" var="paciente"                                   varStatus="loop">
                            <tr>
                                <td>${paciente.nombre}</td>
                                <td>${paciente.apellidos}</td>
                                <td>${paciente.aseguradora}</td>
                                <td>${paciente.dni}</td>
                                <td>
                                    <label><input type="radio" name="modificar_paciente" value="${paciente.id}"></label>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                    <table class="table tablesorter-bootstrap" id="tabla_operaciones_pacientes"
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
                                Operación:
                            </td>
                            <td>
                                <input type="button" value="Modificar" id="btn_modificar" class="btn btn-warning"
                                       onclick="submit_modificar();"/>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
        </form>
    </div>
</div>

<jsp:include page="templates/clifis_pie.jsp" flush="true"/>

<%@include file="templates/clifis_end.jsp" %>
