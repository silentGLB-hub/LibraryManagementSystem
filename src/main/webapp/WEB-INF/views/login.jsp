<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="includes.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Đăng nhập thư viện</title>
    <link rel="stylesheet" href="${ctx}/assets/css/style.css">
</head>
<body>
<div class="login">
    <section class="hero">
        <h1>Hệ thống quản lý thư viện</h1>
        <p>Quản lý sách, độc giả, phiếu mượn, tiền phạt quá hạn, báo cáo và nhắc nhở email trong một hệ thống Spring MVC gọn gàng.</p>
    </section>
    <form class="login-card" method="post" action="${ctx}/login">
        <h2>Chào mừng trở lại</h2>
        <p class="muted">Quản trị viên / Thủ thư / Độc giả</p>
        <input name="username" placeholder="Tên đăng nhập" required>
        <input name="password" type="password" placeholder="Mật khẩu" required>
        <button class="btn full">Đăng nhập</button>
        <c:if test="${not empty error}"><p class="error-text">${error}</p></c:if>
        <c:if test="${not empty message}"><p class="muted">${message}</p></c:if>
        <p class="muted"><a href="${ctx}/register">Tạo tài khoản độc giả</a></p>
    </form>
</div>
</body>
</html>