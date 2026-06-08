package com.example.task4.pool;

import com.example.task4.exception.AppException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ConnectionPool {

    private static final Logger logger = LogManager.getLogger(ConnectionPool.class);

    private static final String URL       = "jdbc:postgresql://localhost:5432/bookstore";
    private static final String USER      = "postgres";
    private static final String PASSWORD  = "postgres";
    private static final int    POOL_SIZE = 10;

    private static ConnectionPool instance;
    private final BlockingQueue<Connection> pool;

    private ConnectionPool() throws AppException {
        pool = new ArrayBlockingQueue<>(POOL_SIZE);
        try {
            Class.forName("org.postgresql.Driver");
            logger.info("PostgreSQL driver loaded successfully");
            for (int i = 0; i < POOL_SIZE; i++) {
                pool.offer(createConnection());
            }
            logger.info("Connection pool initialized with {} connections", POOL_SIZE);
        } catch (ClassNotFoundException | SQLException e) {
            logger.error("Failed to initialize connection pool", e);
            throw new AppException("Failed to initialize connection pool", e);
        }
    }

    public static synchronized ConnectionPool getInstance() throws AppException {
        if (instance == null) {
            logger.debug("Creating new ConnectionPool instance");
            instance = new ConnectionPool();
        }
        return instance;
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public Connection getConnection() throws AppException {
        try {
            Connection connection = pool.take();
            logger.debug("Connection acquired from pool, remaining: {}", pool.size());
            return connection;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Interrupted while waiting for connection", e);
            throw new AppException("Failed to get connection from pool", e);
        }
    }

    public void releaseConnection(Connection connection) {
        if (connection != null) {
            pool.offer(connection);
            logger.debug("Connection released back to pool, size: {}", pool.size());
        }
    }

    public void shutdown() {
        logger.info("Shutting down connection pool");
        for (Connection connection : pool) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.warn("Error closing connection during shutdown", e);
            }
        }
        logger.info("Connection pool shut down successfully");
    }
}
