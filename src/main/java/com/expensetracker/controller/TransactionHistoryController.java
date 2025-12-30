package com.expensetracker.controller;

import com.expensetracker.MainApp;
import com.expensetracker.model.Transaction;
import com.expensetracker.service.ExpenseService;
import com.expensetracker.util.SceneManager;
import com.expensetracker.util.SessionManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.event.ActionEvent;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class TransactionHistoryController implements Initializable {

    @FXML private TableView<Transaction> historyTable;
    @FXML private TableColumn<Transaction, String> typeColumn;
    @FXML private TableColumn<Transaction, String> dateColumn;
    @FXML private TableColumn<Transaction, String> categoryColumn;
    @FXML private TableColumn<Transaction, String> descriptionColumn;
    @FXML private TableColumn<Transaction, Double> amountColumn;
    @FXML private Label currentDateLabel;
    @FXML private TableColumn<Transaction, Void> actionsColumn;

    private final ExpenseService expenseService = new ExpenseService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentDateLabel.setText(LocalDate.now()
                .format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));

        // Setup columns
        setupAmountColumn();
        setupActionColumn();
        loadTransactionHistory();

        // Bind columns to Transaction properties
        typeColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createStringBinding(
                () -> data.getValue().getType()
        ));

        dateColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createStringBinding(
                () -> data.getValue().getDate()
        ));

        categoryColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createStringBinding(
                () -> data.getValue().getCategory()
        ));

        descriptionColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createStringBinding(
                () -> data.getValue().getDescription()
        ));

        amountColumn.setCellValueFactory(data -> javafx.beans.binding.Bindings.createObjectBinding(
                () -> data.getValue().getAmount()
        ));
    }

    private void setupAmountColumn() {
        amountColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                    setGraphic(null);
                    getStyleClass().removeAll("amount-cell-negative", "amount-cell-positive");
                } else {
                    Transaction item = getTableView().getItems().get(getIndex());
                    boolean isIncome = "Income".equalsIgnoreCase(item.getType());

                    if (isIncome) {
                        setText(String.format("+%.2f ETB", amount));
                        getStyleClass().remove("amount-cell-negative");
                        if (!getStyleClass().contains("amount-cell-positive")) {
                            getStyleClass().add("amount-cell-positive");
                        }
                    } else {
                        setText(String.format("-%.2f ETB", amount));
                        getStyleClass().remove("amount-cell-positive");
                        if (!getStyleClass().contains("amount-cell-negative")) {
                            getStyleClass().add("amount-cell-negative");
                        }
                    }
                }
            }
        });
    }

    private void setupActionColumn() {
        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox container = new HBox(10, editBtn, deleteBtn);

            {
                container.getStyleClass().add("action-box");
                editBtn.getStyleClass().add("edit-btn");
                deleteBtn.getStyleClass().add("delete-btn");

                editBtn.setOnAction(e -> editTransaction(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(e -> deleteTransaction(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        });
    }
    private void loadTransactionHistory() {
        List<Transaction> transactions = expenseService.getAllTransactions();
        if (transactions != null) {
            ObservableList<Transaction> data = FXCollections.observableArrayList(transactions);
            historyTable.setItems(data);
        }
    }

    private void deleteTransaction(Transaction transaction) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Transaction");
        confirm.setHeaderText("Are you sure?");
        confirm.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = expenseService.deleteTransaction(
                    transaction.getId(),
                    transaction.getType()
            );

            if (success) {
                loadTransactionHistory();
                showAlert(Alert.AlertType.INFORMATION, "Deleted", "Transaction deleted successfully");
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed", "Failed to delete transaction");
            }
        }
    }

    private void editTransaction(Transaction transaction) {
        // Load appropriate edit dialog based on transaction type
        if ("Income".equalsIgnoreCase(transaction.getType())) {
            // Edit Income
            SceneManager.showPopup("/fxml/edit-income.fxml", (EditIncomeController controller) -> {
                // Create Expense object from Transaction
                com.expensetracker.model.Income income = new com.expensetracker.model.Income(
                        transaction.getId(),
                        transaction.getDate(),
                        transaction.getCategory(),
                        transaction.getDescription(),
                        transaction.getAmount()
                );
                controller.setIncomeData(income);
                controller.setRefreshCallback(this::loadTransactionHistory);
            });
        } else {
            // Edit Expense
            SceneManager.showPopup("/fxml/edit-expense.fxml", (EditExpenseController controller) -> {
                // Create Expense object from Transaction
                com.expensetracker.model.Expense expense = new com.expensetracker.model.Expense(
                        transaction.getId(),
                        transaction.getDate(),
                        transaction.getCategory(),
                        transaction.getDescription(),
                        transaction.getAmount()
                );
                controller.setExpenseData(expense);
                controller.setRefreshCallback(this::loadTransactionHistory);
            });
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }

    @FXML
    private void handleDashboard(ActionEvent event) {
        SceneManager.loadScene(MainApp.dashboardPage, "MyExpenses - Dashboard");
    }

    @FXML
    private void handleAnalytics(ActionEvent event) {
        SceneManager.loadScene(MainApp.analyticsPage, "MyExpenses - Analytics");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        SessionManager.logout();
        SceneManager.loadScene(MainApp.landingPage, "MyExpenses - Login");
    }
}