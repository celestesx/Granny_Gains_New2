package com.example.granny_gains_new.controller;

import com.example.granny_gains_new.model.User;
import com.example.granny_gains_new.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignUpController {

    @FXML
    private TextField tfFirstName;

    @FXML
    private TextField tfLastName;

    @FXML
    private TextField tfEmail;

    @FXML
    private TextField tfPassword;

    @FXML
    private Button Buttonsignup;

    @FXML
    private Button BackToSignIn;

    @FXML
    protected void handleBackToSignIn() {
        try {
            Stage stage = (Stage) Buttonsignup.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/sign_in_page.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 650);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to handle the sign-up action
    @FXML
    protected void handleSignUp() {
        // Create a new User object using the input data
        User user = new User(
                tfEmail.getText(),
                tfPassword.getText(),
                tfFirstName.getText(),
                tfLastName.getText()
        );

        // Validate input data
        if (user.getEmail().isEmpty() || user.getPassword().isEmpty()) {
            System.out.println("Please fill in all required fields.");
            return; // Stop if validation fails
        }

        // Insert the user into the database
        insertUserIntoDatabase(user);

        // After signing up, navigate to the user profile page and pass the email
        try {
            Stage stage = (Stage) Buttonsignup.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/user_profile_bmi.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);

            // Get the controller for the BMI calculator
            BMICalculatorController bmiController = fxmlLoader.getController();
            bmiController.setEmail(user.getEmail());  // Pass the email to the BMI controller

            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to insert the user information into the database
    private void insertUserIntoDatabase(User user) {
        Connection conn = DatabaseConnection.getInstance(); // Get the database connection
        String sql = "INSERT INTO User (email, password, first_name, last_name) VALUES (?, ?, ?, ?)"; // SQL insert statement

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getFirstName());
            pstmt.setString(4, user.getLastName());

            // Execute the insert
            pstmt.executeUpdate();
            System.out.println("User signed up and inserted into database successfully!");
        } catch (SQLException e) {
            System.err.println("Error inserting user into database: " + e.getMessage());
        }
    }
}
