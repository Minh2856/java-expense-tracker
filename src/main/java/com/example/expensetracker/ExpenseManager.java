package com.example.expensetracker;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ExpenseManager {
    private final List<Expense> expenses;
    private static final String FILE_NAME = "expenses.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public ExpenseManager() {
        this.expenses = new ArrayList<>();
        loadExpenses();
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
        saveExpenses();
    }

    public List<Expense> getAllExpenses() {
        return new ArrayList<>(expenses);
    }

    public Optional<Expense> getExpenseById(UUID id) {
        return expenses.stream().filter(e -> e.getId().equals(id)).findFirst();
    }

    public void updateExpense(Expense updatedExpense) {
        getExpenseById(updatedExpense.getId()).ifPresent(e -> {
            e.setDate(updatedExpense.getDate());
            e.setCategory(updatedExpense.getCategory());
            e.setDescription(updatedExpense.getDescription());
            e.setPrice(updatedExpense.getPrice());
            saveExpenses();
        });
    }

    public void deleteExpense(UUID id) {
        expenses.removeIf(e -> e.getId().equals(id));
        saveExpenses();
    }

    private void saveExpenses() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            writer.println("ID,Date,Category,Description,Price");
            for (Expense e : expenses) {
                String cleanDescription = e.getDescription().replace(",", ";");
                writer.printf("%s,%s,%s,%s,%.2f%n",
                        e.getId(),
                        e.getDate().format(DATE_FORMATTER),
                        e.getCategory(),
                        cleanDescription,
                        e.getPrice());
            }
        } catch (IOException e) {
            System.err.println("Error saving expenses: " + e.getMessage());
        }
    }

    private void loadExpenses() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 5);
                if (parts.length == 5) {
                    UUID id = UUID.fromString(parts[0]);
                    LocalDate date = LocalDate.parse(parts[1], DATE_FORMATTER);
                    String category = parts[2];
                    String description = parts[3].replace(";", ",");
                    double amount = Double.parseDouble(parts[4]);
                    expenses.add(new Expense(id, date, category, description, amount));
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error loading expenses: " + e.getMessage());
        }
    }
}