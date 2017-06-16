<%--
  Products Category list view
 
  @author Anton Borovyk
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Product categories</title>
</head>
<body>
<h2 align="center">Product categories</h2>
<table align="center" border="1">
    <tr>
        <th>Name</th>
    </tr>
    <c:if test="${not empty categories}">
        <c:forEach items="${categories}" var="category">
            <tr><td><b><i>${category.name}</i></b></td></tr>
        </c:forEach>
    </c:if>
</table>
</body>
</html>
