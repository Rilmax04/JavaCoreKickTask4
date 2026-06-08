package com.kurilo.task4.controller.command;

import com.kurilo.task4.model.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.*;

import java.io.IOException;

public class LogoutCommand implements Command {

    private static final Logger logger = LogManager.getLogger(LogoutCommand.class);

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            logger.info("User logged out: username={}",
                    user != null ? user.getUsername() : "unknown");
            session.invalidate();
        }
        return "redirect:/app/login";
    }
}