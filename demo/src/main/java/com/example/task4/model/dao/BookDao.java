package com.example.task4.model.dao;

import com.example.task4.exception.AppException;
import com.example.task4.model.entity.Book;

import java.util.List;
import java.util.Optional;

public interface BookDao {
    Optional<Book> findById(Long id) throws AppException;
    List<Book> findAll() throws AppException;
    List<Book> findByTitle(String title) throws AppException;
    Book save(Book book) throws AppException;
    Book update(Book book) throws AppException;
    boolean delete(Long id) throws AppException;
    boolean updateQuantity(Long id, int delta) throws AppException;
}