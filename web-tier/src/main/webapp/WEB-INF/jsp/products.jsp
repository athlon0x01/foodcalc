<%--
  Products list view
 
  @author Anton Borovyk
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Products</title>
</head>
<body>
<h2 align="center">Products</h2>
<table align="center" border="1" width="100%">
    <tr>
        <th>Name</th>
        <th>Calorific</th>
        <th>Proteins</th>
        <th>Fats</th>
        <th>Carbs</th>
        <th>Default weight</th>
    </tr>
    <c:if test="${not empty categories}">
        <c:forEach items="${categories}" var="category">
            <tr><td><b><i>${category.name}</i></b></td></tr>
            <c:if test="${not empty category.products}">
                <c:forEach items="${category.products}" var="product">
                    <tr>
                        <td>${product.name}</td>
                        <td>${product.calorific}</td>
                        <td>${product.proteins}</td>
                        <td>${product.fats}</td>
                        <td>${product.carbs}</td>
                        <td>${product.defaultWeight}</td>
                    </tr>
                </c:forEach>
            </c:if>
        </c:forEach>
    </c:if>
</table>
</body>
</html>
