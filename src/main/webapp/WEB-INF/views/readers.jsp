<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="includes.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Độc giả</title>
    <link rel="stylesheet" href="${ctx}/assets/css/style.css">
</head>
<body>
<div class="layout">
    <jsp:include page="menu.jsp"/>
    <main class="main">
        <div class="top"><h1>Tài khoản độc giả</h1></div>
        <c:if test="${not empty message}"><div class="alert success">${message}</div></c:if>
        <c:if test="${not empty error}"><div class="alert error">${error}</div></c:if>
        <form class="form" method="post" action="${ctx}/readers/save">
            <input name="fullName" placeholder="Họ và tên" required>
            <input name="email" type="email" placeholder="Email">
            <input name="phone" placeholder="Số điện thoại">
            <input name="address" placeholder="Địa chỉ">
            <button class="btn alt">Thêm độc giả</button>
        </form>
        <div class="table-wrap">
            <table>
                <tr><th>Họ tên</th><th>Email</th><th>Số điện thoại</th><th>Địa chỉ</th><th>Thao tác</th></tr>
                <c:forEach items="${readers}" var="r">
                    <tr>
                        <td>${r.fullName}</td>
                        <td>${r.email}</td>
                        <td>${r.phone}</td>
                        <td>${r.address}</td>
                        <td><a class="btn danger" href="${ctx}/readers/delete/${r.id}" onclick="return confirm('Xóa độc giả này?')">Xóa</a></td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </main>
</div>
</body>
</html>