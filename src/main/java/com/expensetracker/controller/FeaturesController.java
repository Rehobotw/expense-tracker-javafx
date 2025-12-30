package com.expensetracker.controller;

import com.expensetracker.MainApp;
import com.expensetracker.util.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class FeaturesController {

    @FXML
    private Button getStartedBtn;

    @FXML
    void handleGetStarted(ActionEvent event) {
        SceneManager.loadScene(MainApp.signupPage, "Register");
    }
    @FXML
    void handleBackToLanding(ActionEvent event) {
        SceneManager.loadScene(MainApp.landingPage, "Register");
    }

    @FXML
    public void initialize() {
        // Initialization logic if needed
    }


}