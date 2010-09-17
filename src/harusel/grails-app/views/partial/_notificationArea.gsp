<%@ page contentType="text/html;charset=UTF-8" %>
<div id="notificationArea">
  <g:if test="${flash.errors}">
    <div class="errors">
      ${flash.errors.join(", ").encodeAsHTML()}
    </div>
  </g:if>
  <g:if test="${flash.messages}">
    <div class="messages">
      ${flash.messages.join(", ").encodeAsHTML()}
    </div>
  </g:if>
</div>
