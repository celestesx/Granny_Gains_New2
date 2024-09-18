package com.example.granny_gains_new;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class SettingsController {

    @FXML
    private ComboBox<String> textSizeDropdown;

    @FXML
    private TextField emailField;

    @FXML
    private TextField userReportField;

    @FXML
    public void initialize() {
        // Set the default value for text size
        textSizeDropdown.getSelectionModel().select("Medium");
    }

    @FXML
    public void handleSendInquiry() {
        String email = emailField.getText();
        if (!email.isEmpty()) {
            // Logic to handle sending inquiry via email
            showAlert("Inquiry Sent", "Your inquiry has been sent.");
        } else {
            showAlert("Error", "Please enter a valid email.");
        }
    }

    @FXML
    public void handleReportUser() {
        String userReport = userReportField.getText();
        if (!userReport.isEmpty()) {
            // Logic to handle reporting a user
            showAlert("User Reported", "The user has been reported.");
        } else {
            showAlert("Error", "Please enter a username or email to report.");
        }
    }

    @FXML
    public void handleConnectMedicalAccount() {
        // Logic to connect a medical account
        showAlert("Medical Account Connected", "Your medical account has been connected.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}