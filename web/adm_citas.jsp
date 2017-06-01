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

<style>
    .tablesorter th {
        text-align: center;
        padding: 4px 4px 4px 4px !important;
    }

    .celda {
        text-align: center;
        vertical-align: middle !important;
        font-size: small;
        padding: 4px 4px 4px 4px !important;
    }

    td.col_numSala {
        min-width: 8% !important;
        max-width: 8% !important;
        width: 8% !important;
    }

    td.col_hora {
        min-width: 8% !important;
        max-width: 8% !important;
        width: 8% !important;
    }

    td.col_duracion {
        min-width: 8% !important;
        max-width: 8% !important;
        width: 8% !important;
    }

    td.col_paciente {
        text-align: left;
        min-width: 20% !important;
        max-width: 20% !important;
        width: 20% !important;
    }

    td.col_especialidad {
        text-align: left;
        min-width: 20% !important;
        max-width: 20% !important;
        width: 20% !important;
    }

    td.col_medico {
        text-align: left;
        min-width: 20% !important;
        max-width: 20% !important;
        width: 20% !important;
    }

    td.col_modificar {
        min-width: 8% !important;
        max-width: 8% !important;
        width: 8% !important;
    }

    td.col_eliminar {
        min-width: 8% !important;
        max-width: 8% !important;
        width: 8% !important;
    }
</style>
<script>
    <%-- Envía el formulario completo --%>
    function submit_eliminar() {
        $('#formulario').submit();
    }

    function submit_modificar() {
        $('#formulario')
            .attr("action", "/process_modificar_cita")
            .submit();
    }

    $(document).ready(function () {
        $(function () {
            var fechaInicio = new Date();
            fechaInicio.setMonth(fechaInicio.getMonth()-1);
            var fechaFin = new Date();
            fechaFin.setMonth(fechaFin.getMonth()+7);

            $('#calendario')
                .datepicker({
                    format: "dd/mm/yyyy",
                    weekStart: 1,
                    <%-- Se activan las fechas desde hace dos meses en adelante hasta 6 meses. --%>
                    startDate: fechaInicio.getDate() + "/" + fechaInicio.getMonth() + "/" + fechaInicio.getFullYear(),
                    endDate: fechaFin.getDate() + "/" + fechaFin.getMonth() + "/" + fechaFin.getFullYear(),
                    clearBtn: false,
                    language: "es",
                    todayHighlight: true
                    <%-- Cuando cambia el valor del calendario, actualiza el <input hidden> dia. --%>
                })
                .on('changeDate', function () {
                    waitingDialog.show("Cargado citas");
                    var fecha = $('#calendario').datepicker('getFormattedDate');
                    $.ajax({
                        url: "/process_fecha_citas",
                        method: "POST",
                        data: {
                            fecha_citas: fecha
                        },
                        success: function (response) {
                            $('#tabla_citas_body').empty();

                            var citas = JSON.parse(response);
                            $.each(citas, function (item, cita) {

                                var hora = new Date(cita.fecha);

                                $('<tr>')
                                    .append($('<td class="celda col_numSala">').html(cita.consulta.numSala))
                                    .append($('<td class="celda col_hora">').html(("0" + hora.getHours()).slice(-2) + ":" + ("0" + hora.getMinutes()).slice(-2)))
                                    .append($('<td class="celda col_duracion">').html(("0" + cita.consulta.duracion).slice(-2) + " mins."))
                                    .append($('<td class="celda col_paciente">').html(cita.paciente.nombre + " " + cita.paciente.apellidos))
                                    .append($('<td class="celda col_especialidad">').html(cita.especialidad.nombre))
                                    .append($('<td class="celda col_medico">').html(cita.medico.nombre + " " + cita.medico.apellidos))
                                    <c:if test="${sessionScope.usuario.id == 0}">
                                        .append($('<td class="celda col_modificar">').html('<label><input type="radio" name="modificar_cita" value="' + cita.id + '"></label>'))
                                        .append($('<td class="celda col_eliminar">').html('<label><input type="checkbox" name="baja_cita" value="' + cita.id + '"></label>'))
                                    </c:if>
                                    .appendTo('#tabla_citas_body');
                            });
                            $('#tabla_citas').trigger('update');
                        }
                    }).done(function () {
                        waitingDialog.hide();
                    });
                });

            $("#tabla_citas").tablesorter(
                {
                    theme: 'bootstrap',
                    headerTemplate: '{content} {icon}',
                    widthFixed: false,
                    widgets: ['uitheme', 'zebra', 'scroller'],
                    widgetOptions: {
                        scroller_height: 450
                    },
                    dateFormat: "ddmmyyyy",
                    headers: {}
                }
            );

            $("#tabla_operaciones_citas").tablesorter(
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
</script>

<div class="container clifis_container">
    <clifis:titulo>
        Administración de citas
    </clifis:titulo>

    <div class="row" style="height: 60%;">
        <%-- CALENDARIO --%>
        <div class="col-sm-4" style="text-align: center;">
            <div class="row">
                <h5>(Seleciona una fecha para ver las citas de ese día):</h5>
                <div id="calendario" style="display: inline-block; padding: 10px 10px 10px 10px; border-radius: 10px; background-color: rgba(255, 255, 255, 0.75);">
                    <div>
                        <%-- Esto lo rellena el plugin datepicker con el calendario --%>
                    </div>
                </div>

                <%-- CITAS BORRADAS O ERRORES --%>
                <c:if test="${requestScope.excepciones != null || requestScope.citas != null}">
                    <hr style="border-width: 3px; border-color: #6785aa !important; margin-left: 25px; margin-right: 25px;"/>
                    <div id="notificaciones" style="max-height: 200px; overflow-y: auto;">
                        <c:if test="${requestScope.excepciones != null}">
                            <c:forEach items="${requestScope.excepciones}" var="excepcion">
                                <label class="control-label" for="excepcion">No se ha podido dar de baja esta cita:</label>
                                <span class="list-group-item list-group-item-danger" id="excepcion">
                                        ${excepcion.message}
                                </span>
                                <br/>
                            </c:forEach>
                        </c:if>
                        <c:if test="${requestScope.citas != null}">
                            <c:forEach items="${requestScope.citas}" var="cita">
                                <label class="control-label" for="excepcion">Cita borrada correctamente:</label>
                                <span class="list-group-item list-group-item-success" id="excepcion">
                                    Que D/a. <b>${cita.paciente.nombre} ${cita.paciente.apellidos}</b> tenía el día
                                    <b><fmt:formatDate value="${cita.fecha}" pattern="dd/MM/yyyy"/></b> a las
                                    <b><fmt:formatDate value="${cita.fecha}" pattern="HH:mm"/></b> con el Dr.
                                    <b>${cita.medico.nombre} ${cita.medico.apellidos}</b> (${cita.especialidad.nombre}).
                                </span>
                                <br/>
                            </c:forEach>
                        </c:if>
                    </div>
                </c:if>
            </div>
        </div>

        <%-- LISTADO DE CITAS --%>
        <form action="/process_eliminar_citas" method="post" id="formulario">
            <div class="col-sm-8" style="text-align: center;">
                <table class="table tablesorter-bootstrap" id="tabla_citas">
                    <thead>
                    <tr>
                        <th>Sala</th>
                        <th>Hora</th>
                        <th>Duración</th>
                        <th>Paciente</th>
                        <th>Especialidad</th>
                        <th>Médico</th>
                        <c:if test="${sessionScope.usuario.id == 0}">
                            <th class="sorter-false">Modificar</th>
                            <th class="sorter-false">Eliminar</th>
                        </c:if>
                    </tr>
                    </thead>

                    <tbody id="tabla_citas_body">

                    </tbody>
                </table>

                <c:if test="${sessionScope.usuario.id == 0}">
                    <table class="table tablesorter-bootstrap" id="tabla_operaciones_citas"
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
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td colspan="6" style="text-align: right; font-weight: bold; vertical-align: middle;">
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