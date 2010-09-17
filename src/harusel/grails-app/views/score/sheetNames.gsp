<%@ page contentType="text/html;charset=UTF-8" %>
<div id="dialog" title='Sheet Names'>
    <h3>
        Choose one:
    </h3>
    <g:radioGroup name="sheetName" labels="${sheetNames}" values="${sheetNames}">
        <p><%=it.radio%> <%=it.label%></p>
    </g:radioGroup>

</div>