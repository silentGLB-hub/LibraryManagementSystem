<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="includes.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Lịch sử mượn của tôi</title>
    <link rel="stylesheet" href="${ctx}/assets/css/style.css">
</head>
<body>
<div class="layout">
    <jsp:include page="menu.jsp"/>
    <main class="main">
        <div class="top"><h1>Lịch sử mượn của tôi</h1><span class="badge">${sessionScope.user.fullName} - Độc giả</span></div>
        <c:if test="${not empty message}"><div class="alert success">${message}</div></c:if>
        <c:if test="${not empty error}"><div class="alert error">${error}</div></c:if>
        <c:if test="${empty readerProfile}"><div class="alert warning">Tài khoản này chưa liên kết với hồ sơ độc giả. Hãy nhờ thủ thư tạo hồ sơ bằng họ tên hoặc email của bạn.</div></c:if>
        <c:if test="${not empty readerProfile}">
            <div class="card report-table">
                <h2>Thông tin liên hệ độc giả</h2>
                <p class="muted">Cập nhật email và số điện thoại dùng cho thông báo quá hạn và hồ sơ độc giả.</p>
                <form class="form" method="post" action="${ctx}/reader/contact">
                    <input type="email" name="email" value="${readerProfile.email}" placeholder="Email">
                    <input type="text" name="phone" value="${readerProfile.phone}" placeholder="Số điện thoại">
                    <button class="btn alt">Cập nhật liên hệ</button>
                </form>
            </div>
        </c:if>
        <div class="card report-table">
            <h2>Lịch sử mượn sách</h2>
            <div class="table-wrap"><table>
                <tr><th>Sách</th><th>Ngày mượn</th><th>Hạn trả</th><th>Ngày trả</th><th>Trạng thái</th><th>Tiền phạt</th><th>Đọc sách</th><th>Trả sách</th></tr>
                <c:forEach items="${records}" var="r"><tr>
                    <td>${r.bookTitle}</td><td>${r.borrowDate}</td><td>${r.dueDate}</td><td>${empty r.returnDate ? '-' : r.returnDate}</td>
                    <td><span class="badge">${fn:replace(fn:replace(fn:replace(r.status, 'BORROWING', 'Đang mượn'), 'OVERDUE', 'Quá hạn'), 'RETURNED', 'Đã trả')}</span></td>
                    <td>${r.fine}</td>
                    <td><c:choose><c:when test="${r.status != 'RETURNED'}"><a class="btn alt" href="${ctx}/reader/books/${r.bookId}/read">Đọc sách</a></c:when><c:otherwise><span class="muted">Đã trả</span></c:otherwise></c:choose></td>
                    <td><c:choose><c:when test="${r.readerReturnAllowed}"><form method="post" action="${ctx}/reader/return/${r.id}" style="margin:0"><button class="btn">Trả sách</button></form></c:when><c:when test="${r.status == 'OVERDUE'}"><span class="muted">Chỉ thủ thư xử lý</span></c:when><c:otherwise><span class="muted">-</span></c:otherwise></c:choose></td>
                </tr></c:forEach>
            </table></div>
        </div>
        <div class="card report-table">
            <h2>Sách hiện có</h2>
            <div class="table-wrap"><table>
                <tr><th>Tên sách</th><th>Thông tin tham chiếu</th><th>Còn lại</th><th>Xem trước</th><th>Yêu cầu mượn</th></tr>
                <c:forEach items="${books}" var="b"><tr>
                    <td><strong>${b.title}</strong><c:if test="${not empty b.chapters}"><div class="muted small-inline">Có mục lục</div></c:if></td>
                    <td><div class="reference-stack"><c:choose><c:when test="${not empty b.authorUrl}"><a href="${b.authorUrl}" target="_blank" rel="noopener">Tác giả: ${b.author}</a></c:when><c:otherwise><span>Tác giả: ${empty b.author ? '-' : b.author}</span></c:otherwise></c:choose><c:choose><c:when test="${not empty b.categoryUrl}"><a href="${b.categoryUrl}" target="_blank" rel="noopener">Thể loại: ${b.category}</a></c:when><c:otherwise><span>Thể loại: ${empty b.category ? '-' : b.category}</span></c:otherwise></c:choose><c:choose><c:when test="${not empty b.publisherUrl}"><a href="${b.publisherUrl}" target="_blank" rel="noopener">Nhà xuất bản: ${b.publisher}</a></c:when><c:otherwise><span>Nhà xuất bản: ${empty b.publisher ? '-' : b.publisher}</span></c:otherwise></c:choose></div></td>
                    <td>${b.available}/${b.quantity}</td><td><a class="btn" href="${ctx}/reader/books/${b.id}/preview">Xem trước</a></td>
                    <td><c:choose><c:when test="${b.available > 0 && not empty readerProfile}"><form method="post" action="${ctx}/reader/request" class="borrow-request-form"><input type="hidden" name="bookId" value="${b.id}"><input type="date" name="borrowDate" required><input type="date" name="dueDate" required><button class="btn alt">Gửi yêu cầu mượn</button></form></c:when><c:otherwise><span class="muted">Không khả dụng</span></c:otherwise></c:choose></td>
                </tr></c:forEach>
            </table></div>
        </div>
    </main>
</div>
</body>
</html>