<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.ocpsoft.pretty.time.TimeUnit; com.ocpsoft.pretty.time.PrettyTime; common.TimelinePeriod; domain.event.StageChangedEvent; domain.event.ScoreSheetEvent; domain.event.NoteEvent; domain.event.GenericDataEvent; domain.event.DocumentRemovedEvent; domain.event.DocumentAddedEvent; domain.event.CreateEvent; domain.event.PersonEvent" %>
<g:if test="${!notes}">
    <g:message code="notes.no.notes"/>
</g:if>
<table class="style_notesTable">
    <g:set var="curPeriodCode" value=""/>

    <g:each in="${notes}" var="note">

        <g:if test="${prettyTime.getCode(note.getDate())!=curPeriodCode}">
            <g:set var="curPeriodCode" value="${prettyTime.getCode(note.getDate())}"/>
            <tr>
                <th colspan="3">
                    <g:message code="${curPeriodCode}"/>
                </th>
            </tr>
        </g:if>
        <tr>
            <td class="style_notesTableData">
                <g:formatDate format="MMM dd, yyyy HH:mm" date="${note.getDate()}"/>
            </td>
            <td>
                <b>${fieldValue(bean: note?.user, field: "login")}</b>
            </td>
            <td>
                ${fieldValue(bean: note, field: "text")}
            </td>
        </tr>
    </g:each>
</table>