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
<%@ page import="es.upm.etsisi.clifis.model.Medico" %>
<%@ page import="com.google.gson.Gson" %>
<%@ page import="es.upm.etsisi.clifis.model.MedicoBuilder" %>
<%@ page import="es.upm.etsisi.clifis.gestores.GestorEspecialidades" %>

<%@include file="templates/clifis_head.jsp" %>

<c:if test="${sessionScope.usuario.id != 0}">
    <jsp:forward page="/medico_no_autorizado.jsp" />
</c:if>

<jsp:include page="templates/clifis_cabecera.jsp" flush="true"/>

<jsp:include page="templates/clifis_menu.jsp" flush="true"/>

<%
    Medico medico  = (Medico) request.getAttribute("medicoParaModificar");
    String mod = null;
    if (medico != null) {
        medico.setEspecialidades(((GestorEspecialidades)application.getAttribute("gestor_especialidades")).getEspecialidadesByMedico(medico));
        mod = new Gson().toJson(medico);
        request.setAttribute("tituloPagina", "Modificar Medico");
    } else {
        request.setAttribute("tituloPagina", "Alta Medico");
    }
%>

<script>

    function fill_form() {
        var medico = <%= mod %>;
        if (medico !== null) {
            $('#med_id').val(medico.id);
            $('#Submit').val('Modificar');
            $('#nombre').val(medico.nombre);
            $('#apellidos').val(medico.apellidos);
            var arr = $.map(medico.especialidades, function(el) { return el });
            var esps = [];
            $.each(arr, function (item, esp) {
                esps.push(esp.id);
            });
            $('#password').attr('type', 'hidden');
            $('#lbl_pwd').text('');
            $('#especialidad').selectpicker('val', esps).selectpicker('refresh');
            $('#numCol').val(medico.numCol);
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

    <c:if test="${requestScope.excepcion != null || requestScope.medico != null}">
        <div class="row">
            <div class="col-sm-12" style="height: 7%">
                <c:choose>
                    <c:when test="${requestScope.excepcion != null }">
                        <div class="row">
                            <div class="col-sm-12">
                                <label class="control-label" for="excepcion">No se ha podido dar de alta al médico:</label>
                                <span class="list-group-item list-group-item-danger" id="excepcion">
                                    <%--@elvariable id="excepcion" type="es.upm.etsisi.clifis.gestores.GestorException"--%>
                                    ${excepcion.message}
                                </span>
                            </div>
                        </div>
                    </c:when>
                    <c:when test="${requestScope.medico != null && requestScope.excepcion == null }">
                        <div class="row">
                            <div class="col-sm-12">
                                <label class="control-label" for="alta_medico">Médico dado de alta:</label>
                                <span class="list-group-item list-group-item-success" id="alta_medico">
                                    <%--@elvariable id="medico" type="es.upm.etsisi.clifis.model.Medico"--%>
                                    Dr/a. ${medico.nombre} ${medico.apellidos} con número de colegiado ${medico.numCol}
                                </span>
                            </div>
                        </div>
                    </c:when>
                </c:choose>
            </div>
        </div>

        <hr style="border-width: 3px; border-color: #6785aa !important; margin: 35px 0 10px 0;"/>
    </c:if>

    <form method="post" action="process_alta_medico">
        <input type="hidden" name="med_id" id="med_id">
        <c:if test="${requestScope.excepcion != null || requestScope.medico != null}">
        <div class="row" style="height: 47%">
            </c:if>
            <c:if test="${requestScope.excepcion == null && requestScope.medico == null}">
            <div class="row" style="height: 58%">
                </c:if>
                <div class="col-xs-6">
                    <div class="form-group">
                        <label class="control-label" for="nombre">Nombre:</label>
                        <input class="form-control" type="text" name="nombre" id="nombre">
                    </div>
                    <div class="form-group">
                        <label class="control-label" for="apellidos">Apellidos:</label>
                        <input class="form-control" type="text" name="apellidos" id="apellidos">
                    </div>
                    <div class="form-group">
                        <!-- SELECT: Especialidad -->
                        <label class="form-group" for="especialidad">Especialidad:</label>
                        <br/>
                        <select class="selectpicker" data-container="body" data-live-search="true" name="especialidad"
                                id="especialidad" multiple>
                            <c:forEach items="${applicationScope.gestor_especialidades.especialidades}"
                                       var="especialidad_id" varStatus="loop">
                                <option value="${especialidad_id.id}">${especialidad_id.nombre}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <div class="col-xs-6">
                    <div class="form-group">
                        <label class="control-label" for="numCol">Número de Colegiado:</label>
                        <input class="form-control" type="text" name="numCol" id="numCol"/>
                    </div>
                    <div class="form-group">
                        <label class="control-label" for="password" id="lbl_pwd">Contraseña:</label>
                        <input class="form-control" type="password" name="password" id="password"/>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-xs-12">
                    <input type="submit" value="Alta" name= "operacion" id="Submit" class="btn btn-default"/>
                </div>
            </div>
    </form>
</div>


<jsp:include page="templates/clifis_pie.jsp" flush="true"/>

<%@include file="templates/clifis_end.jsp" %>
