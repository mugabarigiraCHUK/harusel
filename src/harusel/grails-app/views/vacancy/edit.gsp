<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="domain.Criterion; domain.Vacancy" %>

<!--
<g:javascript library='jquery'/>
-->

<div id="vacancyView">
    <g:hiddenField name='id' value="${vacancy.id}" id="vacancyId"/>
    <g:remoteLink controller="vacancy" action="list" update="vacancyDomainDetails">
        &lt;&lt;&lt;<g:message code="vacancy.edit.backToList"/>
    </g:remoteLink>
    <br/>
    <g:if test="${flash.message}">
        <div class="notify">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${vacancy}">
        <div class="errors">
            <div class="error-icon"></div>
            <g:renderErrors bean="${vacancy}"/>
        </div>
    </g:hasErrors>
    <table>
        <tr>
            <g:set var="cssClass" value=""/>
            <g:hasErrors bean="${vacancy}" field="name">
                <g:set var="cssClass" value="error"/>
            </g:hasErrors>
            <td>
                <label for="name"><g:message code="vacancy.edit.name"/></label>&nbsp;
                <input id="name" class="${cssClass}" type="text" name='name' value='${fieldValue(bean: vacancy, field: 'name')}'/>
            </td>
            <td>
                <label><g:message code="vacancy.edit.state"/></label>
                <g:radioGroup class="${cssClass}" name="active" value="${vacancy.active}" labels='["domain.state.active","domain.state.closed"]' values="['true', 'false']">
                    <p><%=it.radio%> <g:message code="${it.label}"/></p>
                </g:radioGroup>
            </td>
        </tr>
        <tr>
            <td>
                <label><g:message code="vacancy.edit.reviewers"/></label>
                <div class='reviewersList' id="reviewersList">
                    <ul class="reviewers">
                        <g:each var='user' in="${vacancy.users}">
                            <li>
                                <g:hiddenField name="usersIdList" value="${user.id}"/>${user.fullName?.encodeAsHTML()}
                            </li>
                        </g:each>
                    </ul>
                </div><br>
                <button value="edit" id="editReviewers"><g:message code="common.button.edit"/></button>
            </td>
            <td>
                <label for="description"><g:message code="vacancy.edit.notes"/></label>
                <textarea id="description" class='longTextArea' name='description'>${vacancy.description?.encodeAsHTML()}</textarea>
            </td>
        </tr>
        <tr>
            <td colspan='2'>
                <label><g:message code="vacancy.edit.criterias"/></label>
                <div id="vacancyCriterionPane">
                    <ul>
                        <g:if test="${selectedCriteriaIdList.isEmpty()}">
                            <g:set var="isChecked" value="unchecked"/>
                        </g:if>
                        <g:elseif test="${criteriaList.find {!selectedCriteriaIdList.contains(it.id) && it.children.isEmpty()}}">
                            <g:set var="isChecked" value="undetermined"/>
                        </g:elseif>
                        <g:else>
                            <g:set var="isChecked" value="checked"/>
                        </g:else>
                        <li rel='superRoot' id="criteriasTree" class="open">
                            <a href='' class="${isChecked}"><g:message code="criterion.all.criterias"/></a>
                            <ul>
                                <g:each var="criteria" in="${criteriaList}">
                                    <li id="${criteria.id}" rel="root">
                                        <g:checker var="isChecked" selected="${selectedCriteriaIdList}" criteria="${criteria}"/>
                                        <a href='' class="${isChecked}">${criteria.name?.encodeAsHTML()}</a>
                                        <g:if test="${criteria.children?.size()>0}">
                                            <ul>
                                                <g:each var="child" in="${criteria.children}">
                                                    <li id="${child.id}" rel="item">
                                                        <g:checker var="isChecked" selected="${selectedCriteriaIdList}" criteria="${child}"/>
                                                        <a href='' class="${isChecked}">${child.name?.encodeAsHTML()}</a>
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
            </td>
        </tr>
        <tr align='center'>
            <td colspan='2' style="text-align:center;">
                <button id='vacancyEditCancel'><g:message code="common.button.cancel"/></button>
                <button id='vacancyEditSave'><g:message code="common.button.save"/></button>
                <button id='vacancyEditSaveAndClose'><g:message code="common.button.saveAndClose"/></button>
            </td>
        </tr>
    </table>
</div>
