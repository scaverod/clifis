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

<%@ page import="es.upm.etsisi.clifis.model.Medico" %>
<%@ page import="es.upm.etsisi.clifis.model.Usuario" %>
<%@ page import="es.upm.etsisi.clifis.gestores.GestorMedicos" %>
<%@ page import="es.upm.etsisi.clifis.gestores.GestorException" %>
<%@ page import="es.upm.etsisi.clifis.model.MedicoBuilder" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>

<%
    // Se comprueba si el usuario es un medico y se rellena un objeto medico para usarlo en la página.
    Medico medico = null;
    try {
        int idUsuario = ((Usuario)session.getAttribute("usuario")).getId();
        if(idUsuario != 0)
            medico = ((GestorMedicos)application.getAttribute("gestor_medicos")).getMedicoById(idUsuario);
        else {
            medico = new MedicoBuilder().setId(0).setNombre("Gestor").setApellidos("Gestor").build();
        }

        pageContext.setAttribute("medico", medico);
    } catch (GestorException e) {
        request.setAttribute("excepcion", e);
    }
%>

<%@include file="templates/clifis_head.jsp"%>

<jsp:include page="templates/clifis_cabecera.jsp" flush="true" />

<jsp:include page="templates/clifis_menu.jsp" flush="true" />

<script>
    function submit_nueva_entrada() {
        $('#hiddenBtnEnviar').val($('#btn_enviar').val());
        $('#formulario')
            .attr('action', '/process_nueva_entrada')
            .submit();
    }

    function bring_historial(paciente_id){
        waitingDialog.show("Cargando historial del paciente...");
        $.ajax({
           url: "/get_historial_paciente",
            method: "POST",
            data: {
                paciente_id: paciente_id
            },
            success: function(response) {
                $('#body_tabla_historial').empty();

                if ($.trim(response)) {
                    <%-- La respuesta es un JSON con las entradas del historial de un paciente --%>
                    var entradas_historial = JSON.parse(response);

                    $.each(entradas_historial, function (item, entrada) {
                        var ff = new Date(entrada.fecha);

                        $('<tr>')
                            .append($('<td class="columna col_fecha">').html(("0" + ff.getDate()).slice(-2) + '/' + ("0" + (ff.getMonth()+1)).slice(-2) + '/'+ ff.getFullYear()))
                            .append($('<td class="columna col_medico">').html(entrada.medico.apellidos))
                            .append($('<td class="columna col_especialidad">').html(entrada.especialidad.nombre))
                            .append($('<td class="columna col_comentario">').html('<div class="coment" id="coment_' + entrada.id + '">' + entrada.comentario + '</div>'))
                            <c:if test="${sessionScope.usuario.id != 0}">
                                .append($('<td class="columna col_modificar">').html('<label><input type="radio" name="modificar_entrada" value="' + entrada.id + '"></label>'))
                            </c:if>
                            .appendTo('#body_tabla_historial');
                    });

                    $('#body_tabla_historial').show();
                    $('#tabla_historial').trigger('update');
                    $('#tabla_vacia').hide();

                    <%-- Defenición del evento VER/MODIFICAR --%>
                    $('input[name=modificar_entrada]').click
                    (function(){
                        if ($(this).prop('checked')) {
                            $('#nuevoComentario').val($('#coment_' + $(this).val()).html());
                            $('#btn_enviar').val('Modificar');
                            $('#btn_reset').show();
                            $('#nuevaFecha').show();
                        } else {
                            $('#nuevoComentario').val('');
                            $('#btn_enviar').val('Enviar');
                        }
                    });

                    <c:if test="${sessionScope.usuario.id == 0}">
                        $('.coment').click(function(){
                            $(this).closest('tbody').find('.columna').removeAttr("style");
                            $(this).closest('tr').children('td').css("background-color", "#9eb3c9");
                            $('#comentario').val($(this).text());
                        });
                    </c:if>

                    var entradaMod = ${entradaModificada != null};
                    if(entradaMod) {
                        $('#tabla_historial input[value=' + ${entradaModificada.id} + ']').prop('checked', true);
                    }

                    $('#tabla_vacia').hide();
                    $('#body_tabla_historial').show();

                } else {
                    <%-- Si la respuesta está vacia es que no tiene historia --%>
                    $('#body_tabla_historial').hide();
                    $('#tabla_vacia').show();
                }
            }
        }).done(function () {
            waitingDialog.hide();
        });
    }

    function reset_entradas () {
        $('input[name=modificar_entrada]').prop('checked', false);
        $('#nuevoComentario').val('');
        $('#btn_enviar').val('Enviar');
        $('#btn_reset').hide();
        $('#nuevaFecha').hide();
    }

    <%-- Carga las especialidades de un médico en el <select> de especialidades.
         Si el parámetro 'medico_id' está vacio, carga todas las especialidades de la clínica. --%>
    function bring_especialidades(medico_id) {
        $.ajax({
            url: "/loadEspecialidades",
            method: "POST",
            data: {
                medico_id: medico_id
            },
            success: function (response) {
                <%-- La respuesta es un JSON con un array de objetos 'Especialidad' serilizados. --%>
                var especialidades = JSON.parse(response);
                var selectEspecialidades = $('#especialidades');
                <%-- Se borrán las opciones actuales del <select> de especialidades (inicialización). --%>
                selectEspecialidades.find('option').remove();

                <%-- Se crea un primer <option> vacío para el <select>. Así, no hay nada elegido por defecto. --%>
                $('<option>')
                    .attr('data-hidden', 'true')
                    .html(" ")
                    .appendTo(selectEspecialidades);

                <%-- Por cada 'Especialidad' en el JSON se crea un <option>. --%>
                $.each(especialidades, function (item, especialidad) {
                    $('<option>')
                        .val(especialidad.id)
                        .text(especialidad.nombre)
                        .appendTo(selectEspecialidades)
                });
                <%-- Se renderiza el selectpicker para que muestre las nuevas opciones. --%>
                selectEspecialidades.selectpicker('refresh');

                var especialidadID = "${especialidad.id}";

                if (especialidadID !== '') {
                    $('#especialidades').selectpicker('val', especialidadID);
                }
            }
        });
    }

    $(document).ready(function () {
        $(function () {

            $('#body_tabla_historial').hide();
            $('#tabla_vacia').show();
            $('#btn_enviar').prop('disabled', true);

            bring_especialidades(<%= medico.getId() %>);

            $('#dni')
                .on("input", function () {
                    $.ajax({
                        url: "/checkDNI",
                        method: "POST",
                        data: {
                            dniChanged: $(this).val()
                        },
                        success: function (response) {
                            <%-- Si la respuesta es 'null', es que el paciente no existe. --%>
                            if (response === 'null') {
                                <%-- Oculta los datos del paciente. --%>
                                $('#l_datos_paciente').hide();
                                var dni = $('#dni').val();
                                if (dni === null || dni === '') {
                                    $('#p_datos_paciente').html("Para poder seleccionar médico y/o especialidad, hay que introducir el DNI de un paciente.");
                                } else {
                                    $('#p_datos_paciente').html("No se ha encontrado ningún pacientte con DNI: <b>" + dni + "</b>.");
                                }
                                $('#l_datos_paciente').hide();
                                $('#p_datos_paciente').show();
                                reset_entradas();
                                $('#body_tabla_historial').hide();
                                $('#tabla_vacia').show();
                                $('#btn_enviar').prop('disabled', true);
                            } else {
                                <%-- Crea el objeto 'Paciente' encontrado a partir del JSON de la respuesta. --%>
                                var paciente = JSON.parse(response);
                                <%-- Carga los items de la lista que muestran los datos del paciente encontrado. --%>
                                $('#li_pac_nombre').text(paciente.nombre);
                                $('#li_pac_apellidos').text(paciente.apellidos);
                                $('#li_pac_aseguradora').text(paciente.aseguradora);
                                $('#paciente_id').val(paciente.id);
                                <%-- Muestra los datos del paciente encontrado. --%>
                                $('#p_datos_paciente').hide();
                                $('#l_datos_paciente').show();

                                $('#btn_enviar').prop('disabled', false);
                                bring_historial(paciente.id);

                            }
                        }
                    });
                });

            var pacienteDNI = "${paciente.dni}";
            if (pacienteDNI !== '') {
                $('#dni')
                    .val(pacienteDNI)
                    .trigger("input");
            }

            var comentario = `${fn:trim(comentario)}`;
            if (comentario !== '') {
                $('#nuevoComentario').val(comentario);
            }

            $("#tabla_historial").tablesorter(
                {
                    theme: 'bootstrap',
                    headerTemplate: '{content} {icon}',
                    widthFixed: false,
                    widgets: ['uitheme', 'zebra', 'scroller'],
                    widgetOptions: {
                        scroller_height: 300
                    },
                    dateFormat: "ddmmyyyy",
                    headers: {}
                }
            );
        });
    });

</script>

<style>
    <c:if test="${sessionScope.usuario.id == 0}">
        .coment {
            cursor: pointer;
        }
    </c:if>


    #datos_paciente {
        padding: 10px 10px 10px 10px;
        border-radius: 10px;
        background-color: rgba(255, 255, 255, 0.75);
    }

    .tablesorter th {
        text-align: center;
    }

    td.columna {
        text-align: center;
        font-size: small;
        padding: 4px 4px 4px 4px;
    }

    td.col_fecha {
        min-width: 10% !important;
        max-width: 10% !important;
        width: 10% !important;
    }

    td.col_medico {
        text-align: left;
        min-width: 25% !important;
        max-width: 25% !important;
        width: 25% !important;
    }

    td.col_especialidad {
        text-align: left;
        min-width: 20% !important;
        max-width: 20% !important;
        width: 20% !important;
    }

    td.col_comentario {
        min-width: 30% !important;
        max-width: 30% !important;
        width: 30% !important;
    }

    div.coment {
        max-width: 220px;
        min-width: 50px !important;
        text-align: left;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
    }

    td.col_modificar {
        min-width: 10% !important;
        max-width: 10% !important;
        width: 10% !important;
    }
</style>

<div class="container clifis_container">
    <clifis:titulo>
        Administración de historiales médicos
    </clifis:titulo>

    <form action="/process_nueva_entrada" method="post" id="formulario" onkeypress="return event.keyCode != 13;">
        <div class="row" style="height: 60%;">
            <div class="col-sm-4" style="margin-top: 5px;">
                <%-- ESPACIO PARA DATOS DE PACIENTE --%>
                <div class="row">
                    <div class="col-sm-12">
                    <!-- TEXT: DNI Paciente -->
                        <div class="form-group">
                            <label class="control-label" for="dni">Búsqueda de paciente por DNI:</label>
                            <input class="form-control" type="text" size="20" name="dni" id="dni">
                        </div>

                        <div id="datos_paciente">
                            <ul id="l_datos_paciente" style="display: none" class="list-group">
                                Nombre:
                                <li class="list-group-item" id="li_pac_nombre"></li>

                                Apellidos:
                                <li class="list-group-item" id="li_pac_apellidos"></li>

                                Aseguradora:
                                <li class="list-group-item" id="li_pac_aseguradora"></li>
                            </ul>
                            <p id="p_datos_paciente" class="text-primary" style="text-align: justify;">
                                Para poder seleccionar médico y/o especialidad, hay que introducir el DNI de un paciente.
                            </p>
                            <input type="hidden" name="paciente_id" id="paciente_id">
                        </div>
                    </div>
                </div>


                <%-- ESPACIO PARA AVISOS --%>
                <div class="row" style="margin-bottom: 10px;">
                    <div class="col-sm-12" style="margin-top: 15px;">
                        <c:if test="${requestScope.excepcion != null}">
                            <label class="control-label" for="excepcion">Ha habido un error:</label>
                            <span class="list-group-item list-group-item-danger" id="excepcion">
                                ${requestScope.excepcion.message}
                            </span>
                        </c:if>

                        <c:if test="${requestScope.entradaNuevaCreada != null}" >
                            <label class="control-label" for="entradaNuevaCreada">Creada entrada nueva:</label>
                            <span class="list-group-item list-group-item-success" id="entradaNuevaCreada">
                                ${fn:substring(requestScope.entradaNuevaCreada.comentario, 0, 100)}...
                            </span>
                        </c:if>

                        <c:if test="${requestScope.entradaModificada != null}" >
                            <label class="control-label" for="entradaModificada">Modificación entrada correcta:</label>
                            <span class="list-group-item list-group-item-success" id="entradaModificada">
                                <c:if test="${entradaModificada.fecha !=null}">
                                    <span style="text-decoration: underline;">
                                        <fmt:formatDate value="${entradaModificada.fecha}" pattern="dd/MM/yyyy" />
                                    </span>
                                    <br />
                                </c:if>
                                ${fn:substring(requestScope.entradaModificada.comentario, 0, 100)}...
                            </span>
                        </c:if>
                    </div>
                </div>
            </div>


            <div class="col-sm-8">
                <%-- ESPACIO PARA MÉDICO y ESPECIALIDAD --%>
                <div class="row">
                    <div class="col-sm-12">
                        <c:if test="${sessionScope.usuario.id != 0}">
                            <table class="table table-striped">
                                <tr>
                                    <td>
                                        <label class="form-group" for="medico">Médico:</label>
                                        <input class="form-control" disabled type="text" size="50" name="medico" id="medico" value="${medico.nombre} ${medico.apellidos}">
                                        <input type="hidden" name="medico_id" value="${medico.id}">
                                    </td>
                                    <td>
                                        <label class="form-group" for="especialidades">Especialidad:</label>
                                        <select class="selectpicker" data-width="100%" data-container="body" data-live-search="true"
                                                name="especialidad" id="especialidades">
                                        </select>
                                    </td>
                                </tr>
                            </table>
                        </c:if>
                    </div>
                </div>

                <%-- ESPACIO PARA TABLA DE ENTRADAS DE HISTORIAL--%>
                <div class="row" style="margin-bottom: 20px">
                    <div class="col-sm-12">
                        <table class="table table-responsive tablesorter-bootstrap" id="tabla_historial">
                            <thead>
                            <tr>
                                <th>Fecha</th>
                                <th>Medico</th>
                                <th>Especialidad</th>
                                <th>comentario</th>
                                <c:if test="${sessionScope.usuario.id != 0}">
                                    <th class="sorter-false">Ver/Modificar</th>
                                </c:if>
                            </tr>
                            </thead>
                            <tbody id="body_tabla_historial">

                            </tbody>
                        </table>
                        <div id="tabla_vacia" style="display: none;">
                            <span class="list-group-item list-group-item-danger">
                            El paciente seleccionado no tiene historial.
                        </span>
                        </div>
                    </div>
                </div>

                <%-- ESPACIO PARA INSERCIONES EN HISTORIAL--%>
                <div class="row" style="margin-right: 0px; margin-left: 0px;">
                    <div class="col-sm-12" style="padding: 4px 4px 4px 4px; border-radius: 4px; background-color: rgba(255, 255, 255, 0.75);">
                        <c:if test="${sessionScope.usuario.id == 0}">
                            <label class="form-group" for="comentario">Comentario:</label>
                            <textarea class="form-control" rows="3" cols="40" name="comentario" id="comentario">(Haz click en un comentario de la tabla para verlo aquí completo).</textarea>
                        </c:if>

                        <c:if test="${sessionScope.usuario.id != 0}">
                            <label class="form-group" for="nuevoComentario" style="margin-left: 5px;">Nueva entrada en el historial:</label>
                            <table class="table" style="margin-top: -10px;">
                                <tr>
                                    <td rowspan="2" style="border: none;">
                                        <textarea class="form-control" rows="3" cols="40" name="nuevoComentario" id="nuevoComentario"></textarea>
                                    </td>
                                    <td style="border: none; width: 90px; text-align: center; vertical-align: middle;">
                                        <input type="hidden" name="btn_enviar" id="hiddenBtnEnviar" value="Enviar">
                                        <input type="button" value="Enviar" name="btn_enviar" id="btn_enviar" class="btn btn-success btn-sm"
                                               onclick="submit_nueva_entrada();"/>
                                    </td>
                                    <td style="border: none; width: 70px; text-align: center; vertical-align: middle;">
                                        <input type="button" value="Reset" id="btn_reset" class="btn btn-warning btn-sm"
                                               style="display: none;" onclick="reset_entradas();"/>
                                    </td>
                                <tr>
                                    <td colspan="2" class="form-inline" style="border: none; font-size: small; text-align: left; vertical-align: middle;">
                                        <label style="display: none;" id="nuevaFecha"><input type="checkbox" name="nuevaFecha" value="si">Actualizar fecha</label>
                                    </td>
                                </tr>
                            </table>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>

<jsp:include page="templates/clifis_pie.jsp" flush="true" />

<%@include file="templates/clifis_end.jsp"%>