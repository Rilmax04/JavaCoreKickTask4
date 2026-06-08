<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Bookstore – Register</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">
  <div class="form-wrapper">
    <h2>Create Account</h2>

    <c:if test="${not empty errorMessage}">
      <div class="alert alert-danger">${errorMessage}</div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/app/register">
      <div class="form-group">
        <label>Username</label>
        <input type="text" name="username" class="form-control"
               value="${param.username}" minlength="3" required>
      </div>
      <div class="form-group">
        <label>Email</label>
        <input type="email" name="email" class="form-control"
               value="${param.email}" required>
      </div>
      <div class="form-group">
        <label>Password</label>
        <input type="password" name="password"
               class="form-control" minlength="6" required>
      </div>
      <button type="submit" class="btn btn-success btn-block">Register</button>
    </form>

    <p class="text-center mt-2">
      Already have an account?
      <a href="${pageContext.request.contextPath}/app/login">Sign In</a>
    </p>
  </div>
</div>
</body>
</html>