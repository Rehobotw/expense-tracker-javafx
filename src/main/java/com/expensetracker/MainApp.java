package com.expensetracker;

import com.expensetracker.util.SceneManager;
import javafx.application.Application;
import com.expensetracker.dao.Database;
import javafx.stage.Stage;

public class MainApp extends Application {

    public static final String landingPage = "/fxml/landing.fxml";
    public static final String loginPage = "/fxml/login.fxml";
    public static final String addExpensePage = "/fxml/add-expense.fxml";
    public static final String addIncomePage= "/fxml/add-income.fxml";
    public static final String dashboardPage = "/fxml/dashboard.fxml";
    public static final String expenseHistoryPage  = "/fxml/transaction-history.fxml";
    public static final String analyticsPage = "/fxml/analytics.fxml";
    public static final String signupPage= "/fxml/signup.fxml";
    public static final String featuresPage="/fxml/features.fxml";
    // Dialogs are loaded separately using new Stages

    @Override
    public void start(Stage primaryStage) {
        Database.initialize();
        // Initialize the SceneManager with the primary window stage
        SceneManager.setPrimaryStage(primaryStage);
        primaryStage.setMaximized(true);
        // Load the initial scene (Landing Page)
        SceneManager.loadScene(landingPage, "MyExpenses - Track Your Finances");

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}