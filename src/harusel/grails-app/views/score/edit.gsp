<%@ page contentType="text/html;charset=UTF-8" %>
<!--
<g:javascript library="jquery"/>
-->
<div id='addScores'>
    <div id='dialog' title='${message(code: "toolbar.addScores.title")}'>
        <g:form>
            <g:hasErrors bean="${command}" field="name">
                <div class="errors">
                    <div class="error-icon"></div>
                    <g:renderErrors bean="${command}" as="list" field="name"/>
                </div>
            </g:hasErrors>
            <g:message code="person.scoreSheet.name"/>: <g:textField name='name'
          value="${command.name}"/><a href="#" id="getSheetNames"><p:image src="skin/database_table.png"/></a>
            <br>

            <g:hasErrors bean="${command}" field="date">
                <div class="errors">
                    <div class="error-icon"></div>
                    <g:renderErrors bean="${command}" as="list" field="date"/>
                </div>
            </g:hasErrors>
            <g:message code="person.scoreSheet.date"/>: <g:textField name='date' value="${command.date}"/>
            <table>
                <tr>
                    <th><g:message code="person.scoreSheet.questionNumber"/></th>
                    <th><g:message code="person.scoreSheet.question"/></th>

                    <th><g:message code="person.scoreSheet.point"/></th>
                    <th><g:message code="person.scoreSheet.comment"/></th>
                </tr>
                <g:hasErrors bean="${command}" field="val">
                    <tr>
                        <td colspan='4'>
                            <div class="errors">
                                <div class="error-icon"></div>
                                <g:renderErrors bean="${command}" as="list" field="val"/>
                            </div>
                        </td>
                    </tr>
                </g:hasErrors>
                <g:set var="abc" value="${('a'..'z')}"/>
                <g:set var="number" value='${1}'/>
                <g:each in="${criterions}" var="root">
                    <g:if test="${visibleCriterions.contains(root.id)}">
                        <g:render template="scorePointEdit" model="${[number: number+'.', criteria: root, command: command, labelCssClass: 'rootMark']}"/>
                        <g:set var="childNumber" value='${0}'/>
                        <g:each in="${root.children}" var="child">
                            <g:if test="${visibleCriterions.contains(child.id)}">
                                <g:render template="scorePointEdit" model="${[number: number+'.'+abc[childNumber]+'.', criteria: child, command: command, labelCssClass: 'childMark']}"/>
                                <g:set var="childNumber" value='${childNumber+1}'/>
                            </g:if>
                        </g:each>
                        <g:set var="number" value='${number+1}'/>
                    </g:if>
                </g:each>
            </table>
            <br/>

            <g:hasErrors bean="${command}" as="list" field="sheetComment">
                <div class="errors">
                    <div class="error-icon"></div>
                    <g:renderErrors bean="${command}" as="list" field="sheetComment"/>
                </div>
            </g:hasErrors>
            <g:textArea name='sheetComment' style="height:75px;width:670px; "
              value="${command.sheetComment}"/>
            <g:hiddenField name='personId' value="${command.personId}" id="personScoreSheetId"/>
            <g:hiddenField name='id' value="${command.id}" id="scoreSheetId"/>
        </g:form>
    </div>
</div>