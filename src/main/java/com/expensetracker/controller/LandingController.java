package com.expensetracker.controller;

import javafx.fxml.FXML;
import com.expensetracker.MainApp;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.stage.Modality;
import java.io.IOException;
import com.expensetracker.util.SceneManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import org.w3c.dom.events.MouseEvent;


public class LandingController implements Initializable {

    public Label featuresLabel;
    @FXML
    private Button loginButton;

    @FXML
    private Button getStartedButton;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialization code here, e.g., setting initial state
    }

    @FXML
    private void handleLogin() {
        SceneManager.loadScene(MainApp.loginPage, "Login");
    }


    @FXML
    private void handleGetStarted(ActionEvent event) {
        SceneManager.loadScene(MainApp.signupPage, "Register");
    }

    public void handleFeatures(javafx.scene.input.MouseEvent mouseEvent) {
        SceneManager.loadScene(MainApp.featuresPage, "Register");
    }
}