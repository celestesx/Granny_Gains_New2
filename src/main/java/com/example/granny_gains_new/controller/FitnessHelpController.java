package com.example.granny_gains_new.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller class for handling fitness help views.
 * Contains methods related to navigating back to the home screen.
 */
public class FitnessHelpController {

    @FXML
    private Button HomeButton;

    /**
     * Handles the action event when the back to home button is clicked.
     * It loads the FitnessCardio.fxml file and sets it as the scene for the stage.
     *
     * @throws IOException if an error occurs while loading the FXML file
     */
    @FXML
    protected void handleBackToHome() throws IOException {
        Stage stage = (Stage) HomeButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/FitnessCardio.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
        stage.setScene(scene);
    }
}
