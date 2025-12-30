package com.expensetracker.controller;

import com.expensetracker.MainApp;
import com.expensetracker.model.User;
import com.expensetracker.service.UserService;
import com.expensetracker.util.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SignUpController {

    @FXML
    private TextField fullNameField; // Matches fx:id in FXML

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button registerBtn;

    @FXML
    private Hyperlink loginLink;

    private final UserService userService = new UserService();

    @FXML
    public void handleSingUp (ActionEvent event) {
        // Use trim() to avoid accidental spaces in database
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPass = confirmPasswordField.getText();

        // 1. Validation
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Form Error", "Please fill in all fields.");
            return;
        }

        if (!password.equals(confirmPass)) {
            showAlert(AlertType.ERROR, "Validation Error", "Passwords do not match.");
            return;
        }

        // 2. Business Logic (Service Layer)
        try {
            if (userService.emailExists(email)) {
                showAlert(AlertType.ERROR, "Registration Error", "This email is already registered.");
                return;
            }

            // Creating the user object with the full name
            User user = new User(fullName, email, password);
            boolean success = userService.register(user);

            if (success) {
                showAlert(AlertType.INFORMATION, "Success", "Account created successfully!");
                // Switch back to login
                SceneManager.loadScene(MainApp.loginPage, "Login");
            } else {
                showAlert(AlertType.ERROR, "System Error", "Database could not save the user.");
            }
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Critical Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    @FXML
    void handleLoginLink(ActionEvent event) {
        SceneManager.loadScene(MainApp.loginPage, "Login");
    }

    @FXML
    void handleBackToLanding(ActionEvent event) {
        SceneManager.loadScene(MainApp.landingPage, "Login");
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}