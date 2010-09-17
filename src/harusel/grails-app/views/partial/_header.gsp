<%@ page contentType="text/html;charset=UTF-8" %>
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
