<%@ page import="domain.filter.FilterAction" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>FilterAction List</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
    <span class="menuButton"><g:link class="create" action="create">New FilterAction</g:link></span>
</div>
<div class="body">
    <h1>FilterAction List</h1>
    <g:if test="${flash.message}">
        <div class="message">
            <div class="message-icon"></div>
            ${flash.message}
        </div>
    </g:if>
    <div class="list">
        <table>
            <thead>
            <tr>

                <g:sortableColumn property="id" title="Id"/>

                <g:sortableColumn property="description" title="Description"/>

                <g:sortableColumn property="criteraClosure" title="Critera Closure"/>

                <g:sortableColumn property="name" title="Name"/>

                <g:sortableColumn property="active" title="Active"/>

            </tr>
            </thead>
            <tbody>
            <g:each in="${filterActionInstanceList}" status="i" var="filterActionInstance">
                <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                    <td><g:link action="show" id="${filterActionInstance.id}">${fieldValue(bean: filterActionInstance, field: 'id')}</g:link></td>

                    <td>${fieldValue(bean: filterActionInstance, field: 'description')}</td>

                    <td>${fieldValue(bean: filterActionInstance, field: 'query')}</td>

                    <td>${fieldValue(bean: filterActionInstance, field: 'name')}</td>

                    <td>${fieldValue(bean: filterActionInstance, field: 'active')}</td>

                </tr>
            </g:each>
            </tbody>
        </table>
    </div>
    <div class="paginateButtons">
        <g:paginate total="${filterActionInstanceTotal}"/>
    </div>
</div>
</body>
</html>
