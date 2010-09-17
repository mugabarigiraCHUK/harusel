<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <meta name="layout" content="performanceReviewLayout">
  <link rel="stylesheet" type="text/css" href="${resource(dir: 'css', file: 'reviewForm.css')}">
  <title><g:message code="performanceReview.title.reviewPage"/></title>
  <g:each in="${[
    'jquery/jquery-1.3.2.js',
    'jquery/jquery-ui-1.7.2.custom.min.js',
    'jquery/jquery.livequery.js',
    'reviewForm.js',
    ]}">
    <g:javascript src="${it}"/>
  </g:each>
</head>
<body>
<g:render template="/partial/notificationArea"/>

<g:form controller="review" action="saveComments" method="POST">
  <div id="PRAnketa">
    <div class="message">
      <div class="message-icon"></div>
      <g:message code="performanceReview.message.review.howTo"/>
    </div>
    <input type="hidden" name="formId" value="${formId}"/>
    <div class="formHeader">
      <input onclick="window.close();" class="formSubmit" type="button" value="<g:message code='common.button.close'/>"/>
      <input class="formSubmit" type="submit" value="<g:message code='common.button.save'/>"/>
      <div class="employeeFullName">
        <g:message code="performanceReview.label.employee" args="${[userName?.encodeAsHTML()]}"/>
      </div>
      <div class="publishingDate">
        <g:message code="performanceReview.label.publishDate" args="${[publishDate]}"/>
      </div>
    </div>
    <table class="reviewFormTable">
      <col width="5%">
      <col width="35%">
      <col width="5%">
      <col width="5%">
      <col width="25%">
      <col width="25%">
      <thead>

      <tr>
        <th><g:message code="performanceReview.number"/></th>
        <th><g:message code="performanceReview.question"/></th>
        <th><g:message code="performanceReview.relevanceShorted"/></th>
        <th><g:message code="performanceReview.satisfactionShorted"/></th>
        <th><g:message code="performanceReview.userComments"/></th>
        <th><g:message code="performanceReview.managerComments"/></th>
      </tr>
      </thead>
      <tbody>
      <g:each var="group" in="${commentsByGroupMap}" status="index">
        <g:set var="groupN">${index + 1}</g:set>
        <tr class="group">
          <td>${groupN}</td>
          <td colspan="6">${group.key.text?.encodeAsHTML()}</td>
        </tr>
        <g:each var="comment" in="${group.value}" status="questionIndex">
          <g:set var="questionN">${groupN}.${questionIndex + 1}</g:set>
          <g:set var="oddClass" value='${questionIndex%2 ? "odd" : ""}'/>
          <tr class="${oddClass}">
            <td>${questionN}</td>
            <td>${comment.questionText?.encodeAsHTML()}</td>

            <td align="center" class="relevance${comment.relevance}"><span>${comment.relevance}</span></td>
            <td align="center" class="relevance${comment.relevance} satisfaction${comment.satisfaction}"><span>${comment.satisfaction}</span></td>
            <td class="commentsColumn">
              <g:if test="${comment.userComment}">
                <div>
                  <pre>${comment.userComment?.encodeAsHTML()}</pre>
                </div>
              </g:if>
              <g:else>
                <div>
                  <pre>&nbsp;</pre>
                </div>
              </g:else>
            </td>
            <td class="commentsColumn transformationText">
              <div>
                <pre>${comment.managerComment?.encodeAsHTML()}</pre>
              </div>
              <input type="hidden" name="comment_${comment.answerId}" value="${comment.managerComment?.encodeAsHTML()}"/>
            </td>
          </tr>
        </g:each>
      </g:each>
      </tbody>
    </table>
  </div>
</g:form>
</body>
</html>
