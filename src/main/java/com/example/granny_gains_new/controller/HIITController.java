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
 * Controller class for managing interactions with the HIIT workout section of the application.
 * Includes methods for toggling workout favorites, adding and removing workouts from the fitness table,
 * marking workouts as completed, handling navigation to different sections, and displaying help information.
 */
public class HIITController {
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
    private Button logHiit1, logHiit2 , logHiit3, logHiit4;

    @FXML
    private ImageView unfavourited1, unfavourited2, unfavourited3, unfavourited4; // ImageViews for the heart icons

    private boolean[] isFavourited = {false, false, false, false}; // Track if each workout is favorited

    private String[] currentWorkoutNames = {
            "Beginner Seated HIIT Workout",
            "HIIT Workout for Seniors to Lose Weight",
            "High Intensity Exercises for Seniors",
            "Beginner HIIT Workout For Seniors"
    };

    /**
     * Toggles the favorite status of a workout icon based on the provided MouseEvent.
     *
     * @param event The MouseEvent that triggered the method.
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
     * Updates the ImageView of a heart icon based on the favorited status.
     *
     * @param heartIcon The ImageView representing the heart icon to be updated.
     * @param favorited A boolean indicating whether the heart icon should be filled or unfilled.
     */
    private void updateHeartImage(ImageView heartIcon, boolean favorited) {
        String heartImagePath = favorited
                ? "/com/example/granny_gains_new/images/icons8-favorite-50 (1).png" // Filled heart image
                : "/com/example/granny_gains_new/images/icons8-favorite-50.png"; // Unfilled heart image
        Image heartImage = new Image(getClass().getResourceAsStream(heartImagePath));
        heartIcon.setImage(heartImage);
    }

    /**
     * Updates the heart icons based on the favorited status.
     * Loops through the isFavourited array to set the initial state of heart icons
     * corresponding to their favorited status.
     * If a heart icon is found for a particular favorited status, it calls updateHeartImage
     * method to update the heart icon image based on the favorited status.
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
     * Adds a workout to the FitnessTable in the database.
     *
     * @param workoutName The name of the workout to be added.
     * @return true if the workout was successfully added, false otherwise.
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
     * Removes the specified workout from the FitnessTable.
     *
     * @param workoutName The name of the workout to be removed.
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
     * Private method handleLog is responsible for opening a new stage that displays the fitness log UI.
     * It loads the fitnesslog.fxml file using FXMLLoader, creates a new Stage, sets the scene with the loaded Parent, and shows the stage.
     * If an IOException occurs during the loading process, it prints the stack trace and displays an error message.
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
     * Marks the selected workout as complete by updating the button text and saving the workout to the database.
     *
     * @param event The ActionEvent triggering the method.
     */
    @FXML
    private void markAsComplete(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String workoutName = ""; // This will hold the name of the workout to save to the database

        if (clickedButton == logHiit1) {
            workoutName = HIIT1Title.getText();
        } else if (clickedButton == logHiit2) {
            workoutName = HIIT2Title.getText();
        } else if (clickedButton == logHiit3) {
            workoutName = HIIT3Title.getText();
        } else if (clickedButton == logHiit4) {
            workoutName = HIIT4Title.getText();
        }

        // Update button text
        clickedButton.setText("Completed");

        // Call method to update the database
        addWorkoutToDiary(workoutName);
    }

    /**
     * Adds a workout to the Workout Diary database table with the specified workout name and the current timestamp.
     *
     * @param workoutName The name of the workout to be added to the diary.
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
     * Method handleHelp is responsible for handling the Help button action.
     * It loads the fitness_help.fxml file using FXMLLoader, creates a new Scene with the loaded Parent,
     * and sets it to the current Stage to display the help information.
     *
     * @throws IOException*/
    @FXML
    protected void handleHelp() throws IOException {
        Stage stage = (Stage) HelpImageView.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/fitness_help.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
        stage.setScene(scene);
    }

    /**
     * Navigates to the FitnessCardio.fxml scene when the NavCardio method is invoked.
     * Retrieves the current window's Stage, loads the FitnessCardio.fxml file with FXMLLoader,
     * creates a new Scene with the loaded content, and sets the Stage to display the new Scene.
     * Throws an IOException if an error occurs during the loading process.
     *
     * @throws IOException If an error occurs during loading the FitnessCardio.fxml file.
     */
    @FXML
    protected void NavCardio() throws IOException {
        Stage stage = (Stage) CardioButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/FitnessCardio.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
        stage.setScene(scene);
    }

    /**
     * Navigates to the FitnessStrength.fxml scene when the NavStrength method is invoked.
     * Retrieves the current window's Stage, loads the FitnessStrength.fxml file with FXMLLoader,
     * creates a new Scene with the loaded content, and sets the Stage to display the new Scene.
     * Throws an IOException if an error occurs during the loading process.
     *
     * @throws IOException If an error occurs during loading the FitnessStrength.fxml file.
     */
    @FXML
    protected void NavStrength() throws IOException {
        Stage stage = (Stage) StrengthButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/FitnessStrength.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
        stage.setScene(scene);
    }

    /**
     * Navigates to the FitnessHIIT.fxml scene when the NavHIIT method is invoked.
     * Retrieves the current window's Stage, loads the FitnessHIIT.fxml file with FXMLLoader,
     * creates a new Scene with the loaded content, and sets the Stage to display the new Scene.
     * Throws an IOException if an error occurs during the loading process.
     *
     * @throws IOException If an error occurs during loading the FitnessHIIT.fxml file.
     */
    @FXML
    protected void NavHIIT() throws IOException {
        Stage stage = (Stage) HIITButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/FitnessHIIT.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
        stage.setScene(scene);
    }

    /**
     * Handle the action of navigating back to the home screen.
     * This method retrieves the current Stage of the application window, loads the Granny Gains home FXML file,
     * creates a new Scene with the loaded content, and sets the Stage to display the home screen.
     * Throws an IOException if an error occurs during the loading process.
     *
     * @throws IOException If an error occurs while loading the Granny Gains home FXML file
     */
    @FXML
    protected void handleBackToHome() throws IOException {
        Stage stage = (Stage) HomeButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/granny_gains_home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
        stage.setScene(scene);
    }

    @FXML
    private ImageView HIIT1, HIIT2, HIIT3, HIIT4;

    @FXML
    private Label HIIT1Title, HIIT2Title, HIIT3Title, HIIT4Title;

    @FXML
    public void initialize() {
        loadHIITWorkouts();
    }

    /**
     * Loads HIIT workouts from a CSV file and updates the workout tiles with corresponding information.
     * The CSV file should have the format: "title, thumbnailPath, videoLink" per row.
     * The method reads the CSV file, skips the header row, and extracts workout data for each row.
     * It then updates the workout tiles based on the workout counter, assigning the title, thumbnail,
     * and video link to the appropriate ImageView and Label elements.
     */
    private void loadHIITWorkouts() {
        String csvFile = "src/main/java/com/example/granny_gains_new/database/HIIT.csv";
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
                        updateWorkoutTile(HIIT1, HIIT1Title, title, thumbnailPath, videoLink);
                        break;
                    case 3:
                        updateWorkoutTile(HIIT2, HIIT2Title, title, thumbnailPath, videoLink);
                        break;
                    case 4:
                        updateWorkoutTile(HIIT3, HIIT3Title, title, thumbnailPath, videoLink);
                        break;
                    case 5:
                        updateWorkoutTile(HIIT4, HIIT4Title, title, thumbnailPath, videoLink);
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
     * @param imageView The ImageView to display the workout image.
     * @param titleLabel The Label to display the workout title.
     * @param title The title of the workout.
     * @param imagePath The path to the image file.
     * @param videoLink The link to the workout video.
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
     * @param videoUrl The URL of the video to be played in the video player window.
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
