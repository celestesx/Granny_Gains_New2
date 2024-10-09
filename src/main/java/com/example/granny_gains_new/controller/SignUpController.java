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
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignUpController {

    @FXML
    TextField tfFirstName;

    @FXML
    TextField tfLastName;

    @FXML
    TextField tfEmail;

    @FXML
    TextField tfPhone;

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
                tfLastName.getText(),
                tfPhone.getText()
        );

        // Validate input data
        if (user.getEmail().isEmpty() || user.getPassword().isEmpty() || user.getFirstName().isEmpty() || user.getLastName().isEmpty() || user.getPhone().isEmpty()) {
            System.out.println("Please fill in all required fields.");
            lblincorrectdetails.setText("Please fill out all required fields.");
            return; // Stop if validation fails
        }
        else if (user.getEmail().length() < 10 || !user.getEmail().contains("@")) {
            System.out.println("Invalid Email.");
            lblincorrectdetails.setText("Invalid Email.");
            return; // Stop if validation fails
        }
        try (Connection conn = DatabaseConnection.getInstance()) {
            if (conn == null || conn.isClosed()) {
                System.err.println("Database connection is closed.");
                return;
            }

            String sql = "SELECT * FROM User WHERE email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, user.getEmail());
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    System.out.println("Email already in use.");
                    lblincorrectdetails.setText("Email already in use.");
                    return;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error validating user credentials: " + e.getMessage());
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
        boolean digit2 = true;
        for (char c : user.getPhone().toCharArray()) {
            if (!Character.isDigit(c)) {
                digit2 = false;
                break;
            }
        }
        if (user.getPhone().length() < 8 || user.getPhone().length() > 12 || !digit2) {
            System.out.println("Invalid Phone Number.");
            lblincorrectdetails.setText("Invalid Phone Number.");
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

        // After signing up, navigate to the user profile page and pass the email
        try {
            Stage stage = (Stage) Buttonsignup.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/security_question_page.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 650);

            // Get the controller for the BMI calculator
            SecurityQuestionController securityController = fxmlLoader.getController();
            securityController.setUser(user);

            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
