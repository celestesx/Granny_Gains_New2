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

/**
 * This class represents the controller for editing user settings in the Granny Gains application.
 * It loads the user session data from the database to populate the settings fields and allows the user
 * to save the updated settings or change their password.
 */
public class EditSettingsController {

    @FXML
    public void initialize() {
        loadUserSession();
    }
    @FXML
    private Button backButton;


    /**
     * Handles the action when the Back to Home button is clicked. Loads the settings_page.fxml file
     * and sets it as the scene for the current stage to navigate back to the main home screen.
     *
     * @throws IOException if an I/O error occurs during loading the settings_page.fxml file
     */
    @FXML
    protected void handleBackToHome() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/settings_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
        stage.setScene(scene);
    }

    @FXML
    private Label FetchNameLabel;

    @FXML
    private Label FetchDOBLabel;

    @FXML
    private TextField heightField;

    @FXML
    private TextField weightField;

    @FXML
    private Label FetchBMI;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField emailField;

    @FXML
    private Label lblincorrectdetails;

    /**
     * This method loads the user session by fetching the user's information from the database.
     * It retrieves the user's full name, phone number, email, date of birth, height, weight, and BMI from the most recent session entry in the database.
     * If the user information is successfully retrieved, it populates the respective fields in the UI with the fetched values.
     * If any of the information is missing or empty, appropriate default values are displayed in the UI fields.
     * If there is an error while fetching the user information from the database, an error message is printed to the console.
     */
    @FXML
    private void loadUserSession() {
        String userName = "";
        String phone = "";
        String email = "";
        LocalDate dateOfBirth = null;
        double height = 0;
        double weight = 0;
        double bmi = 0;
        String query = "SELECT u.first_name, u.last_name, u.phone, u.email, u.date_of_birth, u.height, u.weight, u.bmi FROM User u "
                + "JOIN sessions s ON u.email = s.user_id "
                + "ORDER BY s.login_time DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                userName = rs.getString("first_name") + " " + rs.getString("last_name");
                phone = rs.getString("phone");
                email = rs.getString("email");
                dateOfBirth = rs.getDate("date_of_birth").toLocalDate();
                height = rs.getDouble("height");
                weight = rs.getDouble("weight");
                bmi = rs.getDouble("bmi");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user name from session: " + e.getMessage());
        }

        if (!userName.isEmpty()) {
            FetchNameLabel.setText( userName );
        } else {
            FetchNameLabel.setText("Guest!");
        }
        if (dateOfBirth != null) {
            FetchDOBLabel.setText(dateOfBirth.toString());
        } else {
            FetchDOBLabel.setText("Date of Birth not available!");
        }
        if (height > 0) {
            heightField.setText(String.format("%.1f", height));
        } else {
            heightField.setText("");
        }
        if (weight > 0) {
            weightField.setText(String.format("%.1f", weight));
        } else {
            weightField.setText("");
        }
        if (bmi > 0) {
            FetchBMI.setText(String.format("%.1f", bmi));
        } else {
            FetchBMI.setText("");
        }
        if (phone != null && !phone.isEmpty()) {
            phoneField.setText(phone);
        } else {
            phoneField.setText("");
        }
        if (email != null && !email.isEmpty()) {
            emailField.setText(email);
        } else {
            emailField.setText("");
        }
    }

    @FXML
    private Button saveButton;

    /**
     * This method handles the action when the Save button is clicked in the user settings form.
     * It validates the input fields for email, phone number, height, and weight.
     * Checks for empty fields, valid email format, unique email, valid phone number, height range (50-250 cm), and weight range (20-500 kg).
     * If any validation fails, appropriate error messages are displayed.
     * If all validations pass, it updates the user's information in the database by calling the UpdateDatabase method.
     * Finally, it navigates back to the settings page.
     *
     * @throws IOException if an I/O error occurs during navigating back to the settings page
     */
    @FXML
    protected void handleSave() throws IOException {
        String email = emailField.getText();
        String phone = phoneField.getText();
        String height = heightField.getText();
        String weight = weightField.getText();
        String activeEmail = "";
        if (email.isEmpty() || phone.isEmpty() || height.isEmpty() || weight.isEmpty()){
            System.out.println("Please fill in all required fields.");
            lblincorrectdetails.setText("Please fill out all required fields.");
            return;
        }
        else if (email.length() < 10 || !email.contains("@")) {
            System.out.println("Invalid Email.");
            lblincorrectdetails.setText("Invalid Email.");
            return;
        }
        try (Connection conn = DatabaseConnection.getInstance()) {
            if (conn == null || conn.isClosed()) {
                System.err.println("Database connection is closed.");
                return;
            }

            String sql = "SELECT * FROM User WHERE email = ?";
            String query = "SELECT u.email FROM User u "
                    + "JOIN sessions s ON u.email = s.user_id "
                    + "ORDER BY s.login_time DESC LIMIT 1";
            Boolean skip = false;

            try (PreparedStatement stmt = conn.prepareStatement(query)) {

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    activeEmail = rs.getString("email");
                    if (activeEmail.equals(email)) skip = true;
                }
            } catch (SQLException e) {
                System.err.println("Error fetching user name from session: " + e.getMessage());
            }
            if (!skip){
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, email);
                    ResultSet rs = pstmt.executeQuery();

                    if (rs.next()) {
                        System.out.println("Email already in use.");
                        lblincorrectdetails.setText("Email already in use.");
                        return;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error validating user credentials: " + e.getMessage());
        }

        boolean digit = true;
        for (char c : phone.toCharArray()) {
            if (!Character.isDigit(c)) {
                digit = false;
                break;
            }
        }
        if (phone.length() < 8 || phone.length() > 12 || !digit) {
            System.out.println("Invalid Phone Number.");
            lblincorrectdetails.setText("Invalid Phone Number.");
            return;
        }

        try {
            double heightValue = Double.parseDouble(height);
            if (heightValue < 50 || heightValue > 250) {
                System.out.println("Invalid Height.");
                lblincorrectdetails.setText("Invalid Height. Must be between 50 and 250 cm.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid Height Format.");
            lblincorrectdetails.setText("Invalid Height Format. Please enter a number.");
            return;
        }

        try {
            double weightValue = Double.parseDouble(weight);
            if (weightValue < 20 || weightValue > 500) {
                System.out.println("Invalid Weight.");
                lblincorrectdetails.setText("Invalid Weight. Must be between 20 and 500 kg.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid Weight Format.");
            lblincorrectdetails.setText("Invalid Weight Format. Please enter a number.");
            return;
        }

        UpdateDatabase(activeEmail, email, phone, Double.parseDouble(height), Double.parseDouble(weight));

        Stage stage = (Stage) saveButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/settings_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
        stage.setScene(scene);
        System.out.println("Save button clicked");
    }

    /**
     * Updates the user profile information in the database based on the provided parameters.
     *
     * @param activeEmail The current active email of the user whose profile is being updated.
     * @param email The new email to update in the user profile.
     * @param phone The new phone number to update in the user profile.
     * @param height The new height to update in the user profile.
     * @param weight The new weight to update in the user profile.
     */
    private void UpdateDatabase(String activeEmail, String email, String phone, double height, double weight) {
        String sql = "UPDATE User SET email = ?, phone = ?, height = ?, weight = ?, bmi = ? WHERE email = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            double bmi = weight / ((height / 100) * (height / 100));
            pstmt.setString(1, email);
            pstmt.setString(2, phone);
            pstmt.setDouble(3, height);
            pstmt.setDouble(4, weight);
            pstmt.setDouble(5, bmi);
            pstmt.setString(6, activeEmail);

            pstmt.executeUpdate();
            System.out.println("User profile updated successfully!");


            if (!email.equals(activeEmail)){
                String sessionId = UUID.randomUUID().toString();
                String query = "INSERT INTO sessions (session_id, user_id) VALUES (?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, sessionId);
                stmt.setString(2, email);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error inserting user profile into the database: " + e.getMessage());
        }
    }

    @FXML
    private Button changePasswordButton;

    /**
     * Handles the action when the Change Password button is clicked.
     * Loads the change_password.fxml file and sets it as the scene for the current stage.
     *
     * @throws IOException if an I/O error occurs during loading the change_password.fxml file
     */
    @FXML
    protected void handleChangePassword() throws IOException {
        System.out.println("Change Password button clicked");
        Stage stage = (Stage) changePasswordButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/change_password.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
        stage.setScene(scene);
    }
}
