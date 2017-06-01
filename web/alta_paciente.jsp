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
<%@ page import="es.upm.etsisi.clifis.model.Paciente" %>
<%@ page import="com.google.gson.Gson" %>

<%@include file="templates/clifis_head.jsp"%>

<jsp:include page="templates/clifis_cabecera.jsp" flush="true" />

<jsp:include page="templates/clifis_menu.jsp" flush="true" />

<%
    Paciente paciente = (Paciente) request.getAttribute("pacienteParaModificar");
    String mod = null;
    if (paciente != null) {
        mod = new Gson().toJson(paciente);
        request.setAttribute("tituloPagina", "Modificar Paciente");
    } else {
        request.setAttribute("tituloPagina", "Alta Paciente");
    }
%>


<script>

    function fill_form() {
        var paciente = <%= mod %>;
        if (paciente !== null) {
            $('#nombre').val(paciente.nombre);
            $('#apellidos').val(paciente.apellidos);
            $('#aseguradora').val(paciente.aseguradora);
            $('#dni')
                .val(paciente.dni)
;
            $('#id_paciente').val(paciente.id);
            $('#Submit').val('Modificar');
        }
    }
    $(document).ready(function () {
        $(function () {
            fill_form();
        });
    });
</script>


<div class="container clifis_container">
    <clifis:titulo>
        ${requestScope.tituloPagina}
    </clifis:titulo>



    <c:if test="${requestScope.gestorException != null || requestScope.paciente != null}">
    <div class="row">
        <div class="col-sm-12" style="height: 8%">
            <c:choose>
                <c:when test="${requestScope.gestorException != null}">
                    <div class="row">
                        <div class="col-sm-12">

                            <label class="control-label" for="excepcion">No se ha podido dar de alta al paciente:</label>
                            <span class="list-group-item list-group-item-danger" id="excepcion">
                                    ${gestorException.message}
                            </span>
                        </div>
                    </div>
                </c:when>
                <c:when test="${requestScope.paciente != null}">
                    <div class="row">
                        <div class="col-sm-12">

                            <label class="control-label" for="alta_paciente">Paciente dado de alta:</label>
                            <span class="list-group-item list-group-item-success" id="alta_paciente">
                                           D./Da.: ${paciente.nombre} ${paciente.apellidos} con DNI: ${paciente.dni} y aseguradora ${paciente.aseguradora}.
                                    </span>

                        </div>
                    </div>
                </c:when>
            </c:choose>
        </div>
    </div>
    <hr style="border-width: 3px; border-color: #6785aa !important; margin-left: 25px; margin-right: 25px;"/>
    </c:if>

    <div class="row">
        <!-- Formulario paciente nuevo. -->
        <c:if test="${requestScope.gestorException != null || requestScope.paciente != null}">
        <div class="col-sm-12 clifis_scrollable" style="height: 49%">
            </c:if>
            <c:if test="${requestScope.gestorException == null && requestScope.paciente == null}">
            <div class="col-sm-12 clifis_scrollable">
                </c:if>
                <form method="post" action="process_alta_paciente">
                    <div class="form-group">
                        <label class="control-label col-md-2" for="nombre">Nombre</label>
                        <input class="form-control" type="text" name="nombre" id="nombre">
                    </div>
                    <div class="form-group">
                        <label class="control-label col-md-2" for="apellidos">Apellidos</label>
                        <input class="form-control" type="text" name="apellidos" id="apellidos">
                    </div>
                    <div class="form-group">
                        <label class="control-label col-md-2" for="dni">DNI</label>
                        <input class="form-control" type="text" name="dni" id="dni"/>
                    </div>
                    <div class="form-group">
                        <label class="control-label col-md-2" for="aseguradora">Aseguradora</label>
                        <input class="form-control" type="text" name="aseguradora" id="aseguradora">
                    </div>
                    <div class="form-group">
                        <label class="control-label col-md-2" style="display:none;"for="id_paciente"></label>
                        <input class="form-control" type="text" name="id_paciente" style="display:none" id="id_paciente">
                    </div>
                    <input type="submit" value="Alta" name ="operacion" id="Submit" class="btn btn-default"/>
                </form>
            </div>

        </div>
    </div>

    <jsp:include page="templates/clifis_pie.jsp" flush="true" />

    <%@include file="templates/clifis_end.jsp"%>
