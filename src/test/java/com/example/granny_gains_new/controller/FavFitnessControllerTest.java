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

/**
 * This class contains unit tests for the FavFitnessController class.
 * It tests various functionalities related to loading fitness items, getting the current scene,
 * and navigating to a specific page in a JavaFX application.
 */
class FavFitnessControllerTest {

    private FavFitnessController favFitnessController;
    private static Connection connection;

    private TableView<FavFitnessController.FitnessEntry> fitnessTableView;
    private TableColumn<FavFitnessController.FitnessEntry, String> workoutNameColumn;
    private TableColumn<FavFitnessController.FitnessEntry, String> savedDateColumn;

    /**
     * Initializes the toolkit before all tests run. This method starts the JavaFX platform and awaits its initialization.
     * It also ensures the database connection is opened by obtaining an instance of the DatabaseConnection class.
     *
     * @throws Exception if an error occurs during the initialization process
     */
    @BeforeAll
    static void initToolkit() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(() -> latch.countDown());
        latch.await();

        // Initialize the database connection here
        connection = DatabaseConnection.getInstance(); // Ensure the connection is opened
    }

    /**
     * Set up the test environment before each test method execution.
     * Initializes the FavFitnessController, simulates UI components including TableView and TableColumns,
     * assigns the simulated fields to the controller, and sets up the database by creating the FitnessTable if it doesn't exist.
     *
     * @throws SQLException if an SQL exception occurs during the setup process
     */
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

    /**
     * Performs the necessary teardown steps after each test in the FavFitnessControllerTest class.
     * This method is annotated with @AfterEach to indicate its execution after each test method.
     * It internally calls the clearFitnessTable method to clear entries in the fitness table.
     * Any SQLException that occurs during the cleanup process is propagated.
     */
    @AfterEach
    void tearDown() throws SQLException {
        clearFitnessTable(); // Clear entries in the table
    }

    /**
     * Creates the FitnessTable in the database if it does not already exist.
     * This method ensures that the connection to the database is open by either using the existing connection
     * or reinitializing it if it is closed. It then prepares a SQL statement to create the FitnessTable
     * with columns for workout_name and saved_date. If the table does not already exist in the database,
     * the statement is executed to create it.
     *
     * @throws SQLException if an SQL exception occurs during the process of creating the FitnessTable
     */
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

    /**
     * Clears all entries in the FitnessTable by executing a SQL DELETE query.
     * If the database connection is closed or null, it reinitializes the connection
     * by obtaining an instance of the DatabaseConnection class.
     *
     * @throws SQLException if an SQL exception occurs during the deletion process
     */
    private void clearFitnessTable() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DatabaseConnection.getInstance(); // Reinitialize if closed
        }

        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM FitnessTable")) {
            stmt.executeUpdate();
        }
    }

    /**
     * Adds a fitness entry to the database with the specified workout name and saved date.
     *
     * @param workoutName the name of the workout to be added
     * @param savedDate the date when the workout was saved
     * @throws SQLException if an SQL exception occurs during database operation
     */
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

    /**
     * Test case to validate the functionality of loading saved fitness items from the database.
     * Inserts test fitness entries into the database, calls the method to load saved fitness items,
     * and asserts that the TableView contains the expected entries after loading.
     *
     * @throws SQLException if an SQL exception occurs during the test execution.
     */
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

    /**
     * Test method for {@link FavFitnessController#getCurrentScene(Node)}.
     *
     * This method tests the functionality of the getCurrentScene method in the FavFitnessController class.
     * It creates a Button and a Scene with that Button, simulates the Node having a Scene,
     * and then calls the getCurrentScene method with the Button.
     * The test asserts that the returned current scene is not null.
     */
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

    /**
     * This class represents a basic JavaFX application that creates a simple window with a button.
     * When the button is clicked, no action is performed in this implementation.
     */
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

    /**
     * This method tests the navigation functionality to a specific page in the application.
     * It uses Platform.runLater to perform UI operations on the JavaFX Application thread.
     * The method loads a FXML file representing the target page, creates a Stage to display the page,
     * and ensures that the required UI elements are present by looking them up using their IDs.
     * A CountDownLatch is used to wait for the UI operations to complete before proceeding with assertions.
     * IOException is caught and printed in case of any errors during loading.
     *
     * @throws Exception if an error occurs during the navigation process
     */
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

    /**
     * Closes the database connection if it is open and not already closed.
     * It checks if the connection is not null and if it is not closed before attempting to close it.
     * This method is intended to be executed after all tests have run to ensure proper cleanup.
     * If an SQLException occurs during the closing process, it is caught and printed to the standard error.
     */
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
