package com.example.granny_gains_new.controller;

import com.example.granny_gains_new.database.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import javafx.scene.Parent;

/**
 * Controller class responsible for managing the strength training section of the fitness application.
 * It handles workout favoriting, database interactions, UI updates, and scene navigation.
 */
public class StrengthController {
    @FXML
    private Button HomeButton;

    @FXML
    private Button CardioButton;

    @FXML
    private Button StrengthButton;

    @FXML
    private Button HIITButton;

    @FXML
    private Button HelpButton;

    @FXML
    private Button LogButton;

    @FXML
    private Button logStrength1, logStrength2, logStrength3, logStrength4;

    @FXML
    private ImageView unfavourited1, unfavourited2, unfavourited3, unfavourited4; // ImageViews for the heart icons

    private boolean[] isFavourited = {false, false, false, false}; // Track if each workout is favorited

    private String[] currentWorkoutNames = {
            "20 Min Strength Training for Seniors",
            "30 minute Full Body Strength Workout",
            "The Best Tips for Strength Training",
            "30 Min Strength Training for Over 60"
    };

    /**
     *
     */
    @FXML
    public void toggleFavorite(MouseEvent event) {
        // Get the source of the click event
        ImageView source = (ImageView) event.getSource();
        int index = -1;

        if (source == unfavourited1) {
            index = 0;
        } else if (source == unfavourited2) {
            index = 1;
        } else if (source == unfavourited3) {
            index = 2;
        } else if (source == unfavourited4) {
            index = 3;
        }

        if (index == -1) {
            System.out.println("Unknown heart icon clicked.");
            return;
        } else {
            System.out.println("Heart icon " + index + " clicked. Current favorited status: " + isFavourited[index]);
        }


        if (isFavourited[index]) {
            // Remove from favorites
            boolean success = removeWorkoutFromFitnessTable(currentWorkoutNames[index]);
            if (success) {
                updateHeartImage(source, false);
                System.out.println("Workout Unfavorited");
            } else {
                System.out.println("Failed to unfavorite the workout.");
            }
        } else {
            // Save to favorites
            boolean success = addWorkoutToFitnessTable(currentWorkoutNames[index]);
            if (success) {
                updateHeartImage(source, true);
                System.out.println("Workout Favourited");
            } else {
                System.out.println("Failed to favorite the workout due to database errors.");
            }
        }

        isFavourited[index] = !isFavourited[index]; // Toggle favorited status
    }

    /**
     * Update the heart icon image based on the favorited status.
     *
     * @param heartIcon The ImageView to update with the heart image.
     * @param favorited A boolean representing the favorited status (true for favorited, false for not favorited).
     */
    private void updateHeartImage(ImageView heartIcon, boolean favorited) {
        String heartImagePath = favorited
                ? "/com/example/granny_gains_new/images/icons8-favorite-50 (1).png" // Filled heart image
                : "/com/example/granny_gains_new/images/icons8-favorite-50.png"; // Unfilled heart image
        Image heartImage = new Image(getClass().getResourceAsStream(heartImagePath));
        heartIcon.setImage(heartImage);
    }

    /**
     * Updates the heart icons based on the favorited status of workouts.
     * Loops through the favorited status array and sets the appropriate heart icon
     * image for each corresponding workout tile. Calls updateHeartImage method to
     * update the ImageView with the filled or unfilled heart image.
     */
    private void updateHeartIcons() {
        // Set initial state of heart icons based on favorited status
        for (int i = 0; i < isFavourited.length; i++) {
            ImageView heartIcon = null;
            switch (i) {
                case 0:
                    heartIcon = unfavourited1;
                    break;
                case 1:
                    heartIcon = unfavourited2;
                    break;
                case 2:
                    heartIcon = unfavourited3;
                    break;
                case 3:
                    heartIcon = unfavourited4;
                    break;
            }
            if (heartIcon != null) {
                updateHeartImage(heartIcon, isFavourited[i]);
            }
        }
    }

    /**
     * Adds a workout to the FitnessTable database table.
     *
     * @param workoutName The name of the workout to be added to the FitnessTable.
     * @return true if the workout is successfully added to the FitnessTable, false otherwise.
     */
    public boolean addWorkoutToFitnessTable(String workoutName) {
        String insertSQL = "INSERT INTO FitnessTable (workout_name, saved_date) VALUES (?, ?)";
        String savedDate = LocalDate.now().toString();  // Get the current date

        try (Connection connection = DatabaseConnection.getInstance();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

            preparedStatement.setString(1, workoutName);
            preparedStatement.setString(2, savedDate);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Workout added to favorites: " + workoutName + " on " + savedDate);
                return true; // Success
            } else {
                System.out.println("No rows affected when adding workout.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Failure
    }

    /**
     * Remove a workout from the FitnessTable based on the workout name.
     *
     * @param workoutName The name of the workout to be removed from the FitnessTable.
     * @return true if the workout is successfully removed, false otherwise.
     */
    public boolean removeWorkoutFromFitnessTable(String workoutName) {
        String deleteSQL = "DELETE FROM FitnessTable WHERE workout_name = ?";
        try (Connection connection = DatabaseConnection.getInstance();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {

            preparedStatement.setString(1, workoutName);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Workout removed from favorites: " + workoutName);
                return true; // Success
            } else {
                System.out.println("No rows affected when removing workout.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Failure
    }

    /**
     * Method to handle logging out to show fitness log screen.
     * It loads the fitness log.fxml file using FXMLLoader and displays it in a new Stage.
     * If an IOException occurs during loading, it prints the error message.
     */
    @FXML
    private void handleLog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/fitnesslog.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading the favourites page.");
        }
    }

    /**
     * Mark a workout as completed by updating the button text and adding it to the diary database.
     *
     */
    @FXML
    private void markAsComplete(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String workoutName = ""; // This will hold the name of the workout to save to the database

        if (clickedButton == logStrength1) {
            workoutName = Strength1Title.getText();
        } else if (clickedButton == logStrength2) {
            workoutName = logStrength2.getText();
        } else if (clickedButton == logStrength3) {
            workoutName = Strength3Title.getText();
        } else if (clickedButton == logStrength4) {
            workoutName = Strength4Title.getText();
        }

        // Update button text
        clickedButton.setText("Completed");

        // Call method to update the database
        addWorkoutToDiary(workoutName);
    }

    /**
     * Adds a workout to the workout diary table in the database.
     *
     * @param workoutName The name of the workout to add to the diary.
     */
    private void addWorkoutToDiary(String workoutName) {
        String query = "INSERT INTO WorkoutDiary (workout_name, date_completed) VALUES (?, CURRENT_TIMESTAMP)";

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, workoutName);
            stmt.executeUpdate();

            System.out.println("Workout " + workoutName + " marked as completed and saved to the diary.");
        } catch (SQLException e) {
            System.err.println("Error saving workout to diary: " + e.getMessage());
        }
    }

    @FXML
    private ImageView HelpImageView;

    /**
     * Handle the action when the HelpButton is clicked.
     * Loads the fitness_help.fxml file using FXMLLoader and displays it in a new Stage.
     *
     * @throws IOException if an input or output exception occurred
     */
    @FXML
    protected void handleHelp() throws IOException {
        Stage stage = (Stage) HelpImageView.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/fitness_help.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
        stage.setScene(scene);
    }


    /**
     * Navigates the user to the Cardio screen.
     *
     * @throws IOException If an error occurs while loading the FitnessCardio.fxml file.
     */
    @FXML
    protected void NavCardio() throws IOException {
        Stage stage = (Stage) CardioButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/FitnessCardio.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
        stage.setScene(scene);
    }

    /**
     * Navigates to the FitnessStrength.fxml scene when the NavStrength method is called.
     * Retrieves the current Stage from the StrengthButton and loads FitnessStrength.fxml using FXMLLoader.
     * Sets the scene with a width of 1200 and height of 700 to the retrieved Stage.
     *
     * @throws IOException if an input or output exception occurs during scene loading
     */
    @FXML
    protected void NavStrength() throws IOException {
        Stage stage = (Stage) StrengthButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/FitnessStrength.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
        stage.setScene(scene);
    }

    /**
     * Navigates the user to the High-Intensity Interval Training (HIIT) screen.
     * Loaded using FXMLLoader to display the FitnessHIIT.fxml file in a new Stage.
     *
     * @throws IOException if an error occurs while loading FitnessHIIT.fxml
     */
    @FXML
    protected void NavHIIT() throws IOException {
        Stage stage = (Stage) HIITButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/FitnessHIIT.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
        stage.setScene(scene);
    }

    /**
     * Handles the action to navigate back to the Home screen.
     * Retrieves the current Stage from the HomeButton's Scene and loads the granny_gains_home.fxml file using FXMLLoader.
     * Sets the scene with a size of 1000x1000 to the retrieved Stage.
     *
     * @throws IOException if an input or output exception occurred during scene loading
     */
    @FXML
    protected void handleBackToHome() throws IOException {
        Stage stage = (Stage) HomeButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/granny_gains_home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
        stage.setScene(scene);
    }

    @FXML
    private ImageView Strength1, Strength2, Strength3, Strength4;

    @FXML
    private Label Strength1Title, Strength2Title, Strength3Title, Strength4Title;

    @FXML
    public void initialize() {
        loadCardioWorkouts();
    }

    /**
     * Loads cardio workouts data from a CSV file and updates the workout tiles.
     * Each line in the CSV file represents a specific workout with columns for title, thumbnail path, and video link.
     * The method reads the CSV file, skips the header row, parses each line, and updates the workout tiles based on the counter.
     * If an IOException occurs during file reading, the error is printed to the standard output.
     */
    private void loadCardioWorkouts() {
        String csvFile = "src/main/java/com/example/granny_gains_new/database/strength.csv";
        String line;
        String csvSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            int counter = 1;
            while ((line = br.readLine()) != null) {
                // Skip the header row
                if (counter == 1) {
                    counter++;
                    continue;
                }

                // Split the line into columns
                String[] workoutData = line.split(csvSplitBy);

                String title = workoutData[0].replace("\"", "").trim();
                String thumbnailPath = workoutData[1].replace("\"", "").trim();
                String videoLink = workoutData[2].replace("\"", "").trim();

                // Assign the title, thumbnail, and video link to the correct ImageView and Label based on the counter
                switch (counter) {
                    case 2:
                        updateWorkoutTile(Strength1, Strength1Title, title, thumbnailPath, videoLink);
                        break;
                    case 3:
                        updateWorkoutTile(Strength2, Strength2Title, title, thumbnailPath, videoLink);
                        break;
                    case 4:
                        updateWorkoutTile(Strength3, Strength3Title, title, thumbnailPath, videoLink);
                        break;
                    case 5:
                        updateWorkoutTile(Strength4, Strength4Title, title, thumbnailPath, videoLink);
                        break;
                }

                counter++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the workout tile with the provided image, title, and video link.
     *
     * @param imageView The ImageView where the image will be displayed.
     * @param titleLabel The Label where the title will be displayed.
     * @param title The title of the workout.
     * @param imagePath The path to the image file.
     * @param videoLink The video link associated with the workout.
     */
    private void updateWorkoutTile(ImageView imageView, Label titleLabel, String title, String imagePath, String videoLink) {
        // Load and set the image in the ImageView
        try {
            Image thumbnail = new Image(getClass().getResource(imagePath).toExternalForm());
            imageView.setImage(thumbnail);
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
        }

        // Set the title in the Label
        titleLabel.setText(title);

        // Add event handler to open video on click
        imageView.setOnMouseClicked(event -> openVideoPlayer(videoLink));
        titleLabel.setOnMouseClicked(event -> openVideoPlayer(videoLink));  // You can also click the title
    }

    /**
     * Opens a new video player window with the specified video URL.
     *
     * @param videoUrl The URL of the video to be played in the video player.
     */
    private void openVideoPlayer(String videoUrl) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/fitness_player.fxml"));
            Parent root = loader.load();

            // Get the controller for the new scene
            FitnessVideoPlayerController controller = loader.getController();
            controller.setVideoUrl(videoUrl);


            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Video Player");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
