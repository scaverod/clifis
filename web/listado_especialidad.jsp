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

<jsp:include page="templates/clifis_cabecera.jsp" flush="true" />

<script>
    $(document).ready(function(){
        $(function() {
            $("#tabla_especialidades").tablesorter(
                {
                    theme: 'bootstrap',
                    headerTemplate: '{content} {icon}',
                    widgets: ['uitheme', 'zebra', 'scroller'],
                    widgetOptions : {
                        scroller_height: 450
                    },
                    dateFormat: "ddmmyyyy",
                    headers: {
                        2: { sorter: "shortDate" },
                    }
                });
        });

    });
</script>

<jsp:include page="templates/clifis_menu.jsp" flush="true" />

<div class="container clifis_container">
    <clifis:titulo>
        Listado de especialidades
    </clifis:titulo>

    <div class="row">
        <div class="col-sm-12 clifis_scrollable">
            <table class="table tablesorter-bootstrap" id="tabla_especialidades">
                <thead>
                <tr>
                    <th>Nombre de Especialidad</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach items="${applicationScope.gestor_especialidades.especialidades}" var="especialidades">
                    <tr>
                        <td>${especialidades.nombre}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="templates/clifis_pie.jsp" flush="true" />

<%@include file="templates/clifis_end.jsp"%>

