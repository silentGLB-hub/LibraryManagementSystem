<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="includes.jsp" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Danh mục</title>
    <link rel="stylesheet" href="${ctx}/assets/css/style.css">
</head>
<body>
<div class="layout">
    <jsp:include page="menu.jsp"/>
    <main class="main">
        <div class="top">
            <h1>Danh mục Management</h1>
            <span class="badge">Liên kết tham chiếu</span>
        </div>

        <c:if test="${not empty message}"><div class="alert success">${message}</div></c:if>
        <c:if test="${not empty error}"><div class="alert error">${error}</div></c:if>

        <div class="charts catalog-grid">
            <div class="card catalog-card">
                <h2>Thể loại</h2>
                <form class="catalog-form" method="post" action="${ctx}/catalog/category/save">
                    <input name="name" placeholder="Tên thể loại" required>
                    <input name="infoUrl" placeholder="URL thông tin">
                    <button class="btn alt">Thêm</button>
                </form>
                <c:forEach items="${categories}" var="item">
                    <form class="catalog-row" method="post" action="${ctx}/catalog/category/update/${item.id}">
                        <input name="name" value="${item.name}" required>
                        <input name="infoUrl" value="${item.infoUrl}" placeholder="URL thông tin">
                        <c:if test="${not empty item.infoUrl}"><a class="btn" href="${item.infoUrl}" target="_blank" rel="noopener">Mở</a></c:if>
                        <button class="btn alt">Lưu</button>
                        <a class="btn danger" href="${ctx}/catalog/category/delete/${item.id}">Xóa</a>
                    </form>
                </c:forEach>
            </div>

            <div class="card catalog-card">
                <h2>Tác giả</h2>
                <form class="catalog-form" method="post" action="${ctx}/catalog/author/save">
                    <input name="name" placeholder="Tên tác giả" required>
                    <input name="infoUrl" placeholder="URL thông tin">
                    <button class="btn alt">Thêm</button>
                </form>
                <c:forEach items="${authors}" var="item">
                    <form class="catalog-row" method="post" action="${ctx}/catalog/author/update/${item.id}">
                        <input name="name" value="${item.name}" required>
                        <input name="infoUrl" value="${item.infoUrl}" placeholder="URL thông tin">
                        <c:if test="${not empty item.infoUrl}"><a class="btn" href="${item.infoUrl}" target="_blank" rel="noopener">Mở</a></c:if>
                        <button class="btn alt">Lưu</button>
                        <a class="btn danger" href="${ctx}/catalog/author/delete/${item.id}">Xóa</a>
                    </form>
                </c:forEach>
            </div>

            <div class="card catalog-card">
                <h2>Nhà xuất bản</h2>
                <form class="catalog-form" method="post" action="${ctx}/catalog/publisher/save">
                    <input name="name" placeholder="Tên nhà xuất bản" required>
                    <input name="infoUrl" placeholder="URL thông tin">
                    <button class="btn alt">Thêm</button>
                </form>
                <c:forEach items="${publishers}" var="item">
                    <form class="catalog-row" method="post" action="${ctx}/catalog/publisher/update/${item.id}">
                        <input name="name" value="${item.name}" required>
                        <input name="infoUrl" value="${item.infoUrl}" placeholder="URL thông tin">
                        <c:if test="${not empty item.infoUrl}"><a class="btn" href="${item.infoUrl}" target="_blank" rel="noopener">Mở</a></c:if>
                        <button class="btn alt">Lưu</button>
                        <a class="btn danger" href="${ctx}/catalog/publisher/delete/${item.id}">Xóa</a>
                    </form>
                </c:forEach>
            </div>
        </div>
    </main>
</div>
</body>
</html>