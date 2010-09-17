<%@ page import="org.codehaus.groovy.grails.plugins.springsecurity.AuthorizeTools" %>
<g:if test="${AuthorizeTools.ifAllGranted('ROLE_SET_STAGE')}">
    <g:set var='dialogTitle' value="${message(code: 'common.label.change.stage')}"/>
</g:if>
<g:else>
    <g:set var='dialogTitle' value="${message(code: 'common.label.decision.change.stage')}"/>
</g:else>
<div title="${dialogTitle}">
    <g:form controller="person" action="setStage" method="post">
        <g:hiddenField name="dialogViewName" value="changeStateNoteDialog"/>
        <div id="noteDialog">
            <h1 id='title'><g:message code="dialog.stage.comment"/>:<br/></h1>
            <div id="errorsHolder"></div>
            <textarea style="width:100%; height:100px;" name="comment"></textarea>
        </div>
        <br>

        <table>
            <tr>
                <td><g:checkBox name="trivialChanges" id="trivialChanges" value="${command.trivialChanges}"/></td>
                <td><label for='trivialChanges'><g:message code="common.label.trivialChanges"/></label></td>
            </tr>
            <tr>
                <td><g:checkBox name="composeEmail" id="composeEmail" value="${command.composeEmail}" disabled="true"/></td>
                <td><label for='composeEmail'><g:message code="common.label.compose.email"/></label></td>
            </tr>
            <tr>
                <td><g:checkBox name="unsubscribe" id="unsubscribe" value="${command.unsubscribe}" disabled="true"/></td>
                <td><label for="unsubscribe"><g:message code="common.label.dont.receive.notifications"/></label></td>
            </tr>
            <tr>
                <td><g:checkBox name="trackDecisions" id="trackDecisions" disabled="${AuthorizeTools.ifNotGranted('ROLE_SET_STAGE')}"
                  value="${command.trackDecisions}"/></td>
                <td><label for='trackDecisions'><g:message code="common.label.start.track.decisions"/></label></td>
            </tr>
            <g:each in="${unsubscribedReviewers}" var="manager" status="i">
                <tr>
                    <td><g:set var='checkboxId' value="userIdListToSubscribe_${manager.id}"/>
                        <g:checkBox name="userIdListToSubscribe" id="${checkboxId}" checked="${command.userIdListToSubscribe?.contains(manager.id)}"
                          value="${manager.id}" disabled="true"/></td>
                    <td><label for='${checkboxId}'><g:message code="common.label.subscribe"/> <span style="font-weight: bold;">${manager.login}</span></label></td>
                </tr>
            </g:each>
            <tr>
                <td><g:checkBox name="subscribeAll" id="${subscribeAll}" value="${command.subscribeAll}" disabled="true"/></td>
                <td><label for='subscribeAll'><g:message code="common.label.subscribe.all"/></label></td>
            </tr>
        </table>
    </g:form>
</div>