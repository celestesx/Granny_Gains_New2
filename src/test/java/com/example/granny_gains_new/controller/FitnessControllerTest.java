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

/**
 * Test class for the FitnessController class.
 */
public class FitnessControllerTest {

    /**
     * Initializes the JavaFX toolkit for testing purposes. This method is annotated with
     * {@code @BeforeAll} to ensure it runs before any tests in the test class.
     * <p>
     * It starts the JavaFX platform and waits until it is fully initialized before proceeding.
     * This method is typically used in a test setup to prepare the environment for JavaFX testing.
     * Any exceptions thrown during the initialization process will be propagated to the caller.
     *
     * @throws Exception if an error occurs during the initialization process.
     */
    @BeforeAll
    static void initToolkit() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(() -> latch.countDown());
        latch.await();
    }

    /**
     * Set up method to initialize the FitnessController for testing purposes.
     * This method is annotated with {@code @BeforeEach} to ensure it runs before each test method in the test class.
     * Initializes a new instance of FitnessController for testing purposes.
     *
     * @throws Exception if any error occurs during the setup process.
     */
    @BeforeEach
    public void setUp() throws Exception {
        FitnessController fitnessController = new FitnessController();
    }

    /**
     * Test the navigation to the FitnessStrength page by simulating the loading of the FitnessCardio.fxml file.
     * This test method runs on the JavaFX application thread and verifies that the FitnessCardioPage is not null after navigation.
     * It uses a CountDownLatch to synchronize the test running on the application thread.
     *
     * @throws Exception if an error occurs during the navigation testing process
     */
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
