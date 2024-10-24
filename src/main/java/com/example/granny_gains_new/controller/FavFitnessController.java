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

/**
 * Controller class that manages the favorite fitness items view and interactions.
 */
public class FavFitnessController {

    @FXML
    TableView<FitnessEntry> fitnessTableView;

    @FXML
    TableColumn<FitnessEntry, String> workoutNameColumn; // Adjusted column name

    @FXML
    TableColumn<FitnessEntry, String> savedDateColumn; // Added saved_date column

    @FXML
    Button backToHomeButton;

    /**
     * Method to display the favorite Meals page. It loads the FXML file of the Fav_Meals_Page.fxml
     * and sets it as the scene of the current stage, effectively navigating to the Meals page.
     * If an IOException occurs during the loading process, it is printed to the standard error stream.
     */
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

    /**
     * Retrieves the current scene associated with the provided Node.
     *
     * @param node The node for which to retrieve the current scene
     * @return The current scene of the provided node
     */
    public Scene getCurrentScene(Node node) {
        return node.getScene(); // Returns the current scene of the provided node
    }

    /**
     * Method to display favourited Fitness items. It navigates to the Favorite Fitness Page by loading
     * the FXML file of Fav_Fitness_Page.fxml and setting it as the scene of the current stage.
     */
    @FXML
    public void handleFitness() {
        navigateToPage("/com/example/granny_gains_new/Fav_Fitness_Page.fxml", "Favorite Fitness Page");
    }


    /**
     * Navigates to the specified page by loading the provided FXML file and setting it as the scene of the current stage.
     *
     * @param fxmlFilePath The file path of the FXML file to load
     * @param pageName The name of the page being navigated to
     */
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

    /**
     * Initialises the controller. Binds cell value factories for workout name and saved date columns.
     * Sets column widths based on the percentage of the table width. Loads saved fitness items and
     * sets the column resize policy of the fitness table view.
     */
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

    /**
     * Method to load saved fitness items from the database and populate the fitness table view.
     * Retrieves workout names and saved dates from the FitnessTable, constructs FitnessEntry objects,
     * and adds them to an observable list. Sets the observable list as the items of the fitness table view.
     * If an SQLException occurs during database operations, it prints the error message to the standard error stream.
     */
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


    /**
     * Method to handle navigation back to the home page. Retrieves the current window stage,
     * loads the FXML file for the home page (granny_gains_home.fxml), creates a new scene with
     * the loaded FXML content, and sets it as the scene of the current stage. If an IOException
     * occurs during the loading process, it is propagated.
     *
     * @throws IOException If an error occurs during the loading of the home page FXML file
     */
    @FXML
    protected void handleBackToHome() throws IOException {
        Stage stage = (Stage) backToHomeButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/granny_gains_home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
        stage.setScene(scene);
    }


    /**
     * Represents a fitness entry with details about a workout including the workout name and the date it was saved.
     */
    public static class FitnessEntry {
        private final String workoutName; // Only this field needed
        private final String savedDate; // Added savedDate

        /**
         * Constructs a FitnessEntry object with the provided workout name and saved date.
         *
         * @param workoutName The name of the workout
         * @param savedDate The date when the workout was saved
         */
        public FitnessEntry(String workoutName, String savedDate) {
            this.workoutName = workoutName; // Set workout_name
            this.savedDate = savedDate; // Set saved_date
        }

        /**
         * Retrieves the name of the workout associated with this fitness entry.
         *
         * @return The name of the workout.
         */
        public String getWorkoutName() { // Getter for workout_name
            return workoutName;
        }

        /**
         * Retrieves the saved date associated with a fitness entry.
         *
         * @return The saved date of the fitness entry.
         */
        public String getSavedDate() { // Getter for saved_date
            return savedDate;
        }
    }
}
