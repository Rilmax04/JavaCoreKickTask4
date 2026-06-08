package com.example.task4.controller.command;

import com.example.task4.exception.AppException;
import com.example.task4.model.entity.User;
import com.example.task4.model.service.UserService;
import com.example.task4.model.service.impl.UserServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.Optional;

public class LoginCommand implements Command {

    private static final Logger logger = LogManager.getLogger(LoginCommand.class);

    private final UserService userService = new UserServiceImpl();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
            throws IOException, AppException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        logger.info("Login attempt for username={}", username);

        Optional<User> userOpt = userService.authenticate(username, password);

        HttpSession session = request.getSession(true);
        session.setAttribute("user", userOpt.get());
        session.setMaxInactiveInterval(30 * 60);

        logger.info("User logged in successfully: username={}", username);
        return "redirect:/app/books";
    }
}