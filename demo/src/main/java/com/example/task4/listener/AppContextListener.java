package com.example.task4.listener;

import com.example.task4.exception.AppException;
import com.example.task4.pool.ConnectionPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

public class AppContextListener implements ServletContextListener {

    private static final Logger logger = LogManager.getLogger(AppContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Application starting up...");
        try {
            ConnectionPool.getInstance();
            logger.info("Application started successfully");
        } catch (AppException e) {
            logger.fatal("Failed to initialize connection pool on startup", e);
            throw new AppException("Application startup failed", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Application shutting down...");
        try {
            ConnectionPool.getInstance().shutdown();
            logger.info("Application shut down successfully");
        } catch (AppException e) {
            logger.warn("Error during connection pool shutdown", e);
        }
    }
}