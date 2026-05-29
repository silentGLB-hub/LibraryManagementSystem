<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="includes.jsp" %>
<c:set var="uri" value="${pageContext.request.requestURI}" />

<aside class="side">
    <div class="brand">LibraryMS</div>

    <nav class="nav">
        <a class="${fn:contains(uri, '/dashboard') ? 'active' : ''}" href="${ctx}/dashboard">Dashboard</a>
        <a class="${fn:contains(uri, '/books') ? 'active' : ''}" href="${ctx}/books">Books</a>
        <c:if test="${sessionScope.user.role == 'ADMIN' || sessionScope.user.role == 'LIBRARIAN'}">
            <a class="${fn:contains(uri, '/catalog') ? 'active' : ''}" href="${ctx}/catalog">Catalog</a>
            <a class="${fn:contains(uri, '/readers') ? 'active' : ''}" href="${ctx}/readers">Readers</a>
            <a class="${fn:contains(uri, '/borrow') ? 'active' : ''}" href="${ctx}/borrow">Borrow / Return</a>
            <a class="${fn:contains(uri, '/reports') ? 'active' : ''}" href="${ctx}/reports">Reports</a>
        </c:if>
        <a href="${ctx}/logout">Logout</a>
    </nav>
</aside>
