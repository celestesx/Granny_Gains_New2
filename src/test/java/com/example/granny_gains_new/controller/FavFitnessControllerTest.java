package com.example.granny_gains_new.controller;

import com.example.granny_gains_new.database.DatabaseConnection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

import static javafx.application.Application.launch;
import static org.junit.jupiter.api.Assertions.*;

class FavFitnessControllerTest {

    private FavFitnessController favFitnessController;
    private static Connection connection;

    private TableView<FavFitnessController.FitnessEntry> fitnessTableView;
    private TableColumn<FavFitnessController.FitnessEntry, String> workoutNameColumn;
    private TableColumn<FavFitnessController.FitnessEntry, String> savedDateColumn;

    @BeforeAll
    static void initToolkit() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(() -> latch.countDown());
        latch.await();

        // Initialize the database connection here
        connection = DatabaseConnection.getInstance(); // Ensure the connection is opened
    }

    @BeforeEach
    void setUp() throws SQLException {
        favFitnessController = new FavFitnessController();

        // Simulate UI components
        fitnessTableView = new TableView<>();
        workoutNameColumn = new TableColumn<>("Workout Name");
        savedDateColumn = new TableColumn<>("Saved Date");

        // Assign simulated fields to the controller
        favFitnessController.fitnessTableView = fitnessTableView;
        favFitnessController.workoutNameColumn = workoutNameColumn;
        favFitnessController.savedDateColumn = savedDateColumn;

        // Database setup
        createFitnessTable();
    }

    @AfterEach
    void tearDown() throws SQLException {
        clearFitnessTable(); // Clear entries in the table
    }

    private void createFitnessTable() throws SQLException {
        // Make sure to check if the connection is still open
        if (connection == null || connection.isClosed()) {
            connection = DatabaseConnection.getInstance(); // Reinitialize if closed
        }

        try (PreparedStatement stmt = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS FitnessTable (" +
                        "workout_name TEXT, " +
                        "saved_date TEXT)")) {
            stmt.executeUpdate();
        }
    }

    private void clearFitnessTable() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DatabaseConnection.getInstance(); // Reinitialize if closed
        }

        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM FitnessTable")) {
            stmt.executeUpdate();
        }
    }

    private void addFitnessEntry(String workoutName, String savedDate) throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DatabaseConnection.getInstance(); // Reinitialize if closed
        }

        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO FitnessTable (workout_name, saved_date) VALUES (?, ?)")) {
            stmt.setString(1, workoutName);
            stmt.setString(2, savedDate);
            stmt.executeUpdate();
        } catch (SQLException e) {
            fail("Failed to insert fitness entry: " + e.getMessage());
        }
    }

    @Test
    void testLoadSavedFitnessItems() throws SQLException {
        // Prepare test data
        addFitnessEntry("Yoga", "2024-10-15");
        addFitnessEntry("Running", "2024-10-16");

        // Act: Load saved fitness items
        favFitnessController.loadSavedFitnessItems();

        // Assert: Check that the TableView contains the correct entries
        assertEquals(2, fitnessTableView.getItems().size());
        assertEquals("Yoga", fitnessTableView.getItems().get(0).getWorkoutName());
        assertEquals("2024-10-15", fitnessTableView.getItems().get(0).getSavedDate());
        assertEquals("Running", fitnessTableView.getItems().get(1).getWorkoutName());
        assertEquals("2024-10-16", fitnessTableView.getItems().get(1).getSavedDate());
    }

    @Test
    public void testGetCurrentScene() {
        // No need to start the platform again

        // Create a Button and a Scene with that Button
        Button sampleButton = new Button("Sample Button");
        Scene scene = new Scene(sampleButton, 400, 300);

        // Simulate the Node having a Scene
        sampleButton.getScene();

        // Call getCurrentScene with the button
        Scene currentScene = favFitnessController.getCurrentScene(sampleButton);

        // Assert that the current scene is not null
        assertNotNull(currentScene);
    }

    public class MyApp extends Application {
        @Override
        public void start(Stage primaryStage) {
            Button button = new Button("Click Me");

            // Create a layout to hold the button
            StackPane root = new StackPane();
            root.getChildren().add(button);

            // Create a scene and set it on the primary stage
            Scene scene = new Scene(root, 300, 250);
            primaryStage.setTitle("JavaFX Example");
            primaryStage.setScene(scene);
            primaryStage.show();
        }

        public static void main(String[] args) {
            launch(args);
        }
    }

    @Test
    void testNavigateToPage() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/Fav_Meals_Page.fxml"));
                Parent root = loader.load();

                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.show();

                // Look up the AnchorPane by its ID
                AnchorPane favoriteMealsPage = (AnchorPane) scene.lookup("#favoriteMealsPage");
                assertNotNull(favoriteMealsPage, "The favorite meals page should not be null.");

                latch.countDown(); // Signal that the platform work is done
            } catch (IOException e) {
                e.printStackTrace(); // Handle any loading errors
            }
        });

        latch.await(); // Wait for the Platform.runLater to finish
    }

    @AfterAll
    static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                DatabaseConnection.closeConnection(); // Close the connection once after all tests
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
