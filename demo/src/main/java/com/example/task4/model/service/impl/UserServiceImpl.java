package com.example.task4.model.service.impl;

import com.example.task4.exception.AppException;
import com.example.task4.model.dao.UserDao;
import com.example.task4.model.dao.impl.UserDaoImpl;
import com.example.task4.model.entity.User;
import com.example.task4.model.service.UserService;
import com.example.task4.util.PasswordUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    private final UserDao userDao = new UserDaoImpl();

    @Override
    public Optional<User> authenticate(String username, String password) throws AppException {
        logger.info("Authentication attempt for username: {}", username);

        if (username == null || username.isBlank()) {
            logger.warn("Authentication failed: username is blank");
            throw new AppException("Username must not be empty");
        }
        if (password == null || password.isBlank()) {
            logger.warn("Authentication failed: password is blank");
            throw new AppException("Password must not be empty");
        }

        String hashed = PasswordUtil.hash(password);
        Optional<User> user = userDao.findByUsernameAndPassword(username, hashed);

        if (user.isEmpty()) {
            logger.warn("Authentication failed: invalid credentials for username: {}", username);
            throw new AppException("Invalid username or password");
        }

        logger.info("User authenticated successfully: {}", username);
        return user;
    }

    @Override
    public User register(String username, String password, String email) throws AppException {
        logger.info("Registering new user: {}", username);

        if (username == null || username.length() < 3) {
            logger.warn("Registration failed: invalid username length");
            throw new AppException("Username must be at least 3 characters");
        }
        if (password == null || password.length() < 6) {
            logger.warn("Registration failed: password too short");
            throw new AppException("Password must be at least 6 characters");
        }
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            logger.warn("Registration failed: invalid email format: {}", email);
            throw new AppException("Invalid email format");
        }
        if (userDao.existsByUsername(username)) {
            logger.warn("Registration failed: username already taken: {}", username);
            throw new AppException("Username already taken: " + username);
        }
        if (userDao.existsByEmail(email)) {
            logger.warn("Registration failed: email already registered: {}", email);
            throw new AppException("Email already registered: " + email);
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtil.hash(password));
        user.setEmail(email);
        user.setRole(User.Role.CLIENT);

        User saved = userDao.save(user);
        logger.info("User registered successfully: id={}, username={}", saved.getId(), saved.getUsername());
        return saved;
    }

    @Override
    public Optional<User> findById(Long id) throws AppException {
        logger.debug("Finding user by id: {}", id);
        return userDao.findById(id);
    }

    @Override
    public List<User> findAll() throws AppException {
        logger.debug("Fetching all users");
        return userDao.findAll();
    }

    @Override
    public boolean deleteUser(Long id) throws AppException {
        logger.info("Deleting user: id={}", id);
        boolean deleted = userDao.delete(id);
        if (deleted) {
            logger.info("User deleted: id={}", id);
        } else {
            logger.warn("User not found for deletion: id={}", id);
        }
        return deleted;
    }
}