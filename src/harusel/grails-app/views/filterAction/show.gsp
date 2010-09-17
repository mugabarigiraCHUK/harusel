<%@ page import="domain.filter.FilterAction" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show FilterAction</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">FilterAction List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New FilterAction</g:link></span>
</div>
<div class="body">
    <h1>Show FilterAction</h1>
    <g:if test="${flash.message}">
        <div class="message">
            <div class="message-icon"></div>
            ${flash.message}
        </div>
    </g:if>
    <div class="dialog">
        <table>
            <tbody>

            <tr class="prop">
                <td valign="top" class="name">Id:</td>

                <td valign="top" class="value">${fieldValue(bean: filterActionInstance, field: 'id')}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name">Description:</td>

                <td valign="top" class="value">${fieldValue(bean: filterActionInstance, field: 'description')}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name">Critera Closure:</td>

                <td valign="top" class="value">${fieldValue(bean: filterActionInstance, field: 'query')}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name">Name:</td>

                <td valign="top" class="value">${fieldValue(bean: filterActionInstance, field: 'name')}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name">Active:</td>

                <td valign="top" class="value">${fieldValue(bean: filterActionInstance, field: 'active')}</td>

            </tr>

            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${filterActionInstance?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
