<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="includes.jsp" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Readers</title>
        <link rel="stylesheet" href="${ctx}/assets/css/style.css">
    </head>
    <body>
        <div class="layout">
            <jsp:include page="menu.jsp"/>
            <main class="main">
                <div class="top">
                    <h1>Reader Accounts</h1>
                </div>

                <c:if test="${not empty message}">
                    <div class="alert success">${message}</div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="alert error">${error}</div>
                </c:if>

                <form class="form" method="post" action="${ctx}/readers/save">
                    <input name="fullName" placeholder="Full name" required>
                    <input name="email" type="email" placeholder="Email">
                    <input name="phone" placeholder="Phone">
                    <input name="address" placeholder="Address">
                    <button class="btn alt">Add Reader</button>
                </form>

                <div class="table-wrap">
                    <table>
                        <tr>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Phone</th>
                            <th>Address</th>
                            <th>Action</th>
                        </tr>
                        <c:forEach items="${readers}" var="r">
                            <tr>
                                <td>${r.fullName}</td>
                                <td>${r.email}</td>
                                <td>${r.phone}</td>
                                <td>${r.address}</td>
                                <td>
                                    <a class="btn danger" href="${ctx}/readers/delete/${r.id}" onclick="return confirm('Delete this reader?')">Delete</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                </div>
            </main>
        </div>
    </body>
</html>
