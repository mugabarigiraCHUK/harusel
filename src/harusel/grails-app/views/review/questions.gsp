<%@ page contentType="text/html;charset=UTF-8" %>
<g:if test="${request.messages}">
    <div class="notify">
        ${request.messages.join(", ").encodeAsHTML()}
    </div>
</g:if>
<div id="manageTemplatesButtonPanel" style="padding-bottom: 3px;">
    <button class="ui-state-default ui-corner-all style_userInfoButton" id="cancelTemplate">
        <g:message code="common.button.cancel"/>
    </button>
    <button class="ui-state-default ui-corner-all style_userInfoButton" id="saveTemplate">
        <g:message code="common.button.save"/>
    </button>
</div>
<ul>
    <li rel="superRoot" class="${model ? 'open' : ''}">
        <a href="#" class="${rootCheckedClass}">
            <g:message code="performanceReview.templates.superRoot"/>
        </a>
        <g:if test="${model}">
            <ul>
                <g:each in="${model}" var="group">
                    <li rel="root" id="question_${group.id}" style="${group.isDeleted ? 'display: none;' : ''}">
                        <a href="#" class="${group.checkedClass}">${group.text}</a>
                        <g:if test="${group.children}">
                            <ul>
                                <g:each in="${group.children}" var="child">
                                    <li rel="item" id="question_${child.id}" style="${child.isDeleted ? 'display: none;' : ''}">
                                        <a href="#" class="${child.checkedClass}">${child.text}</a>
                                    </li>
                                </g:each>
                            </ul>
                        </g:if>
                    </li>
                </g:each>
            </ul>
        </g:if>
    </li>
</ul>
