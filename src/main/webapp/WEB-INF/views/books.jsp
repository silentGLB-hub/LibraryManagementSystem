<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="includes.jsp" %>
<c:set var="isReader" value="${sessionScope.user.role == 'READER'}" />
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Sách</title>
        <link rel="stylesheet" href="${ctx}/assets/css/style.css">
    </head>
    <body>
        <div class="layout">
            <jsp:include page="menu.jsp"/>
            <main class="main">
                <div class="top"><h1>${canManage ? 'Quản lý sách' : 'Tìm kiếm sách'}</h1></div>
                <c:if test="${not empty message}"><div class="alert success">${message}</div></c:if>
                <c:if test="${not empty error}"><div class="alert error">${error}</div></c:if>
                    <form class="toolbar" method="get">
                        <input name="q" value="${q}" placeholder="Tìm theo tên sách, mã sách, thể loại, tác giả, nhà xuất bản">
                    <button class="btn">Tìm kiếm</button>
                </form>
                <c:if test="${canManage}">
                    <form class="book-form" method="post" action="${ctx}/books/save" enctype="multipart/form-data">
                        <input name="code" placeholder="Mã sách" required>
                        <input name="title" placeholder="Tên sách" required>
                        <select name="categoryId"><option value="0">Không có thể loại</option><c:forEach items="${categories}" var="c"><option value="${c.id}">${c.name}</option></c:forEach></select>
                        <select name="authorId"><option value="0">Không có tác giả</option><c:forEach items="${authors}" var="a"><option value="${a.id}">${a.name}</option></c:forEach></select>
                        <select name="publisherId"><option value="0">Không có nhà xuất bản</option><c:forEach items="${publishers}" var="p"><option value="${p.id}">${p.name}</option></c:forEach></select>
                            <input name="quantity" type="number" min="0" placeholder="Số lượng" required>
                            <input name="coverImage" placeholder="URL/đường dẫn ảnh bìa">
                            <input name="coverFile" type="file" accept="image/*">
                            <input name="pdfFile" type="file" accept="application/pdf">
                            <textarea name="previewText" placeholder="Nội dung xem trước: 1-2 trang"></textarea>
                            <textarea name="chapters" placeholder="Chương / mục lục, mỗi dòng một mục"></textarea>
                            <textarea name="contentText" placeholder="Nội dung đầy đủ cho độc giả đã mượn"></textarea>
                            <button class="btn alt">Thêm sách</button>
                        </form>
                </c:if>
                <div class="table-wrap">
                    <table>
                        <tr>
                            <th>Bìa sách</th><th>Mã sách</th><th>Tên sách</th><th>Thông tin tham chiếu</th><th>Số lượng</th><th>Còn lại</th>
                            <c:if test="${canManage}"><th>Nội dung đọc</th><th>Tệp PDF</th><th>Quản lý bìa</th><th>Thao tác</th></c:if>
                            <c:if test="${isReader}"><th>Thao tác của độc giả</th></c:if>
                            </tr>
                        <c:forEach items="${books}" var="b">
                            <tr>
                                <td><c:choose><c:when test="${not empty b.coverImage && (fn:startsWith(b.coverImage, 'http://') || fn:startsWith(b.coverImage, 'https://') || fn:startsWith(b.coverImage, 'data:'))}"><img class="cover" src="${b.coverImage}" alt="${b.title}"></c:when><c:when test="${not empty b.coverImage}"><img class="cover" src="${ctx}${b.coverImage}" alt="${b.title}"></c:when><c:otherwise><img class="cover" src="${ctx}/assets/img/book1.svg" alt="${b.title}"></c:otherwise></c:choose></td>
                                <td>${b.code}</td>
                                <td><strong>${b.title}</strong><c:if test="${not empty b.chapters}"><div class="muted small-inline">Có mục lục</div></c:if><c:if test="${not empty b.previewText}"><div class="muted small-inline">Có bản xem trước</div></c:if><c:if test="${not empty b.pdfFile}"><div class="muted small-inline">Đã tải PDF</div></c:if></td>
                                <td><div class="reference-stack"><c:choose><c:when test="${not empty b.authorUrl}"><a href="${b.authorUrl}" target="_blank" rel="noopener">Tác giả: ${b.author}</a></c:when><c:otherwise><span>Tác giả: ${empty b.author ? '-' : b.author}</span></c:otherwise></c:choose><c:choose><c:when test="${not empty b.categoryUrl}"><a href="${b.categoryUrl}" target="_blank" rel="noopener">Thể loại: ${b.category}</a></c:when><c:otherwise><span>Thể loại: ${empty b.category ? '-' : b.category}</span></c:otherwise></c:choose><c:choose><c:when test="${not empty b.publisherUrl}"><a href="${b.publisherUrl}" target="_blank" rel="noopener">Nhà xuất bản: ${b.publisher}</a></c:when><c:otherwise><span>Nhà xuất bản: ${empty b.publisher ? '-' : b.publisher}</span></c:otherwise></c:choose></div></td>
                                <td>${b.quantity}</td><td>${b.available}</td>
                                <c:if test="${canManage}">
                                    <td><form class="reading-update" method="post" action="${ctx}/books/${b.id}/reading"><textarea name="previewText" placeholder="Xem trước 1-2 trang">${b.previewText}</textarea><textarea name="chapters" placeholder="Mục lục">${b.chapters}</textarea><textarea name="contentText" placeholder="Nội dung đầy đủ">${b.contentText}</textarea><button class="btn alt">Lưu nội dung</button></form></td>
                            <td><form class="pdf-upload" method="post" action="${ctx}/books/${b.id}/pdf" enctype="multipart/form-data"><input name="pdfFile" type="file" accept="application/pdf" required><button class="btn">Tải PDF lên</button><c:if test="${not empty b.pdfFile}"><a class="btn alt" href="${ctx}${b.pdfFile}" target="_blank" rel="noopener">Mở PDF</a></c:if></form></td>
                            <td><form class="inline-upload" method="post" action="${ctx}/books/${b.id}/cover" enctype="multipart/form-data"><input name="coverImage" placeholder="URL/đường dẫn"><input name="coverFile" type="file" accept="image/*"><button class="btn">Cập nhật</button></form></td>
                            <td><a class="btn danger" href="${ctx}/books/delete/${b.id}" onclick="return confirm('Xóa sách này?')">Xóa</a></td>
                                </c:if>
                                <c:if test="${isReader}">
                            <td><div class="reader-actions"><a class="btn" href="${ctx}/reader/books/${b.id}/preview">Xem trước</a><c:choose><c:when test="${b.available > 0}"><form method="post" action="${ctx}/reader/request" class="borrow-request-form"><input type="hidden" name="bookId" value="${b.id}"><input type="date" name="borrowDate" required><input type="date" name="dueDate" required><button class="btn alt">Gửi yêu cầu mượn</button></form></c:when><c:otherwise><span class="muted">Không khả dụng</span></c:otherwise></c:choose></div></td>
                                            </c:if>
                    </tr>
                        </c:forEach>
            </table>
        </div>
    </main>
</div>
</body>
</html>