package com.kurilo.task4.controller.command;

import com.kurilo.task4.exception.AppException;
import com.kurilo.task4.model.entity.User;
import com.kurilo.task4.model.service.BookService;
import com.kurilo.task4.model.service.impl.BookServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.*;

public class DeleteBookCommand implements Command {

    private static final Logger logger = LogManager.getLogger(DeleteBookCommand.class);

    private final BookService bookService = new BookServiceImpl();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
            throws AppException {

        User user = (User) request.getSession().getAttribute("user");
        if (user == null || user.getRole() != User.Role.ADMIN) {
            logger.warn("Access denied to delete book for user={}",
                    user != null ? user.getUsername() : "anonymous");
            throw new AppException("Access denied: ADMIN role required");
        }

        try {
            Long id = Long.parseLong(request.getParameter("id"));
            logger.info("Admin {} deleting book id={}", user.getUsername(), id);

            bookService.deleteBook(id);

            logger.info("Book id={} deleted by admin={}", id, user.getUsername());
            request.getSession().setAttribute("successMessage", "Book deleted successfully");
            return "redirect:/app/books";

        } catch (NumberFormatException e) {
            logger.warn("Invalid book id format in delete request", e);
            throw new AppException("Invalid book id", e);
        }
    }
}