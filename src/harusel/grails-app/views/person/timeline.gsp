<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.commons.lang.StringUtils; org.codehaus.groovy.grails.commons.ConfigurationHolder; com.ocpsoft.pretty.time.TimeUnit; com.ocpsoft.pretty.time.PrettyTime; common.TimelinePeriod; domain.event.StageChangedEvent; domain.event.ScoreSheetEvent; domain.event.NoteEvent; domain.event.GenericDataEvent; domain.event.DocumentRemovedEvent; domain.event.DocumentAddedEvent; domain.event.CreateEvent; domain.event.PersonEvent" %>

<g:if test="${!events}">
    <g:message code="timeline.no.events"/>
</g:if>

<div class="style_paging">
    <g:if test="${pageStep<eventsTotalCount}">
        <g:if test="${currentSize > pageStep}">
            <a class="style_pagingBack" href="#" onclick="timelinePaginate(${max - pageStep}, ${person.id});" title="<g:message code="common.label.newest"/>"></a>
        </g:if>
        <div class="style_pagingView">[1 ... ${currentSize}]</div>
        <g:if test="${currentSize < eventsTotalCount}">
            <a class="style_pagingFrwrd" href="#" onclick="timelinePaginate(${max + pageStep}, ${person.id});" title="<g:message code="common.label.older"/>"></a>
        </g:if>
    </g:if>
</div>

<div class="clear"></div>

<table class="style_timelineTable">
    <g:set var="curPeriodCode" value=""/>

    <g:each in="${events}" var="event" status="i">
        <g:if test="${prettyTime.getCode(event.getDate()) != curPeriodCode}">
            <g:set var="curPeriodCode" value="${prettyTime.getCode(event.getDate())}"/>
            <tr>
                <th colspan='2'>
                    <g:message code="${curPeriodCode}"/>
                </th>
            </tr>
        </g:if>
        <tr>
            <td class="style_timelineTableData">
                <g:formatDate format="MMM dd, yyyy HH:mm" date="${event.getDate()}"/>
            </td>
            <td class="note_cell">
                %{-- Template name is event class name with first letter in lower case --}%
                <g:render template="event/${StringUtils.uncapitalize(event.class.simpleName)}" model="['event': event]"/>
            </td>
        </tr>
    </g:each>
</table>
