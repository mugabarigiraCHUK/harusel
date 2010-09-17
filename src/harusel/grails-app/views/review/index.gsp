<%@ page import="org.codehaus.groovy.grails.plugins.springsecurity.AuthorizeTools; security.UserContextHolder" contentType="text/html;charset=UTF-8" %>
<html>

<head>
  <meta name="layout" content="main">
  <title><g:message code="review.page.title"/></title>
  <!-- <g:javascript library="jquery"/>-->
</head>

<body><center>
  <g:if test="${flash.error}">
    <div class="error-notify">${flash.error}</div>
  </g:if>
  <div class="messages-properties" style="display: none;">
    <div class="message-property common-label-updated">${message(code: "common.label.updated")}</div>
    <div class="message-property common-label-canceled">${message(code: "common.label.canceled")}</div>
    <div class="message-property performanceReview-label-newQuestion">${message(code: "performanceReview.label.newQuestion")}</div>
    <div class="message-property performanceReview-label-newSection">${message(code: "performanceReview.label.newSection")}</div>
  </div>

  <div id="page" class="style_main">

    <div id="headerPane" class="style_header">

      <div class="style_logo"></div>

      <div class="style_userBlock">
        <div id="topLinksPane" class="style_userBlockContent">
          <g:renderPerspectives current="review"/>
          <div class="mainLinks">
            <g:link controller="login" action="signout"><g:message code="home.signout"/></g:link>
          </div>
        </div>
      </div>

      <div id="notificationHolder" class="style_bodyNotification"></div>
    </div>

    <div class="clear"></div>

    <div id="contentPane">

      <div id="page-body" class="style_body">

        <div id="mainPane" class="style_bodyContent">
          <div id="performanceReviewTabs">
            <ul>
              <g:ifAllGranted role="ROLE_ACCESS_QUIZ_ASSIGN_FORM"><li><g:link action="assignedTemplates"><g:message code="performanceReview.tab.assigningTemplate"/></g:link></li></g:ifAllGranted>
              <g:ifAllGranted role="ROLE_ACCESS_QUIZ_MANAGE_FORM"><li><g:link action="editTemplates"><g:message code="performanceReview.tab.manageringTemplate"/></g:link></li></g:ifAllGranted>
              <g:ifAllGranted role="ROLE_ACCESS_QUIZ_RESULTS"><li><g:link action="employerFormsReview"><g:message code="performanceReview.tab.review"/></g:link></li></g:ifAllGranted>
              <g:ifAllGranted role="ROLE_ACCESS_QUIZ"><li><g:link controller="employee" action="blankForm" class="separatedWindow"><g:message code="performanceReview.tab.fillForm"/></g:link></li></g:ifAllGranted>
            </ul>
          </div>
        </div>

        <g:render template="/partial/footer"/>
      </div>

    </div>
  </div>

</center>
<p:javascript src="javascript-bundle"/>
</body>
</html>
