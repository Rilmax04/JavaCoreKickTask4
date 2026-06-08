package com.example.task4.model.dao.impl;

import com.example.task4.exception.AppException;
import com.example.task4.model.dao.BookDao;
import com.example.task4.model.entity.Book;
import com.example.task4.pool.ConnectionPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookDaoImpl implements BookDao {

    private static final Logger logger = LogManager.getLogger(BookDaoImpl.class);

    private static final String FIND_BY_ID =
            "SELECT id, title, author, price, quantity, description FROM books WHERE id = ?";
    private static final String FIND_ALL =
            "SELECT id, title, author, price, quantity, description FROM books ORDER BY title";
    private static final String FIND_BY_TITLE =
            "SELECT id, title, author, price, quantity, description FROM books WHERE title ILIKE ?";
    private static final String SAVE =
            "INSERT INTO books (title, author, price, quantity, description) VALUES (?, ?, ?, ?, ?) RETURNING id";
    private static final String UPDATE =
            "UPDATE books SET title = ?, author = ?, price = ?, quantity = ?, description = ? WHERE id = ?";
    private static final String DELETE =
            "DELETE FROM books WHERE id = ?";
    private static final String UPDATE_QUANTITY =
            "UPDATE books SET quantity = quantity + ? WHERE id = ?";

    @Override
    public Optional<Book> findById(Long id) throws AppException {
        logger.debug("Finding book by id: {}", id);
        Connection connection = ConnectionPool.getInstance().getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_ID)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Book book = mapRow(rs);
                logger.debug("Book found: {}", book);
                return Optional.of(book);
            }
            logger.debug("Book not found with id: {}", id);
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding book by id: {}", id, e);
            throw new AppException("Error finding book by id: " + id, e);
        } finally {
            ConnectionPool.getInstance().releaseConnection(connection);
        }
    }

    @Override
    public List<Book> findAll() throws AppException {
        logger.debug("Fetching all books");
        List<Book> books = new ArrayList<>();
        Connection connection = ConnectionPool.getInstance().getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(FIND_ALL)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(mapRow(rs));
            }
            logger.debug("Fetched {} books", books.size());
            return books;
        } catch (SQLException e) {
            logger.error("Error fetching all books", e);
            throw new AppException("Error fetching all books", e);
        } finally {
            ConnectionPool.getInstance().releaseConnection(connection);
        }
    }

    @Override
    public List<Book> findByTitle(String title) throws AppException {
        logger.debug("Searching books by title: {}", title);
        List<Book> books = new ArrayList<>();
        Connection connection = ConnectionPool.getInstance().getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_TITLE)) {
            stmt.setString(1, "%" + title + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(mapRow(rs));
            }
            logger.debug("Found {} books matching title: {}", books.size(), title);
            return books;
        } catch (SQLException e) {
            logger.error("Error searching books by title: {}", title, e);
            throw new AppException("Error searching books by title: " + title, e);
        } finally {
            ConnectionPool.getInstance().releaseConnection(connection);
        }
    }

    @Override
    public Book save(Book book) throws AppException {
        logger.debug("Saving book: {}", book.getTitle());
        Connection connection = ConnectionPool.getInstance().getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(SAVE)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setBigDecimal(3, book.getPrice());
            stmt.setInt(4, book.getQuantity());
            stmt.setString(5, book.getDescription());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                book.setId(rs.getLong(1));
            }
            logger.info("Book saved successfully: id={}, title={}",
                    book.getId(), book.getTitle());
            return book;
        } catch (SQLException e) {
            logger.error("Error saving book: {}", book.getTitle(), e);
            throw new AppException("Error saving book: " + book.getTitle(), e);
        } finally {
            ConnectionPool.getInstance().releaseConnection(connection);
        }
    }

    @Override
    public Book update(Book book) throws AppException {
        logger.debug("Updating book: id={}", book.getId());
        Connection connection = ConnectionPool.getInstance().getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(UPDATE)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setBigDecimal(3, book.getPrice());
            stmt.setInt(4, book.getQuantity());
            stmt.setString(5, book.getDescription());
            stmt.setLong(6, book.getId());
            stmt.executeUpdate();
            logger.info("Book updated successfully: id={}", book.getId());
            return book;
        } catch (SQLException e) {
            logger.error("Error updating book: id={}", book.getId(), e);
            throw new AppException("Error updating book: " + book.getId(), e);
        } finally {
            ConnectionPool.getInstance().releaseConnection(connection);
        }
    }

    @Override
    public boolean delete(Long id) throws AppException {
        logger.debug("Deleting book: id={}", id);
        Connection connection = ConnectionPool.getInstance().getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(DELETE)) {
            stmt.setLong(1, id);
            boolean deleted = stmt.executeUpdate() > 0;
            if (deleted) {
                logger.info("Book deleted successfully: id={}", id);
            } else {
                logger.warn("Book not found for deletion: id={}", id);
            }
            return deleted;
        } catch (SQLException e) {
            logger.error("Error deleting book: id={}", id, e);
            throw new AppException("Error deleting book: " + id, e);
        } finally {
            ConnectionPool.getInstance().releaseConnection(connection);
        }
    }

    @Override
    public boolean updateQuantity(Long id, int delta) throws AppException {
        logger.debug("Updating quantity for book id={}, delta={}", id, delta);
        Connection connection = ConnectionPool.getInstance().getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(UPDATE_QUANTITY)) {
            stmt.setInt(1, delta);
            stmt.setLong(2, id);
            boolean updated = stmt.executeUpdate() > 0;
            logger.debug("Quantity updated for book id={}, delta={}", id, delta);
            return updated;
        } catch (SQLException e) {
            logger.error("Error updating quantity for book id={}", id, e);
            throw new AppException("Error updating quantity for book: " + id, e);
        } finally {
            ConnectionPool.getInstance().releaseConnection(connection);
        }
    }

    private Book mapRow(ResultSet rs) throws SQLException {
        return new Book(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getBigDecimal("price"),
                rs.getInt("quantity"),
                rs.getString("description")
        );
    }
}