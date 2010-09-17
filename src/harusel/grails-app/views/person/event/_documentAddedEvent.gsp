<%@ page import="domain.event.DocumentAddedEvent" %>
<div>
    <span onclick="$('#tabPane').tabs('select', 2);">
        <g:set var="docType">
            <g:if test="${event.document.type.privileged}">
                <g:message code="document.type.${g.fieldValue(bean: event.document.type, field: 'typeCode')}"/>
            </g:if>
            <g:else>
                ${g.fieldValue(bean: event.document.type, field: 'name')}
            </g:else>
        </g:set>
        <g:message code="timeline.document.added.label" args="[ event.user.login, docType]"/>
    </span>
    <br/>
    <g:if test="${event.document.removed}">
        <i>${fieldValue(bean: event?.document, field: "name")}</i>
    </g:if>
    <g:else>
        <g:link
          controller="document"
          action="download"
          params="[id:event?.document?.id]">
            ${fieldValue(bean: event?.document, field: "name")}
        </g:link>
    </g:else>
</div>