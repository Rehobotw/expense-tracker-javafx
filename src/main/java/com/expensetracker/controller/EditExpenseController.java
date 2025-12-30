package com.expensetracker.controller;

import com.expensetracker.model.Expense;
import com.expensetracker.service.ExpenseService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.ResourceBundle;

public class EditExpenseController implements Initializable {

    @FXML private TextField amountField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker datePicker;

    private final ExpenseService expenseService = new ExpenseService();
    private Expense currentExpense;
    private Runnable refreshCallback;

    /**
     * Called by the History Controller to pass the selected expense data
     */
    public void setExpenseData(Expense expense) {
        this.currentExpense = expense;

        // Fill fields with existing data
        amountField.setText(String.valueOf(expense.getAmount()));
        categoryComboBox.setValue(expense.getCategory());
        descriptionField.setText(expense.getDescription());

        try {
            datePicker.setValue(LocalDate.parse(expense.getDate()));
        } catch (Exception e) {
            datePicker.setValue(LocalDate.now());
        }
    }

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Setup Categories
        categoryComboBox.setItems(FXCollections.observableArrayList(
                Arrays.asList("Food", "Transport", "Shopping","Entertainment",
                        "Bills", "health", "Other")
        ));

        // Date restriction logic (5 years back to today)
        LocalDate today = LocalDate.now();
        LocalDate fiveYearsAgo = today.minusYears(5);
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                boolean disable = date.isAfter(today) || date.isBefore(fiveYearsAgo);
                setDisable(disable);
                if (disable) setStyle("-fx-opacity: 0.4");
            }
        });
        datePicker.setEditable(false);
        amountField.setOnAction(e -> handleOK());
    }

    @FXML
    private void handleOK() {
        if (datePicker.getValue() == null || categoryComboBox.getValue() == null || amountField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields");
            return;
        }

        try {
            double amount = Double.parseDouble(amountField.getText());

            // Update the object properties
            currentExpense.setAmount(amount);
            currentExpense.setCategory(categoryComboBox.getValue());
            currentExpense.setDate(datePicker.getValue().toString());
            currentExpense.setDescription(descriptionField.getText());

            // Save via Service
            boolean success = expenseService.updateExpense(currentExpense);

            if (success) {
                if (refreshCallback != null) {
                    refreshCallback.run(); // Refresh the table in the background
                }
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Update Failed", "Could not update the database.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Please enter a valid number.");
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow();
    }

    @FXML
    private void handleClose(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) amountField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }
}