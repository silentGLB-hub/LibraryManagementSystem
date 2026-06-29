<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="includes.jsp" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Catalog</title>
        <link rel="stylesheet" href="${ctx}/assets/css/style.css">
    </head>
    <body>
        <div class="layout">
            <jsp:include page="menu.jsp"/>
            <main class="main">
                <div class="top">
                    <h1>Catalog Management</h1>
                </div>

                <c:if test="${not empty message}">
                    <div class="alert success">${message}</div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="alert error">${error}</div>
                </c:if>

                <div class="charts">
                    <div class="card">
                        <h2>Categories</h2>
                        <form class="toolbar" method="post" action="${ctx}/catalog/category/save">
                            <input name="name" placeholder="Category name" required>
                            <button class="btn alt">Add</button>
                        </form>
                        <c:forEach items="${categories}" var="item">
                            <div class="list-row">
                                <span>${item.name}</span>
                                <a class="btn danger" href="${ctx}/catalog/category/delete/${item.id}">Delete</a>
                            </div>
                        </c:forEach>
                    </div>

                    <div class="card">
                        <h2>Authors</h2>
                        <form class="toolbar" method="post" action="${ctx}/catalog/author/save">
                            <input name="name" placeholder="Author name" required>
                            <button class="btn alt">Add</button>
                        </form>
                        <c:forEach items="${authors}" var="item">
                            <div class="list-row">
                                <span>${item.name}</span>
                                <a class="btn danger" href="${ctx}/catalog/author/delete/${item.id}">Delete</a>
                            </div>
                        </c:forEach>
                    </div>

                    <div class="card">
                        <h2>Publishers</h2>
                        <form class="toolbar" method="post" action="${ctx}/catalog/publisher/save">
                            <input name="name" placeholder="Publisher name" required>
                            <button class="btn alt">Add</button>
                        </form>
                        <c:forEach items="${publishers}" var="item">
                            <div class="list-row">
                                <span>${item.name}</span>
                                <a class="btn danger" href="${ctx}/catalog/publisher/delete/${item.id}">Delete</a>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </main>
        </div>
    </body>
</html>
