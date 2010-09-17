<%@ page contentType="text/html;charset=UTF-8" %>
<div id="userPane">
  <div class="style_backLink">
    <a id="backToListLink" href="#" onclick='SelectionManager.backToList();'>
      <g:message code="common.label.backToList"/>
    </a>
  </div>

  <div id="userHeader">
    <div class="style_userFoto"></div>
    <div class="style_userAbout">
      <h1 id="headerFullName">${g.fieldValue(bean: person, field: "fullName")}</h1>
      <table>
        <tr>
          <td><g:message code="stage.label"/>:</td>
          <td><div id="headerStage"><g:message code="stage.${person.stage.codeName}"/></div></td>
        </tr>
        <tr>
          <td><div class='email'><g:message code="person.generic.emails"/>:</div></td>
          <td><span id="headerEmails">
            <g:set var="mails" value="${g.fieldValue(bean: person, field:'emails')?.tokenize(';')}"/>
            <g:each in="${mails}" var="link">
              <a href="mailto:${link}">${link.trim()}</a><g:if test="${link!=mails.last()}"><br/></g:if>
            </g:each>
          </span></td>
        </tr>
        <tr>
          <td><span class='phone'><g:message code="person.generic.phones"/>:</span></td>
          <td><span id="headerPhones">${g.fieldValue(bean: person, field: 'phones')}</span></td>
        </tr>
      </table>
    </div>
  </div>

  <div class="clear"></div>

  <div id="tabPane">
    <ul>
      <g:each in="${[
      [title: 'person.tab.timeline', controller:'person', action: 'timeline'],
      [title: 'person.tab.generic', controller:'person', action: 'edit'],
      [title: 'person.tab.documents', controller:'document', action: 'list'],
      [title: 'person.tab.scores', controller:'score', action: 'list',
                    linkAction: 'listAlone',
                    linkClass: 'openScores', altMessageCode:'score.tab.open'],
      [title: 'person.tab.notes', controller:'person', action: 'notes'],
      ]}" var="tab">
        <span class="tabTitle">
          <li>
            <g:link controller="${tab.controller}" action="${tab.action}" id="${person.id}">
              <span id="${tab.action}TabTitle">
                <g:message code="${tab.title}"/>
              </span>
            </g:link>
            <g:if test="${tab.linkAction}">
              <g:link controller="${tab.controller}" action="${tab.linkAction}" title="${message(code: tab.altMessageCode)}"
                  class="imageOnTab" target="_blank">
                <div class="${tab.linkClass}"></div>
              </g:link>
            </g:if>
          </li>
        </span>
      </g:each>
    </ul>
  </div>
</div>


<script type="text/javascript">
  $(function() {
    SelectionManager.setSelectedPerson("${person.id}", "${person.stage.id}");
    initTabs();
  });
</script>
