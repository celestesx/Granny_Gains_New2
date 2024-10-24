package com.example.granny_gains_new.controller;

import com.example.granny_gains_new.HelloApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.event.ActionEvent;

import java.io.IOException;

/**
 * Controller class for the Granny Gains application. Handles user interactions and navigation logic.
 */
public class GrannyGainsController {

    @FXML
    private Label welcomeText;
    @FXML
    private TextArea termsAndConditions;
    @FXML
    private CheckBox agreeCheckBox;
    @FXML
    private Button nextButton;

    /**
     * Initializes the terms and conditions text in the GUI.
     */
    @FXML
    public void initialize() {
        termsAndConditions.setText("""
    
            We collect and store the following information:
                            
            Personal Information: Your email address and password are required for account creation and login. 
            We use this information to authenticate your account and communicate with you regarding App 
            updates or relevant information.

            Health Metrics: The App tracks health-related data such as fitness activities, dietary habits, 
            and other metrics. This data is used to provide personalized insights and recommendations aimed 
            at improving your health and fitness.
    """);
    }

    /**
     * Handles the button click event for the "Hello" button in the Granny Gains application.
     * Updates the welcome text to display a welcome message.
     */
    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to Granny Gains!");
    }

    /**
     * Handles the event when the agree checkbox is clicked.
     * If the checkbox is selected, enables the next button; otherwise, disables it.
     */
    @FXML
    protected void onAgreeCheckBoxClick() {
        boolean accepted = agreeCheckBox.isSelected();
        nextButton.setDisable(!accepted);
    }

    /**
     * Handles the button click event for the "Next" button in the Granny Gains application.
     * Loads the sign-in page and displays it in a new scene.
     * @throws IOException if an error occurs during loading the FXML file or setting the scene
     */
    @FXML
    protected void onNextButtonClick() throws IOException {
        Stage stage = (Stage) nextButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("sign_in_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 650);

        stage.setScene(scene);
        stage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
        stage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
        stage.setMaximized(true);
        stage.show();
    }
}
