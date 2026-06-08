package com.kurilo.task4.model.dao;

import com.kurilo.task4.exception.AppException;
import com.kurilo.task4.model.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderDao {
    Optional<Order> findById(Long id) throws AppException;
    List<Order> findAll() throws AppException;
    List<Order> findByUserId(Long userId) throws AppException;
    Order save(Order order) throws AppException;
    boolean updateStatus(Long id, Order.Status status) throws AppException;
    boolean delete(Long id) throws AppException;
}