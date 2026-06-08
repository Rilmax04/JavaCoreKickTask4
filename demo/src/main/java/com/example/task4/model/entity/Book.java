package com.example.task4.model.entity;

import java.math.BigDecimal;

public class Book {

    private Long id;
    private String title;
    private String author;
    private BigDecimal price;
    private Integer quantity;
    private String description;

    public Book() {}

    public Book(Long id, String title, String author,
                BigDecimal price, Integer quantity, String description) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}