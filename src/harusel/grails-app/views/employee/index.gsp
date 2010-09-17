<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="performanceReviewLayout">
    <title><g:message code="performanceReview.employee.pageTitle"/></title>
</head>
<body>
<div id="noAjaxBlockMarker"></div>
<center>
    <div class="style_welcomePage">
        <h2>
            <g:message code="page.welcome.message" args="${[user.fullName]}"/>
        </h2>
        <g:if test="${user.assignedTemplate}">
            <g:if test="${user.assignedTemplate.questions}">
                <g:message code="performanceReview.template.linkToForm.label" args="${[user.assignedTemplate.name]}"/>
                <g:link action="blankForm">
                    <g:message code="performanceReview.template.linkToForm.name"/>
                </g:link>
            </g:if>
            <g:else>
                <h3 class="warningMessage">
                    <g:message code="performanceReview.template.hasNo.questions"/>
                </h3>
            </g:else>
        </g:if>
        <g:else>
            <h3 class="warningMessage">
                <g:message code="performanceReview.user.hasNo.template"/>
            </h3>
        </g:else>
        <g:if test="${user.manager}">
            <p>
                <g:message code="performanceReview.user.has.manager" args="${[user.manager.fullName]}"/>
            </p>
        </g:if>
        <g:else>
            <h3 class="warningMessage">
                <g:message code="performanceReview.user.hasNo.manager"/>
            </h3>
        </g:else>
    </div>
</center>
</body>
</html>