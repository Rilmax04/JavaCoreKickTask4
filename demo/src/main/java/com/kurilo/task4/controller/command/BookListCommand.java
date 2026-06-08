package com.kurilo.task4.controller.command;

import com.kurilo.task4.exception.AppException;
import com.kurilo.task4.model.entity.Book;
import com.kurilo.task4.model.service.BookService;
import com.kurilo.task4.model.service.impl.BookServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.*;

import java.util.List;

public class BookListCommand implements Command {

    private static final Logger logger = LogManager.getLogger(BookListCommand.class);

    private final BookService bookService = new BookServiceImpl();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
            throws AppException {

        String search = request.getParameter("search");
        logger.debug("Book list requested, search={}", search);

        List<Book> books = bookService.findByTitle(search);

        request.setAttribute("books", books);
        request.setAttribute("search", search);

        logger.debug("Returning {} books to view", books.size());
        return "/views/books.jsp";
    }
}