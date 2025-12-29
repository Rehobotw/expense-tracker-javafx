package com.expensetracker.controller;

import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.dao.IncomeDAO;
import com.expensetracker.model.Expense;
import com.expensetracker.model.Income;
import com.expensetracker.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.fxml.Initializable;

import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.ResourceBundle;

public class IncomeController implements Initializable {

    @FXML private TextField amountField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker datePicker;

    private final IncomeDAO incomeDAO = new IncomeDAO();

    // Callback to refresh Dashboard
    private Runnable refreshCallback;

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Setup Category Items
        categoryComboBox.setItems(FXCollections.observableArrayList(
                Arrays.asList("Salary", "Freelance", "Investment", "Gift", "Other")
        ));

        // Date Restrictions (No future dates)
        LocalDate today = LocalDate.now();
        datePicker.setValue(today);
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isAfter(today)) {
                    setDisable(true);
                    setStyle("-fx-opacity: 0.4;");
                }
            }
        });
    }

   @FXML
    private void handleAddIncome() {
        if (datePicker.getValue() == null ||
                categoryComboBox.getValue() == null ||
                amountField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields");
            return;
        }
        try {
            double amount = Double.parseDouble(amountField.getText());
            String category = categoryComboBox.getValue();
            String date = datePicker.getValue().toString();
            String desc = descriptionField.getText();
            int userId = SessionManager.getUser().getId();

            Income income = new Income(0, date, category, desc, amount);
            IncomeDAO.addIncome(income, userId); // DATABASE SAVE

            if (refreshCallback != null) {
                refreshCallback.run(); // TRIGGER REAL-TIME UPDATE
            }

            ((Stage) amountField.getScene().getWindow()).close();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Please enter a valid number.");
        }
    }

    @FXML private void handleCancel(ActionEvent event) { closeDialog(event); }
    @FXML private void handleClose(ActionEvent event) { closeDialog(event); }

    private void closeDialog(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
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