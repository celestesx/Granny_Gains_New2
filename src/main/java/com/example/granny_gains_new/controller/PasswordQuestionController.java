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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class PasswordQuestionController {

    private String email;

    @FXML
    private DatePicker dpDateOfBirth;

    @FXML
    private Button SubmitDate;

    @FXML
    private Button BackToEmail;

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleEnterDate() {
        try {
            LocalDate dateOfBirth = dpDateOfBirth.getValue();

            if (validateDOB(email, dateOfBirth)) {
                Stage stage = (Stage) SubmitDate.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/redo_password_page.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1200, 650);
                RedoPasswordController controller = fxmlLoader.getController();
                controller.setEmail(email);
                stage.setScene(scene);
            }
            else {
                Stage stage = (Stage) BackToEmail.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/sign_in_page.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1200, 650);
                stage.setScene(scene);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean validateDOB(String email, LocalDate dateOfBirth) {
        try (Connection conn = DatabaseConnection.getInstance()) {
            if (conn == null || conn.isClosed()) {
                System.err.println("Database connection is closed.");
                return false;
            }

            String sql = "SELECT date_of_birth FROM User WHERE email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, email);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    LocalDate storedDate = rs.getDate("date_of_birth").toLocalDate();
                    return storedDate.equals(dateOfBirth);
                } else {
                    return false;  // No user found
                }
            }
        } catch (SQLException e) {
            System.err.println("Error validating user credentials: " + e.getMessage());
            return false;
        }
    }
}
