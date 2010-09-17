<%@ page contentType="text/html;charset=UTF-8" %>
<fieldset style="padding: 3px 3px;">
    <g:message code="reports.tab.vacancyStatistics.vacancies.label"/>
    <g:link style="display:none;" action="exportVacanciesReport.xml" class="linkButton ui-state-default ui-corner-all style_userInfoButton" target="_blank"/>
    <button id="getReportButton" name="export" class="ui-state-default ui-corner-all style_userInfoButton" style="float: right;">
        <g:message code="common.button.download"/>
    </button>
</fieldset>

<br/>
<fieldset style="padding: 3px 3px;">
    <g:message code="reports.tab.vacancyStatistics.sources.label"/><br/>
    <g:form action="exportSourcesReport.xml" method="post">
        <div style="line-height: 30px; height:30px;">
            <g:message code="reports.tab.vacancyStatistics.sources.period1"/>
            <g:datePicker name="dateFrom" precision="day" value="${dateFromInit}"/>
            <g:message code="reports.tab.vacancyStatistics.sources.period2"/>
            <g:datePicker name="dateTo" precision="day" value="${new Date()}"/>
            <g:submitButton name="export" value="${message(code:'common.button.download')}" class="ui-state-default ui-corner-all style_userInfoButton" style="float: right;"/>
        </div>
    </g:form>
</fieldset>
