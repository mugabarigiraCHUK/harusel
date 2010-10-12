<%@ page contentType="text/html;charset=UTF-8" %>
<div class="style_bodyBottom">
  <g:message code="common.footer.copyright" args="${[g.formatDate(format:'yyyy')]}"/>
  <span class="reportIssue">

    %{-- TODO: extract to configuration --}%
    <a href="https://code.google.com/p/harusel/issues/entry" target="_blank">
      <g:message code="common.report.error"/>
    </a>
  </span>
</div>
