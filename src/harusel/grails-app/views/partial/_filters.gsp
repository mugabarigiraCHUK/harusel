<%@ page contentType="text/html;charset=UTF-8" %>
<!--
<g:javascript library="jquery"/>
-->
%{-- TODO: Refactor in order to remove itemId attribute--}%
<g:each in="${filters}" var="filter">
  <div class="filter">
    <div class="parentFilter">
      <h3><g:remoteLink
          action="filter"
          class="header"
          controller="person"
          id="${filter.id}"
          update="mainPane"
          itemId="item_${filter.id}">
        ${filter.name}
      </g:remoteLink></h3>

      <div class="viewSubFiltresIcon"></div>
    </div>

    <div class="clear"></div>

    <div class="subfilters">
      <g:each in="${filter.vacancyList}" var="vacancy">
        <p>
          <g:remoteLink
              class="subfilter"
              action="filter"
              controller="person"
              id="${filter.id}"
              update="mainPane"
              params="{vacancyId: '${vacancy.id}'}"
              itemId="item_${filter.id}_${vacancy.id}">${vacancy.name}</g:remoteLink>
        </p>
      </g:each>
    </div>
  </div>
</g:each>
