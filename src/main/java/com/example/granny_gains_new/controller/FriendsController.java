package com.example.granny_gains_new.controller;

import com.example.granny_gains_new.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.Period;

public class FriendsController {

    @FXML
    private Button BackButton;

    @FXML
    private TitledPane userDetailsPane;
    @FXML
    private Text name;
    @FXML
    private Text userEmail;
    @FXML
    private Text userPhone;
    @FXML
    private Text userAge;
    @FXML
    private Button prevButton;
    @FXML
    private Button nextButton;

    private List<List<Object>> Users = new ArrayList<>();
    private int currentUserIndex = 0;

    @FXML
    public void initialize() {
        loadUsers();
        updateUserDetails();
        updateNavigationButtons();
    }

    @FXML
    protected void handleBackToHome() throws IOException {
        Stage stage = (Stage) BackButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/granny_gains_home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
        stage.setScene(scene);
    }

    private void loadUsers() {
        String email = fetchUserFromSession();
        addUsers(email);
    }

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
                        details.add(rs.getString("first_name") + " " + rs.getString("last_name")); // Full name
                        details.add(rs.getString("email"));
                        details.add(rs.getDouble("phone"));
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

    @FXML
    private void handlePrevUser() {
        if (currentUserIndex > 0) {
            currentUserIndex--;
            updateUserDetails();
            updateNavigationButtons();
        }
    }

    @FXML
    private void handleNextUser() {
        if (currentUserIndex < Users.size() - 1) {
            currentUserIndex++;
            updateUserDetails();
            updateNavigationButtons();
        }
    }

    private void updateUserDetails() {
        if (!Users.isEmpty()) {
            List<Object> currentUser = Users.get(currentUserIndex);
            userDetailsPane.setText((String) currentUser.get(0));
            name.setText((String) currentUser.get(0));
            userEmail.setText((String) currentUser.get(1));
            userPhone.setText(convertPhoneNumberToString((Double) currentUser.get(2)));
            userAge.setText(String.valueOf(currentUser.get(3)));
        }
    }

    private String convertPhoneNumberToString(Double phoneNumber) {
        long phoneNumberLong = phoneNumber.longValue();

        return String.valueOf(phoneNumberLong);
    }

    private void updateNavigationButtons() {
        prevButton.setDisable(currentUserIndex == 0);
        nextButton.setDisable(currentUserIndex == Users.size() - 1);
    }
}
