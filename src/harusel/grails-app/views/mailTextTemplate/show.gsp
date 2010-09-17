<%@ page import="org.apache.commons.lang.StringEscapeUtils; domain.MailTextTemplate" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show MailTextTemplate</title>
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
    <span class="menuButton"><g:link class="list" action="list">MailTextTemplate List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New MailTextTemplate</g:link></span>
</div>
<div class="body">
    <h1>Show MailTextTemplate</h1>
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

                <td valign="top" class="value">${fieldValue(bean: mailTextTemplateInstance, field: 'id')}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name">Name:</td>

                <td valign="top" class="value">${fieldValue(bean: mailTextTemplateInstance, field: 'name')}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name">Group Name:</td>

                <td valign="top" class="value">${fieldValue(bean: mailTextTemplateInstance, field: 'groupName')}</td>

            </tr>


            <tr class="prop">
                <td valign="top" class="name">Subject:</td>

                <td valign="top" class="value">${fieldValue(bean: mailTextTemplateInstance, field: 'subject')}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name">Body:</td>

                <td valign="top" class="value">
                    <div id="showTemplateBody">
                        <input type="hidden" class="htmlTextHolder" value="${mailTextTemplateInstance.body}"/>
                        <span></span>
                    </div>
                </td>

            </tr>

            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${mailTextTemplateInstance?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
