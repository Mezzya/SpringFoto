<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: mezz
  Date: 26.11.16
  Time: 20:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<div align="center">
    <form action="/del_many" method="post">
    <table border="1">
        <tr>
            <td>#</td>
            <td>id</td>
            <td>pic</td>
            <td>action</td>
            <td>zip</td>
        </tr>

        <c:forEach var="pic" items="${list}" varStatus="i">
            <tr>
                <td>${i.count}</td>
                <td>${pic}</td>
                <td><img width="200" src="/photo/${pic}" /></td>
                <td><input type="checkbox" name="id" value="${pic}" /></td>
                <td><a href="/getzip?id=${pic}">get Zip</a></td>
            </tr>
        </c:forEach>


    </table>
        <input type="submit" value="Del checked photos"/>
    </form>
    <a href="/">Start page</a>


</div>

</body>
</html>
