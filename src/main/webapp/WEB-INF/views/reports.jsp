<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="includes.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Báo cáo</title>
    <link rel="stylesheet" href="${ctx}/assets/css/style.css?v=report-combo-2">
</head>
<body>
<div class="layout">
    <jsp:include page="menu.jsp"/>
    <main class="main reports-main">
        <div class="report-hero">
            <div><span class="eyebrow">Thống kê thư viện</span><h1>Báo cáo thống kê</h1><p>Theo dõi xu hướng mượn sách, hoạt động quá hạn, mức độ sử dụng của độc giả và dữ liệu giao dịch có thể xuất file.</p></div>
            <div class="actions report-actions"><a class="btn" href="${ctx}/reports/export/excel">Xuất Excel</a><a class="btn" href="${ctx}/reports/export/pdf">Xuất PDF</a><c:choose><c:when test="${smtpConfigured}"><a class="btn alt" href="${ctx}/reports/reminders">Gửi email nhắc quá hạn</a></c:when><c:otherwise><span class="btn disabled">Gửi email nhắc quá hạn</span></c:otherwise></c:choose></div>
        </div>
        <c:if test="${not empty message}"><div class="alert success">${message}</div></c:if>
        <c:if test="${not empty warning}"><div class="alert warning">${warning}</div></c:if>
        <c:if test="${not empty error}"><div class="alert error">${error}</div></c:if>
        <div class="report-kpis">
            <div class="card metric-card"><span class="metric-label">Giao dịch</span><div class="stat">${records.size()}</div><small>Tổng số phiếu mượn</small></div>
            <div class="card metric-card danger-metric"><span class="metric-label">Quá hạn</span><div class="stat">${overdueRecords.size()}</div><small>Phiếu cần chú ý</small></div>
            <div class="card metric-card"><span class="metric-label">Sách nổi bật</span><div class="stat">${topBooks.size()}</div><small>Xếp hạng theo số lượt mượn</small></div>
        </div>
        <section class="card chart-card combo-chart-card analytics-panel">
            <div class="section-head"><div><h2>Số sách mượn theo tháng</h2><p class="muted">Cột màu xanh thể hiện số sách được mượn. Đường màu cam thể hiện số sách đã trả trên cùng tỷ lệ.</p></div><div class="chart-legend compact"><span><i class="legend-bar"></i>Đã mượn</span><span><i class="legend-line"></i>Đã trả</span></div></div>
            <c:choose><c:when test="${not empty monthlyChart}"><div class="report-svg-wrap"><svg class="report-svg-chart" viewBox="0 0 100 100" preserveAspectRatio="none" role="img" aria-label="Số sách mượn và trả theo tháng"><g class="svg-grid"><line x1="4" y1="18" x2="96" y2="18"></line><line x1="4" y1="34" x2="96" y2="34"></line><line x1="4" y1="50" x2="96" y2="50"></line><line x1="4" y1="66" x2="96" y2="66"></line><line x1="4" y1="86" x2="96" y2="86"></line></g><g class="svg-bars"><c:forEach items="${monthlyChart}" var="m"><rect x="${m.x}" y="${m.barY}" width="5.2" height="${m.barHeight}" rx="0.8" transform="translate(-2.6 0)"></rect></c:forEach></g><polyline class="svg-return-line" points="${monthlyLinePoints}"></polyline></svg><div class="svg-month-labels"><c:forEach items="${monthlyChart}" var="m"><span>${m.label}</span></c:forEach></div></div></c:when><c:otherwise><p class="muted empty-state">Chưa có dữ liệu mượn sách.</p></c:otherwise></c:choose>
        </section>
        <div class="report-grid-two">
            <section class="card chart-card analytics-panel stat-combo-card">
                <div class="section-head"><div><h2>Số sách mượn theo năm</h2><p class="muted">So sánh tổng lượt mượn và xu hướng qua từng năm.</p></div><div class="chart-legend compact"><span><i class="legend-bar"></i>Số lượt</span><span><i class="legend-line"></i>Xu hướng</span></div></div>
                <c:choose><c:when test="${not empty yearlyStats}"><div class="stat-combo-chart"><div class="stat-combo-plot"><div class="stat-combo-grid"><i></i><i></i><i></i><i></i><i></i></div><div class="stat-combo-columns" style="--item-count:${yearlyStats.size()}"><c:forEach items="${yearlyStats}" var="y"><div class="stat-combo-column" title="${y.label}: ${y.value}"><strong>${y.value}</strong><i style="height:${y.percent}%"></i></div></c:forEach></div><svg class="stat-combo-line" viewBox="0 0 100 100" preserveAspectRatio="none" aria-hidden="true"><polyline points="${yearlyLinePoints}"></polyline></svg></div><div class="stat-combo-labels" style="--item-count:${yearlyStats.size()}"><c:forEach items="${yearlyStats}" var="y"><span>${y.label}</span></c:forEach></div></div></c:when><c:otherwise><p class="muted empty-state">Chưa có dữ liệu theo năm.</p></c:otherwise></c:choose>
            </section>
            <section class="card chart-card analytics-panel status-panel-classic">
                <div class="section-head"><div><h2>Trạng thái mượn</h2><p class="muted">Phân bổ phiếu mượn theo trạng thái hiện tại.</p></div></div>
                <div class="classic-status-list">
                    <c:forEach items="${statusStats}" var="s">
                        <div class="bar-row pro-row">
                            <span>${fn:replace(fn:replace(fn:replace(s.label, 'BORROWING', 'Đang mượn'), 'OVERDUE', 'Quá hạn'), 'RETURNED', 'Đã trả')}</span>
                            <div class="bar"><i style="width:${s.percent}%"></i></div>
                            <strong>${s.value}</strong>
                        </div>
                    </c:forEach>
                </div>
                <c:if test="${empty statusStats}"><p class="muted empty-state">Chưa có dữ liệu trạng thái.</p></c:if>
            </section>
        </div>
        <div class="report-grid-two">
            <section class="card chart-card analytics-panel stat-combo-card">
                <div class="section-head"><div><h2>Sách được mượn nhiều nhất</h2><p class="muted">So sánh số lượt mượn của các đầu sách nổi bật.</p></div><div class="chart-legend compact"><span><i class="legend-bar"></i>Số lượt</span><span><i class="legend-line"></i>Xu hướng</span></div></div>
                <c:choose><c:when test="${not empty topBooks}"><div class="stat-combo-chart"><div class="stat-combo-plot"><div class="stat-combo-grid"><i></i><i></i><i></i><i></i><i></i></div><div class="stat-combo-columns" style="--item-count:${topBooks.size()}"><c:forEach items="${topBooks}" var="b"><div class="stat-combo-column" title="${b.label}: ${b.value}"><strong>${b.value}</strong><i style="height:${b.percent}%"></i></div></c:forEach></div><svg class="stat-combo-line" viewBox="0 0 100 100" preserveAspectRatio="none" aria-hidden="true"><polyline points="${topBooksLinePoints}"></polyline></svg></div><div class="stat-combo-labels" style="--item-count:${topBooks.size()}"><c:forEach items="${topBooks}" var="b"><span title="${b.label}">${b.label}</span></c:forEach></div></div></c:when><c:otherwise><p class="muted empty-state">Chưa có sách nào được mượn.</p></c:otherwise></c:choose>
            </section>
            <section class="card chart-card analytics-panel stat-combo-card">
                <div class="section-head"><div><h2>Hoạt động của độc giả</h2><p class="muted">So sánh tần suất mượn sách giữa các độc giả.</p></div><div class="chart-legend compact"><span><i class="legend-bar"></i>Số lượt</span><span><i class="legend-line"></i>Xu hướng</span></div></div>
                <c:choose><c:when test="${not empty readerActivity}"><div class="stat-combo-chart"><div class="stat-combo-plot"><div class="stat-combo-grid"><i></i><i></i><i></i><i></i><i></i></div><div class="stat-combo-columns" style="--item-count:${readerActivity.size()}"><c:forEach items="${readerActivity}" var="r"><div class="stat-combo-column" title="${r.label}: ${r.value}"><strong>${r.value}</strong><i style="height:${r.percent}%"></i></div></c:forEach></div><svg class="stat-combo-line" viewBox="0 0 100 100" preserveAspectRatio="none" aria-hidden="true"><polyline points="${readerActivityLinePoints}"></polyline></svg></div><div class="stat-combo-labels" style="--item-count:${readerActivity.size()}"><c:forEach items="${readerActivity}" var="r"><span title="${r.label}">${r.label}</span></c:forEach></div></div></c:when><c:otherwise><p class="muted empty-state">Chưa có hoạt động của độc giả.</p></c:otherwise></c:choose>
            </section>
        </div>
        <section class="card chart-card analytics-panel overdue-panel"><div class="section-head"><h2>Sách quá hạn</h2><p class="muted">Các phiếu hiện cần xử lý</p></div><div class="mini-list"><c:forEach items="${overdueRecords}" var="o"><div class="mini-list-row"><span><b>${o.bookTitle}</b><small>${o.readerName} - hạn trả ${o.dueDate}</small></span><span class="badge">${fn:replace(fn:replace(fn:replace(o.status, 'BORROWING', 'Đang mượn'), 'OVERDUE', 'Quá hạn'), 'RETURNED', 'Đã trả')}</span></div></c:forEach><c:if test="${empty overdueRecords}"><p class="muted empty-state">Hiện không có sách quá hạn.</p></c:if></div></section>
        <section class="card report-table analytics-panel"><div class="section-head"><h2>Lịch sử giao dịch</h2><p class="muted">Chi tiết lịch sử mượn sách</p></div><div class="table-wrap"><table><tr><th>Sách</th><th>Độc giả</th><th>Email</th><th>Hạn trả</th><th>Trạng thái</th><th>Tiền phạt</th></tr><c:forEach items="${records}" var="r"><tr><td>${r.bookTitle}</td><td>${r.readerName}</td><td>${r.readerEmail}</td><td>${r.dueDate}</td><td><span class="badge">${fn:replace(fn:replace(fn:replace(r.status, 'BORROWING', 'Đang mượn'), 'OVERDUE', 'Quá hạn'), 'RETURNED', 'Đã trả')}</span></td><td>${r.fine}</td></tr></c:forEach></table></div></section>
    </main>
</div>
</body>
</html>
