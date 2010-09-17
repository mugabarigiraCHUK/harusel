<%@ page import="domain.event.NoteEvent" %>
<div>
    <span style="font-weight: bold;">${fieldValue(bean: event.user, field: "login")}:</span>
    <span style="font-style: italic;">
        <g:set var="stageName">
            <g:message code="stage.${event.decidedStage.codeName}"/>
        </g:set>
        <g:message code="timeline.person.made.decision" args="${[stageName]}"/>
    </span>
    <br/>
    <div class="textOverflowContainer">
        <div>
            <span class="text-overflow">${g.fieldValue(bean: event, field: 'comment')}</span>
        </div>
    </div>
</div>