<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Bookstore – Sign In</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">
    <div class="form-wrapper">
        <h2>Sign In</h2>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">${errorMessage}</div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/app/login">
            <div class="form-group">
                <label>Username</label>
                <input type="text" name="username" class="form-control"
                       value="${param.username}" required>
            </div>
            <div class="form-group">
                <label>Password</label>
                <input type="password" name="password" class="form-control" required>
            </div>
            <button type="submit" class="btn btn-primary btn-block">Sign In</button>
        </form>

        <p class="text-center mt-2">
            No account?
            <a href="${pageContext.request.contextPath}/app/register">Register</a>
        </p>
    </div>
</div>
</body>
</html>