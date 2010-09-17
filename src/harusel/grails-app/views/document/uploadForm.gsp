<g:uploadForm controller="document" action="create" id="${params.id}">

    <div class="errors" style="display: none;">
    </div>

    <table>

        <g:each in="${0 ..< fileCount}" var="fileIndex">

            <g:set var="prefix">documents[${fileIndex}]</g:set>

            <g:each status="typeIndex" var="documentType" in="${privilegedTypes}">
                <tr>

                    <td>
                        <label>
                            <g:radio name="${prefix}.type.id" value="${documentType.id}"/>
                            <g:message code="document.type.${documentType.name}"/>
                        </label>
                    </td>

                    <g:if test="${!typeIndex}">
                        <td rowspan="${privilegedTypes.size}">
                            <input type="file" name="${prefix}.file">
                        </td>
                    </g:if>

                </tr>
            </g:each>

            <tr>

                <td style="border-bottom: 1px dotted silver">
                    <label>
                        <g:radio name="${prefix}.type.id" value="0" class="otherDocTypeOption"/>
                        <g:message code="person.document.typeSelector.other"/>
                    </label>
                </td>

                <td style="border-bottom: 1px dotted silver">
                    <input name="${prefix}.typeName" type="text">
                    <g:if test="${otherTypes}">
                        <input type="button" name="${prefix}.selectButton" value="Select">
                    </g:if>
                </td>

            </tr>

        </g:each>

    </table>

</g:uploadForm>

<div style="display: none">
    <select size="8" style="width: 200px;">
        <g:each var="documentType" in="${otherTypes}">
            <option>${documentType.name}</option>
        </g:each>
    </select>
</div>
