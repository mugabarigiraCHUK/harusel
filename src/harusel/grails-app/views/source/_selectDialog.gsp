<%@ page import="domain.PersonSource" %>
<div id='dialog' title="${message(code: 'dialog.chooseSource.title')}">
    <h1 id='title'><g:message code="dialog.chooseSource.header"/></h1>
    <div style="overflow: auto; height:300px;">
        <table>
            <g:each in="${sourceList}" var="source">
                <tr>
                    <td>
                        <g:set var="radioId" value="radioSource${source.id}"/>
                        <g:radio name="source" checked="false" value="${source.id}" id="${radioId}"/>
                        <label for="${radioId}">${source.name?.encodeAsHTML()}</label>
                    </td>
                </tr>
            </g:each>
            <tr>
                <td>

                </td>
            </tr>
        </table>
    </div>

    <g:message code="dialog.chooseSource.beforeEditText"/>
    <br/>
    <g:link action="index" controller="preferences" params="[tab: 2]">
        <g:message code="dialog.chooseSource.editText"/>
    </g:link>
</div>