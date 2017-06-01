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

<%@include file="templates/clifis_head.jsp"%>

<c:if test="${sessionScope.usuario.id != 0}">
    <jsp:forward page="/medico_no_autorizado.jsp" />
</c:if>

<jsp:include page="templates/clifis_cabecera.jsp" flush="true" />

<jsp:include page="templates/clifis_menu.jsp" flush="true" />

<div class="container clifis_container" >
    <clifis:titulo>
        Alta de especialidades
    </clifis:titulo>

    <div class="row">
        <!-- Formulaespecialidadesidad nueva. -->
        <div class="col-sm-6 clifis_scrollable">
            <form method="post" action="process_alta_especialidad">
                <div class="form-group">
                    <label class="control-label col-md-6" for="nombre">Nombre de la nueva especialidades:</label>
                    <input class="form-control" type="text" name="nombre" id="nombre">
                </div>
                <input type="submit" value="Enviar" id="Submit" class="btn btn-default"/>
            </form>
        </div>

        <div class="col-sm-6 clifis_scrollable">
            <c:choose>
                <c:when test="${requestScope.gestorException != null}">
                    <label class="control-label" for="excepcion">No se ha podido dar de alta esa especialidades:</label>
                    <span class="list-group-item list-group-item-danger" id="excepcion">
                        <%--@elvariable id="gestorException" type="es.upm.etsisi.clifis.gestores.GestorException"--%>
                        ${gestorException.message}
                    </span>
                    <br/>
                </c:when>
                <c:when test="${requestScope.especialidad != null}">
                    <label class="control-label" for="nueva_especialidad">Nueva especialidades dada de alta:</label>
                    <span class="list-group-item list-group-item-success" id="nueva_especialidad">
                        <%--@elvariable id="especialidad" type="es.upm.etsisi.clifis.model.Paciente"--%>
                        ${especialidad.nombre}
                    </span>
                    <br/>
                </c:when>
            </c:choose>

            <!-- Lista que muestra las especialidades. -->
            <%-- TODO: Pasar lista a tablesorter ¿? --%>
            <label class="control-label" for="lista_especialidades">Especialidades existentes:</label>
            <c:if test="${empty applicationScope.gestor_especialidades.especialidades}">
                <br />
                <p>
                    No exsite ninguna especialidades ahora mismo.
                </p>
            </c:if>
            <ul class="list-group" id="lista_especialidades">
                <c:forEach items="${applicationScope.gestor_especialidades.especialidades}" var="especialidad_id">
                    <li class="list-group-item">${especialidad_id.nombre}</li>
                </c:forEach>
            </ul>
        </div>
    </div>
</div>

<jsp:include page="templates/clifis_pie.jsp" flush="true" />

<%@include file="templates/clifis_end.jsp"%>
