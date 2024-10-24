package com.example.granny_gains_new.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This class represents a controller for managing help-related functionality in the Granny Gains application.
 * It includes a method to handle the action of going back to the home screen.
 */
public class HelpController {

    @FXML
    private Button BackButton;

    /**
     * Handles the action of navigating back to the home screen in the Granny Gains application.
     * This method retrieves the current stage from the BackButton's scene window, loads the home screen FXML file,
     * and sets a new scene with the home screen contents to the stage. The new scene dimensions are set to 1000x1000.
     *
     * @throws IOException if an input/output error occurs during the loading of the FXML file
     */
    @FXML
    protected void handleBackToHome() throws IOException {
        Stage stage = (Stage) BackButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/granny_gains_home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
        stage.setScene(scene);
    }
}
