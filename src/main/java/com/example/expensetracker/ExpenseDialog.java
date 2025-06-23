package com.example.expensetracker;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class ExpenseDialog extends JDialog {

    private Optional<Expense> expenseOptional = Optional.empty();
    private final Expense existingExpense;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final JTextField dateField;
    private final JTextField categoryField;
    private final JTextField descriptionField;
    private final JTextField amountField;

    public ExpenseDialog(Frame parent, Expense expenseToEdit) {
        super(parent, true);
        this.existingExpense = expenseToEdit;

        setLayout(new BorderLayout(10, 10));
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        dateField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(dateField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Category:"), gbc);
        categoryField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(categoryField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Description:"), gbc);
        descriptionField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(descriptionField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Price:"), gbc);
        amountField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(amountField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);

        if (existingExpense != null) {
            setTitle("Edit Expense");
            dateField.setText(existingExpense.getDate().format(DATE_FORMATTER));
            categoryField.setText(existingExpense.getCategory());
            descriptionField.setText(existingExpense.getDescription());
            amountField.setText(String.valueOf(existingExpense.getPrice()));
        } else {
            setTitle("Add New Expense");
            dateField.setText(LocalDate.now().format(DATE_FORMATTER));
        }

        okButton.addActionListener(e -> onOK());
        cancelButton.addActionListener(e -> onCancel());
    }

    public Optional<Expense> getExpense() {
        return expenseOptional;
    }

    private void onOK() {
        try {
            LocalDate date = LocalDate.parse(dateField.getText().trim(), DATE_FORMATTER);
            String category = categoryField.getText().trim();
            String description = descriptionField.getText().trim();
            double amount = Double.parseDouble(amountField.getText().trim());

            if (category.isEmpty() || description.isEmpty() || amount <= 0) {
                JOptionPane.showMessageDialog(this, "All fields are required and amount must be positive.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (existingExpense != null) {
                existingExpense.setDate(date);
                existingExpense.setCategory(category);
                existingExpense.setDescription(description);
                existingExpense.setPrice(amount);
                expenseOptional = Optional.of(existingExpense);
            } else {
                expenseOptional = Optional.of(new Expense(date, category, description, amount));
            }

            dispose();
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        expenseOptional = Optional.empty();
        dispose();
    }
}