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

<jsp:include page="templates/clifis_menu.jsp" flush="true" />

<div class="container clifis_container" >
    <clifis:titulo>
        ERROR GRAVE.
    </clifis:titulo>

    <div class="row">
        <div class="col-sm-12  clifis_scrollable">
            <br />
            <p>
                No te agobies. Algo ha ido mal y tendrás que informar a los de mantenimiento para que lo arreglen.
                Pásales esta información por si la necesitan:
            </p>

            <br />
            <%
                // Analyze the servlet exception
                String nombreExecepcion = request.getAttribute("javax.servlet.error.exception").getClass().getName();
                if (nombreExecepcion == null){
                    nombreExecepcion = "Desconocido";
                }

                String statusCode = request.getAttribute("javax.servlet.error.status_code").toString();
                if (statusCode == null){
                    statusCode = "Desconocido";
                }

                String servletName = (String) request.getAttribute("javax.servlet.error.servlet_name");
                if (servletName == null){
                    servletName = "Desconocido";
                }
                String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");
                if (requestUri == null){
                    requestUri = "Desconocido";
                }
            %>
            <ul class="list-unstyled">
                <li style="margin-left: 25px;"><b>Tipo de Excepción</b>: <%= nombreExecepcion %></li>
                <li style="margin-left: 25px;"><b>Estado:</b> <%= statusCode %></li>
                <li style="margin-left: 25px;"><b>Nombre del servlet:</b> <%= servletName %></li>
                <li style="margin-left: 25px;"><b>Uri:</b> <%= requestUri %></li>
            </ul>
            <br />
            <p>
                ¡Hasta la vista!
            </p>
        </div>
    </div>
</div>

<jsp:include page="templates/clifis_pie.jsp" flush="true" />

<%@include file="templates/clifis_end.jsp"%>
