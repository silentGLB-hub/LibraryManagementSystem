<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="includes.jsp" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Trang đọc được bảo vệ - ${book.title}</title>
        <link rel="stylesheet" href="${ctx}/assets/css/style.css">
    </head>
    <body class="read-protected standalone-reader" data-watermark="${reader.fullName} - ${reader.email}">
        <div class="reader-watermark">${reader.fullName} - ${reader.email}</div>
        <header class="secure-reader-top">
            <div>
                <span class="eyebrow">Nội dung sách được bảo vệ</span>
                <h1>${book.title}</h1>
                <p>Chỉ độc giả đang mượn sách mới mở được trang này. Hệ thống chặn sao chép, bôi chọn, in và các phím tắt chụp phổ biến trong trình duyệt.</p>
            </div>
            <div class="secure-reader-actions"><span class="badge">${reader.fullName}</span><a class="btn" href="${ctx}/reader/history">Quay lại lịch sử mượn</a></div>
        </header>
        <main class="secure-reader-shell">
            <aside class="secure-reader-sidebar">
                <h2>Mục lục</h2>
                <c:choose><c:when test="${not empty book.chapters}"><div class="chapter-list">${book.chapters}</div></c:when><c:otherwise><div class="empty-state">Chưa có mục lục.</div></c:otherwise></c:choose>
                        <div class="alert warning">Trang này sử dụng watermark và cơ chế bảo vệ ở cấp trình duyệt. Việc chụp bằng thiết bị bên ngoài không thể bị chặn tuyệt đối bởi một trang web.</div>
                    </aside>
                    <section class="secure-reader-content protected-paper">
                <c:choose>
                    <c:when test="${not empty book.pdfFile}"><div class="secure-pdf-frame"><iframe src="${ctx}${book.pdfFile}#toolbar=0&navpanes=0&scrollbar=1" title="${book.title}"></iframe></div></c:when>
                    <c:when test="${not empty book.contentText}"><article class="secure-text-book book-text">${book.contentText}</article></c:when>
                    <c:otherwise><div class="empty-state">Thủ thư chưa thêm nội dung đọc đầy đủ cho sách này.</div></c:otherwise>
                </c:choose>
            </section>
        </main>
        <script>
            (function () {
                function block(event) {
                    event.preventDefault();
                    event.stopPropagation();
                    return false;
                }
                ['contextmenu', 'copy', 'cut', 'paste', 'selectstart', 'dragstart'].forEach(function (type) {
                    document.addEventListener(type, block, true);
                });
                document.addEventListener('keydown', function (event) {
                    var key = (event.key || '').toLowerCase();
                    var blockedCtrl = ['a', 'c', 'i', 'j', 'p', 's', 'u', 'x'];
                    if ((event.ctrlKey || event.metaKey) && blockedCtrl.indexOf(key) >= 0) {
                        block(event);
                    }
                    if (event.key === 'F12' || key === 'printscreen') {
                        block(event);
                        if (navigator.clipboard && navigator.clipboard.writeText) {
                            navigator.clipboard.writeText('Chế độ đọc bảo vệ sẽ chặn chụp màn hình khi trình duyệt cho phép.');
                        }
                    }
                }, true);
                document.addEventListener('keyup', function (event) {
                    if ((event.key || '').toLowerCase() === 'printscreen' && navigator.clipboard && navigator.clipboard.writeText) {
                        navigator.clipboard.writeText('Chế độ đọc bảo vệ.');
                    }
                }, true);
                window.addEventListener('blur', function () {
                    document.body.classList.add('privacy-blur');
                });
                window.addEventListener('focus', function () {
                    document.body.classList.remove('privacy-blur');
                });
            })();
        </script>
    </body>
</html>