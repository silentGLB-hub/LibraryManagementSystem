<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="includes.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Người dùng & vai trò</title>
    <link rel="stylesheet" href="${ctx}/assets/css/style.css">
</head>
<body>
<div class="layout">
    <jsp:include page="menu.jsp"/>
    <main class="main">
        <div class="top">
            <h1>Người dùng & vai trò</h1>
            <span class="badge">Chỉ quản trị viên</span>
        </div>
        <c:if test="${not empty message}"><div class="alert success">${message}</div></c:if>
        <c:if test="${not empty error}"><div class="alert error">${error}</div></c:if>
        <div class="card report-table">
            <h2>Tài khoản hệ thống</h2>
            <p class="muted">Quản trị viên có thể quản lý vai trò, email và số điện thoại của toàn bộ người dùng.</p>
            <div class="table-wrap">
                <table>
                    <tr><th>Tên đăng nhập</th><th>Họ và tên</th><th>Liên hệ</th><th>Vai trò hiện tại</th><th>Đổi vai trò</th><th>Thao tác</th></tr>
                    <c:forEach items="${users}" var="u">
                        <tr>
                            <td><strong>${u.username}</strong></td>
                            <td>${u.fullName}</td>
                            <td>
                                <form class="toolbar" method="post" action="${ctx}/users/${u.id}/contact" style="margin:0;gap:8px;flex-wrap:wrap">
                                    <input type="email" name="email" value="${u.email}" placeholder="Email" style="min-width:190px">
                                    <input type="text" name="phone" value="${u.phone}" placeholder="Số điện thoại" style="min-width:140px">
                                    <button class="btn alt">Lưu</button>
                                </form>
                            </td>
                            <td><span class="badge"><c:choose><c:when test="${u.role == 'ADMIN'}">Quản trị viên</c:when><c:when test="${u.role == 'LIBRARIAN'}">Thủ thư</c:when><c:otherwise>Độc giả</c:otherwise></c:choose></span></td>
                            <td>
                                <form class="toolbar" method="post" action="${ctx}/users/${u.id}/role" style="margin:0;gap:8px">
                                    <select name="role">
                                        <option value="ADMIN" ${u.role == 'ADMIN' ? 'selected' : ''}>Quản trị viên</option>
                                        <option value="LIBRARIAN" ${u.role == 'LIBRARIAN' ? 'selected' : ''}>Thủ thư</option>
                                        <option value="READER" ${u.role == 'READER' ? 'selected' : ''}>Độc giả</option>
                                    </select>
                                    <button class="btn">Cập nhật</button>
                                </form>
                            </td>
                            <td><c:if test="${u.id != sessionScope.user.id}"><a class="btn danger" href="${ctx}/users/delete/${u.id}" onclick="return confirm('Xóa người dùng này?')">Xóa</a></c:if></td>
                        </tr>
                    </c:forEach>
                </table>
            </div>
        </div>
    </main>
</div>
</body>
</html>