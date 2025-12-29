package com.expensetracker.controller;

import com.expensetracker.MainApp;
import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.dao.IncomeDAO;
import com.expensetracker.model.Expense;
import com.expensetracker.util.SceneManager;
import com.expensetracker.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AnalyticsController implements Initializable {

    @FXML private Label currentDateLabel;
    @FXML private Label biggestExpenseValue;
    @FXML private Label biggestExpenseDetails;
    @FXML private Label averageSpendingValue;

    @FXML private BarChart<String, Number> monthlyBarChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private PieChart categoryPieChart;

    private final ExpenseDAO expenseDAO = new ExpenseDAO();
    private final IncomeDAO incomeDAO = new IncomeDAO();

    private int userId;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        userId = SessionManager.getUser().getId();

        currentDateLabel.setText(
                LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
        );

        populateMetrics();
        populateBarChart();
        populatePieChart();
    }

    /* ============================
       METRICS
       ============================ */

    private void populateMetrics() {
        List<Expense> expenses = expenseDAO.getAll(userId);

        if (expenses.isEmpty()) {
            biggestExpenseValue.setText("0");
            biggestExpenseDetails.setText("No expenses yet");
            averageSpendingValue.setText("0");
            return;
        }

        Expense maxExpense = expenses.stream()
                .max(Comparator.comparingDouble(Expense::getAmount))
                .orElse(null);

        double avg = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .average()
                .orElse(0);

        biggestExpenseValue.setText(String.format("%.2f ETB", maxExpense.getAmount()));
        biggestExpenseDetails.setText(
                maxExpense.getDate() + ", " +
                        maxExpense.getDescription() + ", " +
                        maxExpense.getCategory()
        );

        averageSpendingValue.setText(String.format("%.2f ETB", avg));
    }

    /* ============================
       BAR CHART (MONTHLY)
       ============================ */

    private void populateBarChart() {
        monthlyBarChart.getData().clear();
        monthlyBarChart.setLegendVisible(true);

        Map<String, Double> incomeData = incomeDAO.getMonthlyIncome(userId);
        Map<String, Double> expenseData = expenseDAO.getMonthlyExpenses(userId);

        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");

        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expense");

        Set<String> months = new LinkedHashSet<>();
        months.addAll(incomeData.keySet());
        months.addAll(expenseData.keySet());

        for (String month : months) {
            incomeSeries.getData().add(
                    new XYChart.Data<>(month, incomeData.getOrDefault(month, 0.0))
            );
            expenseSeries.getData().add(
                    new XYChart.Data<>(month, expenseData.getOrDefault(month, 0.0))
            );
        }

        monthlyBarChart.getData().addAll(incomeSeries, expenseSeries);
    }

    /* ============================
       PIE CHART (CATEGORY)
       ============================ */
    private void populatePieChart() {
        int userId = SessionManager.getUser() != null ? SessionManager.getUser().getId() : 0;
        Map<String, Double> categoryData = expenseDAO.getExpensesByCategory(userId);

        categoryPieChart.getData().clear();
        double total = categoryData.values().stream().mapToDouble(Double::doubleValue).sum();

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();

        categoryData.forEach((category, value) -> {
            double percent = (total == 0) ? 0 : (value / total) * 100;
            // The name of the data point will be the percentage string (e.g., "14%")
            // We use a custom property (UserObject) to store the category name for coloring
            PieChart.Data data = new PieChart.Data(String.format("%.0f%%", percent), value);
            pieData.add(data);

            // Apply color immediately after adding to ensure consistency
            applySliceColor(data, category);
        });

        categoryPieChart.setData(pieData);
    }

    private void applySliceColor(PieChart.Data data, String category) {
        // Definitive color map to ensure "Shopping" is Green and others match the legend
        Map<String, String> colorMap = Map.ofEntries(
                Map.entry("Food", "#0052cc"),
                Map.entry("Transport", "#ff9900"),// Handle variations
                Map.entry("Shopping", "#2ecc71"),       // Consistent Green
                Map.entry("Entertainment", "#9b59b6"),
                Map.entry("Bills", "#e74c3c"),
                Map.entry("Health", "#1abc9c"),
                Map.entry("others", "#95a5a6")
        );

        String color = colorMap.getOrDefault(category, "#8E44AD");

        // This listener ensures the color is applied even if the node isn't rendered yet
        data.nodeProperty().addListener((ov, oldNode, newNode) -> {
            if (newNode != null) {
                newNode.setStyle("-fx-pie-color: " + color + ";");
            }
        });
    }

    /* ============================
       NAVIGATION
       ============================ */

    @FXML
    private void handleDashboard(ActionEvent event) {
        SceneManager.loadScene(MainApp.dashboardPage, "Dashboard", 1200, 700);
    }

    @FXML
    private void handleHistory(ActionEvent event) {
        SceneManager.loadScene(MainApp.expenseHistoryPage, "Expense History", 1200, 700);
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        SessionManager.logout();
        SceneManager.loadScene(MainApp.loginPage, "Login", 1200, 700);
    }
}
