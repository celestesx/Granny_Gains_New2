package com.example.granny_gains_new.controller;

import com.example.granny_gains_new.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignInController {
    @FXML
    private TextField tfEmail;

    @FXML
    private PasswordField tfPassword;

    @FXML
    private Button ButtonSignin;

    @FXML
    private Label lblforgotPassword;

    @FXML
    protected void handleSignIn() {

        String email = tfEmail.getText();
        String password = tfPassword.getText();

        // Validate the credentials with the database
        if (validateCredentials(email, password)) {
            try {
                // Load the Granny Gains home page
                Stage stage = (Stage) ButtonSignin.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/granny_gains_home.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
                stage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            lblforgotPassword.setText("Invalid credentials. Please try again.");
        }
    }

    // Method to validate credentials with the database
    private boolean validateCredentials(String email, String password) {
        String sql = "SELECT password FROM User WHERE email = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            // If a result is found, validate the password
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword.equals(password);  // Compare passwords (you may want to hash passwords in production)
            } else {
                return false;  // No user found with the provided email
            }
        } catch (SQLException e) {
            System.err.println("Error validating user credentials: " + e.getMessage());
            return false;
        }
    }

    @FXML
    protected void buttonSignup() {
        try {
            Stage stage = (Stage) ButtonSignin.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/sign_up_page.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 650);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
