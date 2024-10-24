package com.example.granny_gains_new.controller;

import com.example.granny_gains_new.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableView.TableViewSelectionModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Controller class responsible for handling the fitness log functionality in the application.
 */
public class FitnesslogController {

    @FXML
    TableView<WorkoutEntry> diaryTableView;

    @FXML
    TableColumn<WorkoutEntry, String> workoutNameColumn;

    @FXML
    TableColumn<WorkoutEntry, String> dateCompletedColumn;

    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        workoutNameColumn.setCellValueFactory(new PropertyValueFactory<>("workoutName"));
        dateCompletedColumn.setCellValueFactory(new PropertyValueFactory<>("dateCompleted"));
        loadCompletedWorkouts();
    }

    /**
     * Load the completed workouts from the database and populate the diaryTableView.
     * The date and time from the database are formatted as 'YYYY-MM-DD HH:MM:SS'.
     * Any errors that occur during the process are printed to the standard error stream.
     */
    void loadCompletedWorkouts() {
        // Format the date and time from the database in 'YYYY-MM-DD HH:MM:SS' format
        String query = "SELECT workout_name, strftime('%Y-%m-%d %H:%M:%S', date_completed) AS formatted_date " +
                "FROM WorkoutDiary " +
                "ORDER BY date_completed ASC"; // Order explicitly by date_completed

        ObservableList<WorkoutEntry> completedWorkouts = FXCollections.observableArrayList();

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String workoutName = rs.getString("workout_name");
                String dateCompleted = rs.getString("formatted_date"); // Use the formatted date
                completedWorkouts.add(new WorkoutEntry(workoutName, dateCompleted));
            }

            diaryTableView.setItems(completedWorkouts);
        } catch (SQLException e) {
            System.err.println("Error loading completed workouts: " + e.getMessage());
        }
    }

    /**
     * Handle the action event triggered when the back button is pressed.
     * This method retrieves the current stage and loads the fitness page view using a FXMLLoader.
     * The fitness page view is then set as the scene on the current stage.
     * Any exceptions thrown during this process are propagated.
     *
     * @throws Exception if an error occurs during the loading of the fitness page view
     */
    @FXML
    private void handleBack() throws Exception {
        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/fitness_page.fxml"));
        Scene scene = new Scene(loader.load(), 1000, 800);
        stage.setScene(scene);
    }

    /**
     * WorkoutEntry class represents a single entry in a workout diary.
     */
    public static class WorkoutEntry {
        private final String workoutName;
        private final String dateCompleted;

        public WorkoutEntry(String workoutName, String dateCompleted) {
            this.workoutName = workoutName;
            this.dateCompleted = dateCompleted;
        }

        public String getWorkoutName() {
            return workoutName;
        }

        public String getDateCompleted() {
            return dateCompleted;
        }
    }
}
