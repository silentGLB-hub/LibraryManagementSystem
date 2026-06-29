<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="includes.jsp" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Xem trước sách</title>
        <link rel="stylesheet" href="${ctx}/assets/css/style.css">
    </head>
    <body>
        <div class="layout">
            <jsp:include page="menu.jsp"/>
            <main class="main reading-main">
                <div class="top"><div><h1>${book.title}</h1><p class="muted">Xem trước 1-2 trang và mục lục sách</p></div><a class="btn" href="${ctx}/reader/history">Quay lại</a></div>
                <div class="reading-grid">
                    <section class="card reading-card"><h2>Xem trước</h2><c:choose><c:when test="${not empty book.previewText}"><div class="book-text preview-text">${book.previewText}</div></c:when><c:otherwise><div class="empty-state">Sách này chưa có nội dung xem trước.</div></c:otherwise></c:choose></section>
                    <aside class="card chapter-card"><h2>Mục lục</h2><c:choose><c:when test="${not empty book.chapters}"><div class="chapter-list">${book.chapters}</div></c:when><c:otherwise><div class="empty-state">Chưa có mục lục.</div></c:otherwise></c:choose><h2>Thông tin tham chiếu</h2><div class="reference-stack big"><c:if test="${not empty book.authorUrl}"><a href="${book.authorUrl}" target="_blank" rel="noopener">Tác giả: ${book.author}</a></c:if><c:if test="${not empty book.categoryUrl}"><a href="${book.categoryUrl}" target="_blank" rel="noopener">Thể loại: ${book.category}</a></c:if><c:if test="${not empty book.publisherUrl}"><a href="${book.publisherUrl}" target="_blank" rel="noopener">Nhà xuất bản: ${book.publisher}</a></c:if></div></aside>
                </div>
            </main>
        </div>
    </body>
</html>