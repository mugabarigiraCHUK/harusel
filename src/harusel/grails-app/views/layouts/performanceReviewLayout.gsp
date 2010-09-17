<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <title>
    <g:set var="defaultPageTitle">
      <g:message code="performanceReview.default.pageTitle"/>
    </g:set>
    <g:layoutTitle default="${defaultPageTitle}"/>
  </title>
  <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon"/>
  <p:css name='bundled'/>
  <p:javascript src="jquery/jquery.all"/>
  <p:javascript src="performanceReview-bundle"/>
  <g:layoutHead/>
</head>
<body>
<g:if test=''>

</g:if>
<div id="baseUrl" style="display:none;">${request.getContextPath()}</div>
<div id="headerPane">
  <div class="style_logo"></div>
  <div class="style_handyLinks">
    <g:link controller="login" action="signout" class="exitButton controlButton"><g:message code="home.signout"/></g:link>
    <g:if test="${canPublicate}">
      <g:link controller="employee" action="publicateForm" class="publicButton controlButton"><g:message code="performanceReview.publicAnketa.button"/></g:link>
    </g:if>
  </div>
  <div style="clear:right"></div>
  <div id="notificationHolder" class="style_notificationOnPerformanceReviewPage"></div>
</div>

<div class="clear"></div>
<g:layoutBody/>
<div class="clear"></div>
<div class="footerPane">
  <g:render template="/partial/footer"/>
</div>
</body>
</html>
