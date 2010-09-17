<%@ page contentType="text/html;charset=UTF-8" %>
<!-- <g:javascript library="jquery"/> -->

<%-- preference menu items --%>
<div class="style_leftMenuContent menu">
    <g:each in="${[
    [controller:'preferences', action: 'vacancy', title: message(code: 'preference.index.vacancies')],
    [controller:'preferences', action: 'criteria', title: message(code: 'preference.index.criterias')],
    [controller:'preferences', action: 'source', title: message(code: 'preference.index.sources')],
  ]}" var="tab">
        <div class="parentFilter menu-item">
            <h3>
                <g:remoteLink controller="${tab.controller}" action="${tab.action}" update="page-body">
                    ${tab.title}
                </g:remoteLink>
            </h3>
            <div class="clear"></div>
        </div>
    </g:each>
    <div>
        <h3>
            <g:link controller="mailTextTemplate" action="index" target="_blank">
                <g:message code='preference.index.mailTemplate'/>
            </g:link>
        </h3>
        <div class="clear"></div>
    </div>
</div>
