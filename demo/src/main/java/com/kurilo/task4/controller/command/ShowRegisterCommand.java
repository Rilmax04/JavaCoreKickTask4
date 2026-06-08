package com.kurilo.task4.controller.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.*;

public class ShowRegisterCommand implements Command {

    private static final Logger logger = LogManager.getLogger(ShowRegisterCommand.class);

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        logger.debug("Showing register page");
        return "/views/register.jsp";
    }
}