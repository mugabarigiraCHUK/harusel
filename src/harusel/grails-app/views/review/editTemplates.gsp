<%@ page contentType="text/html;charset=UTF-8" %>
<g:if test="${request.messages}">
    <div class="notify">
        ${request.messages.join(", ").encodeAsHTML()}
    </div>
</g:if>
<div id="editTemplateTab">
    <div class="templateSelectionColumn">
        <strong id="createTemplateButton" class="openDialogLink"><g:message code="performanceReview.templates.add"/></strong>
        <div id="templateList">
            <ul>
                <g:each in="${templates}" var="template">
                    <li id="template_${template.id}">
                        <g:link action="questions" id="${template.id}">${g.fieldValue(bean: template, field: 'name')}</g:link>
                    </li>
                </g:each>
            </ul>
        </div>
        <div class="manageTemplate">
            <g:link controller="template" action="list" target="_blank">
                <g:message code="performanceReview.templates.manage"/>
            </g:link>
        </div>
    </div>
    <div id="templateEditor">
    </div>
    <div class="clear"></div>

</div>
<div id="addTemplatePopup" style="display: none;">
    <g:form controller="review" action="addTemplate" class="addTemplateForm">
        <h3><g:message code="performanceReview.templates.add.prompt"/>:</h3>
        <input id="addTemplateNameField" type="text" name="name" class="addTemplateInput"/>
        <input id="addTemplatePopupSubmit" type="submit" class="submitTemplateNameButton" value=""/>
    </g:form>
</div>
