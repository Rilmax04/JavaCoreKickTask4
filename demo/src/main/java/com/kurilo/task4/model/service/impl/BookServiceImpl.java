package com.kurilo.task4.model.service.impl;

import com.kurilo.task4.exception.AppException;
import com.kurilo.task4.model.dao.BookDao;
import com.kurilo.task4.model.dao.impl.BookDaoImpl;
import com.kurilo.task4.model.entity.Book;
import com.kurilo.task4.model.service.BookService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class BookServiceImpl implements BookService {

    private static final Logger logger = LogManager.getLogger(BookServiceImpl.class);

    private final BookDao bookDao = new BookDaoImpl();

    @Override
    public List<Book> findAll() throws AppException {
        logger.debug("Fetching all books");
        return bookDao.findAll();
    }

    @Override
    public List<Book> findByTitle(String title) throws AppException {
        if (title == null || title.isBlank()) {
            logger.debug("No search term provided, returning all books");
            return bookDao.findAll();
        }
        logger.debug("Searching books by title: {}", title);
        return bookDao.findByTitle(title.trim());
    }

    @Override
    public Optional<Book> findById(Long id) throws AppException {
        logger.debug("Finding book by id: {}", id);
        return bookDao.findById(id);
    }

    @Override
    public Book addBook(String title, String author, BigDecimal price,
                        int quantity, String description) throws AppException {
        logger.info("Adding new book: title={}, author={}", title, author);
        validateBook(title, author, price, quantity);
        Book book = new Book(null, title.trim(), author.trim(), price, quantity, description);
        Book saved = bookDao.save(book);
        logger.info("Book added successfully: id={}, title={}", saved.getId(), saved.getTitle());
        return saved;
    }

    @Override
    public Book updateBook(Long id, String title, String author, BigDecimal price,
                           int quantity, String description) throws AppException {
        logger.info("Updating book: id={}", id);
        validateBook(title, author, price, quantity);

        Book book = bookDao.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Book not found for update: id={}", id);
                    return new AppException("Book not found: id=" + id);
                });

        book.setTitle(title.trim());
        book.setAuthor(author.trim());
        book.setPrice(price);
        book.setQuantity(quantity);
        book.setDescription(description);

        Book updated = bookDao.update(book);
        logger.info("Book updated successfully: id={}", id);
        return updated;
    }

    @Override
    public boolean deleteBook(Long id) throws AppException {
        logger.info("Deleting book: id={}", id);
        boolean deleted = bookDao.delete(id);
        if (deleted) {
            logger.info("Book deleted successfully: id={}", id);
        } else {
            logger.warn("Book not found for deletion: id={}", id);
        }
        return deleted;
    }

    private void validateBook(String title, String author,
                              BigDecimal price, int quantity) throws AppException {
        if (title == null || title.isBlank()) {
            throw new AppException("Book title must not be empty");
        }
        if (author == null || author.isBlank()) {
            throw new AppException("Author must not be empty");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException("Price must be greater than zero");
        }
        if (quantity < 0) {
            throw new AppException("Quantity must not be negative");
        }
    }
}