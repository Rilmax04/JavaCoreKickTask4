package com.example.task4.model.service.impl;

import com.example.task4.exception.AppException;
import com.example.task4.model.dao.BookDao;
import com.example.task4.model.dao.OrderDao;
import com.example.task4.model.dao.impl.BookDaoImpl;
import com.example.task4.model.dao.impl.OrderDaoImpl;
import com.example.task4.model.entity.Book;
import com.example.task4.model.entity.Order;
import com.example.task4.model.entity.OrderItem;
import com.example.task4.model.service.OrderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LogManager.getLogger(OrderServiceImpl.class);

    private final OrderDao orderDao = new OrderDaoImpl();
    private final BookDao  bookDao  = new BookDaoImpl();

    @Override
    public Order createOrder(Long userId,
                             Map<Long, Integer> bookQuantities) throws AppException {
        logger.info("Creating order for userId: {}", userId);

        if (userId == null) {
            throw new AppException("User ID must not be null");
        }
        if (bookQuantities == null || bookQuantities.isEmpty()) {
            throw new AppException("Order must contain at least one book");
        }

        Order order = new Order();
        order.setUserId(userId);

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : bookQuantities.entrySet()) {
            Long bookId = entry.getKey();
            Integer qty = entry.getValue();

            if (qty == null || qty <= 0) {
                continue;
            }

            Book book = bookDao.findById(bookId)
                    .orElseThrow(() -> {
                        logger.warn("Book not found during order creation: id={}", bookId);
                        return new AppException("Book not found: id=" + bookId);
                    });

            if (book.getQuantity() < qty) {
                logger.warn("Insufficient stock for book: id={}, available={}, requested={}",
                        bookId, book.getQuantity(), qty);
                throw new AppException(
                        "Insufficient stock for book '" + book.getTitle() +
                                "'. Available: " + book.getQuantity() + ", requested: " + qty);
            }

            items.add(new OrderItem(bookId, book.getTitle(), qty, book.getPrice()));
            total = total.add(book.getPrice().multiply(BigDecimal.valueOf(qty)));
            bookDao.updateQuantity(bookId, -qty);
            logger.debug("Added book to order: bookId={}, qty={}", bookId, qty);
        }

        if (items.isEmpty()) {
            throw new AppException("No valid books selected for the order");
        }

        order.setItems(items);
        order.setTotalPrice(total);

        Order saved = orderDao.save(order);
        logger.info("Order created successfully: id={}, userId={}, total={}",
                saved.getId(), userId, total);
        return saved;
    }

    @Override
    public Optional<Order> findById(Long id) throws AppException {
        logger.debug("Finding order by id: {}", id);
        return orderDao.findById(id);
    }

    @Override
    public List<Order> findByUserId(Long userId) throws AppException {
        logger.debug("Fetching orders for userId: {}", userId);
        return orderDao.findByUserId(userId);
    }

    @Override
    public List<Order> findAll() throws AppException {
        logger.debug("Fetching all orders");
        return orderDao.findAll();
    }

    @Override
    public boolean cancelOrder(Long orderId, Long userId,
                               boolean isAdmin) throws AppException {
        logger.info("Cancel order request: orderId={}, userId={}, isAdmin={}",
                orderId, userId, isAdmin);

        Order order = orderDao.findById(orderId)
                .orElseThrow(() -> {
                    logger.warn("Order not found for cancellation: id={}", orderId);
                    return new AppException("Order not found: id=" + orderId);
                });

        if (!isAdmin && !order.getUserId().equals(userId)) {
            logger.warn("Access denied: userId={} tried to cancel order of userId={}",
                    userId, order.getUserId());
            throw new AppException("Access denied: cannot cancel another user's order");
        }

        if (order.getStatus() == Order.Status.CANCELLED) {
            logger.warn("Order already cancelled: id={}", orderId);
            throw new AppException("Order is already cancelled");
        }

        if (order.getStatus() == Order.Status.COMPLETED) {
            logger.warn("Cannot cancel completed order: id={}", orderId);
            throw new AppException("Cannot cancel a completed order");
        }

        for (OrderItem item : order.getItems()) {
            bookDao.updateQuantity(item.getBookId(), item.getQuantity());
            logger.debug("Restored stock: bookId={}, qty={}",
                    item.getBookId(), item.getQuantity());
        }

        boolean result = orderDao.updateStatus(orderId, Order.Status.CANCELLED);
        logger.info("Order cancelled successfully: id={}", orderId);
        return result;
    }
}