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

<%@ page pageEncoding="utf-8" contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://clifis.etsisi.upm.es/tagslib" prefix="clifis" %>

<c:if test="${pageContext.request.requestURI ne '/' and pageContext.request.requestURI ne '/index.jsp' and pageContext.request.requestURI ne '/process_login'}">
    <c:if test="${sessionScope.usuario == null}">
        <jsp:forward page="/index.jsp" />
    </c:if>
</c:if>

<html>

<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Clifis</title>

    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="styling/js/jquery-3.2.0.min.js"></script>

    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script src="styling/js/bootstrap.min.js"></script>

    <!-- Tablesorter stuff -->
    <script type="text/javascript" src="styling/js/jquery.tablesorter.min.js"></script>
    <script type="text/javascript" src="styling/js/jquery.tablesorter.widgets.min.js"></script>
    <script type="text/javascript" src="styling/js/widget-scroller.min.js"></script>
    <script type="text/javascript" src="styling/js/widget-cssStickyHeaders.min.js"></script>
    <script type="text/javascript" src="styling/js/bootstrap-select.js"></script>
    <script type="text/javascript" src="styling/js/bootstrap-clockpicker.min.js"></script>
    <script type="text/javascript" src="styling/js/bootstrap-waitingfor.min.js"></script>
    <script type="text/javascript" src="styling/datepicker/js/bootstrap-datepicker.min.js"></script>
    <script type="text/javascript" src="styling/datepicker/locales/bootstrap-datepicker.es.min.js"></script>

    <!-- Bootstrap -->
    <link href="styling/css/bootstrap.min.css" rel="stylesheet">
    <link href="styling/css/theme.bootstrap_3.min.css" rel="stylesheet">
    <link href="styling/css/bootstrap-select.css" rel="stylesheet">
    <link href="styling/css/bootstrap-clockpicker.css" rel="stylesheet">
    <link href="styling/datepicker/css/bootstrap-datepicker3.css" rel="stylesheet">

    <link href="styling/css/clifis.css" rel="stylesheet">

    <!-- Fuentes -->
    <link href="https://fonts.googleapis.com/css?family=Josefin+Slab" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css?family=Raleway" rel="stylesheet">
</head>
