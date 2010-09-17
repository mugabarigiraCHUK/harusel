<%@ page contentType="text/html;charset=UTF-8" %>
<g:if test="${request.messages}">
    <div class="notify">
        ${request.messages.join(", ").encodeAsHTML()}
    </div>
</g:if>
<form id="assigningTemplate">
    <table class="assigningTemplateTable style_mainList">
        <thead>
        <tr>
            <th width="2%">#</th>
            <th width="38%"><g:message code="performanceReview.tab.assigningTemplate.fullName"/></th>
            <th width="30%"><g:message code="performanceReview.tab.assigningTemplate.manager"/></th>
            <th width="30%"><g:message code="performanceReview.tab.assigningTemplate.templateName"/></th>
        </tr>
        </thead>
        <g:each var="user" in="${users}" status="index">
            <tr class="${index % 2 == 0 ? '' : 'odd'}">
                <td align="center">
                    ${index + 1}.
                </td>
                <td class="userName">
                    ${g.fieldValue(bean: user, field: 'fullName')}
                </td>
                <td align="center">
                    <select name="assignedManager" class="managerSelector">
                        <option value="manager ${user.id}"><g:message code="performanceReview.tab.assigningTemplate.selectManaer"/></option>
                        <g:each var="manager" in="${managers}">
                            <g:set var="isSelected" value="${user.manager?.id == manager.id}"/>
                            <option value="manager ${user.id} ${manager.id}" ${isSelected ? 'selected="true"' : ""}>
                                ${g.fieldValue(bean: manager, field: 'fullName')}
                            </option>
                        </g:each>
                    </select>
                </td>
                <td align="center">
                    <select name="assignedTemplate" class="templateName">
                        <option value="template ${user.id}"><g:message code="performanceReview.tab.assigningTemplate.select"/></option>
                        <g:each var="template" in="${templates}">
                            <g:set var="isSelected" value="${user.assignedTemplate?.id == template.id}"/>
                            <option value="template ${user.id} ${template.id}" ${isSelected ? 'selected="true"' : ""}>
                                ${g.fieldValue(bean: template, field: 'name')}
                            </option>
                        </g:each>
                    </select>
                </td>
            </tr>
        </g:each>
    </table>
</form>
<div class="style_userInfoButtons">
    <button class="ui-state-default ui-corner-all style_userInfoButton" id="cancelAssigns">
        <g:message code="common.button.cancel"/>
    </button>
    <button class="ui-state-default ui-corner-all style_userInfoButton" id="saveAssigns">
        <g:message code="common.button.save"/>
    </button>
</div>