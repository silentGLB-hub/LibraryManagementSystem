<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="includes.jsp" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Library Login</title>
        <link rel="stylesheet" href="${ctx}/assets/css/style.css">
    </head>
    <body>
        <div class="login">
            <section class="hero">
                <h1>Library Management System</h1>
                <p>Manage books, readers, borrow records, overdue fines, reports, and email reminders in one clean Spring MVC workspace.</p>
            </section>
            <form class="login-card" method="post" action="${ctx}/login">
                <h2>Welcome back</h2>
                <p class="muted">Admin / Librarian / Reader</p>
                <input name="username" placeholder="Username" required>
                <input name="password" type="password" placeholder="Password" required>
                <button class="btn full">Login</button>
                <c:if test="${not empty error}">
                    <p class="error-text">${error}</p>
                </c:if>
                <c:if test="${not empty message}">
                    <p class="muted">${message}</p>
                </c:if>
                
                <p class="muted"><a href="${ctx}/register">Create reader account</a></p>
            </form>
        </div>
    </body>
</html>
