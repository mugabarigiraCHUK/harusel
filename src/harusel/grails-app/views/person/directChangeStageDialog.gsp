<%@ page import="org.apache.commons.lang.builder.ToStringBuilder; domain.Stage" contentType="text/html;charset=UTF-8" %>
<g:set var="stageList" value="${Stage.list()}"/>
<div id='dialog' title='${message(code: 'common.label.change.stage')}'>
    <g:form controller="person" action="setStage" method="post">
        <g:hiddenField name="dialogViewName" value="directChangeStageDialog"/>
        <div id="errorsHolder"></div>
        <select id='stages' name='stageId' size='${stageList.size()}'>
            <g:each var='stage' in='${stageList}' status="i">
                <option value='${stage.id}' ${command.stageId ? (stage.id == command.stageId ? "selected" : "") : (i ? "" : "selected")}>
                    <g:message code="stage.${stage.codeName}"/>
                </option>
            </g:each>
        </select>
        <br/>
        <g:hasErrors bean="${command}" field="comment">
            <div class="errors">
                <div class="error-icon"></div>
                <g:renderErrors bean="${command}" field="comment"/>
            </div>
        </g:hasErrors>

        <h1><g:message code="dialog.stage.comment"/>:</h1>


        <textarea id="changeStageComment" style="width:100%; height:100px;" name="comment"></textarea>
        <br/>
        <g:checkBox name="trackDecisions" id="trackDecisions" value="${command.trackDecisions}"/>
        <label for='trackDecisions'><g:message code="common.label.start.track.decisions"/></label>
    </g:form>
</div>