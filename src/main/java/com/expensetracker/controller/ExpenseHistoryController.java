package com.expensetracker.controller;

import com.expensetracker.model.Expense;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

public class ExpenseHistoryController implements Initializable {

    @FXML
    private TableView<Expense> historyTable;

    @FXML
    private Label currentDateLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Set the current date in the header
        currentDateLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));

        // 2. Load dummy data (In a real app, this comes from a database)
        ObservableList<Expense> data = FXCollections.observableArrayList(
                new Expense("22/01/2025", "Food", "Lunch at cafe", -200.00),
                new Expense("22/01/2025", "Salary", "Monthly payment", 400.00),
                new Expense("22/01/2025", "Other", "Refund", 400.00),
                new Expense("22/01/2025", "Shopping", "New shoes", -200.00),
                new Expense("22/01/2025", "Food", "Groceries", -200.00),
                new Expense("22/01/2025", "Freelance", "Project payment", 400.00),
                new Expense("22/01/2025", "Food", "Dinner out", -200.00),
                new Expense("22/01/2025", "Gift", "Birthday money", 400.00)
        );

        historyTable.setItems(data);
    }

    @FXML
    private void handleDashboard(ActionEvent event) {
        // Implement logic to switch to Dashboard scene
        System.out.println("Switching to Dashboard.");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        // Implement logic to log out
        System.out.println("Logging out.");
    }

    // You would need similar methods for handleAnalytics if implemented
}