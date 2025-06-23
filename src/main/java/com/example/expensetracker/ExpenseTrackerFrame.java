// src/main/java/com/example/expensetracker/ExpenseTrackerFrame.java
package com.example.expensetracker;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ExpenseTrackerFrame extends JFrame {

    private final ExpenseManager expenseManager;
    private final DefaultTableModel tableModel;
    private final JTable expenseTable;

    public ExpenseTrackerFrame() {
        // Core components
        expenseManager = new ExpenseManager();

        // --- UI Initialization ---
        setTitle("Java Expense Tracker");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame

        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table Model and JTable
        String[] columnNames = {"ID", "Date", "Category", "Description", "Price"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            // Make cells non-editable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            // Set column types for proper sorting
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) return Double.class;
                return String.class;
            }
        };
        expenseTable = new JTable(tableModel);
        setupTable();

        // Scroll Pane for the table
        JScrollPane scrollPane = new JScrollPane(expenseTable);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton addButton = new JButton("Add Expense");
        JButton editButton = new JButton("Edit Selected");
        JButton deleteButton = new JButton("Delete Selected");
        JButton statsButton = new JButton("View Statistics");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(statsButton);

        // Add components to main panel
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Set content pane
        setContentPane(mainPanel);

        // --- Action Listeners ---
        addButton.addActionListener(e -> addExpense());
        editButton.addActionListener(e -> editExpense());
        deleteButton.addActionListener(e -> deleteExpense());
        statsButton.addActionListener(e -> showStatistics());

        // Initial data load
        refreshTable();
    }

    private void setupTable() {
        expenseTable.setAutoCreateRowSorter(true);
        expenseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Hide the ID column
        TableColumn idColumn = expenseTable.getColumnModel().getColumn(0);
        idColumn.setMinWidth(0);
        idColumn.setMaxWidth(0);
        idColumn.setWidth(0);
        idColumn.setPreferredWidth(0);
    }

    private void refreshTable() {
        tableModel.setRowCount(0); // Clear existing data
        List<Expense> expenses = expenseManager.getAllExpenses();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Expense expense : expenses) {
            tableModel.addRow(new Object[]{
                    expense.getId(),
                    expense.getDate().format(formatter),
                    expense.getCategory(),
                    expense.getDescription(),
                    expense.getPrice()
            });
        }
    }

    private void addExpense() {
        ExpenseDialog dialog = new ExpenseDialog(this, null);
        dialog.setVisible(true);

        Optional<Expense> newExpense = dialog.getExpense();
        if (newExpense.isPresent()) {
            expenseManager.addExpense(newExpense.get());
            refreshTable();
        }
    }

    private void editExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an expense to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = expenseTable.convertRowIndexToModel(selectedRow);
        UUID expenseId = (UUID) tableModel.getValueAt(modelRow, 0);

        expenseManager.getExpenseById(expenseId).ifPresent(expenseToEdit -> {
            ExpenseDialog dialog = new ExpenseDialog(this, expenseToEdit);
            dialog.setVisible(true);

            dialog.getExpense().ifPresent(updatedExpense -> {
                expenseManager.updateExpense(updatedExpense);
                refreshTable();
            });
        });
    }

    private void deleteExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an expense to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this expense?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int modelRow = expenseTable.convertRowIndexToModel(selectedRow);
            UUID expenseId = (UUID) tableModel.getValueAt(modelRow, 0);
            expenseManager.deleteExpense(expenseId);
            refreshTable();
        }
    }

    private void showStatistics() {
        StatisticsDialog statsDialog = new StatisticsDialog(this, expenseManager.getAllExpenses());
        statsDialog.setVisible(true);
    }

    public static void main(String[] args) {
        // Use FlatLaf for a modern look and feel
        FlatLightLaf.setup();

        // Run the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new ExpenseTrackerFrame().setVisible(true));
    }
}