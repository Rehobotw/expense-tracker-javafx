package com.expensetracker.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.scene.control.*;
import javafx.fxml.Initializable;

public class ExpenseController implements Initializable {

    @FXML
    private TextField amountField;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private TextField descriptionField;

    @FXML
    private DatePicker datePicker;
    @FXML
    private TextArea descriptionArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize the ComboBox with common expense categories
        categoryComboBox.setItems(FXCollections.observableArrayList(
                Arrays.asList("Food", "Traveling", "Subscription", "Shopping", "Housing", "Utilities", "Other")
        ));
    }

    @FXML
    private void handleAddExpense(ActionEvent event) {
        // 1. Validate input
        if (amountField.getText().isEmpty() || categoryComboBox.getValue() == null) {
            System.out.println("Error: Amount and Category must be selected.");
            // In a real app, you would show a warning label
            return;
        }

        // 2. Process data
        String amountText = amountField.getText();
        String category = categoryComboBox.getValue();
        String description = descriptionField.getText();

        // **Actual expense creation/database interaction would happen here**
        System.out.println("Expense Added:");
        System.out.println("  Amount: " + amountText);
        System.out.println("  Category: " + category);
        System.out.println("  Description: " + description);

        // 3. Close the dialog
        closeDialog(event);
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        System.out.println("Add Expense cancelled.");
        closeDialog(event);
    }

    @FXML
    private void handleClose(ActionEvent event) {
        System.out.println("Add Expense closed.");
        closeDialog(event);
    }

    /**
     * Helper method to close the current dialog stage.
     */
    private void closeDialog(ActionEvent event) {
        // Get the stage from the button that triggered the event
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }
}