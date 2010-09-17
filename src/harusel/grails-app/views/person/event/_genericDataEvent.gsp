<%@ page import="domain.event.GenericDataEvent" %>
<div onclick="$('#tabPane').tabs('select', 1);">
    <g:message code="timeline.genericData.changed.label" args="[event?.user?.login]"/>
    <ul>
        <g:each var="property" in="${event.changedProperties}">
            <li>
                <g:message code="person.generic.${property.propertyName}"/>:
                <g:if test="${property.newValue}">
                    ${property.newValue?.encodeAsHTML()}
                </g:if>
                <g:else>
                    <em>
                        <g:message code="common.label.erased"/>
                    </em>
                </g:else>
            </li>
        </g:each>
    </ul>
</div>
