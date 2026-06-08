package com.example.task4.model.service;

import com.example.task4.exception.AppException;
import com.example.task4.model.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> authenticate(String username, String password) throws AppException;
    User register(String username, String password, String email) throws AppException;
    Optional<User> findById(Long id) throws AppException;
    List<User> findAll() throws AppException;
    boolean deleteUser(Long id) throws AppException;
}