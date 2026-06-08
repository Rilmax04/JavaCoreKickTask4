package com.example.task4.controller;

import com.example.task4.controller.command.*;
import com.example.task4.exception.AppException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FrontController extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(FrontController.class);

    private final Map<String, Command> commands = new HashMap<>();

    @Override
    public void init() {
        logger.info("FrontController initializing, registering commands");

        commands.put("GET:/login",     new ShowLoginCommand());
        commands.put("POST:/login",    new LoginCommand());
        commands.put("GET:/logout",    new LogoutCommand());
        commands.put("GET:/register",  new ShowRegisterCommand());
        commands.put("POST:/register", new RegisterCommand());

        commands.put("GET:/books",         new BookListCommand());
        commands.put("GET:/books/add",     new ShowBookFormCommand());
        commands.put("POST:/books/add",    new AddBookCommand());
        commands.put("GET:/books/edit",    new ShowBookFormCommand());
        commands.put("POST:/books/edit",   new EditBookCommand());
        commands.put("POST:/books/delete", new DeleteBookCommand());

        commands.put("GET:/orders",         new OrderListCommand());
        commands.put("POST:/orders/create", new CreateOrderCommand());
        commands.put("POST:/orders/cancel", new CancelOrderCommand());

        logger.info("FrontController initialized with {} commands", commands.size());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processRequest(req, resp);
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        String key = req.getMethod().toUpperCase() + ":" +
                (pathInfo == null ? "/" : pathInfo);

        logger.debug("Processing request: key={}, remoteAddr={}",
                key, req.getRemoteAddr());

        Command command = commands.get(key);

        if (command == null) {
            logger.warn("No command found for key={}", key);
            req.setAttribute("errorMessage", "Page not found: " + pathInfo);
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
            return;
        }

        try {
            String view = command.execute(req, resp);
            if (view != null) {
                if (view.startsWith("redirect:")) {
                    String location = req.getContextPath() + view.substring(9);
                    logger.debug("Redirecting to: {}", location);
                    resp.sendRedirect(location);
                } else {
                    logger.debug("Forwarding to view: {}", view);
                    req.getRequestDispatcher(view).forward(req, resp);
                }
            }
        } catch (AppException e) {
            logger.warn("AppException for key={}: {}", key, e.getMessage(), e);
            req.setAttribute("errorMessage", e.getMessage());
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
        } catch (Exception e) {
            logger.error("Unexpected error for key={}", key, e);
            req.setAttribute("errorMessage", "Internal server error");
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
        }
    }

    @Override
    public void destroy() {
        logger.info("FrontController destroyed");
    }
}