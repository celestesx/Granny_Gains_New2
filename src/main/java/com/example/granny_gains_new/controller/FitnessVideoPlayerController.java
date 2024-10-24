package com.example.granny_gains_new.controller;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * Controller class for managing the Fitness Video Player functionality.
 * This class handles loading and playing fitness videos in a WebView component.
 */
public class FitnessVideoPlayerController {
    @FXML
    private WebView webView;

    private String videoUrl;

    /**
     * Sets the URL of the video to be played by the Fitness Video Player.
     *
     * @param videoUrl The URL of the video to be set. Calls loadVideo() method to load the video immediately after setting the URL.
     */
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
        loadVideo(); // Load the video immediately after the URL is set
    }


    /**
     * Loads the video into the WebView if the video URL is not empty.
     * Uses the WebEngine of the WebView component to load the video from the specified URL.
     */
    private void loadVideo() {
        if (videoUrl != null && !videoUrl.isEmpty()) {
            WebEngine webEngine = webView.getEngine();
            webEngine.load(videoUrl);
        }
    }

    /**
     * Initializes the Fitness Video Player controller.
     * Checks if a video URL is provided and not empty, then calls the loadVideo() method to load the video into the WebView component.
     */
    @FXML
    public void initialize() {

        if (videoUrl != null && !videoUrl.isEmpty()) {
            loadVideo();
        }
    }


    /**
     * Closes the current stage when the "Back" button is clicked.
     * This method retrieves the current stage from the WebView's scene and closes it.
     */
    @FXML
    private void handleBack() {
        Stage stage = (Stage) webView.getScene().getWindow();
        stage.close();
    }
}
