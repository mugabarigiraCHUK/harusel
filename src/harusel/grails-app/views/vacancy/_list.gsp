<!--
<g:javascript library='jquery'/>
-->

<g:if test="${flash.message}">
    <div class="notify">${flash.message}</div>
</g:if>
<table id="vacancyListTable" class="style_mainList">
    <thead>
    <tr>
        <th class="checkboxCell">&nbsp;</th>
        <th><g:message code="domain.name"/></th>
        <th><g:message code="domain.active"/></th>
        <th><g:message code="domain.notes"/></th>
    </tr>
    </thead>
    <tbody>
    <g:each in="${vacancyList}" status="i" var="vacancy">
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
            <td class="checkboxCell">
                <input type="checkbox" id='${vacancy.id}' name='id'>
            </td>
            <td align="left">
                <g:remoteLink update="vacancyDomainDetails" action="edit" controller='vacancy' id="${vacancy.id}">${fieldValue(bean: vacancy, field: 'name')}</g:remoteLink>
            </td>
            <td align="center">
                <g:if test="${vacancy.active}">
                    <g:message code="domain.state.active"/>
                </g:if>
                <g:else>
                    <g:message code="domain.state.closed"/>
                </g:else>
            </td>
            <td align="left">
                ${fieldValue(bean: vacancy, field: 'description')}
            </td>
        </tr>
    </g:each>
    </tbody>
</table>
