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

/**
 * Controller class for BMI Calculator application.
 * Responsible for handling user input, calculating BMI, saving user profile, and database operations.
 */
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


    /**
     * Set the email address for the user.
     *
     * @param email a String representing the email address to be set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * This method initialises the BMICalculatorController by setting actions for the btnCalculate and btnSave buttons.
     * When the btnCalculate button is clicked, it calls the calculateBMI() method to calculate and display BMI.
     * When the btnSave button is clicked, it calls the saveProfile() method to save user profile data and navigate to the home page.
     */
    @FXML
    protected void initialize() {
        btnCalculate.setOnAction(event -> calculateBMI());
        btnSave.setOnAction(event -> saveProfile());
    }


    /**
     * Calculates the Body Mass Index (BMI) based on the user's input for height and weight.
     * If the input values are valid, it converts the height from centimeters to meters and calculates the BMI.
     * Then it displays the calculated BMI on a label in the UI.
     * If the input values are invalid (e.g., non-numeric inputs), it displays an error message on the UI label.
     */
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


    /**
     * Saves the user profile data by extracting information from the UI components,
     * calculating the BMI, and inserting the profile into the database.
     * After saving, navigates to the sign-in page.
     * Handles NumberFormatException if input values are invalid and IOException during navigation.
     */
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


    /**
     * Inserts the user profile data into the database.
     *
     * @param email a String representing the user's email address
     * @param dateOfBirth a LocalDate representing the user's date of birth
     * @param gender a String representing the user's gender
     * @param height a double representing the user's height
     * @param weight a double representing the user's weight
     * @param bmi a double representing the user's BMI
     */
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

