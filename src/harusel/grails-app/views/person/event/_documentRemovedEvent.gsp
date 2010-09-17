<%@ page import="domain.event.DocumentRemovedEvent" %>
<div>
    <g:set var="docType">
        <g:if test="${event.document.type.privileged}">
            <g:message code="document.type.${g.fieldValue(bean: event.document?.type, field: 'typeCode')}"/>
        </g:if>
        <g:else>
            ${g.fieldValue(bean: event.document?.type, field: 'name')}
        </g:else>
    </g:set>
    <span onclick="$('#tabPane').tabs('select', 2);">
        <g:message code="timeline.document.removed.label" args="[ event?.user?.login, docType]"/>
    </span>
    <br/>
    <i>${fieldValue(bean: event?.document, field: "name")}</i>
</div>