<html>
<head>
    <title>Welcome to Migration page</title>
    <meta name="layout" content="main"/>
</head>
<body>
<center>

    <div class="style_main">
        <div id="headerPane" class="style_header">

            <div class="style_logo"></div>
            <div class="style_userBlock">
                <div id="topLinksPane" class="style_userBlockContent">
                    <g:link controller="home"><g:message code="migration.index.homePage"/></g:link>
                    &nbsp;&middot;&nbsp;<g:link controller="login" action="signout"><g:message code="home.signout"/></g:link>
                </div>
            </div>

        </div>

        <div class="clear"></div>

        <div class="style_migrBody">
            <h1>Миграция с 1С</h1>
            <g:if test="${request.message}">
                <div class="message">
                    <div class="message-icon"></div>
                    <span>${request.message}</span>
                </div>
            </g:if>
            <g:hasErrors bean="${command}">
                <div class="errors">
                    <h1>Ошибки:</h1>
                    <g:renderErrors bean="${command}"/>
                </div>
            </g:hasErrors>
            <g:form action="migrate" method="post">
                <p>Укажите Excel файл:</p>
                <input type="text" name="fileName" class="style_migrInp" value="/var/tmp/hrtool/1Cdb.xls"/><br/>
                <p>Укажите директорию с документами:</p>
                <input type="text" name="documentsDir" class="style_migrInp" value="/var/tmp/hrtool/"/>
                <input title="Migrate" class="style_mgrButton" type="submit" value="">
            </g:form>
        </div>
    </div></center></body>
</html>