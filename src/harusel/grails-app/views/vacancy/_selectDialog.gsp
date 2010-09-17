<%@ page import="domain.Vacancy" %>
<div id='dialog' title="${message(code: 'dialog.chooseVacancy.title')}">
    <h1 id='title'><g:message code="dialog.chooseVacancy.header"/></h1>
    <div style="overflow: auto; height:300px;">
        <table>
            <g:each in="${vacancyList}" var="vacancy">
                <tr>
                    <td>
                        <g:set var="checkBoxId" value="checkboxVacancy${vacancy.id}"/>
                        <g:checkBox name="vacancy" checked="false" value="${vacancy.id}" id="${checkBoxId}"/>
                        <label for="${checkBoxId}">${vacancy.name?.encodeAsHTML()}</label>
                    </td>
                </tr>
            </g:each>

        </table>
    </div>

    <g:message code="dialog.chooseVacancy.beforeEditText"/>
    <br/>
    <g:link action="index" controller="preferences" params="[tab: 0]">
        <g:message code="dialog.chooseVacancy.editText"/>
    </g:link>
</div>