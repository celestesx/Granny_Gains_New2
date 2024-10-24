package com.example.granny_gains_new.controller;

import com.example.granny_gains_new.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Controller class for managing password question retrieval and answer submission.
 * This class handles the retrieval and display of security questions based on the user's email,
 * as well as the validation of the user's answer to proceed with resetting the password.
 */
public class PasswordQuestionController {

    private String email;
    private StringProperty securityQuestion = new SimpleStringProperty();

    @FXML
    private TextField tfAnswer;

    @FXML
    private Button SubmitAnswer;

    @FXML
    private Button BackToEmail;

    public void setEmail(String email) {
        this.email = email;
    }

    public StringProperty securityQuestionProperty() {
        return securityQuestion;
    }

    public String getSecurityQuestion() {
        return securityQuestion.get();
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion.set(securityQuestion);
    }


    /**
     * Method to handle the action of going back to the email entry page.
     * Retrieves the stage from the current scene, loads the 'forgot_password_page.fxml' file
     * in a new scene, sets the stage to display the new scene with maximized dimensions.
     * If an IOException occurs during the loading process, the exception is printed to the error stream.
     */
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

    /**
     * Method to handle the submission of an answer.
     * Retrieves the answer from a text field and validates it with the provided email.
     * If the answer is valid, loads a new scene for redoing the password with the email set in the RedoPasswordController.
     * If the answer is invalid, loads a new scene for signing in.
     * Handles potential IOException during scene loading by printing the exception to the error stream.
     */
    @FXML
    protected void handleSubmitAnswer() {
        try {
            String answer = tfAnswer.getText();

            if (validateAnswer(email, answer)) {
                Stage stage = (Stage) SubmitAnswer.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/redo_password_page.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1200, 650);
                RedoPasswordController controller = fxmlLoader.getController();
                controller.setEmail(email);
                stage.setScene(scene);
                stage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
                stage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
                stage.setMaximized(true);
                stage.show();
            }
            else {
                Stage stage = (Stage) BackToEmail.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/sign_in_page.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1200, 650);
                stage.setScene(scene);
                stage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
                stage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
                stage.setMaximized(true);
                stage.show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Validates the user's answer to the security question saved in the database.
     *
     * @param email the email address of the user
     * @param answer the user's answer to the security question
     * @return true if the provided answer matches the user's secret answer in the database, false otherwise
     */
    public boolean validateAnswer(String email, String answer) {
        try (Connection conn = DatabaseConnection.getInstance()) {
            if (conn == null || conn.isClosed()) {
                System.err.println("Database connection is closed.");
                return false;
            }

            String sql = "SELECT secret_answer FROM User WHERE email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, email);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    String trueAnswer = rs.getString("secret_answer");
                    return trueAnswer.equals(answer);
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
