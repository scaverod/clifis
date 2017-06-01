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

<%@ page import="es.upm.etsisi.clifis.model.Consulta" %>
<%@ page import="com.google.gson.Gson" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>

<%@include file="templates/clifis_head.jsp" %>

<c:if test="${sessionScope.usuario.id != 0}">
    <jsp:forward page="/medico_no_autorizado.jsp" />
</c:if>

<jsp:include page="templates/clifis_cabecera.jsp" flush="true"/>
<%
    Consulta consultaMod = (Consulta) request.getAttribute("consultaParaModificar");
    String consultaModJSON = null;
    if (consultaMod != null) {
        consultaModJSON = new Gson().toJson(consultaMod);
        request.setAttribute("tituloPagina", "Modificar Consulta");
    } else {
        request.setAttribute("tituloPagina", "Alta consulta");
    }
%>
<script>
    function convertTo24Hour(time) {
        var hours = parseInt(time.substr(0, 2));

        if (time.indexOf('AM') != -1 && hours == 12) {
            time = time.replace('12', '0');
        }
        if (time.indexOf('PM') != -1 && hours < 12) {
            time = time.replace(hours, (hours + 12));
            if (hours + 12 < 20)
                time = time.substr(1, time.length);
        }
        return time.substr(0, 5);
    }

    function fill_form() {
        var consulta = <%= consultaModJSON %>;

        if (consulta !== null) {
            var prueba = consulta.horaInicio;

            console.log("PRUEBA: " + prueba);

            $('#idSala').val(consulta.id);
            $('#numSala').val(consulta.numSala);
            $('#horaIni').val(convertTo24Hour(consulta.horaInicio));
            $('#horaFin').val(convertTo24Hour(consulta.horaFin));
            $('#duracion').val(consulta.duracion);
            $('select[name=diaSemana]').val(consulta.diaSemana);
            $('select[name=especialidades]').val(consulta.especialidad.id);
            $('select[name=medico]').val(consulta.medico.id);
            $('.selectpicker').selectpicker('refresh');
            $('#Submit').val('Modificar Consulta');
        }
    }

    $(document).ready(function () {
        $(function () {
            $('.clockpicker').clockpicker({
                donetext: 'Hecho',
                autoclose: true
            });
            var input = $('#duracion').clockpicker({
                donetext: 'Hecho',
                placement: 'bottom',
                align: 'left',
                autoclose: true
            });
            // Manually toggle to the minutes view
            $('#duracion').click(function (e) {
                // Have to stop propagation here
                e.stopPropagation();
                input.clockpicker('show')
                    .clockpicker('toggleView', 'minutes');
            });

            fill_form();
        });
    });
</script>
<jsp:include page="templates/clifis_menu.jsp" flush="true"/>

<div class="container clifis_container">
    <clifis:titulo>
        ${requestScope.tituloPagina}
    </clifis:titulo>

    <div class="row">
        <form method="post" action="process_alta_consulta" id="formulario_alta_consulta">

            <div class="col-xs-6 clifis_scrollable">
                <input type="hidden" id="idSala" name="idSala" value="0">
                <!-- TEXT: Número de sala -->
                <div class="form-group">
                    <label class="control-label" for="numSala">Número de sala:</label>
                    <input class="form-control" type="text" name="numSala" id="numSala">
                </div>

                <!-- TEXT: Hora de Inicio -->
                <div class="form-group clockpicker has-feedback">
                    <label class="control-label" for="horaIni">Hora de inicio de la consulta:</label>
                    <input type="text" class="form-control" id="horaIni" name="horaIni" value="12:00"
                           autocomplete="off">
                    <i class="glyphicon glyphicon-time form-control-feedback"></i>
                </div>

                <!-- TEXT: Hora de Fin -->
                <div class="form-group clockpicker has-feedback">
                    <label class="control-label" for="horaFin">Hora de finalización de la consulta:</label>
                    <input type="text" class="form-control" id="horaFin" name="horaFin" value="12:00"
                           autocomplete="off">
                    <i class="glyphicon glyphicon-time form-control-feedback"></i>
                </div>

                <!-- TEXT: Duración (Y script para quedarse solo con los minutos) -->
                <script>
                    function setDuracion() {
                        var valor = document.getElementById("duracion").value;
                        document.getElementById("duracion").value = valor.substr(valor.length - 2);
                        return true;
                    }
                </script>

                <div class="form-group clockpicker has-feedback">
                    <label class="control-label" for="duracion">Duración cita (minutos):</label>
                    <input class="form-control" id="duracion" name="duracion" value="00" onchange="setDuracion()"
                           autocomplete="off">
                    <i class="glyphicon glyphicon-time form-control-feedback"></i>
                </div>

                <!-- SELECT: Día de la semana -->
                <div class="form-group" style="height: 25%;">
                    <label class="control-label">Día de la semana:</label>
                    <br/>
                    <select class="selectpicker" data-container="body" id="diaSemana" name="diaSemana"
                            title="Día de la semana">
                        <option value="Lunes">Lunes</option>
                        <option value="Martes">Martes</option>
                        <option value="Miércoles">Miércoles</option>
                        <option value="Jueves">Jueves</option>
                        <option value="Viernes">Viernes</option>
                        <option value="Sabado">Sabado</option>
                        <option value="Domingo">Domingo</option>
                    </select>
                </div>

            </div>

            <div class="col-xs-6 clifis_scrollable">

                <!-- Avisos del alta anterior -->
                <c:choose>
                    <c:when test="${requestScope.gestorException != null}">
                        <label class="control-label" for="excepcion">No se ha podido dar de alta esa consulta:</label>
                        <span class="list-group-item list-group-item-danger" id="excepcion">
                            <%--@elvariable id="gestorException" type="es.upm.etsisi.clifis.gestores.GestorException"--%>
                            ${gestorException.message}
                        </span>
                        <br/>
                    </c:when>
                    <c:when test="${requestScope.consulta != null}">
                        <label class="control-label" for="nueva_especialidad">Nueva consulta dada de alta:</label>
                        <span class="list-group-item list-group-item-success" id="nueva_especialidad">
                            <%--@elvariable id="consulta" type="es.upm.etsisi.clifis.model.Consulta"--%>
                            Sala número ${consulta.numSala}. ${consulta.diaSemana} de ${consulta.horaInicio} a ${consulta.horaFin}.
                            Dr/a. ${consulta.medico.nombre} ${consulta.medico.apellidos} (${consulta.especialidad.nombre}).
                        </span>
                        <br/>
                    </c:when>
                </c:choose>
                <c:if test="${requestScope.gestorException != null or requestScope.consulta != null}">
                    <hr style="border-width: 3px; border-color: #6785aa !important; margin-left: 25px; margin-right: 25px;"/>
                </c:if>

                <!-- SELECT: Especialidad -->
                <div class="col-xs-12 form-group" style="height: 30%;">
                    <label class="form-group" for="especialidades">Seleccione especialidades de la consulta:</label>
                    <br/>
                    <select class="selectpicker" data-container="body" data-live-search="true" name="especialidades"
                            id="especialidades">
                        <c:forEach items="${applicationScope.gestor_especialidades.especialidades}"
                                   var="especialidad_id" varStatus="loop">
                            <option value="${especialidad_id.id}">${especialidad_id.nombre}</option>
                        </c:forEach>
                    </select>
                </div>

                <!-- SELECT: Médico-->
                <div class="col-xs-12 form-group" style="height: 30%;">
                    <label class="form-group" for="medico">Seleccione el medico:</label>
                    <br/>
                    <select class="selectpicker" data-container="body" data-live-search="true" id="medico"
                            name="medico">
                        <c:forEach items="${applicationScope.gestor_medicos.medicos}" var="medico" varStatus="loop">
                            <option value="${medico.id}">${medico.nombre} ${medico.apellidos}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>
            <div class="row">
                <div class="col-xs-12">
                    <input type="submit" value="Dar de Alta" name="operacion" id="Submit" class="btn btn-default"/>
                </div>
            </div>
        </form>
    </div>
</div>

<jsp:include page="templates/clifis_pie.jsp" flush="true"/>

<%@include file="templates/clifis_end.jsp" %>

