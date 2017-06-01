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

<c:if test="${sessionScope.usuario.id != 0}">
    <jsp:forward page="/medico_no_autorizado.jsp" />
</c:if>

<jsp:include page="templates/clifis_cabecera.jsp" flush="true"/>

<jsp:include page="templates/clifis_menu.jsp" flush="true"/>
<script>
    $(document).ready(function () {
        $(function () {
            $("#tabla_modificar_medico").tablesorter(
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

            $("#tabla_operaciones_medicos").tablesorter(
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

    function submit_eliminar() {
        $('#formulario')
            .submit();
    }
    function submit_modificar() {
        $('#formulario')
            .attr("action", "/process_modificar_medico")
            .submit();
    }
</script>

<div class="container clifis_container">
    <clifis:titulo>
        Administración del medico:
    </clifis:titulo>

    <c:if test="${requestScope.excepciones != null || requestScope.medicos != null || requestScope.medico != null}">
        <div class="row">
            <div class="col-sm-12 clifis_scrollable" style="height: 12%">
                <c:if test="${requestScope.excepciones != null}">
                    <div class="row">
                        <div class="col-sm-12">
                            <c:forEach items="${requestScope.excepciones}" var="excepcion">
                                <label class="control-label" for="excepcion">Error, no se ha podido realizar la operación</label>
                                <span class="list-group-item list-group-item-danger" id="excepcion">
                                        ${excepcion.message}
                                </span>
                                <br/>
                            </c:forEach>
                        </div>
                    </div>
                </c:if>
                <c:if test="${requestScope.medicos != null}">
                    <div class="row">
                        <div class="col-sm-12">
                            <c:forEach items="${requestScope.medicos}" var="medico">
                                <label class="control-label" for="tabla_modificar_medico">Medico dado de baja:</label>
                                <span class="list-group-item list-group-item-success" id="medico_borrado">
                                    El médico ${medico.nombre} ${medico.apellidos} ha sido dado de baja satisfactoriamente
                                </span>
                                <br/>
                            </c:forEach>
                        </div>
                    </div>
                </c:if>
                    <%--@elvariable id="medico" type="es.upm.etsisi.clifis.model.Medico"--%>
                <c:if test="${requestScope.medico != null}">
                    <div class="row">
                        <div class="col-sm-12">
                            <label class="control-label" for="medico_borrado">Medico modificado:</label>
                            <span class="list-group-item list-group-item-success" id="medico_modificado">
                                Dr/a. ${medico.nombre} ${medico.apellidos} con el número de colegiado: ${medico.numCol}
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
        <form method="post" action="process_baja_medico" id="formulario">
            <label class="control-label" for="tabla_modificar_medico">Puedes seleccionar un medico para modificarlo:</label>
            <c:if test="${requestScope.excepciones != null || requestScope.medicos != null}">
            <div class="col-sm-12" style="height: 44%">
                </c:if>
                <c:if test="${requestScope.excepciones == null && requestScope.medicos == null}">
                <div class="col-sm-12 clifis_scrollable">
                    </c:if>
                    <c:if test="${empty applicationScope.gestor_medicos.medicos}">
                        <br/>
                        <p>
                            No exsite ningun medico en el sistema. Prueba a dar alguno
                            de <a href="alta_medico.jsp">alta</a> antes... :)
                        </p>
                    </c:if>
                    <table class="table tablesorter-bootstrap" id="tabla_modificar_medico">
                        <thead>
                        <tr>
                            <th>Nombre</th>
                            <th>Apellidos</th>
                            <th>Numero de colegiado</th>
                            <th>Especialidades</th>
                            <th class="sorter-false">Modificar</th>
                            <th class="sorter-false">Eliminar</th>
                        </tr>
                        </thead>
                        <tbody>
                        <style>
                            td {
                                text-align: center;
                            }
                        </style>
                        <c:forEach items="${applicationScope.gestor_medicos.medicos}" var="medico" varStatus="loop">
                            <tr>
                                <td style="text-align: left;">${medico.nombre}</td>
                                <td style="text-align: left;">${medico.apellidos}</td>
                                <td style="text-align: right;">${medico.numCol}</td>
                                <td style="text-align: left;">
                                    <c:forEach items="${applicationScope.gestor_especialidades.getEspecialidadesByMedico(medico)}" var="esp" varStatus="loop">
                                        ${esp.nombre}<c:if test="${!loop.last}">,</c:if><c:if test="${loop.last}">.</c:if>
                                    </c:forEach>
                                </td>
                                <td>
                                    <label><input type="radio" name="modificar_medico" value="${medico.id}"></label>
                                </td>
                                <td>
                                    <label><input type="checkbox" name="baja_medico" value="${medico.id}"></label>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                    <table class="table tablesorter-bootstrap" id="tabla_operaciones_medicos"
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
                                Operaciones sobre medicos:
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
                </div>
        </form>
    </div>
</div>

<jsp:include page="templates/clifis_pie.jsp" flush="true"/>

<%@include file="templates/clifis_end.jsp" %>

