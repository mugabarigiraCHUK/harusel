<%@ page import="org.codehaus.groovy.grails.plugins.springsecurity.AuthorizeTools; security.UserContextHolder" contentType="text/html;charset=UTF-8" %>
<html>

<head>
  <title><g:message code="home.page.title"/></title>
  <meta name="layout" content="main">
  <p:javascript src="javascript-bundle"/>
</head>

<body><center>
  <g:render template="/partial/hrMessageProperties"/>

  <div id="page" class="style_main">
    <g:render template="/partial/header"/>

    <div class="clear"></div>

    <div id="contentPane">
      <g:render template="/partial/leftMenu" model="${[filters: filters]}"/>

      <div id="page-body" class="style_body">

        <g:render template="/partial/personToolbar"/>

        <div id="mainPane" class="style_bodyContent">
          <g:render template="/partial/personTemplate" model="${[person: person]}"/>
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
</body>
</html>
