package com.example.task4.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.Set;

public class AuthFilter implements Filter {

    private static final Logger logger = LogManager.getLogger(AuthFilter.class);

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/app/login",
            "/app/register"
    );

    @Override
    public void init(FilterConfig filterConfig) {
        logger.info("AuthFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest  req  = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        String path = req.getRequestURI().substring(req.getContextPath().length());
        logger.debug("AuthFilter processing path: {}", path);

        boolean isPublic = PUBLIC_PATHS.stream().anyMatch(path::startsWith);
        if (isPublic) {
            logger.debug("Public path, skipping auth check: {}", path);
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            logger.debug("User authenticated, proceeding: {}", path);
            chain.doFilter(servletRequest, servletResponse);
        } else {
            logger.warn("Unauthenticated access attempt to: {}", path);
            resp.sendRedirect(req.getContextPath() + "/app/login");
        }
    }

    @Override
    public void destroy() {
        logger.info("AuthFilter destroyed");
    }
}