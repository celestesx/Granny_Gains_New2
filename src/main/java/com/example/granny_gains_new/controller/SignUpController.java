package com.example.granny_gains_new.controller;

import com.example.granny_gains_new.model.User;
import com.example.granny_gains_new.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    TextField tfFirstName;

    @FXML
    TextField tfLastName;

    @FXML
    TextField tfEmail;

    @FXML
    TextField tfPassword;

    @FXML
    Button Buttonsignup;

    @FXML
    Button BackToSignIn;

    @FXML
    private Label lblincorrectdetails;

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
        if (user.getEmail().isEmpty() || user.getPassword().isEmpty() || user.getFirstName().isEmpty() || user.getLastName().isEmpty()) {
            System.out.println("Please fill in all required fields.");
            lblincorrectdetails.setText("Please fill out all required fields.");
            return; // Stop if validation fails
        }
        else if (user.getEmail().length() < 10 || !user.getEmail().contains("@")) {
            System.out.println("Invalid Email.");
            lblincorrectdetails.setText("Invalid Email.");
            return; // Stop if validation fails
        }
        boolean digit = false;
        for (char c : user.getPassword().toCharArray()) {
            if (Character.isDigit(c)) {
                digit = true;
                break;
            }
        }
        if (user.getPassword().length() < 8 || !digit) {
            System.out.println("Invalid Password. Password must have at least 8 characters and contain at least 1 number.");
            lblincorrectdetails.setText("Invalid Password. Password must have at least 8 characters\nand contain at least 1 number.");
            return; // Stop if validation fails
        }
        boolean letters = true;
        for (char c : user.getFirstName().toCharArray()) {
            if (!Character.isLetter(c) && c != '-') {
                letters = false;
                break;
            }
        }
        for (char c : user.getLastName().toCharArray()) {
            if (!Character.isLetter(c) && c != '-') {
                letters = false;
                break;
            }
        }
        if (user.getFirstName().length() < 2 || user.getLastName().length() < 2 || !letters){
            System.out.println("Invalid Name.");
            lblincorrectdetails.setText("Invalid Name.");
            return;
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
    public void insertUserIntoDatabase(User user) {
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
