<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="includes.jsp" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Books</title>
    <link rel="stylesheet" href="${ctx}/assets/css/style.css">
</head>

<body>
<div class="layout">

    <jsp:include page="menu.jsp"/>

    <main class="main">

        <div class="top">
            <h1>Manage Books</h1>
        </div>

        <c:if test="${not empty message}">
            <div class="alert success">${message}</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert error">${error}</div>
        </c:if>

        <form class="toolbar" method="get">
            <input name="q" value="${q}" placeholder="Search by title, code, category, author, publisher">
            <button class="btn">Search</button>
        </form>

        <c:if test="${canManage}">
            <form class="form form-wide" method="post" action="${ctx}/books/save" enctype="multipart/form-data">
                <input name="code" placeholder="Book code" required>
                <input name="title" placeholder="Book title" required>
                <select name="categoryId">
                    <option value="0">No category</option>
                    <c:forEach items="${categories}" var="c">
                        <option value="${c.id}">${c.name}</option>
                    </c:forEach>
                </select>
                <select name="authorId">
                    <option value="0">No author</option>
                    <c:forEach items="${authors}" var="a">
                        <option value="${a.id}">${a.name}</option>
                    </c:forEach>
                </select>
                <select name="publisherId">
                    <option value="0">No publisher</option>
                    <c:forEach items="${publishers}" var="p">
                        <option value="${p.id}">${p.name}</option>
                    </c:forEach>
                </select>
                <input name="quantity" type="number" min="0" placeholder="Quantity" required>
                <input name="coverImage" placeholder="Cover URL/path">
                <input name="coverFile" type="file" accept="image/*">
                <button class="btn alt">Add Book</button>
            </form>
        </c:if>

        <div class="table-wrap">
            <table>
                <tr>
                    <th>Cover</th>
                    <th>Code</th>
                    <th>Title</th>
                    <th>Author</th>
                    <th>Category</th>
                    <th>Publisher</th>
                    <th>Quantity</th>
                    <th>Available</th>
                    <th>Cover Management</th>
                    <th>Action</th>
                </tr>

                <c:forEach items="${books}" var="b">
                    <tr>
                        <td>
                            <c:choose>
                                <c:when test="${not empty b.coverImage && (fn:startsWith(b.coverImage, 'http://') || fn:startsWith(b.coverImage, 'https://') || fn:startsWith(b.coverImage, 'data:'))}">
                                    <img class="cover" src="${b.coverImage}" alt="${b.title}">
                                </c:when>
                                <c:when test="${not empty b.coverImage}">
                                    <img class="cover" src="${ctx}${b.coverImage}" alt="${b.title}">
                                </c:when>
                                <c:otherwise>
                                    <img class="cover" src="${ctx}/assets/img/book1.svg" alt="${b.title}">
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>${b.code}</td>
                        <td>${b.title}</td>
                        <td>${b.author}</td>
                        <td>${b.category}</td>
                        <td>${b.publisher}</td>
                        <td>${b.quantity}</td>
                        <td>${b.available}</td>
                        <td>
                            <c:if test="${canManage}">
                                <form class="inline-upload" method="post" action="${ctx}/books/${b.id}/cover" enctype="multipart/form-data">
                                    <input name="coverImage" placeholder="URL/path">
                                    <input name="coverFile" type="file" accept="image/*">
                                    <button class="btn">Update</button>
                                </form>
                            </c:if>
                        </td>
                        <td>
                            <c:if test="${canManage}">
                                <a class="btn danger"
                                   href="${ctx}/books/delete/${b.id}"
                                   onclick="return confirm('Delete this book?')">
                                    Delete
                                </a>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>

    </main>
</div>
</body>
</html>
