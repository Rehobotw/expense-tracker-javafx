package com.expensetracker.controller;

import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.model.Expense;
import com.expensetracker.model.Income;
import com.expensetracker.util.SessionManager;
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

public class ExpenseController implements Initializable {

    @FXML
    private TextField amountField;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private TextArea descriptionField;

    @FXML
    private DatePicker datePicker;


    private final ExpenseDAO expenseDAO = new ExpenseDAO();
    private final int userId = SessionManager.getUserId();
    private Runnable refreshCallback;

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // ---------- CATEGORY FIX ----------
        categoryComboBox.setItems(FXCollections.observableArrayList(
                Arrays.asList("Food", "Transport", "Shopping","Entertainment",
                        "Bills", "health", "Other")
        ));

        categoryComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setStyle("-fx-text-fill: black;");
            }
        });

        categoryComboBox.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setStyle("-fx-text-fill: black;");
            }
        });

        // ---------- DATE LOGIC ----------
        LocalDate today = LocalDate.now();
        LocalDate fiveYearsAgo = today.minusYears(5);

        // Auto set to today
        datePicker.setValue(today);

        // restrict selectable range
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                boolean disable =
                        date.isAfter(today) ||
                                date.isBefore(fiveYearsAgo);

                setDisable(disable);
                if (disable) setStyle("-fx-opacity: 0.4");
            }
        });

        datePicker.setEditable(false);
    }

    @FXML
    private void handleAddExpense() {
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

            Expense expense = new Expense(0, date, category, desc, amount);
            ExpenseDAO.addExpense(expense, userId); // DATABASE SAVE

            if (refreshCallback != null) {
                refreshCallback.run(); // TRIGGER REAL-TIME UPDATE
            }

            ((Stage) amountField.getScene().getWindow()).close();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Please enter a valid number.");
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeDialog(event);
    }

    @FXML
    private void handleClose(ActionEvent event) {
        closeDialog(event);
    }

    private void closeDialog(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource())
                .getScene().getWindow();
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
