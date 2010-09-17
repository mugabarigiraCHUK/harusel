<%@ page contentType="text/html;charset=UTF-8" %>
<!--
<g:javascript library="jquery"/>
-->
<div id="sourcePane">
    <div class="buttonsWidth70">
        <g:remoteLink title="${g.message(code:"preferances.sources.add.title")}" controller="source" action="create" update="sourceDomainDetails" class="linkButton ui-state-default ui-corner-all">
            <g:message code="preferances.sources.add"/>
        </g:remoteLink>
        <a href="#" title="${g.message(code: "preferances.sources.deactivate.title")}" class="linkButton ui-state-default ui-corner-all" id="sourceDeactivate">
            <g:message code="preferances.sources.deactivate"/>
        </a>
    </div>
    <div class="clear"></div>
    <div id="sourceDomainDetails">
        <g:render template='../source/list' model='${[sourceList: sourceList]}'/>
    </div>
</div>
