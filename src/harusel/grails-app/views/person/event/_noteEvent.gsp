<%@ page import="domain.event.NoteEvent" %>
<g:message code="timeline.comment" args="[event?.user?.login]"/><br/>
<div class="textOverflowContainer">
    <div>
        <span class="text-overflow">${g.fieldValue(bean: event?.note, field: 'text')}</span>
    </div>
</div>
