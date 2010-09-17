<div id="page-menu" class="style_leftMenu">

  <g:render template="/partial/addNewPersonWidget"/>
  <div id="filterPane" class="style_leftMenuContent">
    <g:render template="/partial/filters" controller="home" model="${[filters: filters]}"/>
  </div>
  <div class="clear"></div>
</div>
