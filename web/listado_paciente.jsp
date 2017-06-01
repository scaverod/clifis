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

<div class="container clifis_container">
    <clifis:titulo>
        Listado de paciente
    </clifis:titulo>

    <div class="row">
        <div class="col-sm-12 clifis_scrollable">
            <table class="table tablesorter-bootstrap" id="tabla_pacientes">
                <thead>
                <tr>
                    <th>Nombre</th>
                    <th>Apellidos</th>
                    <th>DNI</th>
                    <th>Aseguradora</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach items="${applicationScope.gestor_pacientes.pacientes}" var="paciente" varStatus="loop">
                    <tr>
                        <td>${paciente.nombre}</td>
                        <td>${paciente.apellidos}</td>
                        <td>${paciente.dni}</td>
                        <td>${paciente.aseguradora}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="templates/clifis_pie.jsp" flush="true"/>

<%@include file="templates/clifis_end.jsp" %>
<script>
    $(document).ready(function() {
        $(function () {
            $("#tabla_pacientes").tablesorter(
                {
                    theme: 'bootstrap',
                    headerTemplate: '{content} {icon}',
                    widgets: ['uitheme', 'zebra', 'scroller'],
                    widgetOptions: {
                        scroller_height: 300
                    },
                    dateFormat: "ddmmyyyy",
                    headers: {

                    }
                }
            );
        });
    });
</script>

