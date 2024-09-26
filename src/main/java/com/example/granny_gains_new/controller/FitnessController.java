package com.example.granny_gains_new.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;



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
    protected void NavCardio() throws IOException {
        Stage stage = (Stage) CardioButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/FitnessCardio.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
        stage.setScene(scene);
    }

    @FXML
    protected void NavStrength() throws IOException {
        Stage stage = (Stage) StrengthButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/fitnessStrength.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
        stage.setScene(scene);
    }

    @FXML
    protected void NavHIIT() throws IOException {
        Stage stage = (Stage) HIITButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/fitnessHIIT.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
        stage.setScene(scene);
    }

    @FXML
    protected void handleBackToHome() throws IOException {
        Stage stage = (Stage) HomeButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/granny_gains_home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
        stage.setScene(scene);
    }
    @FXML
    private ImageView Cardio1, Cardio2, Cardio3, Cardio4;

    @FXML
    private Label Cardio1Title, Cardio2Title, Cardio3Title, Cardio4Title;

    // Method to initialize the controller
    @FXML
    public void initialize() {
        loadCardioWorkouts();
    }

    // Method to load cardio workouts from CSV and update the UI
    private void loadCardioWorkouts() {
        String csvFile = "src/main/java/com/example/granny_gains_new/database/fitness.csv";
        String line;
        String csvSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            int counter = 1;
            while ((line = br.readLine()) != null) {
                // Skip header row
                if (counter == 1) {
                    counter++;
                    continue;
                }

                // Split lines into columns
                String[] workoutData = line.split(csvSplitBy);

                String title = workoutData[0].replace("\"", "").trim();
                String thumbnailPath = workoutData[1].replace("\"", "").trim();
                String videoLink = workoutData[2].replace("\"", "").trim();

                // Assign the title and thumbnail using counter
                switch (counter) {
                    case 2:
                        updateWorkoutTile(Cardio1, Cardio1Title, title, thumbnailPath);
                        break;
                    case 3:
                        updateWorkoutTile(Cardio2, Cardio2Title, title, thumbnailPath);
                        break;
                    case 4:
                        updateWorkoutTile(Cardio3, Cardio3Title, title, thumbnailPath);
                        break;
                    case 5:
                        updateWorkoutTile(Cardio4, Cardio4Title, title, thumbnailPath);
                        break;
                }

                counter++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to update the ImageView and Label for a workout tile in FitnessCardio.fxml
    private void updateWorkoutTile(ImageView imageView, Label titleLabel, String title, String imagePath) {
        // Set the image
        try {
            Image thumbnail = new Image(getClass().getResource(imagePath).toExternalForm());
            imageView.setImage(thumbnail);
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
        }

        // Set the title in the Labels
        titleLabel.setText(title);

        // Add event handler to play video on click (Optional)
        imageView.setOnMouseClicked(event -> {
            // Open the video player with the given link TODO
            System.out.println("Video clicked: " + title);
        });
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


