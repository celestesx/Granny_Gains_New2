package com.example.granny_gains_new.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;

/**
 * Controller class for managing the Favourites page GUI functionality.
 * Handles navigation between different pages within the application.
 */
public class FavouritesPageController {

    @FXML
    private Button backToHomeButton, mealsButton, fitnessButton;

    /**
     * Method to handle the action of navigating back to the home page.
     * Calls the navigateToPage method to load the "Home Page" FXML file and display it.
     */
    @FXML
    public void handleBackToHome() {
        navigateToPage("/com/example/granny_gains_new/granny_gains_home.fxml", "Home Page");
    }


    /**
     * Navigates to a specified page by loading the corresponding FXML file and displaying it on the stage.
     *
     * @param fxmlFilePath the file path to the FXML file of the page to navigate to
     * @param pageName the name of the page being navigated to
     */
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
