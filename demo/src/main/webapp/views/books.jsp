<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Bookstore – Catalogue</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<nav class="navbar">
  <span class="navbar-brand">📚 Bookstore</span>
  <div class="navbar-links">
        <span>Hello, <b>${sessionScope.user.username}</b>
              (${sessionScope.user.role})</span>
    <a href="${pageContext.request.contextPath}/app/orders">My Orders</a>
    <c:if test="${sessionScope.user.role == 'ADMIN'}">
      <a href="${pageContext.request.contextPath}/app/books/add"
         class="btn btn-sm btn-success">+ Add Book</a>
    </c:if>
    <a href="${pageContext.request.contextPath}/app/logout">Sign Out</a>
  </div>
</nav>

<div class="container">
  <h2>Book Catalogue</h2>

  <c:if test="${not empty sessionScope.successMessage}">
    <div class="alert alert-success">${sessionScope.successMessage}</div>
    <c:remove var="successMessage" scope="session"/>
  </c:if>

  <form method="get" action="${pageContext.request.contextPath}/app/books"
        class="search-form">
    <input type="text" name="search" class="form-control"
           placeholder="Search by title..." value="${search}">
    <button type="submit" class="btn btn-secondary">Search</button>
    <c:if test="${not empty search}">
      <a href="${pageContext.request.contextPath}/app/books"
         class="btn btn-outline">Clear</a>
    </c:if>
  </form>

  <c:choose>
    <c:when test="${empty books}">
      <p class="text-center">No books found.</p>
    </c:when>
    <c:otherwise>
      <c:if test="${sessionScope.user.role != 'ADMIN'}">
        <form id="orderForm" method="post"
              action="${pageContext.request.contextPath}/app/orders/create"></form>
      </c:if>

      <table class="table">
        <thead>
        <tr>
          <th>#</th>
          <th>Title</th>
          <th>Author</th>
          <th>Price</th>
          <th>In Stock</th>
          <c:if test="${sessionScope.user.role != 'ADMIN'}">
            <th>Qty</th>
          </c:if>
          <c:if test="${sessionScope.user.role == 'ADMIN'}">
            <th>Actions</th>
          </c:if>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="book" items="${books}" varStatus="s">
          <tr>
            <td>${s.index + 1}</td>
            <td>${book.title}</td>
            <td>${book.author}</td>
            <td>
              <fmt:formatNumber value="${book.price}"
                                type="number"
                                minFractionDigits="2"
                                maxFractionDigits="2"/>
            </td>
            <td>
              <c:choose>
                <c:when test="${book.quantity > 0}">
                  <span class="badge badge-success">${book.quantity}</span>
                </c:when>
                <c:otherwise>
                  <span class="badge badge-danger">Out of stock</span>
                </c:otherwise>
              </c:choose>
            </td>
            <c:if test="${sessionScope.user.role != 'ADMIN'}">
              <td>
                <input type="hidden" name="bookId" value="${book.id}" form="orderForm">
                <input type="number" name="quantity" value="0"
                       min="0" max="${book.quantity}"
                       class="qty-input" form="orderForm"
                  ${book.quantity == 0 ? 'disabled' : ''}>
              </td>
            </c:if>
            <c:if test="${sessionScope.user.role == 'ADMIN'}">
              <td class="actions">
                <a href="${pageContext.request.contextPath}/app/books/edit?id=${book.id}"
                   class="btn btn-sm btn-warning">Edit</a>
                <form method="post" style="display:inline"
                      action="${pageContext.request.contextPath}/app/books/delete"
                      onsubmit="return confirm('Delete this book?')">
                  <input type="hidden" name="id" value="${book.id}">
                  <button type="submit"
                          class="btn btn-sm btn-danger">Delete</button>
                </form>
              </td>
            </c:if>
          </tr>
        </c:forEach>
        </tbody>
      </table>

      <c:if test="${sessionScope.user.role != 'ADMIN'}">
        <button type="submit" form="orderForm" class="btn btn-primary">Place Order</button>
      </c:if>
    </c:otherwise>
  </c:choose>
</div>
</body>
</html>
