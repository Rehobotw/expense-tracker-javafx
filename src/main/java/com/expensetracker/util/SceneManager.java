package com.expensetracker.util;

import com.expensetracker.controller.ExpenseController;
import com.expensetracker.controller.IncomeController;
import javafx.application.Platform;
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
        // Make sure it's maximized from the very start
        primaryStage.setMaximized(true);
    }

    /**
     * Standard method to switch main pages (Dashboard, History, etc.)
     */
    public static void loadScene(String fxml, String title) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource(fxml)));

            // 1. Check if a scene already exists
            if (primaryStage.getScene() == null) {
                // First time setup: create the scene and maximize
                Scene scene = new Scene(root);
                primaryStage.setScene(scene);
                primaryStage.setMaximized(true);
            } else {
                // SMOOTH TRANSITION: Just swap the root content.
                // This keeps the window maximized without flickering.
                primaryStage.getScene().setRoot(root);
            }

            primaryStage.setTitle(title);

            if (!primaryStage.isShowing()) {
                primaryStage.show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Helper to ensure the window stays maximized with window controls
     */
    public static void forceMaximize() {
        if (primaryStage != null) {
            Platform.runLater(() -> primaryStage.setMaximized(true));
        }
    }

    // --- Popup Methods ---

    public static void showPopup(String fxml, Runnable refreshCallback) {
        try {
            Parent mainRoot = primaryStage.getScene().getRoot();
            BoxBlur blur = new BoxBlur(10, 10, 3);
            mainRoot.setEffect(blur);

            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxml));
            Parent popupRoot = loader.load();

            Object controller = loader.getController();
            if (controller instanceof IncomeController) {
                ((IncomeController) controller).setRefreshCallback(refreshCallback);
            } else if (controller instanceof ExpenseController) {
                ((ExpenseController) controller).setRefreshCallback(refreshCallback);
            }

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(primaryStage);
            popupStage.initStyle(StageStyle.TRANSPARENT);

            Scene scene = new Scene(popupRoot);
            scene.setFill(Color.TRANSPARENT);
            popupStage.setScene(scene);

            popupStage.setOnHidden(event -> {
                mainRoot.setEffect(null);
                // Ensure the main window stays maximized after popup closes
                forceMaximize();
            });

            popupStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> void showPopup(String fxml, Consumer<T> controllerSetup) {
        try {
            Parent mainRoot = primaryStage.getScene().getRoot();
            BoxBlur blur = new BoxBlur(10, 10, 3);
            mainRoot.setEffect(blur);

            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxml));
            Parent popupRoot = loader.load();

            T controller = loader.getController();
            if (controllerSetup != null) {
                controllerSetup.accept(controller);
            }

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(primaryStage);
            popupStage.initStyle(StageStyle.TRANSPARENT);

            Scene scene = new Scene(popupRoot);
            scene.setFill(Color.TRANSPARENT);
            popupStage.setScene(scene);

            popupStage.setOnHidden(event -> {
                mainRoot.setEffect(null);
                forceMaximize();
            });

            popupStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}