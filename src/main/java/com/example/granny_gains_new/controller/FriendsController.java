package com.example.granny_gains_new.controller;

import com.example.granny_gains_new.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.Period;

/**
 * Controller class for managing the friends page functionality.
 */
public class FriendsController {

    @FXML
    private Button BackButton;

    @FXML
    private Button prevButton;
    @FXML
    private Button nextButton;

    private List<List<Object>> Users = new ArrayList<>();

    @FXML
    private VBox userPanesContainer;

    private static final int USERS_PER_PAGE = 3;
    private int currentPage = 0;

    /**
     * Initializes the FriendsController by performing the following actions:
     * 1. Loads the currently logged in user's information from the session and retrieves other users' details from the database.
     * 2. Updates the user panes displayed on the screen by creating panes for each user within the current page range.
     * 3. Updates the navigation buttons based on the current page and available users.
     * 4. Sets the spacing and padding for the user panes container for better UI presentation.
     */
    @FXML
    public void initialize() {
        loadUsers();
        updateUserPanes();
        updateNavigationButtons();

        userPanesContainer.setSpacing(30);
        userPanesContainer.setPadding(new Insets(20));
    }

    /**
     * Method to handle navigating back to the home screen.
     *
     * @throws IOException if an input or output exception occurs
     */
    @FXML
    protected void handleBackToHome() throws IOException {
        Stage stage = (Stage) BackButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/granny_gains_home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
        stage.setScene(scene);
    }

    /**
     * Loads users by fetching the currently logged-in user's email from the session
     * and adding other users' details to the Users list.
     *
     * This method consists of the following steps:
     * 1. Fetches the email of the currently logged-in user using fetchUserFromSession().
     * 2. Adds details of other users (excluding the current user) to the Users list using addUsers(email).
     */
    private void loadUsers() {
        String email = fetchUserFromSession();
        addUsers(email);
    }

    /**
     * Fetches the email of the user currently logged in from the session.
     *
     * This method retrieves the user's email from the session by executing a SQL query.
     * It connects to the database, executes the query to retrieve the user's email,
     * and returns the email if found. If an SQL exception occurs during the process,
     * an error message is printed to the standard error output.
     *
     * @return The email of the user fetched from the session, or an empty string if not found.
     */
    private String fetchUserFromSession() {
        String user = "";
        String query = "SELECT u.email FROM User u "
                + "JOIN sessions s ON u.email = s.user_id "
                + "ORDER BY s.login_time DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user = rs.getString("email");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user name from session: " + e.getMessage());
        }
        return user;
    }

    /**
     * Adds details of users with email different from the provided email to the Users list.
     * Retrieves user information from the database based on the provided email.
     *
     * @param email the email address used to retrieve users' details, excluding users with this email
     */
    private void addUsers(String email) {
        try (Connection conn = DatabaseConnection.getInstance()) {
            if (conn == null || conn.isClosed()) {
                System.err.println("Database connection is closed.");
                return;
            }

            String sql = "SELECT first_name, last_name, email, phone, date_of_birth FROM User WHERE email != ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, email);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    try {
                        List<Object> details = new ArrayList<>();
                        details.add(rs.getString("first_name") + " " + rs.getString("last_name"));
                        details.add(rs.getString("email"));
                        details.add(rs.getString("phone"));
                        LocalDate dob = rs.getDate("date_of_birth").toLocalDate();
                        LocalDate currentDate = LocalDate.now();
                        Period age = Period.between(dob, currentDate);
                        details.add(age.getYears());
                        Users.add(details);
                    } catch (SQLException e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Updates the navigation buttons based on the current page and available users.
     * Disables the previous button if the current page is the first page.
     * Disables the next button if the current page is the last page based on the number of users per page and total number of users.
     */
    private void updateNavigationButtons() {
        prevButton.setDisable(currentPage == 0);
        nextButton.setDisable((currentPage + 1) * USERS_PER_PAGE >= Users.size());
    }

    /**
     * Decreases the current page number by 1 if the current page is greater than 0.
     * Calls updateUserPanes() and updateNavigationButtons() to reflect the page change on the UI.
     */
    @FXML
    private void handlePrevPage() {
        if (currentPage > 0) {
            currentPage--;
            updateUserPanes();
            updateNavigationButtons();
        }
    }

    /**
     * Handles the action of navigating to the next page of users.
     * Checks if there are more users to display on the next page based on the current page
     * and the total number of users available. If more users exist, increments the current page index,
     * updates the user panes displayed on the screen, and adjusts the navigation buttons accordingly.
     */
    @FXML
    private void handleNextPage() {
        if ((currentPage + 1) * USERS_PER_PAGE < Users.size()) {
            currentPage++;
            updateUserPanes();
            updateNavigationButtons();
        }
    }

    /**
     * Updates the user panes displayed on the screen by creating panes for each user within the current page range.
     * This method clears the existing user panes container, calculates the start and end index based on the current page
     * and number of users per page, and then iterates over the Users list to create user panes and add them to the container.
     */
    private void updateUserPanes() {
        userPanesContainer.getChildren().clear();
        int startIndex = currentPage * USERS_PER_PAGE;
        int endIndex = Math.min(startIndex + USERS_PER_PAGE, Users.size());

        for (int i = startIndex; i < endIndex; i++) {
            StackPane userPane = createUserPane(Users.get(i));
            userPanesContainer.getChildren().add(userPane);
        }
    }

    /**
     * Creates a StackPane containing user details based on the provided user information.
     *
     * @param userDetails a List of Object containing user details in the following order:
     *                    1. Name (String)
     *                    2. Email (String)
     *                    3. Phone (String)
     *                    4. Age (String)
     * @return a StackPane representing the user's details with formatted information
     */
    private StackPane createUserPane(List<Object> userDetails) {
        StackPane stackPane = new StackPane();
        stackPane.setPrefHeight(200);
        stackPane.setStyle("-fx-background-color: transparent;");

        TitledPane pane = new TitledPane();
        pane.setText((String) userDetails.get(0));
        pane.setExpanded(false);
        pane.setCollapsible(true);
        pane.setMaxHeight(Region.USE_PREF_SIZE);
        
        pane.setStyle("-fx-border-width: 0; -fx-background-color: transparent;");

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.getChildren().addAll(
            createLabel("Name: ", (String) userDetails.get(0)),
            createLabel("Email: ", (String) userDetails.get(1)),
            createLabel("Phone: ", (String) userDetails.get(2)),
            createLabel("Age: ", String.valueOf(userDetails.get(3)))
        );

        pane.setContent(content);
        stackPane.getChildren().add(pane);

        StackPane.setAlignment(pane, javafx.geometry.Pos.TOP_CENTER);

        return stackPane;
    }

    /**
     * Creates a Label with the specified labelText and value.
     *
     * @param labelText the text to display as the label prefix
     * @param value the additional value to append to the label text
     * @return a Label instance with the concatenated text and styled with a font size of 18px
     */
    private Label createLabel(String labelText, String value) {
        Label label = new Label(labelText + value);
        label.setStyle("-fx-font-size: 18px;");
        return label;
    }
}
