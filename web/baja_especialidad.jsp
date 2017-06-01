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

<%@include file="templates/clifis_head.jsp"%>

<c:if test="${sessionScope.usuario.id != 0}">
    <jsp:forward page="/medico_no_autorizado.jsp" />
</c:if>

<jsp:include page="templates/clifis_cabecera.jsp" flush="true" />

<jsp:include page="templates/clifis_menu.jsp" flush="true" />

<div class="container clifis_container">
    <clifis:titulo>
        Baja de especialidades
    </clifis:titulo>

    <c:if test="${requestScope.excepciones != null || requestScope.especialidades != null}">
        <div class="row">
            <div class="col-sm-12 clifis_scrollable" style="height: 17%">
                <c:if test="${requestScope.excepciones != null}">
                    <div class="row">
                        <div class="col-sm-12">
                            <c:forEach items="${requestScope.excepciones}" var="excepcion">
                                <label class="control-label" for="excepcion">No se ha podido dar de baja esta especialidades:</label>
                                <span class="list-group-item list-group-item-danger" id="excepcion">
                                    ${excepcion.message}
                                </span>
                                <br/>
                            </c:forEach>
                        </div>
                    </div>
                </c:if>
                <c:if test="${requestScope.especialidades != null}">
                    <div class="row">
                        <div class="col-sm-12">
                            <c:forEach items="${requestScope.especialidades}" var="especialidades">
                                <label class="control-label" for="borrada_especialidad">Especialidad dada de baja:</label>
                                <span class="list-group-item list-group-item-success" id="borrada_especialidad">
                                    ${especialidades.nombre}
                                </span>
                                <br/>
                            </c:forEach>
                        </div>
                    </div>
                </c:if>
            </div>
        </div>
        <hr style="border-width: 3px; border-color: #6785aa !important; margin-left: 25px; margin-right: 25px;" />
    </c:if>

    <div class="row">
        <c:if test="${requestScope.excepciones != null || requestScope.especialidades != null}">
            <div class="col-sm-12 clifis_scrollable" style="height: 39%">
        </c:if>
        <c:if test="${requestScope.excepciones == null && requestScope.especialidades == null}">
            <div class="col-sm-12 clifis_scrollable">
        </c:if>
            <form method="post" action="process_baja_especialidad">
                <label class="control-label" for="especialidades">Seleccione las especialidades que desea borrar:</label>
                <div class="col-xs-12 clifis_scrollable">
                    <c:if test="${empty applicationScope.gestor_especialidades.especialidades}">
                        <br />
                        <p>
                            No exsite ninguna especialidades en el sistema. Prueba a dar alguna
                            de <a href="alta_especialidad.jsp">alta</a> antes... :-)
                        </p>
                    </c:if>
                    <table class="table table-responsive" id="especialidades">
                        <tbody>
                            <tr>
                                <%-- TODO: Controlar que en baja_especialidad.jsp solo salgan las que no sean claves foráneas. --%>
                                <c:forEach items="${applicationScope.gestor_especialidades.especialidades}" var="especialidades" varStatus="loop">
                                <c:if test="${not loop.first and loop.index % 4 == 0}">
                                    </tr>
                                    <tr>
                                </c:if>
                                <td>
                                    <div class="checkbox list-group-item">
                                        <label>
                                            <input type="checkbox" name="especialidad_id" value="${especialidades.id}">${especialidades.nombre}
                                            <input type="hidden" name="${especialidades.id}" value="${especialidades.nombre}">
                                        </label>
                                    </div>
                                </td>
                                </c:forEach>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div class="row">
                    <div class="col-xs-12">
                        <input type="submit" value="Enviar" id="Submit" class="btn btn-default"/>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="templates/clifis_pie.jsp" flush="true" />

<%@include file="templates/clifis_end.jsp"%>
