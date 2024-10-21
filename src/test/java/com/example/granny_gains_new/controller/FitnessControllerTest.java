package com.example.granny_gains_new.controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FitnessControllerTest {

    @BeforeAll
    static void initToolkit() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(() -> latch.countDown());
        latch.await();
    }

    @BeforeEach
    public void setUp() throws Exception {
        FitnessController fitnessController = new FitnessController();
    }

    @Test
    void testNavStrength() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        // Run the test on the JavaFX application thread
        Platform.runLater(() -> {
            try {
                // Simulate the loading of the FitnessStrength.fxml page
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/FitnessCardio.fxml"));
                Parent root = loader.load();

                // Create a new Scene and Stage to simulate the navigation
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.show();

                // Look up the AnchorPane by its ID
                AnchorPane FitnessCardioPage = (AnchorPane) scene.lookup("#FitnessCardioPage");
                assertNotNull(FitnessCardioPage, "The FitnessCardioPage should not be null.");

                latch.countDown(); // Signal that the test is complete

            } catch (IOException e) {
                e.printStackTrace(); // Handle any loading errors
            }
        });

        latch.await(); // Wait for the JavaFX application thread to finish
    }

}
