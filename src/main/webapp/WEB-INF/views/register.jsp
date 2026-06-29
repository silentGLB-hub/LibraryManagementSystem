<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="includes.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Đăng ký</title>
    <link rel="stylesheet" href="${ctx}/assets/css/style.css">
</head>
<body>
<div class="login">
    <section class="hero">
        <h1>Tạo tài khoản độc giả</h1>
        <p>Tham gia hệ thống thư viện để tra cứu sách và theo dõi hoạt động mượn sách.</p>
    </section>
    <form class="login-card" method="post" action="${ctx}/register">
        <h2>Bắt đầu</h2>
        <input name="fullName" placeholder="Họ và tên" required>
        <input name="username" placeholder="Tên đăng nhập" required>
        <input name="password" type="password" placeholder="Mật khẩu" required>
        <button class="btn full">Tạo tài khoản</button>
        <c:if test="${not empty error}"><p class="error-text">${error}</p></c:if>
        <p class="muted"><a href="${ctx}/login">Quay lại đăng nhập</a></p>
    </form>
</div>
</body>
</html>