<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Bookstore – Error</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">
  <div class="form-wrapper">
    <h2>Something went wrong</h2>

    <c:choose>
      <c:when test="${not empty errorMessage}">
        <div class="alert alert-danger">${errorMessage}</div>
      </c:when>
      <c:when test="${not empty requestScope['javax.servlet.error.message']}">
        <div class="alert alert-danger">${requestScope['javax.servlet.error.message']}</div>
      </c:when>
      <c:otherwise>
        <div class="alert alert-danger">An unexpected error occurred.</div>
      </c:otherwise>
    </c:choose>

    <p class="text-center mt-2">
      <a href="${pageContext.request.contextPath}/app/books">Back to Catalogue</a>
      |
      <a href="${pageContext.request.contextPath}/app/login">Sign In</a>
    </p>
  </div>
</div>
</body>
</html>
