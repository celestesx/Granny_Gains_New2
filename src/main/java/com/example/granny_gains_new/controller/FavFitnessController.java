package com.example.granny_gains_new.controller;

import com.example.granny_gains_new.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FavFitnessController {

    @FXML
    TableView<FitnessEntry> fitnessTableView;

    @FXML
    TableColumn<FitnessEntry, String> workoutNameColumn; // Adjusted column name

    @FXML
    TableColumn<FitnessEntry, String> savedDateColumn; // Added saved_date column

    @FXML
    Button backToHomeButton;

    // Method to display favorited Meals
    @FXML
    public void handleMeals() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/Fav_Meals_Page.fxml")); // Update with correct path
            Parent root = loader.load();

            Scene newScene = new Scene(root);
            Stage currentStage = (Stage) backToHomeButton.getScene().getWindow();
            currentStage.setScene(newScene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Handle the error appropriately
        }
    }

    public Scene getCurrentScene(Node node) {
        return node.getScene(); // Returns the current scene of the provided node
    }

    // Method to display favorited Fitness items
    @FXML
    public void handleFitness() {
        navigateToPage("/com/example/granny_gains_new/Fav_Fitness_Page.fxml", "Favorite Fitness Page");
    }

    // Generic method to navigate between pages
    private void navigateToPage(String fxmlFilePath, String pageName) {
        try {
            Stage stage = (Stage) backToHomeButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFilePath));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
            stage.setScene(scene);
            stage.setMaximized(true);
            System.out.println("Navigated to " + pageName);
        } catch (IOException e) {
            System.err.println("Error loading " + pageName + ": " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        workoutNameColumn.setCellValueFactory(new PropertyValueFactory<>("workoutName")); // Use workout_name
        savedDateColumn.setCellValueFactory(new PropertyValueFactory<>("savedDate")); // Use saved_date

        // Set widths based on the percentage of the table width
        workoutNameColumn.prefWidthProperty().bind(fitnessTableView.widthProperty().multiply(0.5)); // 50%
        savedDateColumn.prefWidthProperty().bind(fitnessTableView.widthProperty().multiply(0.5)); // 50%

        loadSavedFitnessItems();
        fitnessTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    void loadSavedFitnessItems() {
        String query = "SELECT workout_name, saved_date FROM FitnessTable"; // Adjusted query

        ObservableList<FitnessEntry> savedFitnessItems = FXCollections.observableArrayList();

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String workoutName = rs.getString("workout_name"); // Adjusted to match your table
                String savedDate = rs.getString("saved_date"); // Fetching saved_date

                savedFitnessItems.add(new FitnessEntry(workoutName, savedDate)); // Updated to match the new entry structure
            }

            fitnessTableView.setItems(savedFitnessItems);
        } catch (SQLException e) {
            System.err.println("Error loading saved fitness items: " + e.getMessage());
        }
    }

    // Method to go back to the Home page
    @FXML
    protected void handleBackToHome() throws IOException {
        Stage stage = (Stage) backToHomeButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/granny_gains_home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
        stage.setScene(scene);
    }

    // Nested class for fitness entries
    public static class FitnessEntry {
        private final String workoutName; // Only this field needed
        private final String savedDate; // Added savedDate

        public FitnessEntry(String workoutName, String savedDate) {
            this.workoutName = workoutName; // Set workout_name
            this.savedDate = savedDate; // Set saved_date
        }

        public String getWorkoutName() { // Getter for workout_name
            return workoutName;
        }

        public String getSavedDate() { // Getter for saved_date
            return savedDate;
        }
    }
}
