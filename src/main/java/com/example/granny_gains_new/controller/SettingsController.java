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

/**
 * SettingsController class controls the settings page of Granny Gains application.
 * It includes methods for initializing the page, handling navigation, and loading user session information.
 */
public class SettingsController {

    @FXML
    public void initialize() {
        loadUserSession();
    }
    @FXML
    private Button backButton;
    /**
     * Method to handle going back to the home screen.
     * Retrieves the current stage, loads the home FXML file, and sets the scene to navigate back to the home screen.
     *
     * @throws IOException if an error occurs during loading the FXML file
     */
    @FXML
    protected void handleBackToHome() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/granny_gains_home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
        stage.setScene(scene);
    }

    @FXML
    private Label FetchEmailLabel;

    @FXML
    private Label FetchNameLabel;

    @FXML
    private Label FetchDOBLabel;

    @FXML
    private Label FetchPhone;

    @FXML
    private Label FetchHeight;

    @FXML
    private Label FetchWeight;

    @FXML
    private Label FetchBMI;

    @FXML
    private Button editButton;

    /**
     * Method to handle the edit button action. Retrieves the current Stage from the editButton's Scene,
     * loads the edit settings page FXML file using a FXMLLoader, creates a new Scene with the loaded FXML content,
     * and sets the Stage's scene to navigate to the edit settings page.
     *
     * @throws IOException if an error occurs during loading the edit settings page FXML file
     */
    @FXML
    protected void handleEdit() throws IOException {
        Stage stage = (Stage) editButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/edit_settings_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
        stage.setScene(scene);
        System.out.println("Edit button clicked");
    }

    /**
     * Method to load user session data from the database and display it in the UI elements of the settings screen.
     * Fetches the latest user session information including user details like name, phone, email, date of birth, height, weight, and BMI.
     * If the session data is successfully retrieved, it populates the corresponding labels in the UI with the fetched user information.
     * If any data is missing or not available, appropriate default or placeholder values are displayed.
     * If an error occurs during the database query execution, an error message is shown in the console.
     */
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
        if (phone != null && !phone.isEmpty()) {
            FetchPhone.setText( phone );
        } else {
            FetchPhone.setText("");
        }
        if (email != null && !email.isEmpty()) {
            FetchEmailLabel.setText( email );
        } else {
            FetchEmailLabel.setText("Guest!");
        }
        if (dateOfBirth != null) {
            FetchDOBLabel.setText(dateOfBirth.toString());
        } else {
            FetchDOBLabel.setText("Date of Birth not available!");
        }
        if (height > 0) {
            FetchHeight.setText(String.format("%.2f cm", height));
        } else {
            FetchHeight.setText("Not available");
        }
        if (weight > 0) {
            FetchWeight.setText(String.format("%.2f kg", weight));
        } else {
            FetchWeight.setText("Not available");
        }
        if (bmi > 0) {
            FetchBMI.setText(String.format("%.2f", bmi));
        } else {
            FetchBMI.setText("Not available");
        }
    }
}
