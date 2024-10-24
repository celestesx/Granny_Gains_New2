package com.example.granny_gains_new.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.example.granny_gains_new.database.DatabaseConnection;

import java.io.IOException;

/**
 * This class represents the controller for the Granny Gains Home page of the application.
 * It manages the initialization of the page components, loading of user data, and navigation to other pages.
 */
public class GrannyGainsHomeController {

    @FXML
    private ImageView logoImage;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Button mealsButton, fitnessButton, settingsButton, FriendsButton;

    @FXML
    private Button logOutButton;

    /**
     * Initializes the GrannyGainsHomeController by loading the main logo image and the user's name from the latest session.
     */
    @FXML
    public void initialize() {
        loadLogoImage();
        loadUserNameFromSession();
    }

    /**
     * Loads the logo image for the Granny Gains application from the specified resource location.
     * If the image loading fails due to a NullPointerException, an error message is printed to the standard error.
     */
    private void loadLogoImage() {
        try {
            Image logo = new Image(getClass().getResource("/com/example/granny_gains_new/images/landing.jpg").toExternalForm());
            logoImage.setImage(logo);
        } catch (NullPointerException e) {
            System.err.println("Error loading logo image: " + e.getMessage());
        }
    }

    /**
     * Loads the user's name from the session and displays a welcome message on the welcomeLabel.
     * If the user's name is retrieved successfully from the session, the welcome message includes the user's name.
     * If the user's name is not found or empty, the welcome message defaults to "Welcome, Guest!".
     */
    private void loadUserNameFromSession() {
        String userName = fetchUserNameFromSession();
        if (userName != null && !userName.isEmpty()) {
            welcomeLabel.setText("Welcome, " + userName + "!");
        } else {
            welcomeLabel.setText("Welcome, Guest!");
        }
    }

    /**
     * Fetches the user's name from the latest session by executing a query to the database.
     *
     * @return the user's full name (first name + last name) retrieved from the session, or an empty string if the name is not found or an error occurs.
     */
    private String fetchUserNameFromSession() {
        String userName = "";
        String query = "SELECT u.first_name, u.last_name FROM User u "
                + "JOIN sessions s ON u.email = s.user_id "
                + "ORDER BY s.login_time DESC LIMIT 1"; // Fetch the latest session

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                userName = rs.getString("first_name") + " " + rs.getString("last_name");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user name from session: " + e.getMessage());
        }
        return userName;
    }

    /**
     * Navigates to the Meals Page by loading the specified FXML file and setting it as the scene in the current stage.
     * The page will be displayed in a maximized window with dimensions 1200x700.
     */
    @FXML
    public void handleMeals() {
        navigateToPage("/com/example/granny_gains_new/Meals_Page.fxml", "Meals Page");
    }

    /**
     * Navigates to the Fitness page by loading the FitnessCardio.fxml file.
     */
    @FXML
    public void handleFitness() {
        navigateToPage("/com/example/granny_gains_new/FitnessCardio.fxml", "Fitness Page");
    }

    /**
     * Handles the navigation to the Settings Page by loading the specified FXML file and setting it as the scene.
     * This method is triggered when the settingsButton is clicked by the user.
     */
    @FXML
    public void handleSettings() {
        navigateToPage("/com/example/granny_gains_new/settings_page.fxml", "Settings Page");
    }

    /**
     * Handles the navigation to the Friends Page by loading the specified FXML file and setting it as the scene.
     * This method is triggered when the FriendsButton is clicked by the user.
     */
    @FXML
    public void handleFriends() {
        navigateToPage("/com/example/granny_gains_new/Friends_page.fxml", "Friends Page");
    }


    /**
     * Navigates to a specific page by loading the FXML file and setting it as the scene in the current stage.
     *
     * @param fxmlFilePath the file path of the FXML file to be loaded for the page
     * @param pageName the name of the page being navigated to, used for logging purposes
     */
    private void navigateToPage(String fxmlFilePath, String pageName) {
        try {
            Stage stage = (Stage) mealsButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFilePath));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
            stage.setScene(scene);
            stage.setMaximized(true);
            System.out.println("Navigated to " + pageName);
        } catch (IOException e) {
            System.err.println("Error loading " + pageName + ": " + e.getMessage());
        }
    }

    /**
     * Handles the action of navigating back to the sign-in page.
     * Retrieves the current stage from the logout button's scene, loads the sign-in page FXML file,
     * creates a new scene with specified dimensions, and sets it as the current scene in the stage.
     * If an IOException occurs while loading the FXML file, the stack trace is printed.
     */
    @FXML
    protected void handleBackToSignIn() {
        try {
            Stage stage = (Stage) logOutButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/sign_in_page.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 650);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the action when the user navigates to the Favourites Page.
     * Prints a message indicating the navigation to the Favourites Page and navigates to the specified FXML file.
     */
    @FXML
    public void handleFavourites() {
        System.out.println("Navigating to Favourites Page...");
        navigateToPage("/com/example/granny_gains_new/Favourites_Page.fxml", "Favourites Page");
    }

}
