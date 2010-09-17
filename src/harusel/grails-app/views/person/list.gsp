<!--
<g:javascript library="jquery"/>
-->
<div id="personList">
  <g:if test="${flash.message}">
    <div class="notify">${flash.message}</div>
  </g:if>
  <g:if test="${request.query}">
    <g:message code="person.list.empty.search"/>
    <g:remoteLink action="add" controller="person" update="mainPane" method="post" params="[fullName: request.query?.encodeAsHTML()]">
      <g:message code="person.create.request"/>
    </g:remoteLink>
  </g:if>
  <g:if test="${personList.size()>0}">
    <div class="list">
      <table class="style_mainList">
        <thead>
        <tr>
          <th class="style_mainList_checkBox style_mainList_rgtBrd">&nbsp;</th>
          <th class="style_mainList_name style_mainList_rgtBrd" width="200px"><g:message code="person.list.fullName"/></th>
          <th class="style_mainList_rgtBrd" width="70px"><g:message code="person.list.stage"/></th>
          <th class="style_mainList_rgtBrd documentTypeColumn" width="100px"><g:message code="person.list.documents"/></th>
          <th class="style_mainList_comments"><g:message code="person.list.lastOpinions"/></th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${personList}" status="i" var="person">
          <tr class="${i % 2 ? 'odd' : 'even'}">
            <td class="checkboxCell">
              <input type="checkbox" id="${person.id}">
              <input type="hidden" value="${person.stage.id}">
            </td>
            <td
              <g:if test='${person.unread}'>class="unreadPerson"</g:if>>
              <div class="personListFullName">
                <g:remoteLink
                    class="linkToPersonInfo"
                    controller="home"
                    action="person"
                    id="${person.id}"
                    update="mainPane"
                    onSuccess="FilterManager.reload()">
                  ${fieldValue(bean: person, field: 'fullName')}
                </g:remoteLink>
              </div>
            </td>

            <td class="style_mainList_center" id="stageNameInList_${person.id}">
              <span title="<g:message code="stage.${person.stage.codeName}"/>">
                <g:message code="stage.shortname.${person.stage.codeName}"/>
              </span>
            </td>

            <td>
              <g:each in="${person.docs}" var="doc">
                <g:set var="docCssClass" value="${doc.id? '': 'inactiveButton'}"/>
                <g:set var="docTitle"><g:message code="document.type.${g.fieldValue(bean: doc.type, field: 'typeCode')}"/></g:set>
                <g:link controller="document" action="download" id="${doc.id}" title="${docTitle}">
                  <div class="${g.fieldValue(bean: doc.type, field: 'typeCode')} ${docCssClass}"></div>
                </g:link>
              </g:each>
            </td>
            <td>
              <ul class="opinions">
                <g:each in="${person.opinions}" var="opinion">
                  <li>
                    <div class="textOverflowContainer">
                      <g:if test="${opinion.type}">
                        <div>
                          <g:each in="${opinion.type}">
                            <div class="opinion opinion-${it}"></div>
                          </g:each>
                        </div>
                      </g:if>
                      <div style="float:left">
                        <span class="text-overflow">
                          ${opinion.text}
                        </span>
                      </div>
                      <div class="clear"></div>
                    </div>
                  </li>
                </g:each>
              </ul>
            </td>
          </tr>
        </g:each>
        </tbody>
      </table>
      <div id="pagging" class="paginateButtons">
        <g:if test="${personListSize}">
          <g:set var="additionFilterParams" value=""/>
          <g:if test="${params.vacancyId}">
            <g:set var="additionFilterParams" value="${[vacancyId: params.vacancyId]}"/>
          </g:if>
          <g:paginate controller="person" action="filter" id="${filterId}" params="${additionFilterParams}" total="${personListSize}"/>
        </g:if>
      </div>
      <script type="text/javascript">
        SelectionManager.init("${filterId}", "${params.vacancyId}", "${params.max}", "${params.offset}", "${query?.encodeAsHTML()}")
      </script>
    </div>
  </g:if>
  <g:else>
    <div class="notify">
      <g:message code="person.list.empty.list"/>
    </div>
  </g:else>
  <div class="clear"></div>
</div>
<div class="clear"></div>
