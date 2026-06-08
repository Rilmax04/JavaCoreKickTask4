package com.example.task4.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {

    public enum Status {
        PENDING, CONFIRMED, CANCELLED, COMPLETED
    }

    private Long id;
    private Long userId;
    private String username;
    private LocalDateTime createdAt;
    private Status status;
    private BigDecimal totalPrice;
    private List<OrderItem> items;

    public Order() {
        this.items = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.status = Status.PENDING;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}