<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="includes.jsp" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Register</title>
        <link rel="stylesheet" href="${ctx}/assets/css/style.css">
    </head>
    <body>
        <div class="login">
            <section class="hero">
                <h1>Create Reader Account</h1>
                <p>Join the library system with a focused account for browsing books and tracking borrowing activity.</p>
            </section>
            <form class="login-card" method="post" action="${ctx}/register">
                <h2>Get started</h2>
                <input name="fullName" placeholder="Full name" required>
                <input name="username" placeholder="Username" required>
                <input name="password" type="password" placeholder="Password" required>
                <button class="btn full">Create account</button>
                <c:if test="${not empty error}">
                    <p class="error-text">${error}</p>
                </c:if>
                <p class="muted"><a href="${ctx}/login">Back to login</a></p>
            </form>
        </div>
    </body>
</html>
