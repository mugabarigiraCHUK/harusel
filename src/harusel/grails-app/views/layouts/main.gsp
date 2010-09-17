<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <title><g:layoutTitle default="Grails"/></title>
  <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon"/>
  <p:css name='bundled'/>
  <p:javascript src="jquery/jquery.all"/>
  <g:javascript src="ckeditor/ckeditor.js"/>
  <g:javascript src="jquery/jsTree/tree_component.js"/>
  <g:layoutHead/>
</head>
<body>
<div id="baseUrl" style="display:none;">${request.getContextPath()}</div>
<g:layoutBody/>
</body>
</html>
