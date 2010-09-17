<%@ page import="domain.filter.FilterAction" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit FilterAction</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">FilterAction List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New FilterAction</g:link></span>
</div>
<div class="body">
    <h1>Edit FilterAction</h1>
    <g:if test="${flash.message}">
        <div class="message">
            <div class="message-icon"></div>
            ${flash.message}
        </div>
    </g:if>
    <g:hasErrors bean="${filterActionInstance}">
        <div class="errors">
            <div class="error-icon"></div>
            <g:renderErrors bean="${filterActionInstance}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form method="post">
        <input type="hidden" name="id" value="${filterActionInstance?.id}"/>
        <input type="hidden" name="version" value="${filterActionInstance?.version}"/>
        <div class="dialog">
            <table>
                <tbody>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="description">Description:</label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: filterActionInstance, field: 'description', 'errors')}">
                        <input type="text" id="description" name="description" value="${fieldValue(bean: filterActionInstance, field: 'description')}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="hql">Critera Closure:</label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: filterActionInstance, field: 'query', 'errors')}">
                        <input type="text" id="hql" name="hql" value="${fieldValue(bean: filterActionInstance, field: 'query')}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="name">Name:</label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: filterActionInstance, field: 'name', 'errors')}">
                        <input type="text" id="name" name="name" value="${fieldValue(bean: filterActionInstance, field: 'name')}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="active">Active:</label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: filterActionInstance, field: 'active', 'errors')}">
                        <g:checkBox name="active" value="${filterActionInstance?.active}"></g:checkBox>
                    </td>
                </tr>

                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button"><g:actionSubmit class="save" value="Update"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </div>
    </g:form>
</div>
</body>
</html>
