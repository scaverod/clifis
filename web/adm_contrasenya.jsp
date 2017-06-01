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
        Cambio Contraseña
    </clifis:titulo>
    <div class="row">
        <div class="col-sm-6 clifis_scrollable" style="margin-left: 10px;">
            <form method="post" action="/process_change_pwd">

                <%-- INICIO CAMBIO DE CONTRASEÑA --%>
                <%  String origen = (String)request.getAttribute("origen");

                    if (origen == null || origen.equals("A")) {
                        if (origen != null) {

                %>
                    <%-- PANEL SI LA CONTRASEÑA NO ES CORRECTA --%>
                <div class="panel panel-danger">
                    <div class="panel-heading">Error</div>
                    <div class="panel-body">La contraseña es incorrecta.</div>
                </div>
                <%
                    }
                %>
                <p>Buenos días ${sessionScope.usuario.nombre}. A continuación introduzca su contraseña actual para
                    cambiar su contraseña</p>
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" class="form-control" id="password" name="password" placeholder="Contraseña">
                    <input type="hidden" name="origen" value="A">
                </div>
                <div class="form-group" style="text-align: right; margin-top: 25px;">
                    <label for="enviar"></label>
                    <input type="submit" class="btn btn-basic" id="enviar" value="Autenticarse"
                           style="border-color: #3f3f3f;">
                </div>
            </form>
            <form method="post" action="/process_change_pwd">
                <%
                } else if (origen.equals("B")){
                    Boolean error = (Boolean) request.getAttribute("error");
                    if (error != null && error) {
                %>


                <%-- BLOQUE PEDIR CONTRASEÑA NUEVA --%>
                    <%-- BLOQUE SI CONTRASEÑAS NO COINCIDEN O VIENEN VACÍAS --%>
                <div class="panel panel-danger">
                    <div class="panel-heading">Error</div>
                    <div class="panel-body">Las nuevas contraseñas no coinciden o alguna está vacía.</div>
                </div>
                <%
                    }
                %>
                <p>A continuación introduzca su contraseña nueva: </p>
                <div class="form-group">
                    <label for="passwordN1">Conraseña nueva</label>
                    <input type="password" class="form-control" id="passwordN1" name="passwordN1"
                           placeholder="Contraseña">
                </div>
                <div class="form-group">
                    <label for="passwordN2">Repita la contraseña</label>
                    <input type="password" class="form-control" id="passwordN2" name="passwordN2"
                           placeholder="Contraseña">
                    <input type="hidden" name="origen" value="B">
                </div>
                <div class="form-group" style="text-align: right; margin-top: 25px;">
                    <label for="enviar2"></label>
                    <input type="submit" class="btn btn-basic" id="enviar2" value="Cambiar"
                           style="border-color: #3f3f3f;">
                </div>
                <%
                } else if(origen.equals("C")) {
                %>
                <%-- BLOQUE MENSAJE OK --%>
                    <div class="panel panel-success">
                        <div class="panel-heading">OK!</div>
                        <div class="panel-body">La contraseña ha sido actualizada correctamente.</div>
                    </div>
                <%
                    }
                %>
            </form>
        </div>
    </div>
</div>

<jsp:include page="templates/clifis_pie.jsp" flush="true"/>

<%@include file="templates/clifis_end.jsp" %>