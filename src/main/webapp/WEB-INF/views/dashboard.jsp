<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="includes.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Bảng điều khiển</title>
    <link rel="stylesheet" href="${ctx}/assets/css/style.css">
</head>
<body>
<div class="layout">
    <jsp:include page="menu.jsp"/>
    <main class="main">
        <div class="top"><h1>Bảng điều khiển</h1><span class="badge">${sessionScope.user.fullName} - ${sessionScope.user.role}</span></div>
        <div class="grid">
            <div class="card"><b>Tổng số sách</b><div class="stat">${books.size()}</div></div>
            <div class="card"><b>Tổng số độc giả</b><div class="stat">${readers.size()}</div></div>
            <div class="card"><b>Phiếu mượn</b><div class="stat">${records.size()}</div></div>
        </div>
        <div class="charts">
            <div class="card"><h2>Trạng thái mượn</h2><c:forEach items="${statusStats}" var="s"><div class="bar-row"><span>${fn:replace(fn:replace(fn:replace(s.label, 'BORROWING', 'Đang mượn'), 'OVERDUE', 'Quá hạn'), 'RETURNED', 'Đã trả')}</span><div class="bar"><i style="width:${s.percent}%"></i></div><strong>${s.value}</strong></div></c:forEach></div>
            <div class="card"><h2>Sách mượn nhiều</h2><c:forEach items="${topBooks}" var="b"><div class="bar-row"><span>${b.label}</span><div class="bar"><i style="width:${b.percent}%"></i></div><strong>${b.value}</strong></div></c:forEach></div>
        </div>
        <div class="card report-table"><h2>Sách gần đây</h2><div class="table-wrap"><table><tr><th>Mã sách</th><th>Tên sách</th><th>Còn lại</th></tr><c:forEach items="${books}" var="b"><tr><td>${b.code}</td><td>${b.title}</td><td>${b.available}/${b.quantity}</td></tr></c:forEach></table></div></div>
    </main>
</div>
</body>
</html>