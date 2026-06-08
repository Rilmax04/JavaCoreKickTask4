package com.example.task4.controller.command;

import com.example.task4.exception.AppException;
import com.example.task4.model.entity.Order;
import com.example.task4.model.entity.User;
import com.example.task4.model.service.OrderService;
import com.example.task4.model.service.impl.OrderServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.util.HashMap;
import java.util.Map;

public class CreateOrderCommand implements Command {

    private static final Logger logger = LogManager.getLogger(CreateOrderCommand.class);

    private final OrderService orderService = new OrderServiceImpl();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
            throws AppException {

        User user = (User) request.getSession().getAttribute("user");
        logger.info("Order creation started by username={}", user.getUsername());

        Map<Long, Integer> bookQuantities = new HashMap<>();
        String[] bookIds   = request.getParameterValues("bookId");
        String[] quantities = request.getParameterValues("quantity");

        if (bookIds != null && quantities != null) {
            try {
                for (int i = 0; i < bookIds.length; i++) {
                    Long bookId = Long.parseLong(bookIds[i]);
                    Integer qty = Integer.parseInt(quantities[i]);
                    if (qty > 0) {
                        bookQuantities.put(bookId, qty);
                        logger.debug("Cart item: bookId={}, qty={}", bookId, qty);
                    }
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid order data format from username={}", user.getUsername(), e);
                throw new AppException("Invalid order data format", e);
            }
        }

        Order order = orderService.createOrder(user.getId(), bookQuantities);

        logger.info("Order created: orderId={}, username={}, total={}",
                order.getId(), user.getUsername(), order.getTotalPrice());

        request.getSession().setAttribute("successMessage",
                "Order #" + order.getId() + " placed successfully");
        return "redirect:/app/orders";
    }
}