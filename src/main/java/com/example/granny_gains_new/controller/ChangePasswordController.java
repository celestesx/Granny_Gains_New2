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
import java.util.UUID;

public class ChangePasswordController {

    @FXML
    public void initialize() {
        loadUserSession();
        stage = 0;
    }

    private String email;
    private String password;
    private Integer stage;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label title;

    @FXML
    private Label lblincorrectdetails;

    @FXML
    private Button backButton;

    @FXML
    protected void handleBackToHome() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/edit_settings_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
        stage.setScene(scene);
    }

    @FXML
    private Button submitButton;

    @FXML
    protected void handleSubmit() throws IOException {
        String testPassword = passwordField.getText();
        if (stage == 0) {
            if (password.equals(testPassword)) {
                title.setText("Please Enter Your New Password: ");
                lblincorrectdetails.setText(" ");
                stage = 1;
                passwordField.setText("");
            } else {
                lblincorrectdetails.setText("Incorrect Password");
            }
        } else if (stage == 1) {
            boolean digit = false;
            for (char c : testPassword.toCharArray()) {
                if (Character.isDigit(c)) {
                    digit = true;
                    break;
                }
            }
            if (testPassword.length() < 8 || !digit) {
                lblincorrectdetails.setText("Invalid Password.");
            }
            else {
                title.setText("Please Confirm Your New Password: ");
                lblincorrectdetails.setText(" ");
                stage = 2;
                password = testPassword;
                passwordField.setText("");
            }
        } else {
            if (password.equals(testPassword)) {
                String sql = "UPDATE User SET password = ? WHERE email = ?";
                try (Connection conn = DatabaseConnection.getInstance();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, password);
                    pstmt.setString(2, email);

                    pstmt.executeUpdate();
                    System.out.println("User profile updated successfully!");
                } catch (SQLException e) {
                    System.err.println("Error inserting user profile into the database: " + e.getMessage());
                }

                Stage stage = (Stage) backButton.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/edit_settings_page.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
                stage.setScene(scene);
            } else {
                lblincorrectdetails.setText("Password Not Identical");
            }
        }
    }

    @FXML
    private void loadUserSession() {
        String query = "SELECT u.email, u.password FROM User u "
                + "JOIN sessions s ON u.email = s.user_id "
                + "ORDER BY s.login_time DESC LIMIT 1"; // Fetch the latest session

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                email = rs.getString("email");
                password = rs.getString("password");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user name from session: " + e.getMessage());
        }
    }
}
