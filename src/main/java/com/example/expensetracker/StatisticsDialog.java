package com.example.expensetracker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsDialog extends JDialog {

    private final List<Expense> expenses;
    private final JTable dailyTable, weeklyTable, monthlyTable;

    public StatisticsDialog(Frame parent, List<Expense> expenses) {
        super(parent, true);
        this.expenses = expenses;

        setTitle("Expense Statistics");
        setSize(700, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        dailyTable = new JTable();
        weeklyTable = new JTable();
        monthlyTable = new JTable();

        tabbedPane.addTab("Daily", new JScrollPane(dailyTable));
        tabbedPane.addTab("Weekly", new JScrollPane(weeklyTable));
        tabbedPane.addTab("Monthly", createMonthlyPanel());

        add(tabbedPane, BorderLayout.CENTER);

        populateTables();
    }

    private JPanel createMonthlyPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(new JScrollPane(monthlyTable), BorderLayout.CENTER);
        return panel;
    }


    private void populateTables() {
        populateDailyTable();
        populateWeeklyTable();
        populateMonthlyTable();
    }

    private void populateDailyTable() {
        Map<LocalDate, Double> dailyTotals = expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getDate,
                        Collectors.summingDouble(Expense::getPrice)
                ));

        DefaultTableModel model = new DefaultTableModel(new String[]{"Date", "Total Price"}, 0);
        dailyTotals.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> model.addRow(new Object[]{
                        entry.getKey().toString(),
                        String.format("%.2f", entry.getValue())
                }));
        dailyTable.setModel(model);
    }

    private void populateWeeklyTable() {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        Map<String, Double> weeklyTotals = expenses.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getDate().getYear() + "-W" + String.format("%02d", e.getDate().get(weekFields.weekOfWeekBasedYear())),
                        Collectors.summingDouble(Expense::getPrice)
                ));

        DefaultTableModel model = new DefaultTableModel(new String[]{"Week", "Total Price"}, 0);
        weeklyTotals.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> model.addRow(new Object[]{
                        entry.getKey(),
                        String.format("%.2f", entry.getValue())
                }));
        weeklyTable.setModel(model);
    }

    private void populateMonthlyTable() {
        Map<YearMonth, Double> monthlyTotals = expenses.stream()
                .collect(Collectors.groupingBy(
                        e -> YearMonth.from(e.getDate()),
                        Collectors.summingDouble(Expense::getPrice)
                ));

        DefaultTableModel model = new DefaultTableModel(new String[]{"Month", "Total Price"}, 0);
        monthlyTotals.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> model.addRow(new Object[]{
                        entry.getKey().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                        String.format("%.2f", entry.getValue())
                }));
        monthlyTable.setModel(model);
    }
}