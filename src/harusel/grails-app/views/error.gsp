<%@ page import="domain.person.Person; org.apache.log4j.Logger" %>
<html>
<head>
    <title><g:message code="title.page.error"/></title>
    <style type="text/css">
    .error {
        background-color: #ffeedd;
        border: #dd4455 solid 1px;
        padding: 15px;
    }

    .errorList {
        border: #dd4455 solid 1px;
        background-color: #eecccc;
        padding: 10px;
    }

    .errorList li {
        list-style-type: none;
    }

    .goHomeLink {
        background-color: #eeffee;
        text-decoration: none;
        color: black;
    }


    </style>
</head>

<body>
<h1><g:message code="title.page.error"/></h1>
<div class="error">
    <g:message code="title.message.error"/>
    <g:if test="${flash.errors}">
        <ul class="errorList">
            <g:each in="${flash.errors}" var="error">
                <li>${error}</li>
            </g:each>
        </ul>
    </g:if>
</div>

<g:link controller="defaultPage" class="goHomeLink"><g:message code="title.link.goHome"/></g:link>
<%
    Logger logger = Logger.getLogger("grails.app")
    if (exception) {
        logger.error(exception.message, exception)
    }
%>
</body>
</html>