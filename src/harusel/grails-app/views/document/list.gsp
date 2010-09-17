<%@ page contentType="text/html;charset=UTF-8" %>

<div>
    <g:if test="${documents}">

        <g:form action="remove" id="${params.id}" class="ajaxForm">

            <table class="style_docTable">
                <tr>
                    <th></th>
                    <th><g:message code="person.document.name"/></th>
                    <th><g:message code="person.document.type"/></th>
                    <th><g:message code="person.document.date"/></th>
                    <th><g:message code="person.document.user"/></th>
                </tr>

                <g:each in="${documents.sort{it.date}}" var="document">
                    <tr>
                        <td class="style_mainList_checkBox">
                            <g:checkBox name="documentId" value="${document.id}" checked="false"/>
                        </td>
                        <td>
                            <g:link controller="document" action="download" id="${document.id}">
                                ${g.fieldValue(bean: document, field: 'name')}
                            </g:link>
                        </td>
                        <td class="style_docTableType">
                            <g:if test="${document.type.privileged}">
                                <g:message code="document.type.${g.fieldValue(bean: document.type, field: 'typeCode')}"/>
                            </g:if>
                            <g:else>
                                ${g.fieldValue(bean: document.type, field: 'name')}
                            </g:else>
                        </td>
                        <td class="style_docTableData">
                            <g:formatDate format="MMM dd, yyyy HH:mm" date="${document.date}"/>
                        </td>
                        <td>
                            ${g.fieldValue(bean: document.user, field: 'fullName')}
                        </td>
                    </tr>
                </g:each>

            </table>

            <div class="line"></div>

            <div class="style_userInfoButtons">
                <button id="removeDocuments" class="ui-state-default ui-corner-all style_userInfoButton" type="Submit"><g:message code="home.operations.remove"/></button>
            </div>
        </g:form>

    </g:if>
    <g:else>

        <g:message code="person.document.emptyList"/>

    </g:else>

</div>