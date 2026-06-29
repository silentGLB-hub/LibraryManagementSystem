<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="includes.jsp" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Borrow</title>
        <link rel="stylesheet" href="${ctx}/assets/css/style.css">
    </head>

    <body>
        <div class="layout">

            <jsp:include page="menu.jsp"/>

            <main class="main">

                <h1>Borrow / Return Books</h1>

                <c:if test="${not empty message}">
                    <div class="alert success">${message}</div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="alert error">${error}</div>
                </c:if>

                <form class="form" method="post" action="${ctx}/borrow/create">

                    <select name="bookId" required>
                        <c:forEach items="${books}" var="b">
                            <c:choose>
                                <c:when test="${b.available <= 0}">
                                    <option value="${b.id}" disabled>
                                        ${b.title} (${b.available} available)
                                    </option>
                                </c:when>
                                <c:otherwise>
                                    <option value="${b.id}">
                                        ${b.title} (${b.available} available)
                                    </option>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </select>

                    <select name="readerId" required>
                        <c:forEach items="${readers}" var="r">
                            <option value="${r.id}">
                                ${r.fullName}
                            </option>
                        </c:forEach>
                    </select>

                    <input type="date" name="borrowDate" required>
                    <input type="date" name="dueDate" required>

                    <button class="btn alt">Create Borrow</button>
                </form>

                <div class="table-wrap">
                    <table>
                        <tr>
                            <th>Book</th>
                            <th>Reader</th>
                            <th>Borrow</th>
                            <th>Due</th>
                            <th>Return</th>
                            <th>Status</th>
                            <th>Fine</th>
                            <th>Action</th>
                        </tr>

                        <c:forEach items="${records}" var="r">
                            <tr>
                                <td>${r.bookTitle}</td>
                                <td>${r.readerName}</td>
                                <td>${r.borrowDate}</td>
                                <td>${r.dueDate}</td>
                                <td>${r.returnDate}</td>

                                <td>
                                    <span class="badge">${r.status}</span>
                                </td>

                                <td>${r.fine}</td>

                                <td>
                                    <c:if test="${r.status != 'RETURNED'}">
                                        <a class="btn" href="${ctx}/borrow/return/${r.id}">
                                            Return
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
