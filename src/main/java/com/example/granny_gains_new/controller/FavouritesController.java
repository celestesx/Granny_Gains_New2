package com.example.granny_gains_new.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;

/**
 * Controller class responsible for managing the user's favorite items in the Granny Gains application.
 * This class provides functionality to navigate between different favorite sections such as meals and fitness items.
 */
public class FavouritesController {

    @FXML
    private Button backToHomeButton, mealsButton, fitnessButton;

    /**
     * Method to go back to the Home page.
     * Retrieves the primary stage from the scene of the backToHomeButton, loads the Granny Gains Home page FXML file,
     * creates a new Scene with specified dimensions, and sets the loaded scene in the primary stage.
     *
     * @throws IOException if an error occurs during loading of FXML file
     */
    @FXML
    protected void handleBackToHome() throws IOException {
        Stage stage = (Stage) backToHomeButton.getScene().getWindow(); // Use backToHomeButton instead of HomeButton
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/granny_gains_home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 1000); // Match the size used by others
        stage.setScene(scene);
    }


    /**
     * Handles the action to navigate to the Favorite Meals Page.
     * This method is called when the user interacts with the mealsButton on the Favourites page.
     * It uses the navigateToPage method internally to load the specified FXML file representing the Favorite Meals Page.
     */
    @FXML
    public void handleMeals() {
        navigateToPage("/com/example/granny_gains_new/Fav_Meals_Page.fxml", "Favorite Meals Page");
    }


    /**
     * Method to handle the navigation to the Favorite Fitness Page.
     * This method is triggered when the user interacts with the fitnessButton on the Favorites page.
     * It internally uses the navigateToPage method to load the specified FXML file representing the Favorite Fitness Page.
     */
    @FXML
    public void handleFitness() {
        navigateToPage("/com/example/granny_gains_new/Fav_Fitness_Page.fxml", "Favorite Fitness Page");
    }


    /**
     *
     */
    private void navigateToPage(String fxmlFilePath, String pageName) {
        try {
            Stage stage = (Stage) backToHomeButton.getScene().getWindow(); // Use backToHomeButton here as well
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
