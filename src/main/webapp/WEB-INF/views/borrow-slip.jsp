<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="includes.jsp" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Phiếu mượn sách</title>
        <link rel="stylesheet" href="${ctx}/assets/css/style.css">
    </head>
    <body class="print-page">
        <main class="slip-shell">
            <section class="borrow-slip modern-slip">
                <div class="slip-ribbon"></div>
                <div class="slip-head">
                    <div class="slip-brand-block">
                        <span class="slip-logo">L</span>
                        <div>
                            <span class="slip-kicker">LibraryMS</span>
                            <h1>Phiếu mượn sách</h1>
                        </div>
                    </div>
                    <div class="slip-number">
                        <span>Số phiếu</span>
                        <strong>#${record.id}</strong>
                    </div>
                </div>

                <c:set var="recordStatusText" value="${fn:replace(fn:replace(fn:replace(record.status, 'BORROWING', 'Đang mượn'), 'OVERDUE', 'Quá hạn'), 'RETURNED', 'Đã trả')}" />

                <div class="slip-status-row">
                    <span class="badge slip-status">${recordStatusText}</span>
                    <span>Hạn trả: <b>${record.dueDate}</b></span>
                </div>

                <div class="slip-grid">
                    <div>
                        <span>Độc giả</span>
                        <b>${record.readerName}</b>
                    </div>
                    <div>
                        <span>Email</span>
                        <b>${record.readerEmail}</b>
                    </div>
                    <div class="wide highlight-field">
                        <span>Sách</span>
                        <b>${record.bookTitle}</b>
                    </div>
                    <div>
                        <span>Ngày mượn</span>
                        <b>${record.borrowDate}</b>
                    </div>
                    <div>
                        <span>Hạn trả</span>
                        <b>${record.dueDate}</b>
                    </div>
                    <div>
                        <span>Ngày trả</span>
                        <b>${empty record.returnDate ? '-' : record.returnDate}</b>
                    </div>
                    <div>
                        <span>Trạng thái</span>
                        <b>${recordStatusText}</b>
                    </div>
                    <div>
                        <span>Tiền phạt</span>
                        <b>${record.fine}</b>
                    </div>
                </div>

                <div class="slip-note">
                    <b>Lưu ý quy định</b>
                    <span>Vui lòng trả sách đúng hoặc trước hạn. Trả trễ có thể bị tính phí theo quy định thư viện.</span>
                </div>

                <div class="signature-row">
                    <div>
                        <span>Chữ ký độc giả</span>
                    </div>
                    <div>
                        <span>Chữ ký thủ thư</span>
                    </div>
                </div>
            </section>

            <div class="print-actions">
                <button class="btn alt" onclick="window.print()">In phiếu</button>
                <a class="btn" href="${ctx}/borrow">Quay lại</a>
            </div>
        </main>
    </body>
</html>