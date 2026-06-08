package com.kurilo.task4.controller.command;

import com.kurilo.task4.exception.AppException;
import com.kurilo.task4.model.entity.User;
import com.kurilo.task4.model.service.UserService;
import com.kurilo.task4.model.service.impl.UserServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.*;

public class RegisterCommand implements Command {

    private static final Logger logger = LogManager.getLogger(RegisterCommand.class);

    private final UserService userService = new UserServiceImpl();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
            throws AppException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email    = request.getParameter("email");

        logger.info("Registration attempt: username={}, email={}", username, email);

        User user = userService.register(username, password, email);

        HttpSession session = request.getSession(true);
        session.setAttribute("user", user);

        logger.info("User registered and logged in: {}", user);
        return "redirect:/app/books";
    }
}