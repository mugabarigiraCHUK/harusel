<%@ page import="domain.event.ScoreSheetEvent" %>
<div onclick="$('#tabPane').tabs('select', 3)">
    <g:message code="timeline.mark.label" args="[ event?.user?.login, event?.score?.name]"/>
    <br/>
    ${fieldValue(bean: event?.score, field: "comment")}
</div>