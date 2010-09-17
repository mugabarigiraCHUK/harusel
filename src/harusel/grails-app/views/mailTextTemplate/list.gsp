<%@ page import="domain.MailTextTemplate" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>MailTextTemplate List</title>
    <g:each in="${[
    'jquery/jquery-1.3.2.js',
    'jquery/jquery.livequery.js',
    'ckeditor/ckeditor.js',
    'ckeditor/adapters/jquery.js',
    'application.js',
    'mailTemplate.js',
    ]}">
        <g:javascript src="${it}"/>
    </g:each>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
    <span class="menuButton"><g:link class="create" action="create">New MailTextTemplate</g:link></span>
</div>
<div class="body">
    <h1>MailTextTemplate List</h1>
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

                <g:sortableColumn property="name" title="Name"/>

                <g:sortableColumn property="subject" title="Subject"/>

                <g:sortableColumn property="body" title="Body"/>

                <g:sortableColumn property="groupName" title="Group Name"/>

            </tr>
            </thead>
            <tbody>
            <g:each in="${mailTextTemplateInstanceList}" status="i" var="mailTextTemplateInstance">
                <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                    <td><g:link action="show" id="${mailTextTemplateInstance.id}">${fieldValue(bean: mailTextTemplateInstance, field: 'id')}</g:link></td>

                    <td>${fieldValue(bean: mailTextTemplateInstance, field: 'name')}</td>

                    <td>${fieldValue(bean: mailTextTemplateInstance, field: 'subject')}</td>

                    <td>
                        <input type="hidden" class="htmlTextHolder" value="${mailTextTemplateInstance.body}"/>
                        <span></span>
                    </td>

                    <td>${fieldValue(bean: mailTextTemplateInstance, field: 'groupName')}</td>

                </tr>
            </g:each>
            </tbody>
        </table>
    </div>
    <div class="paginateButtons">
        <g:paginate total="${mailTextTemplateInstanceTotal}"/>
    </div>
</div>
</body>
</html>
