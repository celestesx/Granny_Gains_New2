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
import java.sql.SQLException;

public class SecurityQuestionController {

    private User user;

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


    public void setUser(User user) {
        this.user = user;
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

        String sql = "INSERT INTO User (email, password, first_name, last_name, phone, secret_question, secret_answer) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance();  // Get the database connection
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set values for the prepared statement
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getFirstName());
            pstmt.setString(4, user.getLastName());
            pstmt.setString(5, user.getPhone());
            pstmt.setString(6, dropdownMenu.getSelectionModel().getSelectedItem());
            pstmt.setString(7, tfAnswer.getText());

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
            bmiController.setEmail(user.getEmail());  // Pass the email to the BMI controller

            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
