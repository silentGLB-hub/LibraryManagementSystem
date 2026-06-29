<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="includes.jsp" %>
<c:set var="uri" value="${pageContext.request.requestURI}" />
<c:set var="role" value="${sessionScope.user.role}" />

<aside class="side">
    <div class="brand">LibraryMS</div>
    <nav class="nav">
        <a class="${fn:contains(uri, '/dashboard') ? 'active' : ''}" href="${ctx}/dashboard">Bảng điều khiển</a>
        <a class="${fn:contains(uri, '/books') ? 'active' : ''}" href="${ctx}/books">Sách</a>

        <c:if test="${role == 'ADMIN'}">
            <a class="${fn:contains(uri, '/users') ? 'active' : ''}" href="${ctx}/users">Người dùng & vai trò</a>
            <a class="${fn:contains(uri, '/catalog') ? 'active' : ''}" href="${ctx}/catalog">Danh mục</a>
            <a class="${fn:contains(uri, '/readers') ? 'active' : ''}" href="${ctx}/readers">Độc giả</a>
            <a class="${fn:contains(uri, '/borrow') ? 'active' : ''}" href="${ctx}/borrow">Mượn / Trả sách</a>
            <a class="${fn:contains(uri, '/reports') ? 'active' : ''}" href="${ctx}/reports">Báo cáo</a>
        </c:if>

        <c:if test="${role == 'LIBRARIAN'}">
            <a class="${fn:contains(uri, '/readers') ? 'active' : ''}" href="${ctx}/readers">Độc giả</a>
            <a class="${fn:contains(uri, '/borrow') ? 'active' : ''}" href="${ctx}/borrow">Mượn / Trả sách</a>
            <a class="${fn:contains(uri, '/reports') ? 'active' : ''}" href="${ctx}/reports">Báo cáo</a>
        </c:if>

        <c:if test="${role == 'READER'}">
            <a class="${fn:contains(uri, '/reader/history') ? 'active' : ''}" href="${ctx}/reader/history">Lịch sử mượn của tôi</a>
        </c:if>

        <a href="${ctx}/logout">Đăng xuất</a>
    </nav>
</aside>