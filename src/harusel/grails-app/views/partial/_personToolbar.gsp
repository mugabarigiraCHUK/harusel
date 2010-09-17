<%@ page import="org.codehaus.groovy.grails.plugins.springsecurity.AuthorizeTools" %>
<div class="style_topMenu" id="toolbar">
  <g:each in="${toolbarButtons}" var="button">
    <div class="toolbar_${button.actionName} button32x32 disabledToolbarButton toolbarButton"
        id="actionName_${button.actionName}"
        title='${g.message(code: "toolbar.title." + button.actionName)}'>
      <input type="hidden" name="disableOn" value="${button.disabledOnStages.join(',')}"/>
    </div>
  </g:each>

&nbsp;&nbsp;&nbsp;&nbsp;
  <g:if test="${AuthorizeTools.ifAllGranted('ROLE_SET_STAGE')}">
    <div class="toolbarSetStage button32x32" id="setStageButton"></div>
  </g:if>


&nbsp;<div class="toolbarMenuLine"></div>&nbsp;

  <select id="candidateOperations" disabled>
    <g:each in="${candidateOperations}" var="entry">
      <option value="${entry.value}">${entry.key}</option>
    </g:each>
  </select>

  &nbsp;<div class="toolbarMenuLine"></div>&nbsp;

  <div class="toolbarAddDoc button32x32" title="${g.message(code: 'toolbar.title.addDoc')}" id="addDoc"></div>
  <div class="toolbarAddScore button32x32" title="${g.message(code: 'toolbar.title.addScore')}" id="addScore"></div>
  <div class="toolbarAddComment button32x32" title="${g.message(code: 'toolbar.title.addComments')}" id="addComment"></div>
  <g:if test="${AuthorizeTools.ifAllGranted('ROLE_SET_STAGE')}">
    <div id="sendEmail" title="${message(code: 'button.title.sendEmail')}" class="button32x32 toolbarSendEmail"></div>
  </g:if>

</div>
