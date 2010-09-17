<%@ page contentType="text/html;charset=UTF-8" %>
<div class="buttonsWidth70">
    <a href="#" id="criteriaEditCancel" class="linkButton ui-state-default ui-corner-all">
        <g:message code="common.button.cancel"/>
    </a>
    <a href="#" id="criteriaEditSave" class="linkButton ui-state-default ui-corner-all">
        <g:message code="common.button.save"/>
    </a>
</div>
<div class="clear"></div>

<div id="criterionPane" style="display:none;">
    <g:if test="${flash.message}">
        <div class="notify">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${source}">
        <div class="errors">
            <div class="error-icon"></div>
            <g:renderErrors bean="${source}" as="list"/>
        </div>
    </g:hasErrors>

    <ul>
        <li rel='superRoot' id="criteriasTree" class="open">
            <a href=''><g:message code="criterion.all.criterias"/></a>
            <ul>
                <g:each var="criteria" in="${criteriaList}">
                    <li id="${criteria.id}" rel="root">
                        <a href=''>${criteria.name}</a>
                        <g:if test="${criteria.children?.size()>0}">
                            <ul>
                                <g:each var="child" in="${criteria.children}">
                                    <li id="${child.id}" rel="item">
                                        <a href=''>${child.name}</a>
                                    </li>
                                </g:each>
                            </ul>
                        </g:if>
                    </li>
                </g:each>
            </ul>
        </li>
    </ul>

</div>

