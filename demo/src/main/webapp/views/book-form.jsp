<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Bookstore – ${empty book ? 'Add Book' : 'Edit Book'}</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">
  <h2>${empty book ? 'Add New Book' : 'Edit Book'}</h2>

  <c:if test="${not empty errorMessage}">
    <div class="alert alert-danger">${errorMessage}</div>
  </c:if>

  <c:set var="action"
         value="${empty book ? '/app/books/add' : '/app/books/edit'}"/>

  <form method="post"
        action="${pageContext.request.contextPath}${action}"
        class="form-wrapper">

    <c:if test="${not empty book}">
      <input type="hidden" name="id" value="${book.id}">
    </c:if>

    <div class="form-group">
      <label>Title</label>
      <input type="text" name="title" class="form-control"
             value="${book.title}" required maxlength="255">
    </div>
    <div class="form-group">
      <label>Author</label>
      <input type="text" name="author" class="form-control"
             value="${book.author}" required maxlength="255">
    </div>
    <div class="form-group">
      <label>Price</label>
      <input type="number" name="price" class="form-control"
             value="${book.price}" step="0.01" min="0.01" required>
    </div>
    <div class="form-group">
      <label>Quantity</label>
      <input type="number" name="quantity" class="form-control"
             value="${book.quantity}" min="0" required>
    </div>
    <div class="form-group">
      <label>Description</label>
      <textarea name="description" class="form-control"
                rows="4">${book.description}</textarea>
    </div>

    <div class="form-actions">
      <button type="submit" class="btn btn-primary">
        ${empty book ? 'Add Book' : 'Save Changes'}
      </button>
      <a href="${pageContext.request.contextPath}/app/books"
         class="btn btn-secondary">Cancel</a>
    </div>
  </form>
</div>
</body>
</html>