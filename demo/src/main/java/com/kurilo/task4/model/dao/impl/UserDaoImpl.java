package com.kurilo.task4.model.dao.impl;

import com.kurilo.task4.exception.AppException;
import com.kurilo.task4.model.dao.UserDao;
import com.kurilo.task4.model.entity.User;
import com.kurilo.task4.pool.ConnectionPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    private static final Logger logger = LogManager.getLogger(UserDaoImpl.class);

    private static final String FIND_BY_ID =
            "SELECT id, username, password, email, role FROM users WHERE id = ?";
    private static final String FIND_BY_USERNAME_AND_PASSWORD =
            "SELECT id, username, password, email, role FROM users WHERE username = ? AND password = ?";
    private static final String FIND_ALL =
            "SELECT id, username, password, email, role FROM users";
    private static final String SAVE =
            "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, ?) RETURNING id";
    private static final String UPDATE =
            "UPDATE users SET username = ?, password = ?, email = ?, role = ? WHERE id = ?";
    private static final String DELETE =
            "DELETE FROM users WHERE id = ?";
    private static final String EXISTS_BY_USERNAME =
            "SELECT COUNT(*) FROM users WHERE username = ?";
    private static final String EXISTS_BY_EMAIL =
            "SELECT COUNT(*) FROM users WHERE email = ?";

    @Override
    public Optional<User> findById(Long id) throws AppException {
        logger.debug("Finding user by id: {}", id);
        Connection connection = ConnectionPool.getInstance().getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_ID)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = mapRow(rs);
                logger.debug("User found: {}", user);
                return Optional.of(user);
            }
            logger.debug("User not found with id: {}", id);
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding user by id: {}", id, e);
            throw new AppException("Error finding user by id: " + id, e);
        } finally {
            ConnectionPool.getInstance().releaseConnection(connection);
        }
    }

    @Override
    public Optional<User> findByUsernameAndPassword(String username,
                                                    String password) throws AppException {
        logger.debug("Finding user by username: {}", username);
        Connection connection = ConnectionPool.getInstance().getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_USERNAME_AND_PASSWORD)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = mapRow(rs);
                logger.debug("User authenticated successfully: {}", username);
                return Optional.of(user);
            }
            logger.warn("Authentication failed for username: {}", username);
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error authenticating user: {}", username, e);
            throw new AppException("Error authenticating user: " + username, e);
        } finally {
            ConnectionPool.getInstance().releaseConnection(connection);
        }
    }

    @Override
    public List<User> findAll() throws AppException {
        logger.debug("Fetching all users");
        List<User> users = new ArrayList<>();
        Connection connection = ConnectionPool.getInstance().getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(FIND_ALL)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(mapRow(rs));
            }
            logger.debug("Fetched {} users", users.size());
            return users;
        } catch (SQLException e) {
            logger.error("Error fetching all users", e);
            throw new AppException("Error fetching all users", e);
        } finally {
            ConnectionPool.getInstance().releaseConnection(connection);
        }
    }

    @Override
    public User save(User user) throws AppException {
        logger.debug("Saving user: {}", user.getUsername());
        Connection connection = ConnectionPool.getInstance().getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(SAVE)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getRole().name());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user.setId(rs.getLong(1));
            }
            logger.info("User saved successfully: id={}, username={}",
                    user.getId(), user.getUsername());
            return user;
        } catch (SQLException e) {
            logger.error("Error saving user: {}", user.getUsername(), e);
            throw new AppException("Error saving user: " + user.getUsername(), e);
        } finally {
            ConnectionPool.getInstance().releaseConnection(connection);
        }
    }

    @Override
    public User update(User user) throws AppException {
        logger.debug("Updating user: id={}", user.getId());
        Connection connection = ConnectionPool.getInstance().getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(UPDATE)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getRole().name());
            stmt.setLong(5, user.getId());
            stmt.executeUpdate();
            logger.info("User updated successfully: id={}", user.getId());
            return user;
        } catch (SQLException e) {
            logger.error("Error updating user: id={}", user.getId(), e);
            throw new AppException("Error updating user: " + user.getId(), e);
        } finally {
            ConnectionPool.getInstance().releaseConnection(connection);
        }
    }

    @Override
    public boolean delete(Long id) throws AppException {
        logger.debug("Deleting user: id={}", id);
        Connection connection = ConnectionPool.getInstance().getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(DELETE)) {
            stmt.setLong(1, id);
            boolean deleted = stmt.executeUpdate() > 0;
            if (deleted) {
                logger.info("User deleted successfully: id={}", id);
            } else {
                logger.warn("User not found for deletion: id={}", id);
            }
            return deleted;
        } catch (SQLException e) {
            logger.error("Error deleting user: id={}", id, e);
            throw new AppException("Error deleting user: " + id, e);
        } finally {
            ConnectionPool.getInstance().releaseConnection(connection);
        }
    }

    @Override
    public boolean existsByUsername(String username) throws AppException {
        logger.debug("Checking if username exists: {}", username);
        Connection connection = ConnectionPool.getInstance().getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(EXISTS_BY_USERNAME)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            boolean exists = rs.next() && rs.getInt(1) > 0;
            logger.debug("Username '{}' exists: {}", username, exists);
            return exists;
        } catch (SQLException e) {
            logger.error("Error checking username: {}", username, e);
            throw new AppException("Error checking username: " + username, e);
        } finally {
            ConnectionPool.getInstance().releaseConnection(connection);
        }
    }

    @Override
    public boolean existsByEmail(String email) throws AppException {
        logger.debug("Checking if email exists: {}", email);
        Connection connection = ConnectionPool.getInstance().getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(EXISTS_BY_EMAIL)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            boolean exists = rs.next() && rs.getInt(1) > 0;
            logger.debug("Email '{}' exists: {}", email, exists);
            return exists;
        } catch (SQLException e) {
            logger.error("Error checking email: {}", email, e);
            throw new AppException("Error checking email: " + email, e);
        } finally {
            ConnectionPool.getInstance().releaseConnection(connection);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("email"),
                User.Role.valueOf(rs.getString("role"))
        );
    }
}