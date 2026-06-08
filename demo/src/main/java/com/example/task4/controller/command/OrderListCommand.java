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
import java.util.List;

public class OrderListCommand implements Command {

    private static final Logger logger = LogManager.getLogger(OrderListCommand.class);

    private final OrderService orderService = new OrderServiceImpl();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
            throws AppException {

        User user = (User) request.getSession().getAttribute("user");
        logger.debug("Order list requested by username={}, role={}",
                user.getUsername(), user.getRole());

        List<Order> orders;
        if (user.getRole() == User.Role.ADMIN) {
            orders = orderService.findAll();
            logger.debug("Admin fetched all orders, count={}", orders.size());
        } else {
            orders = orderService.findByUserId(user.getId());
            logger.debug("User {} fetched own orders, count={}",
                    user.getUsername(), orders.size());
        }

        request.setAttribute("orders", orders);
        return "/views/orders.jsp";
    }
}