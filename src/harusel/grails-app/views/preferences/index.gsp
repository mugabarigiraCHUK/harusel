<%@ page contentType="text/html;charset=UTF-8" %>
<html>

<head>
    <title><g:message code="preferences.page.title"/></title>
    <meta name="layout" content="main">
    <!-- <g:javascript library="jquery"/> -->
</head>

<body>
<div class="messages-properties" style="display: none;">
    <div class="message-property common-button-cancel">${message(code: "common.button.cancel")}</div>
    <div class="message-property common-button-save">${message(code: "common.button.save")}</div>
    <div class="message-property common-button-ok">${message(code: "common.button.ok")}</div>
</div>
<center>
    <div id="page" class="style_main">
        <div id="headerPane" class="style_header">
            <div class="style_logo"></div>
            <div class="style_userBlock">
                <div id="topLinksPane" class="style_userBlockContent">
                    <div class="mainLinks">
                        <g:link controller="home"><g:message code="home.index"/></g:link>
                        &nbsp;&middot;&nbsp;
                    <g:link controller="login" action="signout"><g:message code="home.signout"/></g:link>
                    </div>
                </div>
            </div>
            <div id="notificationHolder" class="style_bodyNotification"></div>
        </div>

        <div id="contentPane" class="contentPane">
            <div id="preferenceTabPane">
                <input id="selectedPreferanceTab" type="hidden" value="${tab}"/>
                <ul>
                    <g:each in="${[
                      [action: 'vacancy', title: message(code: 'preference.index.vacancies'),controller:'preferences', specialClass:''],
                      [action: 'criteria', title: message(code: 'preference.index.criterias'),controller:'preferences', specialClass:''],
                      [action: 'source', title: message(code: 'preference.index.sources'),controller:'preferences', specialClass:''],
                      [action: 'index', title: message(code: 'preference.index.mailTemplate'),controller:'mailTextTemplate', specialClass:'separatedWindow'],
                      ]}" var="tab">
                        <span class="tabTitle">
                            <li>
                                <g:link controller="${tab.controller}" action="${tab.action}" id="${person?.id}" class="${tab.specialClass}">
                                    <span>${tab.title}</span>
                                </g:link>
                            </li>
                        </span>
                    </g:each>
                </ul>
            </div>
        </div>
    </div></center>
<div style="display:none;" id="helperElement">
</div>
<p:javascript src="javascript-bundle"/>
</body>
</html>
