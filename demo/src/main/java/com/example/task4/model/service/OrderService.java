package com.example.task4.model.service;

import com.example.task4.exception.AppException;
import com.example.task4.model.entity.Order;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrderService {
    Order createOrder(Long userId, Map<Long, Integer> bookQuantities) throws AppException;
    Optional<Order> findById(Long id) throws AppException;
    List<Order> findByUserId(Long userId) throws AppException;
    List<Order> findAll() throws AppException;
    boolean cancelOrder(Long orderId, Long userId, boolean isAdmin) throws AppException;
}