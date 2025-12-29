package com.expensetracker.controller;

import com.expensetracker.MainApp;
import com.expensetracker.model.User;
import com.expensetracker.service.UserService;
import com.expensetracker.util.SceneManager;
import com.expensetracker.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField nameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        System.out.println("Login View Initialized");
    }

    @FXML
    private void handleLoginAction() {
        String name = nameField.getText().trim();
        String password = passwordField.getText();

        if (name.isEmpty() || password.isEmpty()) {
            showAlert("Input Error", "Please enter both username and password.");
            return;
        }

        System.out.println("Attempting login for: " + name);

        // 1. Authenticate user via Service
        User user = userService.login(name, password);

        if (user != null) {
            System.out.println("Login Successful! User ID: " + user.getId());

            // 2. FIXED: Store the whole user object in the session
            SessionManager.setUser(user);

            // 3. Navigate to Dashboard
            SceneManager.loadScene(MainApp.dashboardPage, "Dashboard", 1200, 700);
        } else {
            System.out.println("Login Failed: Invalid credentials.");
            showAlert("Login Failed", "Invalid username or password. Please try again.");
        }
    }

    @FXML
    private void handleRegisterAction(ActionEvent event) {
        SceneManager.loadScene(MainApp.registrationPage, "Register", 1200, 700);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}