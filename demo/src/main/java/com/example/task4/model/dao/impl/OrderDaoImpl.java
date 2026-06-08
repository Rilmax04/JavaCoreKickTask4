package com.example.task4.model.dao.impl;

import com.example.task4.exception.AppException;
import com.example.task4.model.dao.OrderDao;
import com.example.task4.model.entity.Order;
import com.example.task4.model.entity.OrderItem;
import com.example.task4.pool.ConnectionPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderDaoImpl implements OrderDao {

    private static final Logger logger = LogManager.getLogger(OrderDaoImpl.class);

    private static final String FIND_BY_ID =
            "SELECT o.id, o.user_id, u.username, o.created_at, o.status, o.total_price " +
                    "FROM orders o JOIN users u ON o.user_id = u.id WHERE o.id = ?";
    private static final String FIND_ALL =
            "SELECT o.id, o.user_id, u.username, o.created_at, o.status, o.total_price " +
                    "FROM orders o JOIN users u ON o.user_id = u.id ORDER BY o.created_at DESC";
    private static final String FIND_BY_USER_ID =
            "SELECT o.id, o.user_id, u.username, o.created_at, o.status, o.total_price " +
                    "FROM orders o JOIN users u ON o.user_id = u.id " +
                    "WHERE o.user_id = ? ORDER BY o.created_at DESC";
    private static final String SAVE_ORDER =
            "INSERT INTO orders (user_id, created_at, status, total_price) " +
                    "VALUES (?, ?, ?, ?) RETURNING id";
    private static final String SAVE_ITEM =
            "INSERT INTO order_items (order_id, book_id, book_title, quantity, price) " +
                    "VALUES (?, ?, ?, ?, ?) RETURNING id";
    private static final String FIND_ITEMS =
            "SELECT id, order_id, book_id, book_title, quantity, price " +
                    "FROM order_items WHERE order_id = ?";
    private static final String UPDATE_STATUS =
            "UPDATE orders SET status = ? WHERE id = ?";
    private static final String DELETE_ORDER =
            "DELETE FROM orders WHERE id = ?";

    @Override
    public Optional<Order> findById(Long id) throws AppException {
        logger.debug("Finding order by id: {}", id);
        Connection connection = ConnectionPool.getInstance().getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_ID)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Order order = mapRow(rs);
                order.setItems(findItems(connection, order.getId()));
                logger.debug("Order found: {}", order);
                return Optional.of(order);
            }
            logger.debug("Order not found with id: {}", id);
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding order by id: {}", id, e);
            throw new AppException("Error finding order by id: " + id, e);
        } finally {
            ConnectionPool.getInstance().releaseConnection(connection);
        }
    }

    @Override
    public List<Order> findAll() throws AppException {
        logger.debug("Fetching all orders");
        List<Order> orders = new ArrayList<>();
        Connection connection = ConnectionPool.getInstance().getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(FIND_ALL)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Order order = mapRow(rs);
                order.setItems(findItems(connection, order.getId()));
                orders.add(order);
            }
            logger.debug("Fetched {} orders", orders.size());
            return orders;
        } catch (SQLException e) {
            logger.error("Error fetching all orders", e);
            throw new AppException("Error fetching all orders", e);
        } finally {
            ConnectionPool.getInstance().releaseConnection(connection);
        }
    }

    @Override
    public List<Order> findByUserId(Long userId) throws AppException {
        logger.debug("Fetching orders for userId: {}", userId);
        List<Order> orders = new ArrayList<>();
        Connection connection = ConnectionPool.getInstance().getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_USER_ID)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Order order = mapRow(rs);
                order.setItems(findItems(connection, order.getId()));
                orders.add(order);
            }
            logger.debug("Fetched {} orders for userId: {}", orders.size(), userId);
            return orders;
        } catch (SQLException e) {
            logger.error("Error fetching orders for userId: {}", userId, e);
            throw new AppException("Error fetching orders for userId: " + userId, e);
        } finally {
            ConnectionPool.getInstance().releaseConnection(connection);
        }
    }

    @Override
    public Order save(Order order) throws AppException {
        logger.debug("Saving order for userId: {}", order.getUserId());
        Connection connection = ConnectionPool.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);

            try (PreparedStatement stmt = connection.prepareStatement(SAVE_ORDER)) {
                stmt.setLong(1, order.getUserId());
                stmt.setTimestamp(2, Timestamp.valueOf(order.getCreatedAt()));
                stmt.setString(3, order.getStatus().name());
                stmt.setBigDecimal(4, order.getTotalPrice());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    order.setId(rs.getLong(1));
                }
            }

            for (OrderItem item : order.getItems()) {
                try (PreparedStatement stmt = connection.prepareStatement(SAVE_ITEM)) {
                    stmt.setLong(1, order.getId());
                    stmt.setLong(2, item.getBookId());
                    stmt.setString(3, item.getBookTitle());
                    stmt.setInt(4, item.getQuantity());
                    stmt.setBigDecimal(5, item.getPrice());
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        item.setId(rs.getLong(1));
                    }
                    item.setOrderId(order.getId());
                }
            }

            connection.commit();
            logger.info("Order saved successfully: id={}, userId={}",
                    order.getId(), order.getUserId());
            return order;

        } catch (SQLException e) {
            logger.error("Error saving order, rolling back transaction", e);
            try {
                connection.rollback();
                logger.info("Transaction rolled back successfully");
            } catch (SQLException ex) {
                logger.error("Error rolling back transaction", ex);
                throw new AppException("Error rolling back transaction", ex);
            }
            throw new AppException("Error saving order", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                logger.error("Error restoring autocommit", e);
            }
            ConnectionPool.getInstance().releaseConnection(connection);
        }
    }

    @Override
    public boolean updateStatus(Long id, Order.Status status) throws AppException {
        logger.debug("Updating status for order id={} to {}", id, status);
        Connection connection = ConnectionPool.getInstance().getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(UPDATE_STATUS)) {
            stmt.setString(1, status.name());
            stmt.setLong(2, id);
            boolean updated = stmt.executeUpdate() > 0;
            if (updated) {
                logger.info("Order status updated: id={}, status={}", id, status);
            } else {
                logger.warn("Order not found for status update: id={}", id);
            }
            return updated;
        } catch (SQLException e) {
            logger.error("Error updating order status: id={}", id, e);
            throw new AppException("Error updating order status: " + id, e);
        } finally {
            ConnectionPool.getInstance().releaseConnection(connection);
        }
    }

    @Override
    public boolean delete(Long id) throws AppException {
        logger.debug("Deleting order: id={}", id);
        Connection connection = ConnectionPool.getInstance().getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(DELETE_ORDER)) {
            stmt.setLong(1, id);
            boolean deleted = stmt.executeUpdate() > 0;
            if (deleted) {
                logger.info("Order deleted successfully: id={}", id);
            } else {
                logger.warn("Order not found for deletion: id={}", id);
            }
            return deleted;
        } catch (SQLException e) {
            logger.error("Error deleting order: id={}", id, e);
            throw new AppException("Error deleting order: " + id, e);
        } finally {
            ConnectionPool.getInstance().releaseConnection(connection);
        }
    }

    private List<OrderItem> findItems(Connection connection,
                                      Long orderId) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(FIND_ITEMS)) {
            stmt.setLong(1, orderId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setId(rs.getLong("id"));
                item.setOrderId(rs.getLong("order_id"));
                item.setBookId(rs.getLong("book_id"));
                item.setBookTitle(rs.getString("book_title"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPrice(rs.getBigDecimal("price"));
                items.add(item);
            }
        }
        logger.debug("Loaded {} items for orderId: {}", items.size(), orderId);
        return items;
    }

    private Order mapRow(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setUserId(rs.getLong("user_id"));
        order.setUsername(rs.getString("username"));
        order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        order.setStatus(Order.Status.valueOf(rs.getString("status")));
        order.setTotalPrice(rs.getBigDecimal("total_price"));
        return order;
    }
}