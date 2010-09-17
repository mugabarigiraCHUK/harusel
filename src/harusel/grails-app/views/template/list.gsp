<%@ page import="performanceReview.Template" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main"/>
  <title>Template List</title>
</head>
<body>
<div class="nav">
  <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
  <span class="menuButton"><g:link class="create" action="create">New Template</g:link></span>
</div>
<div class="body">
  <h1>Template List</h1>
  <g:if test="${flash.message}">
    <div class="message">
      <div class="message-icon"></div>
      ${flash.message}
    </div>
  </g:if>
  <div class="list">
    <table>
      <thead>
      <tr>

        <g:sortableColumn property="id" title="Id"/>

        <g:sortableColumn property="name" title="Name"/>

      </tr>
      </thead>
      <tbody>
      <g:each in="${templateInstanceList}" status="i" var="templateInstance">
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

          <td><g:link action="show" id="${templateInstance.id}">${fieldValue(bean: templateInstance, field: 'id')}</g:link></td>
          <td><g:link action="show" id="${templateInstance.id}">${fieldValue(bean: templateInstance, field: 'name')}</g:link></td>

        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div class="paginateButtons">
    <g:paginate total="${templateInstanceTotal}"/>
  </div>
</div>
</body>
</html>
