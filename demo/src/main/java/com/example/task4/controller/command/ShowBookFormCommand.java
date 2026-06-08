package com.example.task4.controller.command;

import com.example.task4.exception.AppException;
import com.example.task4.model.entity.Book;
import com.example.task4.model.entity.User;
import com.example.task4.model.service.BookService;
import com.example.task4.model.service.impl.BookServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.util.Optional;

public class ShowBookFormCommand implements Command {

    private static final Logger logger = LogManager.getLogger(ShowBookFormCommand.class);

    private final BookService bookService = new BookServiceImpl();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
            throws AppException {

        User user = (User) request.getSession().getAttribute("user");
        if (user == null || user.getRole() != User.Role.ADMIN) {
            logger.warn("Access denied to book form for user={}",
                    user != null ? user.getUsername() : "anonymous");
            throw new AppException("Access denied: ADMIN role required");
        }

        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isBlank()) {
            try {
                Long id = Long.parseLong(idParam);
                logger.debug("Loading book for edit id={}", id);
                Optional<Book> book = bookService.findById(id);
                book.ifPresent(b -> request.setAttribute("book", b));
            } catch (NumberFormatException e) {
                logger.warn("Invalid book id format: {}", idParam);
                throw new AppException("Invalid book id: " + idParam);
            }
        } else {
            logger.debug("Showing empty book form for new book");
        }

        return "/views/book-form.jsp";
    }
}