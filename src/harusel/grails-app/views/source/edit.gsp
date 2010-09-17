<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="domain.PersonSource" %>

<!--
<g:javascript library='jquery'/>
-->

<div id="sourceView">
    <input type="hidden" name="id" value="${source.id}" id="sourceId">
    <g:remoteLink controller="source" action="list" update="sourceDomainDetails">
        &lt;&lt;&lt;<g:message code="source.edit.backToList"/>
    </g:remoteLink><br>
    <g:if test="${flash.message}">
        <div class="notify">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${source}">
        <div class="errors">
            <div class="error-icon"></div>
            <g:renderErrors bean="${source}" as="list"/>
        </div>
    </g:hasErrors>
    <table>
        <tr>
            <g:hasErrors bean="${source}" field="name">
                <g:set var="cssClass" value="error"/>
            </g:hasErrors>
            <td>
                <label for="name"><g:message code="source.edit.name"/></label>
                <input id="name" class="${cssClass}" type='text' name='name' value="${source.name?.encodeAsHTML()}">
            </td>
            <td>
                <label><g:message code="source.edit.state"/></label>
                <g:radioGroup name="active" value="${source.active}" labels='["domain.state.active","domain.state.closed"]' values="['true', 'false']">
                    <p><%=it.radio%> <g:message code="${it.label}"/></p>
                </g:radioGroup>
            </td>
        </tr>
        <tr>
            <td colspan='2'>
                <label><g:message code="source.edit.agreement"/></label><br>
                <textarea name="agreement" class='longTextArea'>${source.agreement?.encodeAsHTML()}</textarea>
            </td>
        </tr>
        <tr align='center'>
            <td style="text-align:center;">
                <button id="sourceEditCancel"><g:message code="common.button.cancel"/></button>
                <button id="sourceEditSave"><g:message code="common.button.save"/></button>
                <button id="sourceEditSaveAndClose"><g:message code="common.button.saveAndClose"/></button>
            </td>
        </tr>

    </table>
</div>
<script type="text/javascript">
    PreferenceManager.initSaveButtons("source");
</script>
