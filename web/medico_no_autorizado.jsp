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
        <span id="titulo"></span>
    </clifis:titulo>

    <div class="row" id="ppal">
        <div class="col-sm-12  clifis_scrollable">
            <p>
                &nbsp;
            </p>

            <p>
                Los médicos no tienen autorización para usar esta opción.
            </p>
        </div>
    </div>
</div>

<jsp:include page="templates/clifis_pie.jsp" flush="true"/>

<%@include file="templates/clifis_end.jsp" %>