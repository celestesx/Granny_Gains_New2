package com.example.granny_gains_new.controller;

import com.example.granny_gains_new.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class EditSettingsController {

    @FXML
    public void initialize() {
        loadUserSession();
    }
    @FXML
    private Button backButton;


    @FXML
    protected void handleBackToHome() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/granny_gains_home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
        stage.setScene(scene);
    }

    @FXML
    private Label FetchNameLabel;

    @FXML
    private Label FetchDOBLabel;

    @FXML
    private TextField heightField;

    @FXML
    private TextField weightField;

    @FXML
    private Label FetchBMI;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField emailField;

    @FXML
    private void loadUserSession() {
        String userName = "";
        String phone = "";
        String email = "";
        LocalDate dateOfBirth = null;
        double height = 0;
        double weight = 0;
        double bmi = 0;
        String query = "SELECT u.first_name, u.last_name, u.phone, u.email, u.date_of_birth, u.height, u.weight, u.bmi FROM User u "
                + "JOIN sessions s ON u.email = s.user_id "
                + "ORDER BY s.login_time DESC LIMIT 1"; // Fetch the latest session

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                userName = rs.getString("first_name") + " " + rs.getString("last_name");
                phone = rs.getString("phone");
                email = rs.getString("email");
                dateOfBirth = rs.getDate("date_of_birth").toLocalDate();
                height = rs.getDouble("height");
                weight = rs.getDouble("weight");
                bmi = rs.getDouble("bmi");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user name from session: " + e.getMessage());
        }

        if (!userName.isEmpty()) {
            FetchNameLabel.setText( userName );
        } else {
            FetchNameLabel.setText("Guest!");
        }
        if (dateOfBirth != null) {
            FetchDOBLabel.setText(dateOfBirth.toString());
        } else {
            FetchDOBLabel.setText("Date of Birth not available!");
        }
        if (height > 0) {
            heightField.setText(String.format("%.2f", height));
        } else {
            heightField.setText("");
        }
        if (weight > 0) {
            weightField.setText(String.format("%.2f", weight));
        } else {
            weightField.setText("");
        }
        if (height > 0 && weight > 0) {
            FetchBMI.setText(String.format("%.2f", bmi));
        } else {
            FetchBMI.setText("");
        }
        if (phone != null && !phone.isEmpty()) {
            phoneField.setText(phone);
        } else {
            phoneField.setText("");
        }
        if (email != null && !email.isEmpty()) {
            emailField.setText(email);
        } else {
            emailField.setText("");
        }
    }

    @FXML
    private Button saveButton;

    @FXML
    protected void handleSave() throws IOException {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/settings_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
        stage.setScene(scene);
        System.out.println("Save button clicked");
    }
}
