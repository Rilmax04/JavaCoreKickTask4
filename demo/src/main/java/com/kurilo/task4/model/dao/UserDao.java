package com.kurilo.task4.model.dao;

import com.kurilo.task4.exception.AppException;
import com.kurilo.task4.model.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<User> findById(Long id) throws AppException;
    Optional<User> findByUsernameAndPassword(String username, String password) throws AppException;
    List<User> findAll() throws AppException;
    User save(User user) throws AppException;
    User update(User user) throws AppException;
    boolean delete(Long id) throws AppException;
    boolean existsByUsername(String username) throws AppException;
    boolean existsByEmail(String email) throws AppException;
}