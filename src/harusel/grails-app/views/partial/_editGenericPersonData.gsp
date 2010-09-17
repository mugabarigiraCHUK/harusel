<%@ page import="org.apache.commons.lang.StringUtils" contentType="text/html;charset=UTF-8" %>
<div id='genericInfo'>
  <g:form>
    <input id="personId" type="hidden" name="id" value="${person?.id}">
    <input type="hidden" name="version" value="${person?.version}">
    <input type="hidden" name="stage.id" value="${person?.stage?.id}">
    <g:if test="${flash.message}">
      <div class="notify">${flash.message}</div>
    </g:if>

    <table class="style_userInfo">
      <tr>
        <td><label for="fullName"><g:message code="person.generic.fullName"/>:</label></td>
        <td>
          <input class="genericField ${cssClass}" type="text" id='fullName' name="fullName" value="${fieldValue(bean: person, field: 'fullName')}"/>
          <g:set var="cssClass" value=""/>
          <g:hasErrors bean="${person}" field="fullName">

            <div class="errors">
              <div class="error-icon"></div>
              <g:renderErrors bean="${person}" as="list" field="fullName"/>
            </div>
            <g:set var="cssClass" value="error"/>
          </g:hasErrors>
        </td>
        <td><span class="subscript"><g:message code="person.generic.hint.fullName"/></span></td>

      </tr>
      <tr>
        <td><label for="birthDate"><g:message code="person.generic.birthDate"/>:</label></td>
        <td>
          <select id='birthDate' name="birthDate">
            <option value="${year}" ${(StringUtils.isEmpty(person.birthDate)) ? "selected" : ""}>&nbsp;</option>
            <g:each in="${(1970..2000)*.toString()}" var="year">
              <option value="${year}" ${(person.birthDate == year) ? "selected" : ""}>${year}</option>
            </g:each>
          </select>

          <g:set var="cssClass" value=""/>
          <g:hasErrors bean="${person}" field="emails">
            <div class="errors">
              <div class="error-icon"></div>
              <g:renderErrors bean="${person}" as="list" field="emails"/>
            </div>
            <g:set var="cssClass" value="error"/>
          </g:hasErrors>
        </td>
        <td></td>

      </tr>
      <tr>
        <td><label for='emails'><g:message code="person.generic.emails"/>:</label></td>
        <td>
          <input class="genericField ${cssClass}" id="emails" name="emails" type="text" value="${fieldValue(bean: person, field: 'emails')}"/>
          <g:set var="cssClass" value=""/>
          <g:hasErrors bean="${person}" field="phones">
            <div class="errors">
              <div class="error-icon"></div>
              <g:renderErrors bean="${person}" as="list" field="phones"/>
            </div>
            <g:set var="cssClass" value="error"/>
          </g:hasErrors>
        </td>
        <td><span class="subscript"><g:message code="person.generic.hint.emails"/></span></td>

      </tr>
      <tr>
        <td><label for='phones'><g:message code="person.generic.phones"/>:</label></td>
        <td><input class="genericField ${cssClass}" id="phones" name="phones" type="text" value="${fieldValue(bean: person, field: 'phones')}"/></td>
        <td><span class="subscript"><g:message code="person.generic.hint.phones"/></span></td>
      </tr>
      <g:ifAllGranted role="ROLE_ACCESS_PERSON_SOURCE">
        <tr>
          <td><label for="sourceName"><g:message code="person.generic.source"/>:</label></td>
          <td>
            <input class="genericField" id="sourceName" type="text" readonly="true" name="source" value="${person?.source?.name}" disabled="true"/>
            <input type="hidden" name="source.id" value="${person?.source?.id}"/>
          </td>
          <td>
            <button class="style_buttonEdite" title="${g.message(code: 'person.sources.edit')}" id="editSource" type="button" onclick='PersonInfo.editSource();'></button>
          </td>
        </tr>
      </g:ifAllGranted>
      <tr>
        <td><label for="vacancy"><g:message code="person.generic.vacancies"/>:</label></td>
        <td>
          <ul class="vacancies" id="vacancy">
            <g:set var="index" value="0"/>
            <g:each in="${person?.vacancies}" var="vacancy">
              <li>${fieldValue(bean: vacancy, field: 'name')}</li>
              <input type="hidden" name="vacancies.id" value="${fieldValue(bean: vacancy, field: 'id')}"/>
            %{--<input type="hidden" name="vacancies[${index}].id" value="${fieldValue(bean: vacancy, field: 'id')}"/>--}%
              <g:set var="index" value="${index.toInteger()+1}"/>
            </g:each>
          </ul>
        </td>
        <td>
          <g:ifAllGranted role="ROLE_ACCESS_PERSON_VACANCY">
            <button class="style_buttonEdite" title="${g.message(code: 'person.vacancy.edit')}" id="editVacancies" type="button" onclick="PersonInfo.editVacancies(${person.id});"></button>
          </g:ifAllGranted>
        &nbsp;
        </td>
      </tr>
    </table>

    <div class="line"></div>
    <div class="style_userInfoCheck">
      <input type="checkbox" name="trivialChanges" value="true" id="trivialChanges"/>
      <label for="trivialChanges">
        <g:message code="common.label.trivialChanges"/>
      </label>
    </div>
    <div class="style_userInfoButtons">
      <g:if test="${person.id!=null}">
        <button class="ui-state-default ui-corner-all style_userInfoButton" id="cancel" type="button" onclick="PersonInfo.cancelUpdates(${person.id});">
          <g:message code="common.button.cancel"/>
        </button>
        <button class="ui-state-default ui-corner-all style_userInfoButton" id="save" type="button" onclick="PersonInfo.updatePerson();">
          <g:message code="common.button.save"/>
        </button>
      </g:if>
      <g:else>
        <button class="ui-state-default ui-corner-all style_userInfoButton" id="cancel" type="button" onclick="PersonInfo.cancelAddition();">
          <g:message code="common.button.cancel"/>
        </button>
        <button class="ui-state-default ui-corner-all style_userInfoButton" id="add" type="button" onclick="PersonInfo.addPerson();">
          <g:message code="common.button.add"/>
        </button>
      </g:else>

    </div>
    <div class="clear"></div>
    <script type='text/javascript'>
      <!--
      $(function() {
        $("#fullName").focus();
      });
      // -->
    </script>
  </g:form>
</div>
