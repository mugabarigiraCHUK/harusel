<%@ page import="domain.Vacancy" %>
<div id='dialog' title="Choose reviewers">
    <h1 id='title'><g:message code="dialog.title.managers"/></h1>
    <table>
        <g:each in="${reviewersList}" var="manager">
            <tr>
                <td>
                    <g:set var="checkBoxId" value="checkboxmanager${manager.id}"/>
                    <g:checkBox name="manager" checked="false" value="${manager.id}" id="${checkBoxId}"/>
                    <label for="${checkBoxId}">${manager.fullName}</label>
                </td>
            </tr>
        </g:each>
    </table>
</div>