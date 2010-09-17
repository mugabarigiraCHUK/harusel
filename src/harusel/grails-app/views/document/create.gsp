<%@ page contentType="text/html;charset=UTF-8" %>
<g:if test="${flash.message}">
    <div class="notify">
        ${flash.message}
    </div>
</g:if>
<g:each var="error" in="${errors}">
    <div class="error">${error}</div>
</g:each>
