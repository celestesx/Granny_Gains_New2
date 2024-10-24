package com.example.granny_gains_new.controller;

import com.example.granny_gains_new.database.DatabaseConnection;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * This class contains test cases for the FitnesslogController class.
 * It sets up the environment for testing by initializing necessary components and clearing the database table before each test.
 * Test methods include loading completed workouts and verifying that the expected data is displayed in the TableView.
 * It also ensures the database connection is closed properly after all tests are executed.
 */
public class FitnesslogControllerTest {

    private FitnesslogController fitnesslogController;
    private static Connection connection;

    private TableView<FitnesslogController.WorkoutEntry> diaryTableView;
    private TableColumn<FitnesslogController.WorkoutEntry, String> workoutNameColumn;
    private TableColumn<FitnesslogController.WorkoutEntry, String> dateCompletedColumn;

    /**
     * Initializes the toolkit necessary for testing. This method is annotated with @BeforeAll to run before all test methods in the class.
     * It starts the JavaFX platform and waits for it to be ready by using a CountDownLatch.
     * Once the platform is started, it initializes the database connection through DatabaseConnection.getInstance().
     *
     * @throws Exception if an error occurs during the toolkit initialization process
     */
    @BeforeAll
    static void initToolkit() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(() -> latch.countDown());
        latch.await();

        // Initialize the database connection here
        connection = DatabaseConnection.getInstance();
    }

    /**
     * Set up the environment before each test case.
     * This method initializes the FitnesslogController, simulates UI components, assigns fields to the controller,
     * sets up the database by creating or clearing the WorkoutDiary table before each test.
     *
     * @throws SQLException if a SQL exception occurs during setup
     */
    @BeforeEach
    void setUp() throws SQLException {
        fitnesslogController = new FitnesslogController();

        // Simulate UI components
        diaryTableView = new TableView<>();
        workoutNameColumn = new TableColumn<>("Workout Name");
        dateCompletedColumn = new TableColumn<>("Date Completed");

        // Assign simulated fields to the controller
        fitnesslogController.diaryTableView = diaryTableView;
        fitnesslogController.workoutNameColumn = workoutNameColumn;
        fitnesslogController.dateCompletedColumn = dateCompletedColumn;

        // Database setup
        createWorkoutDiaryTable();

        // Clear the table before each test to ensure a clean slate
        clearWorkoutDiaryTable();
    }

    /**
     * Closes the database connection if it is open and not already closed.
     * This method is annotated with '@AfterAll' to ensure it runs after all test methods in the class.
     * It checks if the connection is not null and not closed before attempting to close it.
     * If the conditions are met, the DatabaseConnection.closeConnection() method is called to close the connection.
     *
     * @throws SQLException if an error occurs while closing the connection
     */
    @AfterAll
    static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            DatabaseConnection.closeConnection();
        }
    }

    /**
     * Method to create the WorkoutDiary table in the database if it does not already exist.
     * If the database connection is not established or is closed, it establishes a new connection using DatabaseConnection.getInstance().
     * Executes a SQL statement to create the table with columns workout_id (integer primary key with auto-increment), workout_name (text), and date_completed (timestamp).
     *
     * @throws SQLException if an error occurs during the table creation process.
     */
    private void createWorkoutDiaryTable() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DatabaseConnection.getInstance();
        }

        try (PreparedStatement stmt = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS WorkoutDiary (" +
                        "workout_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "workout_name TEXT, " +
                        "date_completed TIMESTAMP)")) {
            stmt.executeUpdate();
        }
    }

    /**
     * Clears all entries from the WorkoutDiary table in the database.
     * If the database connection is not established or closed, it initializes the connection.
     * Executes a SQL DELETE query to remove all records from the WorkoutDiary table.
     *
     * @throws SQLException if an error occurs during the database operation
     */
    private void clearWorkoutDiaryTable() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DatabaseConnection.getInstance();
        }

        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM WorkoutDiary")) {
            stmt.executeUpdate();
        }
    }

    /**
     * Adds a new workout entry to the WorkoutDiary table in the database.
     *
     * @param workoutName the name of the workout to be added
     * @param dateCompleted the date when the workout was completed
     * @throws SQLException if an error occurs during database operations
     */
    private void addWorkoutEntry(String workoutName, String dateCompleted) throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DatabaseConnection.getInstance();
        }

        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO WorkoutDiary (workout_name, date_completed) VALUES (?, ?)")) {
            stmt.setString(1, workoutName);
            stmt.setString(2, dateCompleted);
            stmt.executeUpdate();
        } catch (SQLException e) {
            fail("Failed to insert workout entry: " + e.getMessage());
        }
    }

    /**
     * Test method for loading completed workouts into the diaryTableView.
     * Adds sample workout entries to the database, loads completed workouts, and asserts
     * that the diaryTableView contains the expected entries.
     *
     * @throws SQLException if an SQL exception occurs during the test
     */
    @Test
    void testLoadCompletedWorkouts() throws SQLException {
        // Prepare test data
        addWorkoutEntry("Morning Yoga", "2024-10-21 00:20:19.0");
        addWorkoutEntry("Evening Run", "2024-10-21 00:30:25.0");

        // Act: Load completed workouts
        fitnesslogController.loadCompletedWorkouts();

        // Assert: Check that the TableView contains the correct entries
        assertEquals(2, diaryTableView.getItems().size());
        assertEquals("Morning Yoga", diaryTableView.getItems().get(0).getWorkoutName()); // First should be Morning Yoga
        assertEquals("2024-10-21 00:20:19", diaryTableView.getItems().get(0).getDateCompleted());
        assertEquals("Evening Run", diaryTableView.getItems().get(1).getWorkoutName()); // Second should be Evening Run
        assertEquals("2024-10-21 00:30:25", diaryTableView.getItems().get(1).getDateCompleted());
    }

    /**
     * Method annotated with @AfterEach to run after each test method in the class.
     * It calls the private method clearWorkoutDiaryTable() to clear all entries from the WorkoutDiary table.
     * Any SQLException thrown during the process is propagated.
     */
    @AfterEach
    void tearDown() throws SQLException {
        clearWorkoutDiaryTable(); // Clear entries in the table
    }
}
