<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Bookstore – Orders</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<nav class="navbar">
  <span class="navbar-brand">📚 Bookstore</span>
  <div class="navbar-links">
    <a href="${pageContext.request.contextPath}/app/books">Catalogue</a>
    <a href="${pageContext.request.contextPath}/app/logout">Sign Out</a>
  </div>
</nav>

<div class="container">
  <h2>${sessionScope.user.role == 'ADMIN' ? 'All Orders' : 'My Orders'}</h2>

  <c:if test="${not empty sessionScope.successMessage}">
    <div class="alert alert-success">${sessionScope.successMessage}</div>
    <c:remove var="successMessage" scope="session"/>
  </c:if>

  <c:choose>
    <c:when test="${empty orders}">
      <p class="text-center">No orders found.</p>
    </c:when>
    <c:otherwise>
      <c:forEach var="order" items="${orders}">
        <div class="order-card">
          <div class="order-header">
            <span><b>Order #${order.id}</b></span>
            <span>Customer: ${order.username}</span>
            <span>${order.createdAt}</span>
            <span class="badge
                            badge-${order.status == 'PENDING'   ? 'warning' :
                                    order.status == 'CONFIRMED' ? 'info'    :
                                    order.status == 'CANCELLED' ? 'danger'  : 'success'}">
                ${order.status}
            </span>
            <span>Total:
                            <b><fmt:formatNumber value="${order.totalPrice}"
                                                 type="number"
                                                 minFractionDigits="2"
                                                 maxFractionDigits="2"/></b>
                        </span>
          </div>

          <table class="table table-sm">
            <thead>
            <tr>
              <th>Book</th>
              <th>Qty</th>
              <th>Price</th>
              <th>Subtotal</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="item" items="${order.items}">
              <tr>
                <td>${item.bookTitle}</td>
                <td>${item.quantity}</td>
                <td>
                  <fmt:formatNumber value="${item.price}"
                                    type="number"
                                    minFractionDigits="2"
                                    maxFractionDigits="2"/>
                </td>
                <td>
                  <fmt:formatNumber value="${item.subtotal}"
                                    type="number"
                                    minFractionDigits="2"
                                    maxFractionDigits="2"/>
                </td>
              </tr>
            </c:forEach>
            </tbody>
          </table>

          <c:if test="${order.status == 'PENDING' ||
                                  order.status == 'CONFIRMED'}">
            <form method="post"
                  action="${pageContext.request.contextPath}/app/orders/cancel"
                  onsubmit="return confirm('Cancel order #${order.id}?')">
              <input type="hidden" name="orderId" value="${order.id}">
              <button type="submit" class="btn btn-sm btn-danger">
                Cancel Order
              </button>
            </form>
          </c:if>
        </div>
      </c:forEach>
    </c:otherwise>
  </c:choose>
</div>
</body>
</html>