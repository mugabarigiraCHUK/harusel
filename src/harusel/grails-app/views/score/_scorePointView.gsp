<%@ page contentType="text/html;charset=UTF-8" %>

<g:set var="rowClass" value="${rowIndex%2? 'odd': ''}"/>
<tr class="${rowClass}">
    <td>
    ${number}
    </td>
    <td>
        <label class="${labelCssClass}">${criteria.name?.encodeAsHTML()}</label>
    </td>
    <g:each in="${sheetLists}" var="sheetList">
        <g:each in="${sheetList}" var="sheet">
            <g:if test="${!criteria.readOnly}">
            %{-- CR critical FIXME DO NOT CALL BUSINESS SERVICES FROM VIEW.--}%
                <g:set var="scorePoint" value="${sheet.points?.find{it.criterion.id == criteria.id}}"/>
                <td class="markCell">
                    ${scorePoint?.value}
                </td>
                <td class="commentCell">
                    ${scorePoint?.comment?.encodeAsHTML()}
                </td>
            </g:if>
            <g:else>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
            </g:else>
        </g:each>
    </g:each>
</tr>
