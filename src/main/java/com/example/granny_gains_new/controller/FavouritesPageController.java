package com.example.granny_gains_new.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;

public class FavouritesPageController {

    @FXML
    private Button backToHomeButton, mealsButton, fitnessButton;

    @FXML
    public void handleBackToHome() {
        navigateToPage("/com/example/granny_gains_new/granny_gains_home.fxml", "Home Page");
    }

    @FXML
    public void handleMeals() {
        navigateToPage("/com/example/granny_gains_new/Fav_Meals_Page.fxml", "Meals Page");
    }

    @FXML
    public void handleFitness() {
        navigateToPage("/com/example/granny_gains_new/Fav_Fitness_Page", "Fitness Page");
    }

    // Generic method to navigate between pages
    private void navigateToPage(String fxmlFilePath, String pageName) {
        try {
            Stage stage = (Stage) backToHomeButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFilePath));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
            stage.setScene(scene);
            stage.setMaximized(true);
            System.out.println("Navigated to " + pageName);
        } catch (IOException e) {
            System.err.println("Error loading " + pageName + ": " + e.getMessage());
        }
    }
}
