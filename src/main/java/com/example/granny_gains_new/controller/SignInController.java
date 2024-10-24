package com.example.granny_gains_new.controller;

import com.example.granny_gains_new.HelloApplication;
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
import java.text.CollationElementIterator;
import java.util.UUID;
import java.util.prefs.Preferences;

/**
 * This class represents a controller for the sign-in functionality in the application.
 * It handles user input, validates credentials, and navigates to different screens based on user actions.
 */
public class SignInController {
    @FXML
    private TextField tfEmail;

    @FXML
    private PasswordField tfPassword;

    @FXML
    private Button ButtonSignin;

    @FXML
    private Label lblwrongPassword;

    @FXML
    private CheckBox rememberMeCheckBox;
    private Preferences preferences;

    /**
     * Constructor for the SignInController class.
     * Initializes the preferences for the controller using the user node for the SignInController class.
     */
    public SignInController() {
        preferences = Preferences.userNodeForPackage(SignInController.class);
    }

    /**
     * Initializes the sign-in view with stored email and password if available in preferences.
     * Retrieves the stored email and password from preferences and sets them in corresponding input fields.
     * Also sets the remember me checkbox based on whether there is a stored email or not.
     */
    @FXML
    protected void initialize() {
        String storedEmail = preferences.get("email", "");
        String storedPassword = preferences.get("password", "");
        tfEmail.setText(storedEmail);
        tfPassword.setText(storedPassword);
        rememberMeCheckBox.setSelected(!storedEmail.isEmpty());
    }

    /**
     * Handle the sign-in process when the user attempts to sign in.
     * Retrieves the email and password input from text fields, validates the credentials,
     * saves the email and password if "Remember Me" is checked, generates a session ID,
     * creates a session in the database, and redirects to the home page upon successful sign-in.
     * Displays an error message if the credentials are invalid.
     */
    @FXML
    protected void handleSignIn() {
        String email = tfEmail.getText();
        String password = tfPassword.getText();

        if (validateCredentials(email, password)) {
            // Save email and password if "Remember Me" is checked
            if (rememberMeCheckBox.isSelected()) {
                preferences.put("email", email);
                preferences.put("password", password);
            } else {
                preferences.remove("email");
                preferences.remove("password");
            }

            // Generate session ID and load the home page
            String sessionId = UUID.randomUUID().toString();
            createSession(email, sessionId);
            try {
                Stage stage = (Stage) ButtonSignin.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/granny_gains_home.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
                stage.setScene(scene);
                stage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
                stage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
                stage.setMaximized(true);
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            lblwrongPassword.setText("Invalid credentials. Please try again.");
        }
    }

    /**
     * Validates the user credentials by checking if the provided email and password match the stored password in the database.
     *
     * @param email The email of the user to validate.
     * @param password The password of the user to validate.
     * @return true if the credentials are valid, false otherwise.
     */
    public boolean validateCredentials(String email, String password) {
        try (Connection conn = DatabaseConnection.getInstance()) {
            if (conn == null || conn.isClosed()) {
                System.err.println("Database connection is closed.");
                return false;
            }

            String sql = "SELECT password FROM User WHERE email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, email);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    return storedPassword.equals(password);
                } else {
                    return false;  // No user found
                }
            }
        } catch (SQLException e) {
            System.err.println("Error validating user credentials: " + e.getMessage());
            return false;
        }
    }

    /**
     * Creates a new session in the database for the specified user email with the given session ID.
     *
     * @param email The email of the user for whom the session is created.
     * @param sessionId The unique session ID to associate with the user's session.
     */
    private void createSession(String email, String sessionId) {
        try (Connection conn = DatabaseConnection.getInstance()) {
            String query = "INSERT INTO sessions (session_id, user_id) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, sessionId);
            stmt.setString(2, email);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the action when the signup button is clicked. Loads the sign-up page FXML file,
     * creates a new scene with the sign-up page, sets the scene on the stage, adjusts the stage size,
     * maximizes the stage, and shows the stage to display the sign-up page.
     *
     * @throws IOException If an error occurs while loading the sign-up page FXML file.
     */
    @FXML
    protected void buttonSignup() throws IOException {
        Stage stage = (Stage) ButtonSignin.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("sign_up_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 650);
        stage.setScene(scene);
        stage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
        stage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
        stage.setMaximized(true);
        stage.show();
    }

    /**
     * Handles the action for "Forgot Password" button click.
     * This method loads the forgot password page when the button is clicked.
     * It retrieves the current stage, initializes a new FXMLLoader with the path to the forgot_password_page.fxml,
     * loads the FXML file, sets the scene with the loaded content, adjusts the stage size to match the screen,
     * maximizes the stage, and shows the updated scene.
     * In case of any IO exceptions during the process, it prints the stack trace.
     */
    @FXML
    protected void handleForgotPassword() {
        try {
            Stage stage = (Stage) ButtonSignin.getScene().getWindow();
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
}
