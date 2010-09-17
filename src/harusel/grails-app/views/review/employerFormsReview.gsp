<%@ page contentType="text/html;charset=UTF-8" %>
<g:if test="${request.messages}">
    <div class="notify">
        ${request.messages.join(", ").encodeAsHTML()}
    </div>
</g:if>
<g:if test="${userTimelineMap.size()>0}">
    <div class="timelineTable">
        <div class="numberCol tableHeader">#</div>
        <div class="fullNameCol tableHeader"><g:message code="performanceReview.tab.assigningTemplate.fullName"/></div>
        <div class="timelineCol tableHeader"><g:message code="performanceReview.title.timeline"/></div>
        <div class="userList">
            <g:each var="userEntry" in="${userTimelineMap}" status="index">
                <g:set var="oddClass" value="${index % 2 == 0 ? '' : 'odd'}"/>
                <div class="${oddClass} numberCol row">
                    ${index + 1}
                </div>
                <div class="${oddClass} fullNameCol row">
                    ${g.fieldValue(bean: userEntry.key, field: 'fullName')}
                </div>
            </g:each>
        </div>
        <div class="timelinesList">
            <g:each var="userEntry" in="${userTimelineMap}" status="index">
                <g:set var="oddClass" value="${index % 2 == 0 ? '' : 'odd'}"/>
                <div class="timelineRow ${oddClass} row" style="width: ${timelineWidth}px;">
                    <g:each var="mark" in="${userEntry.value}">
                        <g:set var="tooltipText"><g:message code="performanceReview.timeline.markPopup.text" args="${[mark?.text?.encodeAsHTML()]}"/></g:set>
                        <div style="width: ${mark.offset}px; float: left; height: 20px;"></div>
                        <g:link controller="review" action="reviewForm" target="_blank" id="${mark.formId}">
                            <p:image class="mark" src="timelineMark.png" title="${tooltipText}"/>
                        </g:link>
                    </g:each>
                </div>
            </g:each>
        </div>
        <div style="clear: both;"></div>
    </div>
</g:if>
<g:form action="export.xml" method="post">
    <div style="line-height: 35px; height: 40px">
        <div style="float: left;">
            <g:message code="performanceReview.export.period1"/>
            <g:datePicker name="dateFrom" precision="day" value="${dateFromInit}"/>
            <g:message code="performanceReview.export.period2"/>
            <g:datePicker name="dateTo" precision="day" value="${new Date()}"/>
        </div>
        <g:submitButton name="export" value="${message(code:'common.button.export')}" class="ui-state-default ui-corner-all style_userInfoButton" style="float:right;"/>
    </div>
</g:form>
