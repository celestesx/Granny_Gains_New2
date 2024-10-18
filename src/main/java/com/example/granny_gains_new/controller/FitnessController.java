package com.example.granny_gains_new.controller;

import com.example.granny_gains_new.database.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.input.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javafx.scene.Parent;
import java.time.LocalDate;




public class FitnessController {

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
    private ImageView unfavourited1, unfavourited2, unfavourited3, unfavourited4; // ImageViews for the heart icons

    private boolean[] isFavourited = {false, false, false, false}; // Track if each workout is favorited

    private String[] currentWorkoutNames = {
            "20 minute Low Impact Cardio Workout",
            "Improve HEART HEALTH At Home",
            "Simple Cardio Workout For Seniors",
            "20 Minute Dance Workout for Seniors"
    };

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

    private void updateHeartImage(ImageView heartIcon, boolean favorited) {
        String heartImagePath = favorited
                ? "/com/example/granny_gains_new/images/icons8-favorite-50 (1).png" // Filled heart image
                : "/com/example/granny_gains_new/images/icons8-favorite-50.png"; // Unfilled heart image
        Image heartImage = new Image(getClass().getResourceAsStream(heartImagePath));
        heartIcon.setImage(heartImage);
    }

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

    @FXML
    protected void handleHelp() throws IOException {
        Stage stage = (Stage) HelpButton.getScene().getWindow();
        double width = stage.getWidth();
        double height = stage.getHeight();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/fitness_help.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        stage.setScene(scene);
    }

    @FXML
    protected void NavCardio() throws IOException {
        Stage stage = (Stage) CardioButton.getScene().getWindow();
        double width = stage.getWidth();
        double height = stage.getHeight();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/FitnessCardio.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        stage.setScene(scene);
    }

    @FXML
    protected void NavStrength() throws IOException {
        Stage stage = (Stage) StrengthButton.getScene().getWindow();
        double width = stage.getWidth();
        double height = stage.getHeight();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/FitnessStrength.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        stage.setScene(scene);
    }

    @FXML
    protected void NavHIIT() throws IOException {
        Stage stage = (Stage) HIITButton.getScene().getWindow();
        double width = stage.getWidth();
        double height = stage.getHeight();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/FitnessHIIT.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        stage.setScene(scene);
    }

    @FXML
    protected void handleBackToHome() throws IOException {
        Stage stage = (Stage) HomeButton.getScene().getWindow();
        double width = stage.getWidth();
        double height = stage.getHeight();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/granny_gains_home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        stage.setScene(scene);
    }
    @FXML
    private ImageView Cardio1, Cardio2, Cardio3, Cardio4;

    @FXML
    private Label Cardio1Title, Cardio2Title, Cardio3Title, Cardio4Title;

    @FXML
    private Button LogButton;

    @FXML
    private Button logCardio1, logCardio2, logCardio3, logCardio4;

    // Method to initialize the controller
    @FXML
    public void initialize() {
        loadCardioWorkouts();
        updateHeartIcons();
    }
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

    // Method to load cardio workouts from CSV and update the UI
    private void loadCardioWorkouts() {
        String csvFile = "src/main/java/com/example/granny_gains_new/database/fitness.csv";
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
                        updateWorkoutTile(Cardio1, Cardio1Title, title, thumbnailPath, videoLink);
                        break;
                    case 3:
                        updateWorkoutTile(Cardio2, Cardio2Title, title, thumbnailPath, videoLink);
                        break;
                    case 4:
                        updateWorkoutTile(Cardio3, Cardio3Title, title, thumbnailPath, videoLink);
                        break;
                    case 5:
                        updateWorkoutTile(Cardio4, Cardio4Title, title, thumbnailPath, videoLink);
                        break;
                }

                counter++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to update the ImageView and Label for a workout tile in FitnessCardio.fxml
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

    // Method to open a new scene with the video player
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
    @FXML
    private void markAsComplete(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String workoutName = ""; // This will hold the name of the workout to save to the database

        if (clickedButton == logCardio1) {
            workoutName = Cardio1Title.getText();
        } else if (clickedButton == logCardio2) {
            workoutName = Cardio2Title.getText();
        } else if (clickedButton == logCardio3) {
            workoutName = Cardio3Title.getText();
        } else if (clickedButton == logCardio4) {
            workoutName = Cardio4Title.getText();
        }

        // Update button text
        clickedButton.setText("Completed");

        // Call method to update the database
        addWorkoutToDiary(workoutName);
    }

    // Method to add workout to diary database
    private boolean addWorkoutToDiary(String workoutName) {
        String query = "INSERT INTO WorkoutDiary (workout_name, date_completed) VALUES (?, CURRENT_TIMESTAMP)";

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, workoutName);
            stmt.executeUpdate();

            System.out.println("Workout " + workoutName + " marked as completed and saved to the diary.");
        } catch (SQLException e) {
            System.err.println("Error saving workout to diary: " + e.getMessage());
        }
        return false;
    }

    private boolean removeWorkoutFromDiary(String currentWorkoutName) {
        if (currentWorkoutName == null) {
            return false; // No workout to remove
        }
        String query = "DELETE FROM WorkoutDiary WHERE workout_name = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, currentWorkoutName);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Return true if workout removed successfully
        } catch (SQLException e) {
            System.err.println("Error removing workout: " + e.getMessage());
            return false; // Return false on error
        }
    }


/**
 REPLACING DYNAMIC ADJUSTMENTS
    FXIDS in "src/main/resources/com/example/granny_gains_new/FitnessCardio.fxml"

 <--Labels-->
    Cardio1Title, Cardio2Title, Cardio3Title, Cardio4Title
 </--Labels-->

 <--Images (Thumbnail) -->
    Cardio1, Cardio2, Cardio3, Cardio4
 </--Labels-->
 */
}


