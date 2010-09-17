<%@ page import="org.codehaus.groovy.grails.plugins.springsecurity.AuthorizeTools; security.UserContextHolder" contentType="text/html;charset=UTF-8" %>
<html>

<head>
  <title><g:message code="home.page.title"/></title>
  <meta name="layout" content="main">
</head>

<body><center>
  <g:render template="/partial/hrMessageProperties"/>

  <div id="page" class="style_main">
    <div id="headerPane" class="style_header">

      <div class="style_logo"></div>
      <div class="style_userBlock">
        <div id="topLinksPane" class="style_userBlockContent">
          <g:renderPerspectives current="home"/>
          <div class="mainLinks">
            <g:ifAllGranted role="ROLE_ACCESS_PREFERENCES">
              <g:link controller="preferences"><g:message code="home.preference"/></g:link>
              &nbsp;&middot;&nbsp;
            </g:ifAllGranted>
            <g:link controller="login" action="signout"><g:message code="home.signout"/></g:link>
          </div>
        </div>
      </div>

      <div id="searchPane" class="style_search">
        <g:form controller="home" action="search" class="searchForm">
          <input id="searchTextField" class="style_searchText" type="text" name="query">
          <input id="searchButton" class="style_searchButton ui-state-default ui-corner-all style_userInfoButton" type="submit" value="${g.message(code: 'button.search.person')}">
        </g:form>
        <div class="clear"></div>
      </div>

      <div id="notificationHolder" class="style_bodyNotification"></div>
    </div>

    <div class="clear"></div>

    <div id="contentPane">
      <g:render template="/partial/leftMenu" model="${[filters: filters]}"/>

      <div id="page-body" class="style_body">

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
        <div id="mainPane" class="style_bodyContent">

        </div>

        <g:render template="/partial/footer"/>
        <div class="clear"></div>
      </div>
      <div class="clear"></div>

    </div>
    <div class="clear"></div>
  </div>

  <div style="display:none;" id="helperElement">
  </div>
</center>
<p:javascript src="javascript-bundle"/>
<script type="text/javascript">
  $(function() {
    $("#filterPane a").eq(0).click();
  });
</script>
</body>
</html>
