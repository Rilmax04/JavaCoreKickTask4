package com.kurilo.task4.controller.command;

import com.kurilo.task4.exception.AppException;
import com.kurilo.task4.model.entity.User;
import com.kurilo.task4.model.service.BookService;
import com.kurilo.task4.model.service.impl.BookServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.*;

import java.math.BigDecimal;

public class AddBookCommand implements Command {

    private static final Logger logger = LogManager.getLogger(AddBookCommand.class);

    private final BookService bookService = new BookServiceImpl();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
            throws AppException {

        User user = (User) request.getSession().getAttribute("user");
        if (user == null || user.getRole() != User.Role.ADMIN) {
            logger.warn("Access denied to add book for user={}",
                    user != null ? user.getUsername() : "anonymous");
            throw new AppException("Access denied: ADMIN role required");
        }

        try {
            String title       = request.getParameter("title");
            String author      = request.getParameter("author");
            BigDecimal price   = new BigDecimal(request.getParameter("price"));
            int quantity       = Integer.parseInt(request.getParameter("quantity"));
            String description = request.getParameter("description");

            logger.info("Admin {} adding book title={}", user.getUsername(), title);

            bookService.addBook(title, author, price, quantity, description);

            logger.info("Book added successfully by admin={}", user.getUsername());
            request.getSession().setAttribute("successMessage", "Book added successfully");
            return "redirect:/app/books";

        } catch (NumberFormatException e) {
            logger.warn("Invalid number format in add book form", e);
            throw new AppException("Invalid price or quantity format", e);
        }
    }
}