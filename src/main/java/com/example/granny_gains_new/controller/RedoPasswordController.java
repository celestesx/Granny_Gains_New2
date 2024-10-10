package com.example.granny_gains_new.controller;

import com.example.granny_gains_new.database.DatabaseConnection;
import com.example.granny_gains_new.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.CollationElementIterator;
import java.util.UUID;

public class RedoPasswordController {

    private String email;

    @FXML
    private PasswordField tfPassword;

    @FXML
    private PasswordField tfVerifyPassword;

    @FXML
    private Button ButtonSubmit;

    @FXML
    private Button BackToEmail;

    @FXML
    private Label lblwrongPassword;

    public void setEmail(String email) {
        this.email = email;
    }

    @FXML
    protected void handleBackToEmail() {
        try {
            Stage stage = (Stage) BackToEmail.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/forgot_password_page.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 650);
            stage.setScene(scene);
            stage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
            stage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handlePassword() {
        String password = tfPassword.getText();
        String password2 = tfVerifyPassword.getText();
        if (password.isEmpty()) {
            lblwrongPassword.setText("Please fill out a Password");
            return;
        } else if (!password.equals(password2)) {
            lblwrongPassword.setText("Passwords are not identical");
            return;
        }

        boolean digit = false;
        for (char c : tfPassword.getText().toCharArray()) {
            if (Character.isDigit(c)) {
                digit = true;
                break;
            }
        }
        if (tfPassword.getText().length() < 8 || !digit) {
            lblwrongPassword.setText("Invalid Password. Password must have at least 8 characters\nand contain at least 1 number.");
        }
        else{
            UpdatePassword(email, password);
            try {
                Stage stage = (Stage) ButtonSubmit.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/sign_in_page.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1200, 650);
                stage.setScene(scene);
                stage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
                stage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
                stage.setMaximized(true);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void UpdatePassword(String email, String password) {
        String sql = "UPDATE User SET password = ? WHERE email = ?";
        try (Connection conn = DatabaseConnection.getInstance();  // Get the database connection
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, password);
            pstmt.setString(2, email);

            // Execute the insert operation
            pstmt.executeUpdate();
            System.out.println("User profile saved successfully!");

        } catch (SQLException e) {
            // Handle SQL errors and display a message
            System.err.println("Error inserting user profile into the database: " + e.getMessage());
        }
    }
}
