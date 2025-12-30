package com.expensetracker.controller;

import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.util.SceneManager;
import com.expensetracker.util.SessionManager;
import com.expensetracker.service.ExpenseService;
import com.expensetracker.service.IncomeService;
import com.expensetracker.model.Expense;
import com.expensetracker.MainApp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.List;
import javafx.fxml.Initializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;

public class DashboardController implements Initializable {

    @FXML private Label userNameLabel, totalBalanceValue, incomeValue, expenseValue, greetingDateLabel;
    @FXML private VBox recentTransactionsContainer;
    @FXML private PieChart categoryPieChart;

    private final ExpenseService expenseService = new ExpenseService();
    private final IncomeService incomeService = new IncomeService();
    private final ExpenseDAO expenseDAO = new ExpenseDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        refreshDashboard();
    }

    public void refreshDashboard() {
        // Update Greeting
        String fullName = SessionManager.getUser() != null ? SessionManager.getUser().getFullName() : "User";
        userNameLabel.setText("Hello " + fullName + " ,");
        greetingDateLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));

        // Update Totals
        double totalIncome = incomeService.getTotalIncome();
        double totalExpense = expenseService.getTotalExpenses();
        double balance = totalIncome - totalExpense;

        incomeValue.setText(String.format("%.0f ETB", totalIncome));
        expenseValue.setText(String.format("%.0f ETB", totalExpense));
        totalBalanceValue.setText(String.format("%.0f ETB", balance));

        // Update UI Components
        loadRecentTransactions();
        populatePieChart();
    }

    private void loadRecentTransactions() {
        recentTransactionsContainer.getChildren().clear();
        List<Expense> recent = expenseService.getRecentExpenses(6);
        for (Expense e : recent) {
            recentTransactionsContainer.getChildren().add(createTransactionRow(e));
        }
    }

    private HBox createTransactionRow(Expense expense) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 0, 8, 0));

        VBox catGroup = new VBox(2);
        catGroup.setPrefWidth(150);
        Label catLabel = new Label(expense.getCategory());
        catLabel.getStyleClass().add("transaction-category");
        Label descLabel = new Label(expense.getDescription());
        descLabel.getStyleClass().add("transaction-description");
        catGroup.getChildren().addAll(catLabel, descLabel);

        Label dateLabel = new Label(expense.getDate().toString());
        dateLabel.setPrefWidth(120);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label amtLabel = new Label(String.format("-%.0f ETB", expense.getAmount()));
        amtLabel.getStyleClass().add("transaction-amount-neg");

        row.getChildren().addAll(catGroup, dateLabel, spacer, amtLabel);
        return row;
    }

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

    private void applyConsistentColors(ObservableList<PieChart.Data> pieData) {
        // Exact hex codes matching the FXML legend
        Map<String, String> colorMap = Map.ofEntries(
                Map.entry("Food", "#0052cc"),
                Map.entry("Transport", "#ff9900"),
                Map.entry("Shopping", "#2ecc71"), // Green as requested
                Map.entry("Entertainment", "#9b59b6"),
                Map.entry("Bills", "#e74c3c"),
                Map.entry("Health", "#1abc9c"),
                Map.entry("Others", "#95a5a6")
        );

        for (PieChart.Data data : pieData) {
            // Extract category name by removing the percentage part
            String name = data.getName().split(" ")[0];
            String color = colorMap.getOrDefault(name, "#8E44AD"); // Default purple if not found

            if (data.getNode() != null) {
                data.getNode().setStyle("-fx-pie-color: " + color + ";");
            }
        }
    }


    @FXML
    private void handleAddIncome(ActionEvent event) {
        // Pass the refreshDashboard method as a callback
        SceneManager.showPopup(MainApp.addIncomePage, this::refreshDashboard);
    }

    @FXML
    private void handleAddExpense() {
        // Pass the refreshDashboard method as a callback
        SceneManager.showPopup(MainApp.addExpensePage, this::refreshDashboard);
    }

    @FXML private void handleHistory() { SceneManager.loadScene(MainApp.expenseHistoryPage, "Expense History"); }
    @FXML private void handleAnalytics() { SceneManager.loadScene(MainApp.analyticsPage, "Analytics"); }
    @FXML private void handleLogout(ActionEvent event) { SessionManager.logout(); SceneManager.loadScene(MainApp.landingPage, "Login"); }
}