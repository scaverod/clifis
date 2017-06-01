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
        HTTP ERROR 405: No se admite esta petición.
    </clifis:titulo>

    <div class="row">
        <div class="col-sm-12  clifis_scrollable">
            <br/>
            <p>
                No sabemos muy bien como has llegado hasta aquí, pero lo que buscas no existe.
            </p>
            <p>
                Puedes usar el menú de arriba para ir a donde te interese. Si no, tienes varias opciones:
            </p>
            <ul class="list-unstyled">
                <li style="margin-left: 25px; padding-top: 10px;">Quedarte aquí indefinidamente.</li>
                <li style="margin-left: 25px; padding-top: 10px;">Volver a la <a class="btn btn-default" href="/"
                                                                                 role="button">página principal</a></li>
                <li style="margin-left: 25px; padding-top: 10px;">Ir a <a class="btn btn-default"
                                                                          href="http://coderfacts.com/" role="button">otro
                    sitio</a></li>
            </ul>
            <br/>
            <p>
                ¡Hasta la vista!
            </p>
        </div>
    </div>
</div>

<jsp:include page="templates/clifis_pie.jsp" flush="true"/>

<%@include file="templates/clifis_end.jsp" %>
