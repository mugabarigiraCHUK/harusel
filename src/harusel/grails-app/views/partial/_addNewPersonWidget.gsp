<div class="style_addNewButton">
  <button class="ui-state-default ui-corner-all style_userInfoButton" id="btnAddNew">
    <g:message code="person.create.add"/>
  </button>
  <div id="addPersonPopup" style="display: none;">
    <g:form controller="home" action="search" class="searchForm">
      <input type="hidden" name="create" value="true"/>
      <g:message code="domain.person.fullName"/>:
      <input id="addPersonNameField" type="text" name="query" class="addPersonInput"/>
      <input id="addPersonPopupSubmit" type="submit" class="searchAndAddPersonButton" value=""/>
      <div class="prompt"><g:message code="domain.person.fullNamePrompt"/></div>
    </g:form>
  </div>
</div>
