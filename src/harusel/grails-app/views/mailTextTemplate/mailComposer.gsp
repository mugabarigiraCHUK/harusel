<%@ page contentType="text/html;charset=UTF-8" %>
<div title="dialog" class="mailComposer">
<g:form action="sendMail">
    <g:hiddenField name="personId" value="${person.id}"/>
    <table>
        <tr>
            <td><g:message code="dialog.selectMail.to"/></td>
            <td class="fieldCell"><g:textField id="mailTo" name="to" value="${person.emails}" class="field required email"/></td>
        </tr>
        <tr>
            <td><g:message code="dialog.selectMail.subject"/></td>
            <td  class="fieldCell"><g:textField id="mailSubject" name="subject" value="${template.subject}" class="field required"/></td>
        </tr>
        <tr>
            <td colspan="2"><g:message code="dialog.selectMail.body"/></td>
        </tr>
        <tr>
            <td colspan="2">
                <div id="mailTextEditor"></div>
            </td>
        </tr>
    </table>
    <g:textArea id="mailText" name="text" value="${template.body}" style="display: none;"/>
</g:form>
</div>