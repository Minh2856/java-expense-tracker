package com.example.expensetracker;

import java.time.LocalDate;
import java.util.UUID;

public class Expense {
    private final UUID id;
    private LocalDate date;
    private String category;
    private String description;
    private double amount;

    public Expense(LocalDate date, String category, String description, double amount) {
        this.id = UUID.randomUUID();
        this.date = date;
        this.category = category;
        this.description = description;
        this.amount = amount;
    }

    public Expense(UUID id, LocalDate date, String category, String description, double amount) {
        this.id = id;
        this.date = date;
        this.category = category;
        this.description = description;
        this.amount = amount;
    }

    public UUID getId() { return id; }
    public LocalDate getDate() { return date; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public double getPrice() { return amount; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double amount) { this.amount = amount; }
}