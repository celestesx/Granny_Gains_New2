package com.example.granny_gains_new.controller;

import com.example.granny_gains_new.model.User;
import com.example.granny_gains_new.database.DatabaseConnection;
import com.example.granny_gains_new.util.UITextField;
import com.example.granny_gains_new.util.UITextLabel;
import com.example.granny_gains_new.util.JavaFXTextField;
import com.example.granny_gains_new.util.JavaFXTextLabel;
import com.example.granny_gains_new.util.ButtonHandler;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
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
    private TextField fxFirstName, fxLastName, fxEmail, fxPhone, fxPassword;
    @FXML
    private Label fxlblincorrectdetails;

    private UITextField tfFirstName, tfLastName, tfEmail, tfPhone, tfPassword;
    private UITextLabel lblincorrectdetails;
    private ButtonHandler buttonHandler;

    public SignUpController(ButtonHandler buttonHandler) {
        this.buttonHandler = buttonHandler;
    }

    @FXML
    public void initialize() {
        if (fxFirstName != null) tfFirstName = new JavaFXTextField(fxFirstName);
        if (fxLastName != null) tfLastName = new JavaFXTextField(fxLastName);
        if (fxEmail != null) tfEmail = new JavaFXTextField(fxEmail);
        if (fxPhone != null) tfPhone = new JavaFXTextField(fxPhone);
        if (fxPassword != null) tfPassword = new JavaFXTextField(fxPassword);
        if (fxlblincorrectdetails != null) lblincorrectdetails = new JavaFXTextLabel(fxlblincorrectdetails);
    }

    @FXML
    protected void handleBackToSignIn() {
        buttonHandler.handleButtonClick();
        try {
            Stage stage = (Stage) fxFirstName.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/sign_in_page.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 650);
            stage.setScene(scene);
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleSignUp() {
        String email = tfEmail.getText();
        String password = tfPassword.getText();
        String firstName = tfFirstName.getText();
        String lastName = tfLastName.getText();
        String phone = tfPhone.getText();

        if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty()) {
            lblincorrectdetails.setText("Please fill out all required fields.");
            return;
        }

        User user = new User(email, password, firstName, lastName, phone);

        if (user.getEmail().length() < 10 || !user.getEmail().contains("@")) {
            lblincorrectdetails.setText("Invalid Email.");
            return;
        }

        if (!isEmailUnique(user.getEmail())) {
            lblincorrectdetails.setText("Email already in use.");
            return;
        }

        if (user.getPassword().length() < 8 || !user.getPassword().matches(".*\\d.*")) {
            lblincorrectdetails.setText("Invalid Password. Password must have at least 8 characters\nand contain at least 1 number.");
            return;
        }

        if (!user.getPhone().matches("\\d{8,12}")) {
            lblincorrectdetails.setText("Invalid Phone Number.");
            return;
        }

        if (!user.getFirstName().matches("[a-zA-Z-]{2,}") || !user.getLastName().matches("[a-zA-Z-]{2,}")) {
            lblincorrectdetails.setText("Invalid Name.");
            return;
        }

        navigateToSecurityQuestion(user);
    }

    protected void navigateToSecurityQuestion(User user) {
        try {
            Stage stage = (Stage) fxFirstName.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/security_question_page.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 650);

            SecurityQuestionController securityController = fxmlLoader.getController();
            securityController.setUser(user);

            stage.setScene(scene);
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isEmailUnique(String email) {
        try (Connection conn = DatabaseConnection.getInstance()) {
            if (conn == null || conn.isClosed()) {
                System.err.println("Database connection is closed.");
                return false;
            }

            String sql = "SELECT * FROM User WHERE email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, email);
                ResultSet rs = pstmt.executeQuery();
                return !rs.next(); // If there's no next, the email is unique
            }
        } catch (SQLException e) {
            System.err.println("Error checking email uniqueness: " + e.getMessage());
            return false;
        }
    }

    public void setTextFields(UITextField tfFirstName, UITextField tfLastName, UITextField tfEmail, UITextField tfPhone, UITextField tfPassword) {
        this.tfFirstName = tfFirstName;
        this.tfLastName = tfLastName;
        this.tfEmail = tfEmail;
        this.tfPhone = tfPhone;
        this.tfPassword = tfPassword;
    }

    public void setLabel(UITextLabel lblincorrectdetails) {
        this.lblincorrectdetails = lblincorrectdetails;
    }
}