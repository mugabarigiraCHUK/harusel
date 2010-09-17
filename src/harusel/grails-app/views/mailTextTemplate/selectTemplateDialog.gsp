<%@ page contentType="text/html;charset=UTF-8" %>
<g:each in="${templates}" var="group">
    <div class="template">
        <div class="templateGroupTitle">
            ${group.key}
        </div>
        <ul class="templateList">
            <g:each in="${group.value}" var="template">
                <li class="templateName">
                    <a href="#" class="templateNameLink" id="template_${template.id}">
                        ${template.name}
                    </a>
                </li>
            </g:each>
        </ul>
    </div>
</g:each>
