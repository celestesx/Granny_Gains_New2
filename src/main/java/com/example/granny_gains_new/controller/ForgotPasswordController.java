package com.example.granny_gains_new.controller;

import com.example.granny_gains_new.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

/**
 * Controller class for handling user actions related to forgot password functionality.
 * Provides methods for entering email, validating credentials, and navigating back to sign in page.
 */
public class ForgotPasswordController {

    @FXML
    private TextField tfEmail;

    @FXML
    private Button ButtonEnterEmail;

    @FXML
    private Button BackToSignIn;

    @FXML
    private Label lblemail;


    /**
     * Handles the event when the "Back to Sign In" button is clicked. It loads the sign-in page
     * FXML file and sets the stage to display the sign-in page again.
     */
    @FXML
    protected void handleBackToSignIn() {
        try {
            Stage stage = (Stage) ButtonEnterEmail.getScene().getWindow();
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

    /**
     * This method handles the action when entering an email. It checks if the email text field is empty.
     * If it's empty, it displays a message requesting to fill in the email. Otherwise, it calls the validateCredentials
     * method to check the entered email against the database and proceed accordingly.
     */
    @FXML
    protected void handleEnterEmail() {

        if (tfEmail.getText().isEmpty()) {
            lblemail.setText("Please fill in Email");
            return;
        }

        validateCredentials(tfEmail.getText());
    }

    /**
     * Validates the user credentials by checking if the provided email exists in the database.
     * If the email exists, it displays the password question page with the corresponding security question.
     * If the email does not exist, it updates the label to show "Invalid Email".
     *
     * @param email the email address to validate credentials for
     */
    protected void validateCredentials(String email) {
        try (Connection conn = DatabaseConnection.getInstance()) {
            if (conn == null || conn.isClosed()) {
                System.err.println("Database connection is closed.");
                return;
            }

            String sql = "SELECT secret_question FROM User WHERE email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, email);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    try {
                        Stage stage = (Stage) ButtonEnterEmail.getScene().getWindow();
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/password_question_page.fxml"));
                        Scene scene = new Scene(fxmlLoader.load(), 1200, 650);
                        PasswordQuestionController controller = fxmlLoader.getController();
                        controller.setEmail(email);
                        String question = rs.getString("secret_question");
                        controller.setSecurityQuestion(question);
                        stage.setScene(scene);
                        stage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
                        stage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
                        stage.setMaximized(true);
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    lblemail.setText("Invalid Email");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error validating user credentials: " + e.getMessage());
        }
    }
}
