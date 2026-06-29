<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="includes.jsp" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Reports</title>
        <link rel="stylesheet" href="${ctx}/assets/css/style.css">
    </head>

    <body>
        <div class="layout">

            <jsp:include page="menu.jsp"/>

            <main class="main">

                <div class="top">
                    <h1>Statistical Reports</h1>
                    <div class="actions">
                        <a class="btn" href="${ctx}/reports/export/excel">Export Excel</a>
                        <a class="btn" href="${ctx}/reports/export/pdf">Export PDF</a>
                        <c:choose>
                            <c:when test="${smtpConfigured}">
                                <a class="btn alt" href="${ctx}/reports/reminders">Send overdue emails</a>
                            </c:when>
                            <c:otherwise>
                                <span class="btn disabled">Send overdue emails</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <c:if test="${not empty message}">
                    <div class="alert success">${message}</div>
                </c:if>
                <c:if test="${not empty warning}">
                    <div class="alert warning">${warning}</div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="alert error">${error}</div>
                </c:if>

                <div class="grid report-summary">
                    <div class="card metric-card">
                        <b>Borrow Records</b>
                        <div class="stat">${records.size()}</div>
                        <span class="muted">Total transactions</span>
                    </div>

                    <div class="card metric-card">
                        <b>Overdue Records</b>
                        <div class="stat">${overdueRecords.size()}</div>
                        <span class="muted">Need follow-up</span>
                    </div>

                    <!--                    <div class="card metric-card">
                                            <b>Email Reminder</b>
                    <c:choose>
                        <c:when test="${smtpConfigured}">
                            <p></p>
                        </c:when>
                        <c:otherwise>
                            <p>SMTP is not configured. Set LIBRARY_SMTP_HOST, LIBRARY_SMTP_PORT, LIBRARY_SMTP_FROM, LIBRARY_SMTP_USER and LIBRARY_SMTP_PASSWORD to enable email reminders.</p>
                        </c:otherwise>
                    </c:choose>
                </div>-->
                </div>

                <div class="charts">
                    <div class="card chart-card combo-chart-card">
                        <h2>Borrowed Books by Month</h2>
                        <p class="muted">Blue columns show borrowed books, orange line shows returned books.</p>
                        <c:choose>
                            <c:when test="${not empty monthlyChart}">
                                <div class="combo-chart">
                                    <div class="combo-plot">
                                        <div class="combo-grid">
                                            <span></span>
                                            <span></span>
                                            <span></span>
                                            <span></span>
                                            <span></span>
                                        </div>

                                        <div class="combo-bars">
                                            <c:forEach items="${monthlyChart}" var="m">
                                                <div class="combo-bar-wrap">
                                                    <span class="combo-value">${m.barValue}</span>
                                                    <i class="combo-bar" style="height: ${m.barPercent}%;"></i>
                                                </div>
                                            </c:forEach>
                                        </div>

                                        <svg class="combo-line"
                                             viewBox="0 0 100 100"
                                             preserveAspectRatio="none"
                                             aria-hidden="true">

                                        <polyline points="${monthlyLinePoints}"></polyline>

                                        <c:forEach items="${monthlyChart}" var="m">
                                            <circle cx="${m.x}" cy="${m.y}" r="1.7"></circle>
                                        </c:forEach>
                                        </svg>
                                    </div>
                                    <div class="combo-labels">
                                        <c:forEach items="${monthlyChart}" var="m">
                                            <span>${m.label}</span>
                                        </c:forEach>
                                    </div>
                                    <div class="chart-legend">
                                        <span><i class="legend-bar"></i>Borrowed</span>
                                        <span><i class="legend-line"></i>Returned</span>
                                    </div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <p class="muted">No borrowing data yet.</p>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="card chart-card">
                        <h2>Borrowed Books by Year</h2>
                        <p class="muted">Yearly borrowing trend</p>
                        <div class="year-bars">
                            <c:forEach items="${yearlyStats}" var="y">
                                <div class="year-bar">
                                    <div class="year-column">
                                        <i style="height:${y.percent}%"></i>
                                    </div>
                                    <strong>${y.value}</strong>
                                    <span>${y.label}</span>
                                </div>
                            </c:forEach>
                        </div>
                        <c:if test="${empty yearlyStats}">
                            <p class="muted">No yearly data yet.</p>
                        </c:if>
                    </div>
                </div>

                <div class="charts">
                    <div class="card chart-card">
                        <h2>Borrow Status</h2>
                        <c:forEach items="${statusStats}" var="s">
                            <div class="bar-row">
                                <span>${s.label}</span>
                                <div class="bar"><i style="width:${s.percent}%"></i></div>
                                <strong>${s.value}</strong>
                            </div>
                        </c:forEach>
                    </div>

                    <div class="card chart-card">
                        <h2>Most Borrowed Books</h2>
                        <c:forEach items="${topBooks}" var="b">
                            <div class="bar-row">
                                <span>${b.label}</span>
                                <div class="bar"><i style="width:${b.percent}%"></i></div>
                                <strong>${b.value}</strong>
                            </div>
                        </c:forEach>
                    </div>
                </div>

                <div class="charts">
                    <div class="card chart-card">
                        <h2>Reader Activity</h2>
                        <p class="muted">Readers ranked by borrowing frequency</p>
                        <c:forEach items="${readerActivity}" var="r">
                            <div class="bar-row">
                                <span>${r.label}</span>
                                <div class="bar"><i style="width:${r.percent}%"></i></div>
                                <strong>${r.value}</strong>
                            </div>
                        </c:forEach>
                    </div>

                    <div class="card chart-card">
                        <h2>Overdue Books</h2>
                        <p class="muted">Current records requiring attention</p>
                        <div class="mini-list">
                            <c:forEach items="${overdueRecords}" var="o">
                                <div class="mini-list-row">
                                    <span>
                                        <b>${o.bookTitle}</b>
                                        <small>${o.readerName} - due ${o.dueDate}</small>
                                    </span>
                                    <span class="badge">${o.status}</span>
                                </div>
                            </c:forEach>
                            <c:if test="${empty overdueRecords}">
                                <p class="muted">No overdue books at the moment.</p>
                            </c:if>
                        </div>
                    </div>
                </div>

                <div class="card report-table">
                    <h2>Transaction Records</h2>

                    <div class="table-wrap">
                        <table>
                            <tr>
                                <th>Book</th>
                                <th>Reader</th>
                                <th>Email</th>
                                <th>Due Date</th>
                                <th>Status</th>
                                <th>Fine</th>
                            </tr>

                            <c:forEach items="${records}" var="r">
                                <tr>
                                    <td>${r.bookTitle}</td>
                                    <td>${r.readerName}</td>
                                    <td>${r.readerEmail}</td>
                                    <td>${r.dueDate}</td>
                                    <td>${r.status}</td>
                                    <td>${r.fine}</td>
                                </tr>
                            </c:forEach>
                        </table>
                    </div>
                </div>

            </main>
        </div>
    </body>
</html>
