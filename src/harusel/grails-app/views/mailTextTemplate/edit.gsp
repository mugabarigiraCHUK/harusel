<%@ page import="domain.MailTextTemplate" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit MailTextTemplate</title>
    <g:each in="${[
    'jquery/jquery-1.3.2.js',
    'ckeditor/ckeditor.js',
    'ckeditor/adapters/jquery.js',
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
    <h1>Edit MailTextTemplate</h1>
    <g:if test="${flash.message}">
        <div class="message">
            <div class="message-icon"></div>
            ${flash.message}
        </div>
    </g:if>
    <g:hasErrors bean="${mailTextTemplateInstance}">
        <div class="errors">
            <div class="error-icon"></div>
            <g:renderErrors bean="${mailTextTemplateInstance}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form method="post" class="retrieveCKEditorBeforeSubmit">
        <input type="hidden" name="id" value="${mailTextTemplateInstance?.id}"/>
        <input type="hidden" name="version" value="${mailTextTemplateInstance?.version}"/>
        <div class="dialog">
            <table>
                <tbody>
                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="groupName">Group Name:</label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: mailTextTemplateInstance, field: 'groupName', 'errors')}">
                        <input type="text" id="groupName" name="groupName" value="${fieldValue(bean: mailTextTemplateInstance, field: 'groupName')}"/>
                    </td>
                </tr>


                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="name">Name:</label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: mailTextTemplateInstance, field: 'name', 'errors')}">
                        <input type="text" id="name" name="name" value="${fieldValue(bean: mailTextTemplateInstance, field: 'name')}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="subject">Subject:</label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: mailTextTemplateInstance, field: 'subject', 'errors')}">
                        <input type="text" id="subject" name="subject" value="${fieldValue(bean: mailTextTemplateInstance, field: 'subject')}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="body">Body:</label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: mailTextTemplateInstance, field: 'body', 'errors')}">
                        <div id="mailTextEditor"></div>
                        <textarea style="display:none;" name="body" id="mailText">${fieldValue(bean: mailTextTemplateInstance, field: 'body')}</textarea>
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
