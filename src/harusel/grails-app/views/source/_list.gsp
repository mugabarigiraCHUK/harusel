<!--
<g:javascript library='jquery'/>
-->

<g:if test="${flash.message}">
    <div class="notify">${flash.message}</div>
</g:if>
<table id="sourceListTable" class="style_mainList">
    <thead>
    <tr>
        <th class="checkboxCell">&nbsp;</th>
        <th><g:message code="domain.name"/></th>
        <th><g:message code="domain.active"/></th>
        <th><g:message code="domain.agreement"/></th>
    </tr>
    </thead>
    <tbody>
    <g:each in="${sourceList}" status="i" var="source">
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
            <td class="checkboxCell">
                <input type="checkbox" id='${source.id}' name='id'>
            </td>
            <td>
                <g:remoteLink update="sourceDomainDetails" action="edit" controller='source' id="${source.id}">${fieldValue(bean: source, field: 'name')}</g:remoteLink>
            </td>
            <td>
                <g:if test="${source.active}">
                    <g:message code="domain.state.active"/>
                </g:if>
                <g:else>
                    <g:message code="domain.state.closed"/>
                </g:else>
            </td>
            <td>
                ${fieldValue(bean: source, field: 'agreement')}
            </td>
        </tr>
    </g:each>
    </tbody>
</table>