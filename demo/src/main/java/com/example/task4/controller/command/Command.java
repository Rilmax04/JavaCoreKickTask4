package com.example.task4.controller.command;

import com.example.task4.exception.AppException;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public interface Command {
    String execute(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, AppException;
}