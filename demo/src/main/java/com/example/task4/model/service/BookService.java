package com.example.task4.model.service;

import com.example.task4.exception.AppException;
import com.example.task4.model.entity.Book;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BookService {
    List<Book> findAll() throws AppException;
    List<Book> findByTitle(String title) throws AppException;
    Optional<Book> findById(Long id) throws AppException;
    Book addBook(String title, String author, BigDecimal price,
                 int quantity, String description) throws AppException;
    Book updateBook(Long id, String title, String author, BigDecimal price,
                    int quantity, String description) throws AppException;
    boolean deleteBook(Long id) throws AppException;
}