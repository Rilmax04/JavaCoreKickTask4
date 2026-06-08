package com.example.task4.controller.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

public class ShowLoginCommand implements Command {

    private static final Logger logger = LogManager.getLogger(ShowLoginCommand.class);

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            logger.debug("User already logged in, redirecting to books");
            return "redirect:/app/books";
        }
        logger.debug("Showing login page");
        return "/views/login.jsp";
    }
}