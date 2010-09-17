<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="security.UserContextHolder; org.apache.commons.lang.time.DateUtils; domain.Criterion; domain.person.Person" %>
<g:if test="${visibleCriterions.size()==0}">
    <g:message code="score.marks.no.sheets"/>
</g:if>
<g:else>
    <div class="style_tableScore">
        <table>
            %{-- head --}%
            <g:set var="sheetNames" value="${sheets.keySet()}"/>
            <tr>
                <th colspan="1" rowspan="3">№</th>
                <th colspan="1" rowspan="3"><g:message code="score.characteristic.title"/></th>
                <g:each in="${sheetNames}" var="sheetName">
                    <th colspan="${sheets[sheetName].size() * 2}">
                        <g:message code="score.characteristic.stage"/> ${sheetName}
                    </th>
                </g:each>
            </tr>
            <tr>
                <g:each in="${sheets.values()}" var="sheetList">
                    <g:each in="${sheetList}" var="sheet">
                        <th colspan="2">
                            ${sheet.sheet.user.fullName}
                            <g:if test="${sheet.sheet.user.id == UserContextHolder.contextUser.id && sheet.sheet.date.after(sheetDateAvailableToEdit)}">
                                <a href="#" style="display: inline;" class="editScoreSheet" sheetId="${sheet.id}">
                                    <g:message code="common.button.edit"/>
                                </a>
                            </g:if>
                        </th>
                    </g:each>
                </g:each>
            </tr>
            <tr>
                <g:each in="${sheets.values()}" var="sheetList">
                    <g:each in="${sheetList}" var="sheet">
                        <th>
                            <g:message code="score.characteristic.mark"/>
                        </th>
                        <th>
                            <g:message code="score.characteristic.comment"/>
                        </th>
                    </g:each>
                </g:each>
            </tr>

            %{-- body --}%
            <g:set var="abc" value="${('a'..'z')}"/>
            <g:set var="number" value='${1}'/>
            <g:set var="rowIndex" value="${0}"/>
            <g:each in="${criterions}" var="root">
                <g:if test="${visibleCriterions.contains(root.id)}">
                    <g:render template="scorePointView"
                      model="${[
                    number: number+'.',
                    criteria: root,
                    sheetLists: sheets.values(),
                    labelCssClass: 'rootMark',
                    rowIndex: rowIndex,
                    ]}"/>
                    <g:set var="childNumber" value='${0}'/>
                    <g:set var="rowIndex" value="${rowIndex+1}"/>
                    <g:each in="${root.children}" var="child">
                        <g:if test="${visibleCriterions.contains(child.id)}">
                            <g:render template="scorePointView"
                              model="${[
                          number: number+'.'+abc[childNumber]+'.',
                          criteria: child,
                          sheetLists: sheets.values(),
                          labelCssClass: 'childMark',
                          rowIndex: rowIndex,
                          ]}"/>
                            <g:set var="rowIndex" value="${rowIndex+1}"/>
                            <g:set var="childNumber" value='${childNumber+1}'/>
                        </g:if>
                    </g:each>
                    <g:set var="number" value='${number+1}'/>
                </g:if>
            </g:each>
        </table>
    </div>
</g:else>
