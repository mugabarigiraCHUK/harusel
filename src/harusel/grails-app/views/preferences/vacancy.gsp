<%@ page contentType="text/html;charset=UTF-8" %>
<!--
<g:javascript library="jquery"/>
-->
<div id="vacancyPane">
    <div class="buttonsWidth70">
        <g:remoteLink title="${g.message(code:"preferances.vacancy.add.title")}" controller="vacancy" action="create" update="vacancyDomainDetails" class="linkButton ui-state-default ui-corner-all">
            <g:message code="preferances.vacancy.add"/>
        </g:remoteLink>
        <a href="#" title="${g.message(code: "preferances.vacancy.deactivate.title")}" class="linkButton ui-state-default ui-corner-all" id="vacancyDeactivate">
            <g:message code="preferances.vacancy.deactivate"/>
        </a>
    </div>
    <div class="clear"></div>
    <div id="vacancyDomainDetails">
        <g:render template='../vacancy/list' model='${[vacancyList: vacancyList]}'/>
    </div>
</div>
