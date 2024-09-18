package com.example.granny_gains_new;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;

public class SettingsController {

    @FXML
    private Button backButton;

    @FXML
    protected void handleBackToHome() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/granny_gains_home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 650);
        stage.setScene(scene);
    }

    @FXML
    private Button HelpButton;

    @FXML
    protected void handleToHelp() throws IOException {
        Stage stage = (Stage) HelpButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/help_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 650);
        stage.setScene(scene);
    }
}

