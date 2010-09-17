<%@ page import="domain.event.StageChangedEvent" %>
<div>
    <g:set var="stageFromName">
        <g:message code="stage.${event.from.codeName}"/>
    </g:set>
    <g:set var="stageToName">
        <g:message code="stage.${event.to.codeName}"/>
    </g:set>
    <g:message code="timeline.stage.changed.label" args="[ event.user.login, stageFromName, stageToName ]"/>
    <br/>
    ${fieldValue(bean: event, field: "comment")}

</div>