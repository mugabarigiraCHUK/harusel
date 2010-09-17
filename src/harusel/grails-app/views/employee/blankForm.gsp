<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <meta name="layout" content="performanceReviewLayout">
  <title><g:message code="performanceReview.employee.pageTitle"/></title>
  <link rel="stylesheet" type="text/css" href="${resource(dir: 'css', file: 'reviewForm.css')}">
</head>
<body>
<div class="messages-properties" style="display: none;">
  <div class="message-property performanceReview-label-waitRespond">${message(code: "performanceReview.label.waitRespond")}</div>
</div>
<div id="noAjaxBlockMarker"></div>
<div id="baseUrl" style="display:none;">${request.getContextPath()}</div>
<g:if test="${questions}">
  <div id="PRAnketa">
    <div class="message">
      <div class="message-icon"></div>
      <g:message code="performanceReview.message.howTo"/>
    </div>
    <table>
      <col width="5%">
      <col width="25%">
      <col width="10%">
      <col width="10%">
      <col width="50%">
      <thead>
      <tr>
        <th><g:message code="performanceReview.number"/></th>
        <th><g:message code="performanceReview.question"/></th>
        <th><g:message code="performanceReview.relevance"/></th>
        <th><g:message code="performanceReview.satisfaction"/></th>
        <th><g:message code="performanceReview.userComments"/></th>
      </tr>
      </thead>
      <tbody>
      <g:each in="${questions}" var="group" status="i">
        <g:set var="groupN" value="${i+1}"/>
        <tr class="groupRow">
          <td class="numberColumn">${groupN}.</td>
          <td colspan="4">${group.key.text}</td>
        </tr>
        <g:each in="${group.value}" var="question" status="k">
          <g:set var="questionN">${groupN}.${k + 1}</g:set>
          <g:set var="rowClass" value='${k%2?"odd":""}'/>
          <g:set var="answer" value="${answersByQuestionIdMap[question.id]}"/>
          <tr class="${rowClass}">
            <td class="numberColumn">${questionN}</td>
            <td class="questionColumn">${question.text?.encodeAsHTML()}</td>
            <td class="answerColumn">
              <div class="slider" id="relevance_${question.id}"></div>
              <input type="hidden" value="${answer?.relevance ?: 0}"/>
            </td>
            <td class="answerColumn">
              <div class="slider" id="satisfaction_${question.id}"></div>
              <input type="hidden" value="${answer?.satisfaction ?: 0}"/>
            </td>
            <td class="commentsColumn transformationText" id="comment_${question.id}">
              <div><pre>${g.fieldValue(bean: answer, field: 'comment')}</pre></div>
            </td>
          </tr>
        </g:each>
      </g:each>
      </tbody>
    </table>
  </div>
</g:if>
<g:else>
  <div class="notify">
    <g:message code="performanceReview.noAssignedTemplate"/>
  </div>
</g:else>

</body>
</html>
