<%@ page contentType="text/html;charset=UTF-8" %>
<g:set var="cssClass" value=""/>
<g:set var='point' value="${command.val[criteria.id.toString()]}"/>
<g:set var='comment' value="${command.comment[criteria.id.toString()]}"/>
<g:hasErrors bean="${command}" field="val[${criteria.id.toString()}]">
    <g:set var="cssClass" value="error"/>
    <tr>
        <td colspan='4'>
            <div class=errors>
                <g:renderErrors bean="${command}" as="list" field="val[${criteria.id.toString()}]"/>
            </div>
        </td>
    </tr>
</g:hasErrors>
<tr>
    <td>
        ${number}
    </td>
    <td>
        <label class="${labelCssClass}">${criteria.name?.encodeAsHTML()}</label>
    </td>
    <td>
        <g:if test="${!criteria.readOnly}">
            <g:textField class="${cssClass} valueInput" name="val[${criteria.id}]" style="width:25px;"
              value="${point}"/>
        </g:if>
        <g:else>
            &nbsp;
        </g:else>
    </td>
    <td>
        <g:if test="${!criteria.readOnly}">
            <g:textArea class="resizeable commentInput" name='comment[${criteria.id}]' rows="1" cols="20" style="height:20px;"
              value="${comment}"/>
        </g:if>
    </td>

</tr>
