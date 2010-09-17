<%@ page contentType="text/html;charset=UTF-8" %>
<!-- <g:javascript library="jquery"/>-->
<html>
<head>
  <title><g:message code="reports.title"/></title>
  <meta name="layout" content="main">
</head>
<body><center>
  <div id="page" class="style_main">

    <div id="headerPane" class="style_header">

      <div class="style_logo"></div>

      <div class="style_userBlock">
        <div id="topLinksPane" class="style_userBlockContent">
          <g:renderPerspectives current="reports"/>
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
          <div id="reportsTabs">
            <ul>
              <li><g:link action="vacancyStatistics"><g:message code="reports.tab.vacancyStatistics"/></g:link></li>
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
