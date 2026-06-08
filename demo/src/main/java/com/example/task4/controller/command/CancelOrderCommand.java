package com.example.task4.controller.command;

import com.example.task4.exception.AppException;
import com.example.task4.model.entity.User;
import com.example.task4.model.service.OrderService;
import com.example.task4.model.service.impl.OrderServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

public class CancelOrderCommand implements Command {

    private static final Logger logger = LogManager.getLogger(CancelOrderCommand.class);

    private final OrderService orderService = new OrderServiceImpl();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
            throws AppException {

        User user = (User) request.getSession().getAttribute("user");

        try {
            Long orderId = Long.parseLong(request.getParameter("orderId"));
            boolean isAdmin = user.getRole() == User.Role.ADMIN;

            logger.info("Cancel order requested: orderId={}, username={}, isAdmin={}",
                    orderId, user.getUsername(), isAdmin);

            orderService.cancelOrder(orderId, user.getId(), isAdmin);

            logger.info("Order cancelled: orderId={}, by username={}",
                    orderId, user.getUsername());

            request.getSession().setAttribute("successMessage",
                    "Order #" + orderId + " has been cancelled");
            return "redirect:/app/orders";

        } catch (NumberFormatException e) {
            logger.warn("Invalid orderId format from username={}", user.getUsername(), e);
            throw new AppException("Invalid order id", e);
        }
    }
}