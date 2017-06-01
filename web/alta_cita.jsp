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

<%@ page import="es.upm.etsisi.clifis.model.Cita" %>
<%@ page import="es.upm.etsisi.clifis.model.CitaBuilder" %>
<%@ page import="com.google.gson.Gson" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>

<%@include file="templates/clifis_head.jsp" %>

<c:if test="${sessionScope.usuario.id != 0}">
    <jsp:forward page="/medico_no_autorizado.jsp" />
</c:if>

<jsp:include page="templates/clifis_cabecera.jsp" flush="true"/>
<%
    Cita citaMod = (Cita)request.getAttribute("citaParaModificar");
    String citaModJSON = null;
    if (citaMod != null) {
        citaModJSON = new Gson().toJson(citaMod);
        request.setAttribute("tituloPagina", "Modificación de una cita");
    } else {
        request.setAttribute("tituloPagina", "Alta de una cita por médico o especialidad");
    }
%>
<script>
    <%-- Rellena el formulario con los daots de una cita --%>
    var citaMod = <%= citaModJSON %>;
    function fill_form() {
        if (citaMod !== null) {

            $('#dni').val(citaMod.paciente.dni).prop('disabled', true);
            $('#li_pac_nombre').text(citaMod.paciente.nombre);
            $('#li_pac_apellidos').text(citaMod.paciente.apellidos);
            $('#li_pac_aseguradora').text(citaMod.paciente.aseguradora);
            $('#p_datos_paciente').hide();
            $('#l_datos_paciente').show();

            $('#especialidades').html('<option value="' + citaMod.especialidad.id + '">' + citaMod.especialidad.nombre + '</option>');
            $('#medicos').html('<option value="' + citaMod.medico.id + '">' + citaMod.medico.nombre + ' ' + citaMod.medico.apellidos + '</option>');
            $('#especialidades, #medicos').selectpicker('refresh');
            $('#idCitaMod').val(citaMod.id);

            var fechaCita = new Date(citaMod.fecha);
            $('#calendario')
                .datepicker('setDate', new Date(fechaCita.toISOString().slice(0,10)))
                .trigger('change');

            bring_citas();

            $('#resultado').show();
            $('#rst').show();
        }
    }

    <%-- Envía el formulario completo --%>
    function submit_form(id_pressed) {
        // Al hacer submit, no llegan al "req.getParameter" los elementos disabled, así que se ponen enabled
        $('#dni').prop('disabled', false);
        $('#especialidades, #medicos').prop('disabled', false).selectpicker('refresh');
        $('#' + id_pressed).click();
        $('#formulario').submit();
    }

    <%-- Pone enabled o disabled los selects de medico y especialidades. --%>
    function selects_enabled(enabled) {
        $('#especialidades, #medicos').prop('disabled', !enabled).selectpicker('refresh');
        <%-- Comprueba si los está desactivando (si no se quieren usar -por no tener un paciente, porejemplo-). --%>
        if (!enabled) {
            <%-- Vacía el valor de los <select> para que aparezcan en blanco. --%>
            $('#especialidades, #medicos').val('').selectpicker('refresh');
            limpia_calendario();
            <%-- Quita el botón de reset. --%>
            $('#rst').hide();
        } else {
            <%-- Se cargan todas las especialidades en su <select> --%>
            bring_especialidades(0);
            <%-- Se cargan todos los médicos en su <select> --%>
            bring_medicos(0);
        }
    }

    function limpia_calendario() {
        <%-- Si había días de consulta resaltados en el calendario, se quitan --%>
        $('#calendario').datepicker('setDaysOfWeekHighlighted', false);
        <%-- Si había consultas descritas, se borran. --%>
        $('#consultas_posibles').find('p').remove();
        <%-- Si había citas mostrándose, se borran. --%>
        $('#datos_citas').empty();
    }

    <%-- Carga los médicos de una especialidad en el <select> de médicos.
         Si el parámetro 'especialidad_id' está vacio, carga todos los médicos de la clínica. --%>
    function bring_medicos(especialidad_id) {
        $.ajax({
            url: "/loadMedicos",
            method: "POST",
            data: {
                especialidad_id: especialidad_id
            },
            success: function (response) {
                <%-- La respuesta es un JSON con un array de objetos 'Medico' serilizados. --%>
                var medicos = JSON.parse(response);
                var selectMedicos = $('#medicos');
                <%-- Se borrán las opciones actuales del <select> de especialidades (inicialización). --%>
                selectMedicos.find('option').remove();

                <%-- Se crea un primer <option> vacío para el <select>. Así, no hay nada elegido por defecto. --%>
                $('<option>')
                    .attr('data-hidden', 'true')
                    .appendTo(selectMedicos);

                <%-- Por cada 'Medico' en el JSON se crea un <option>. --%>
                $.each(medicos, function (item, medico) {
                    $('<option>')
                        .val(medico.id)
                        .text(medico.nombre + " " + medico.apellidos)
                        .appendTo(selectMedicos)
                });
                <%-- Se renderiza el selectpicker para que muestre las nuevas opciones. --%>
                selectMedicos.selectpicker('refresh');
            }
        });
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
            }
        });
    }

    <%-- Actualiza la información del calendario y de las consultas posibles según una especialidad y un médico. --%>
    var consultas;
    function bring_consultas(especialidad_id, medico_id) {
        $.ajax({
            url: "/checkConsultas",
            method: "POST",
            data: {
                especialidad_id: especialidad_id,
                medico_id: medico_id
            },
            <%--
                Para deshabilitar fechas:
                 $("#calendario").datepicker("setDatesDisabled", ['05/05/2017', '21/05/2017']);
            --%>
            success: function (response) {
                <%--
                    La respuesta es un JSON con tres items:
                        - idConsultas --> Array con los ID de las consultas recibidas.
                        - dias --> Array de enteros con los días de la semana.
                        - horas --> Texto con información de las consultas.
                --%>
                consultas = JSON.parse(response);

                <%-- Se colorean los días de consulta posibles en el calendario. --%>
                //if ($('#calendario').datepicker('getDate') === null ) {
                    $('#calendario').datepicker('setDaysOfWeekHighlighted', consultas.dias);;
                //}

                <%-- Si había consultas descritas, se borran. --%>
                $('#consultas_posibles').find('p').remove();

                <%-- Si no se encuentra alguna consulta, se dice. --%>
                if (consultas.horas[0] == null) {
                    $('<p class="citas_p">')
                        .text("No existen consultas que satisfagan estos criterios.")
                        .appendTo($('#consultas_posibles'));
                    <%-- Si se encuentra alguna consulta... --%>
                } else {
                    <%-- Se imprime un título de 'Consultas encontradas' --%>
                    $('<p class="citas_p" style="font-weight: bold;">')
                        .text("CONSULTAS ENCONTRADAS:")
                        .appendTo($('#consultas_posibles'));
                    <%-- Por cada consulta posible, se crea un párrafo para mostrar su información. --%>
                    $.each(consultas.horas, function (item, hora) {
                        $('<p class="citas_p">')
                            .text(hora)
                            .appendTo($('#consultas_posibles'));
                    });
                }
            }
        });
    }

    function modificarDia() {
        $('#btn_modificar_dia').hide();
        bring_consultas($('#especialidades').val(), $('#medicos').val());
    }

    function bring_citas() {
        waitingDialog.show("Cargando citas..");

        var fecha = $('#calendario').datepicker('getFormattedDate');
        var medico_id = $('#medicos').val();
        var especialidad_id = $('#especialidades').val();
        var consultas_id = (consultas == null) ? [citaMod.consulta.id] : consultas.idConsultas;

        $.ajax({
            url: "/loadCitas",
            method: "POST",
            data: {
                fecha: fecha,
                medico_id: medico_id,
                especialidad_id: especialidad_id,
                "consultas_id[]": consultas_id
            },
            success: function (response) {
                var citas = JSON.parse(response);
                var i = 0;
                $.each(citas.citasConsulta, function (item, citaConsulta) {
                    var capa = $('<div class="btn-group" data-toggle="buttons" style="margin-bottom: 10px;">');
                    $('<p class="citas_p" style="font-weight: bold;">')
                        .text(citaConsulta.txtConsulta)
                        .appendTo(capa);
                    $.each(citaConsulta.horas, function (item, hora) {
                        var citaLabel = $('<label for="' + i + '" class="btn btn-default btn-sm" onclick="submit_form(' + i + ')">').text(hora);
                        var citaInput = $('<input type="radio" name="horaCita" value="' + citaConsulta.idConsulta + ',' + fecha + ' ' + hora + '" name="' + hora + '" id="' + i + '"/>');
                        citaInput.appendTo(citaLabel);
                        citaLabel.appendTo(capa);
                        i++;
                    });
                    $(capa).appendTo('#datos_citas');

                });
            }
        }).done(function () {
            waitingDialog.hide();
        });

    }

    <%-- Deshace una cita recién insertada. --%>
    <%
    Cita citaAux = (Cita)request.getAttribute("cita");
    if (citaAux == null)
        citaAux = new CitaBuilder().setId(0).build();
    %>
    function deshacerCita() {
        $('#idCita').attr("value", "<%= citaAux.getId() %>");
        $('#formulario').attr("action", "/deshacer_cita").submit();
    }

    $(document).ready(function () {
        $(function () {

            <%-- Inicializa los Selectpicker --%>
            $('.selectpicker').tooltip('disable').selectpicker('refresh');

            <%-- Inicializa el calendario --%>
            var fechaInicio = new Date();
            fechaInicio.setMonth(fechaInicio.getMonth()+1);
            var fechaFin = new Date();
            fechaFin.setMonth(fechaFin.getMonth()+7);
            $('#calendario')
                .datepicker({
                    format: "dd/mm/yyyy",
                    weekStart: 1,
                    <%-- Se activan las fechas de "hoy" en adelante hasta 6 meses. --%>
                    startDate: fechaInicio.getDate() + "/" + fechaInicio.getMonth() + "/" + fechaInicio.getFullYear(),
                    endDate: fechaFin.getDate() + "/" + fechaFin.getMonth() + "/" + fechaFin.getFullYear(),
                    clearBtn: false,
                    language: "es",
                    todayHighlight: true
                    <%-- Cuando cambia el valor del calendario, actualiza el <input hidden> dia. --%>
                    }).on('changeDate', function () {
                    $('#datos_citas').empty();

                    <%-- Si el objeto consultas existe, es que al pedir una fecha ya se puede traer las citas. --%>
                    if (consultas != null) {
                        bring_citas();
                    }
                });

            <%-- Inicializa los selects a disabled. --%>
            selects_enabled(false);

            <%-- Cuando cambia una especialidad, entonces se cambia la lista de médicos. --%>
            $('#especialidades')
                .on("change", function () {
                    <%-- Se comprueba que médicos no tenga ya un valor. --%>
                    if (!$('#medicos').val()) {
                        bring_medicos($(this).val());
                    }
                    <%-- Se desactiva el <select> de especialidad por entender que ya se ha elegido una. --%>
                    $(this).prop('disabled', true).selectpicker('refresh');
                    <%-- Se muestra el botón de reset para poder activar los <select> --%>
                    $('#rst').show();

                    <%-- Se actualizan las consultas posibles en el calendario. --%>
                    bring_consultas($(this).val(), $('#medicos').val());
                });

            <%-- Cuando cambia un médico, entonces se cambia la lista de especialidades --%>
            $('#medicos')
                .on("change", function () {
                    <%-- Se comprueba que especialidades no tenga ya un valor. --%>
                    if (!$('#especialidades').val()) {
                        bring_especialidades($(this).val());
                    }
                    <%-- Se desactiva el <select> de médico por entender que ya se ha elegido uno. --%>
                    $(this).prop('disabled', true).selectpicker('refresh');
                    <%-- Se muestra el botón de reset para poder activar los <select> --%>
                    $('#rst').show();
                    <%-- Se actualizan las consultas posibles en el calendario. --%>
                    bring_consultas($('#especialidades').val(), $(this).val());
                });

            <%-- Click en el botón "Reset médicos/especialidades" --%>
            $('#rst')
                .click(function () {
                    $('#datos_citas').empty();
                    <%-- Se activan los <select> que estén desactivados y se cargan todos los médicos y especialidades. --%>
                    selects_enabled(true);
                    <%-- Se quita el botón de reset --%>
                    $('#rst').hide();
                    limpia_calendario();
                });

            <%-- Cuando cambia el DNI se comprueba si existe y si es así, se activan
                 los médicos y especialidades --%>
            $('#dni')
                .on("keyup chagne input", function () {
                    $.ajax({
                        url: "/checkDNI",
                        method: "POST",
                        data: {
                            dniChanged: $(this).val()
                        },
                        success: function (response) {
                            <%-- Si la respuesta es 'null', es que el paciente no existe. --%>
                            $('#resultado').hide();
                            limpia_calendario();
                            if (response === 'null') {
                                <%-- Desactiva los <select> de médico y especialidad. --%>
                                selects_enabled(false);
                                <%-- Vacía los items de la lista por si tuvieran los datos de un paciente. --%>
                                $('#li_pac_nombre').html("&nbsp;");
                                $('#li_pac_apellidos').html("&nbsp;");
                                $('#li_pac_aseguradora').html("&nbsp;");
                                <%-- Oculta los datos del paciente. --%>
                                $('#l_datos_paciente').hide();
                                var dni = $('#dni').val();
                                if (dni === null || dni === '') {
                                    $('#p_datos_paciente').html("Para poder seleccionar médico y/o especialidad, hay que introducir el DNI de un paciente.");
                                } else {
                                    $('#p_datos_paciente').html("No se ha encontrado ningún pacientte con DNI: <b>" + dni + "</b>.");
                                }
                                $('#p_datos_paciente').show();
                            } else {
                                <%-- Crea el objeto 'Paciente' encontrado a partir del JSON de la respuesta. --%>
                                var paciente = JSON.parse(response);
                                <%-- Activa los <select> de médico y especialidad. --%>
                                selects_enabled(true);
                                <%-- Carga los items de la lista que muestran los datos del paciente encontrado. --%>
                                $('#li_pac_nombre').text(paciente.nombre);
                                $('#li_pac_apellidos').text(paciente.apellidos);
                                $('#li_pac_aseguradora').text(paciente.aseguradora);
                                <%-- Muestra los datos del paciente encontrado. --%>
                                $('#p_datos_paciente').hide();
                                $('#l_datos_paciente').show();
                            }
                        },
                    });
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

    <form method="post" action="process_alta_cita2" id="formulario">
        <div class="row" style="height: 45%; overflow-y: auto;">
            <%-- Datos del paciente seleccionado --%>
            <div class="col-sm-4 clifis_scrollable" style="margin-top: 25px;  height: auto;">
                <!-- TEXT: DNI Paciente -->
                <div class="form-group">
                    <label class="control-label" for="dni">Búsqueda de paciente por DNI:</label>
                    <input class="form-control" type="text" size="20" name="dni" id="dni">
                </div>

                <style>
                    #datos_paciente {
                        padding: 10px 10px 10px 10px;
                        border-radius: 10px;
                        background-color: rgba(255, 255, 255, 0.75);
                        /* display: none; */
                    }
                </style>
                <div id="datos_paciente">
                    <ul id="l_datos_paciente" style="display: none" class="list-group">
                        <b>Nombre</b>:
                        <li class="list-group-item" id="li_pac_nombre"></li>
                        <br/>
                        <b>Apellidos</b>:
                        <li class="list-group-item" id="li_pac_apellidos"></li>
                        <br/>
                        <b>Aseguradora</b>:
                        <li class="list-group-item" id="li_pac_aseguradora"></li>
                    </ul>
                    <p id="p_datos_paciente" class="text-primary" style="text-align: justify;">
                        Para poder seleccionar médico y/o especialidad, hay que introducir el DNI de un paciente.
                    </p>
                </div>
            </div>

            <div class="col-sm-4 clifis_scrollable" style="margin-top: 25px;  height: auto;">
                <div class="col-xs-12 form-group">
                    <!-- SELECT: Especialidad -->
                    <label class="form-group" for="especialidades">Especialidad:</label>
                    <br/>
                    <select class="selectpicker" data-width="100%" data-container="body" data-live-search="true"
                            name="especialidad" id="especialidades">
                        <option data-hidden="true"></option>
                        <c:forEach items="${applicationScope.gestor_especialidades.especialidades}"
                                   var="especialidad_id" varStatus="loop">
                            <option value="${especialidad_id.id}">${especialidad_id.nombre}</option>
                        </c:forEach>
                    </select>
                    <br/><br/>

                    <!-- SELECT: Médico-->
                    <label class="form-group" for="medicos">Médico:</label>
                    <br/>
                    <select class="selectpicker" data-container="body" data-width="100%" data-live-search="true"
                            id="medicos" name="medicos">
                        <option data-hidden="true"></option>
                        <c:forEach items="${applicationScope.gestor_medicos.medicos}" var="medico" varStatus="loop">
                            <option value="${medico.id}">${medico.nombre} ${medico.apellidos}</option>
                        </c:forEach>
                    </select>
                    <br/>
                    <label class="form-group" for="rst"></label>
                    <input id="rst" type="button" class="btn btn-basic btn-block btn-sm"
                           value="Reiniciar médicos/especialidades" style="display: none;">
                </div>
            </div>

            <style>
                <%-- Personalización del calendario. --%>

                <%-- Separa un poco las celdas. Afecta a todo el <table> del datepicker. --%>
                .table-condensed {
                    border-collapse: separate;
                    border-spacing: 5px;
                }

                <%-- Es el estilo del día cuando pasa el ratón por encima. --%>
                .datepicker table tr td.day:hover,
                .datepicker table tr td.highlighted.disabled:hover,
                .datepicker table tr td.focused {
                    background: #73b787;
                    cursor: pointer;
                }

                <%-- Es el estilo de los días que hay consulta. --%>
                .datepicker table tr td.highlighted {
                    color: #000;
                    background-color: #9db2c8;
                    border-color: #dde4ec;
                    border-radius: 3px;
                }

                <%-- Es el estilo de los días que hay consulta pero que están inactivos. --%>
                .datepicker table tr td.highlighted.disabled,
                .datepicker table tr td.highlighted.disabled:active {
                    background: none;
                    color: #777777;
                }

                <%-- El estilo de los avisos de los horarios de las consultas --%>
                .citas_p {
                    font-size: small;
                    color: #2d3c5d;
                    background-color: rgba(255, 255, 255, 0.75);
                    border-radius: 3px;
                    padding: 2px 4px;
                }
            </style>
            <div class="col-sm-4 clifis_scrollable"
                 style="text-align: center; margin-top: 25px; height: 90%; overflow-y: auto;">
                <div id="calendario" style="display: inline-block;">
                    <div>
                        <%-- Esto lo rellena el plugin datepicker con el calendario --%>
                    </div>
                </div>
                <div id="consultas_posibles" style="margin-top: 10px;">
                    <%-- Esto se rellena desde javascript cuando haya que mostrar las consultas posibles. --%>
                </div>
            </div>
        </div>

        <!-- Fila para CITAS -->
        <style>
            radio {
                visibility: hidden;
            }
        </style>
        <div class="row" style="height: 20%; overflow-y: auto;">
            <div class="col-sm-8" id="datos_citas" style="margin-top: 25px;">

            </div>

            <div class="col-sm-4" id="resultado">
                <c:choose>
                    <c:when test="${requestScope.gestorException != null}">
                        <label class="control-label" for="excepcion">No se ha podido dar de alta esa cita:</label>
                        <span class="list-group-item list-group-item-danger" id="excepcion">
                            <%--@elvariable id="gestorException" type="es.upm.etsisi.clifis.gestores.GestorException"--%>
                            ${gestorException.message}
                        </span>
                        <br/>
                    </c:when>
                    <c:when test="${requestScope.cita != null || requestScope.cita_baja != null || requestScope.citaParaModificar != null}">
                        <style>
                            .reduced_li {
                                padding: 3px 10px;
                                list-style: none;
                            }

                            #btn_deshacer, #btn_modificar_dia {
                                margin-top: -13px;
                            }

                            #nueva_cita {
                                padding: 5px 15px;
                                text-align: center;
                            }
                        </style>
                        <c:if test="${requestScope.cita != null}">
                            <c:set var="cita" scope="page" value="${requestScope.cita}" />
                            <label class="control-label">Nueva cita dada de alta:</label>
                            <span class="list-group-item list-group-item-success" id="nueva_cita">
                        </c:if>
                        <c:if test="${requestScope.cita_baja != null}">
                            <c:set var="cita" scope="page" value="${requestScope.cita_baja}" />
                            <label class="control-label">Alta de cita anulada:</label>
                            <span class="list-group-item list-group-item-danger" id="nueva_cita">
                        </c:if>
                        <c:if test="${requestScope.citaParaModificar != null}">
                            <c:set var="cita" scope="page" value="${requestScope.citaParaModificar}" />
                            <label class="control-label">Datos de cita a modificar:</label>
                            <span class="list-group-item list-group-item-success" id="nueva_cita">
                        </c:if>
                            <%--@elvariable id="cita" type="es.upm.etsisi.clifis.model.Cita"--%>
                            <ul class="list-group">
                                <li class="list-group-item reduced_li">
                                    Paciente:
                                    <span style="font-weight: bold;">
                                        ${cita.paciente.nombre} ${cita.paciente.apellidos}
                                    </span>
                                </li>
                                <li class="list-group-item reduced_li">
                                    Médico:
                                    <span style="font-weight: bold;">
                                        ${cita.medico.nombre} ${cita.medico.apellidos}
                                    </span>
                                </li>
                                <li class="list-group-item reduced_li">
                                    Especialidad:
                                    <span style="font-weight: bold;">
                                            ${cita.especialidad.nombre}
                                    </span>
                                </li>
                                <li class="list-group-item reduced_li">
                                    Fecha y hora:
                                    <span style="font-weight: bold;">
                                        <fmt:formatDate value="${cita.fecha}" pattern="dd/MM/yyyy"/>
                                    </span>
                                    &nbsp;&nbsp;&nbsp;&nbsp;Hora:
                                    <span style="font-weight: bold;">
                                        <fmt:formatDate value="${cita.fecha}" pattern="HH:mm"/>
                                    </span>
                                </li>
                            </ul>
                                <c:if test="${requestScope.cita != null && requestScope.cita_modificada == null}">
                                    <span style="text-align: center">
                                        <label></label>
                                        <input class="btn btn-default btn-sm" id="btn_deshacer" type="button"
                                               value="Deshacer" onclick="deshacerCita()">
                                        <input type="hidden" name="idCita" id="idCita">
                                    </span>
                                </c:if>
                                <c:if test="${requestScope.citaParaModificar != null}">
                                    <span style="text-align: center">
                                        <label></label>
                                        <input class="btn btn-default btn-sm" id="btn_modificar_dia" type="button"
                                               value="Modificar Día" onclick="modificarDia()">
                                        <input type="hidden" name="idCitaMod" id="idCitaMod">
                                    </span>
                                </c:if>
                            </span>

                            <c:if test="${requestScope.cita_modificada != null}">
                                <c:set var="citaMod" scope="page" value="${requestScope.cita_modificada}" />
                                <label class="control-label" style="margin-top: 10px;">En sustitución de:</label>
                                <span class="list-group-item list-group-item-danger" id="cita_modificada">
                                     <ul class="list-group">
                                <li class="list-group-item reduced_li">
                                    Paciente:
                                    <span style="font-weight: bold;">
                                        ${citaMod.paciente.nombre} ${citaMod.paciente.apellidos}
                                    </span>
                                </li>
                                <li class="list-group-item reduced_li">
                                    Médico:
                                    <span style="font-weight: bold;">
                                        ${citaMod.medico.nombre} ${citaMod.medico.apellidos}
                                    </span>
                                </li>
                                <li class="list-group-item reduced_li">
                                    Especialidad:
                                    <span style="font-weight: bold;">
                                            ${citaMod.especialidad.nombre}
                                    </span>
                                </li>
                                <li class="list-group-item reduced_li">
                                    Fecha y hora:
                                    <span style="font-weight: bold;">
                                        <fmt:formatDate value="${citaMod.fecha}" pattern="dd/MM/yyyy"/>
                                    </span>
                                    &nbsp;&nbsp;&nbsp;&nbsp;Hora:
                                    <span style="font-weight: bold;">
                                        <fmt:formatDate value="${citaMod.fecha}" pattern="HH:mm"/>
                                    </span>
                                </li>
                            </ul>
                            </c:if>


                    </c:when>
                </c:choose>
            </div>
        </div>

    </form>

</div>

<jsp:include page="templates/clifis_pie.jsp" flush="true"/>

<%@include file="templates/clifis_end.jsp" %>