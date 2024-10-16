package com.example.granny_gains_new.controller;

import com.example.granny_gains_new.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
    private TableView<FitnessEntry> fitnessTableView;

    @FXML
    private TableColumn<FitnessEntry, String> fitnessNameColumn;

    @FXML
    private TableColumn<FitnessEntry, String> descriptionColumn;

    @FXML
    private TableColumn<FitnessEntry, Integer> durationColumn;

    @FXML
    private TableColumn<FitnessEntry, Integer> caloriesColumn;

    @FXML
    private Button backToHomeButton;

    // Method to display favorited Meals
    @FXML
    public void handleMeals() {
        navigateToPage("/com/example/granny_gains_new/Fav_Meals_Page.fxml", "Favorite Meals Page");
    }

    // Method to display favorited Fitness items
    @FXML
    public void handleFitness() {
        navigateToPage("/com/example/granny_gains_new/Fav_Fitness_Page.fxml", "Favorite Fitness Page");
    }

    // Generic method to navigate between pages
    private void navigateToPage(String fxmlFilePath, String pageName) {
        try {
            Stage stage = (Stage) backToHomeButton.getScene().getWindow(); // Use backToHomeButton here as well
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
        fitnessNameColumn.setCellValueFactory(new PropertyValueFactory<>("fitnessName"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
        caloriesColumn.setCellValueFactory(new PropertyValueFactory<>("caloriesBurned"));

        // Set widths based on the percentage of the table width
        fitnessNameColumn.prefWidthProperty().bind(fitnessTableView.widthProperty().multiply(0.3)); // 30%
        descriptionColumn.prefWidthProperty().bind(fitnessTableView.widthProperty().multiply(0.4)); // 40%
        durationColumn.prefWidthProperty().bind(fitnessTableView.widthProperty().multiply(0.15)); // 15%
        caloriesColumn.prefWidthProperty().bind(fitnessTableView.widthProperty().multiply(0.15)); // 15%

        loadSavedFitnessItems();
        fitnessTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadSavedFitnessItems() {
        String query = "SELECT fitness_name, description, duration, calories_burned FROM FitnessTable";

        ObservableList<FitnessEntry> savedFitnessItems = FXCollections.observableArrayList();

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String fitnessName = rs.getString("fitness_name");
                String description = rs.getString("description");
                int duration = rs.getInt("duration");
                int caloriesBurned = rs.getInt("calories_burned");

                savedFitnessItems.add(new FitnessEntry(fitnessName, description, duration, caloriesBurned));
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
        private final String fitnessName;
        private final String description;
        private final int duration; // Duration in minutes
        private final int caloriesBurned;

        public FitnessEntry(String fitnessName, String description, int duration, int caloriesBurned) {
            this.fitnessName = fitnessName;
            this.description = description;
            this.duration = duration;
            this.caloriesBurned = caloriesBurned;
        }

        public String getFitnessName() {
            return fitnessName;
        }

        public String getDescription() {
            return description;
        }

        public int getDuration() {
            return duration;
        }

        public int getCaloriesBurned() {
            return caloriesBurned;
        }
    }
}
