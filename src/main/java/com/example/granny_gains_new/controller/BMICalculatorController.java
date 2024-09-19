package com.example.granny_gains_new.controller;

import com.example.granny_gains_new.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class BMICalculatorController {

    private String email; // Email passed from the sign-up page

    @FXML
    DatePicker dpDateOfBirth;

    @FXML
    ToggleGroup gender;  // ToggleGroup for gender (Male, Female, Other)

    @FXML
    TextField tfHeight;

    @FXML
    TextField tfWeight;

    @FXML
    Label lblBMI;

    @FXML
    private Button btnCalculate;

    @FXML
    private Button btnSave;

    // Set the email when loading this controller
    public void setEmail(String email) {
        this.email = email;
    }

    @FXML
    protected void initialize() {
        btnCalculate.setOnAction(event -> calculateBMI());
        btnSave.setOnAction(event -> saveProfile());
    }

    // Method to calculate BMI and display it in the lblBMI label
    void calculateBMI() {
        try {
            double height = Double.parseDouble(tfHeight.getText()) / 100; // Convert height from cm to meters
            double weight = Double.parseDouble(tfWeight.getText());

            // Calculate BMI: weight (kg) / (height * height)
            double bmi = weight / (height * height);
            lblBMI.setText(String.format("Your BMI is: %.1f", bmi));
        } catch (NumberFormatException e) {
            // Show error message if inputs are invalid
            lblBMI.setText("Please enter valid numbers for height and weight.");
        }
    }

    // Method to save the user profile to the database and navigate to the home page
    void saveProfile() {
        try {
            // Get the date of birth from DatePicker
            LocalDate dateOfBirth = dpDateOfBirth.getValue();

            double height = Double.parseDouble(tfHeight.getText());
            double weight = Double.parseDouble(tfWeight.getText());
            double bmi = weight / ((height / 100) * (height / 100)); // Calculate BMI

            // Get selected gender from the ToggleGroup
            RadioButton selectedGenderRadioButton = (RadioButton) gender.getSelectedToggle();
            String genderValue = selectedGenderRadioButton.getText();  // Will be "Male", "Female", or "Other"

            // Insert profile into the database using the stored email
            insertProfileIntoDatabase(email, dateOfBirth, genderValue, height, weight, bmi);

            // After saving, navigate to the sign-in page
            Stage stage = (Stage) btnSave.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/sign_in_page.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 650);
            stage.setScene(scene);

        } catch (NumberFormatException e) {
            // Show error message if inputs are invalid
            lblBMI.setText("Please enter valid numbers for height and weight.");
        } catch (IOException e) {
            e.printStackTrace();  // Log IOExceptions for debugging
        }
    }

    // Method to insert the user's profile data into the SQLite database using email
    private void insertProfileIntoDatabase(String email, LocalDate dateOfBirth, String gender, double height, double weight, double bmi) {
        // SQL query to insert the data into the User table
        String sql = "UPDATE User SET date_of_birth = ?, gender = ?, height = ?, weight = ?, bmi = ? WHERE email = ?";
        try (Connection conn = DatabaseConnection.getInstance();  // Get the database connection
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set values for the prepared statement
            pstmt.setDate(1, java.sql.Date.valueOf(dateOfBirth));  // Convert LocalDate to java.sql.Date
            pstmt.setString(2, gender);
            pstmt.setDouble(3, height);
            pstmt.setDouble(4, weight);
            pstmt.setDouble(5, bmi);
            pstmt.setString(6, email);  // Use the email to update the profile data

            // Execute the insert operation
            pstmt.executeUpdate();
            System.out.println("User profile saved successfully!");

        } catch (SQLException e) {
            // Handle SQL errors and display a message
            System.err.println("Error inserting user profile into the database: " + e.getMessage());
        }
    }
}

