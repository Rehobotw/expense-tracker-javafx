package com.expensetracker.util;

import com.expensetracker.controller.ExpenseController;
import com.expensetracker.controller.IncomeController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.BoxBlur;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.function.Consumer;

import java.util.Objects;

public class SceneManager {
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage s) {
        primaryStage = s;
    }

    /**
     * Standard method to switch main pages (Dashboard, History, etc.)
     */
    public static void loadScene(String fxml, String title, int w, int h) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource(fxml)));
            primaryStage.setScene(new Scene(root, w, h));
            primaryStage.setTitle(title);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens a FXML file as a modal popup with a blurred background
     * and supports real-time refresh via a callback.
     * * @param fxml Path to the FXML file
     * @param refreshCallback The method to run when the popup saves data (e.g., dashboard refresh)
     */
    public static void showPopup(String fxml, Runnable refreshCallback) {
        try {
            // 1. Apply Blur to the main dashboard
            Parent mainRoot = primaryStage.getScene().getRoot();
            BoxBlur blur = new BoxBlur(10, 10, 3);
            mainRoot.setEffect(blur);

            // 2. Load the popup FXML
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxml));
            Parent popupRoot = loader.load();

            // 3. Get the controller and inject the refresh callback
            Object controller = loader.getController();
            if (controller instanceof IncomeController) {
                ((IncomeController) controller).setRefreshCallback(refreshCallback);
            } else if (controller instanceof ExpenseController) {
                ((ExpenseController) controller).setRefreshCallback(refreshCallback);
            }

            // 4. Configure the popup Stage
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL); // Blocks interaction with dashboard
            popupStage.initOwner(primaryStage);
            popupStage.initStyle(StageStyle.TRANSPARENT); // Clean, borderless look

            // 5. Set up the scene
            Scene scene = new Scene(popupRoot);
            scene.setFill(Color.TRANSPARENT); // Works with CSS background-radius
            popupStage.setScene(scene);

            // 6. Remove blur when the popup is closed (Hidden)
            popupStage.setOnHidden(event -> mainRoot.setEffect(null));

            // 7. Show and wait (prevents user from clicking dashboard until closed)
            popupStage.showAndWait();

        } catch (Exception e) {
            System.err.println("Error loading popup: " + fxml);
            e.printStackTrace();
        }
    }

    /**
     * A generic popup loader that handles the blur effect and stage setup,
     * while allowing custom configuration of the controller.
     */
    public static <T> void showPopup(String fxml, Consumer<T> controllerSetup) {
        try {
            // 1. Apply Blur
            Parent mainRoot = primaryStage.getScene().getRoot();
            BoxBlur blur = new BoxBlur(10, 10, 3);
            mainRoot.setEffect(blur);

            // 2. Load FXML
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxml));
            Parent popupRoot = loader.load();

            // 3. Configure the Controller using the provided Consumer
            T controller = loader.getController();
            if (controllerSetup != null) {
                controllerSetup.accept(controller);
            }

            // 4. Configure Stage
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(primaryStage);
            popupStage.initStyle(StageStyle.TRANSPARENT);

            Scene scene = new Scene(popupRoot);
            scene.setFill(Color.TRANSPARENT);
            popupStage.setScene(scene);

            // 5. Cleanup blur on close
            popupStage.setOnHidden(event -> mainRoot.setEffect(null));

            popupStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}