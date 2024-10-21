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

public class FitnesslogControllerTest {

    private FitnesslogController fitnesslogController;
    private static Connection connection;

    private TableView<FitnesslogController.WorkoutEntry> diaryTableView;
    private TableColumn<FitnesslogController.WorkoutEntry, String> workoutNameColumn;
    private TableColumn<FitnesslogController.WorkoutEntry, String> dateCompletedColumn;

    @BeforeAll
    static void initToolkit() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(() -> latch.countDown());
        latch.await();

        // Initialize the database connection here
        connection = DatabaseConnection.getInstance();
    }

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

    @AfterAll
    static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            DatabaseConnection.closeConnection();
        }
    }

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

    private void clearWorkoutDiaryTable() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DatabaseConnection.getInstance();
        }

        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM WorkoutDiary")) {
            stmt.executeUpdate();
        }
    }

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


    @AfterEach
    void tearDown() throws SQLException {
        clearWorkoutDiaryTable(); // Clear entries in the table
    }
}
