<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="includes.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Mượn / Trả sách</title>
    <link rel="stylesheet" href="${ctx}/assets/css/style.css">
</head>
<body>
<div class="layout">
    <jsp:include page="menu.jsp"/>
    <main class="main">
        <h1>Mượn / Trả sách</h1>
        <c:if test="${not empty message}"><div class="alert success">${message}</div></c:if>
        <c:if test="${not empty error}"><div class="alert error">${error}</div></c:if>
        <form class="form" method="post" action="${ctx}/borrow/create">
            <select name="bookId" required>
                <c:forEach items="${books}" var="b">
                    <c:choose>
                        <c:when test="${b.available <= 0}"><option value="${b.id}" disabled>${b.title} (${b.available} còn lại)</option></c:when>
                        <c:otherwise><option value="${b.id}">${b.title} (${b.available} còn lại)</option></c:otherwise>
                    </c:choose>
                </c:forEach>
            </select>
            <select name="readerId" required>
                <c:forEach items="${readers}" var="r"><option value="${r.id}">${r.fullName}</option></c:forEach>
            </select>
            <input type="date" name="borrowDate" required>
            <input type="date" name="dueDate" required>
            <button class="btn alt">Tạo phiếu mượn</button>
        </form>
        <div class="table-wrap">
            <table>
                <tr><th>Sách</th><th>Độc giả</th><th>Ngày mượn</th><th>Hạn trả</th><th>Ngày trả</th><th>Trạng thái</th><th>Tiền phạt</th><th>Thao tác</th></tr>
                <c:forEach items="${records}" var="r">
                    <tr>
                        <td>${r.bookTitle}</td><td>${r.readerName}</td><td>${r.borrowDate}</td><td>${r.dueDate}</td><td>${empty r.returnDate ? '-' : r.returnDate}</td>
                        <td><span class="badge">${fn:replace(fn:replace(fn:replace(r.status, 'BORROWING', 'Đang mượn'), 'OVERDUE', 'Quá hạn'), 'RETURNED', 'Đã trả')}</span></td>
                        <td>${r.fine}</td>
                        <td>
                            <a class="btn" href="${ctx}/borrow/print/${r.id}" target="_blank">In</a>
                            <c:if test="${r.status != 'RETURNED'}"><a class="btn alt" href="${ctx}/borrow/return/${r.id}">Trả sách</a></c:if>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </main>
</div>
</body>
</html>