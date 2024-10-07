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
import javafx.scene.control.ComboBox;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SecurityQuestionController {

    private String email; // Email passed from the sign-up page

    @FXML
    ComboBox<String> dropdownMenu;

    @FXML
    TextField tfAnswer;

    @FXML
    Button Buttonsubmit;

    @FXML
    Button BackToSignIn;

    @FXML
    private Label lblincorrectdetails;


    public void setEmail(String email) {
        this.email = email;
    }

    @FXML
    protected void handleBackToSignIn() {
        try {
            Stage stage = (Stage) Buttonsubmit.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/sign_in_page.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 650);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to handle the sign-up action
    @FXML
    protected void handleSecurityQuestion() {
        if (tfAnswer.getText().isEmpty()){
            System.out.println("Invalid Answer.");
            lblincorrectdetails.setText("Invalid Answer.");
            return;
        }

        String sql = "UPDATE User SET secret_question = ?, secret_answer = ? WHERE email = ?";
        try (Connection conn = DatabaseConnection.getInstance();  // Get the database connection
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set values for the prepared statement
            pstmt.setString(1, dropdownMenu.getSelectionModel().getSelectedItem());
            pstmt.setString(2, tfAnswer.getText());
            pstmt.setString(3, email);  // Use the email to update the profile data

            // Execute the insert operation
            pstmt.executeUpdate();
            System.out.println("User security question saved successfully!");

        } catch (SQLException e) {
            // Handle SQL errors and display a message
            System.err.println("Error inserting user security question into the database: " + e.getMessage());
        }

        try {
            Stage stage = (Stage) Buttonsubmit.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/user_profile_bmi.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);

            // Get the controller for the BMI calculator
            BMICalculatorController bmiController = fxmlLoader.getController();
            bmiController.setEmail(email);  // Pass the email to the BMI controller

            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
