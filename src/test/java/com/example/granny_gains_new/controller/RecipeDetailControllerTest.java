package com.example.granny_gains_new.controller;

import com.example.granny_gains_new.database.DatabaseConnection;
import com.example.granny_gains_new.model.Recipe;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Represents a test class for the RecipeDetailController. It contains unit tests to validate the behavior of
 * updating and validating recipe data on the UI components.
 */
class RecipeDetailControllerTest {

    @Mock
    private Recipe mockRecipe;

    @Mock
    private DatabaseConnection mockDatabaseConnection;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @InjectMocks
    private RecipeDetailController recipeDetailController;

    /**
     * Initializes the JavaFX Toolkit before running tests.
     *
     * This method ensures that the JavaFX Toolkit is initialized before all the tests are run. If the JavaFX
     * Toolkit is not already running on the FX application thread, it starts the toolkit and waits for it to be ready
     * within a specified time limit. This method is annotated with @BeforeAll to run once before any test methods
     * in the test class execution.
     *
     * @throws InterruptedException if the thread is interrupted while waiting for the JavaFX Toolkit to start.
     */
    @BeforeAll
    static void initJavaFX() throws InterruptedException {
        // Initialize JavaFX Toolkit once before all tests
        if (!Platform.isFxApplicationThread()) {
            final CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(latch::countDown);
            latch.await(5, TimeUnit.SECONDS);
        }
    }

    /**
     * Set up method for initializing test environment before each test case execution.
     *
     * This method initializes mocks and JavaFX UI components using Platform.runLater(), ensuring that the JavaFX
     * UI components are initialized before proceeding. It also sets up a mock Recipe object with predefined values
     * for testing purposes.
     *
     * @throws Exception if any error occurs during setup
     */
    @BeforeEach
    void setUp() throws Exception {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Initialize JavaFX UI components using Platform.runLater()
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            recipeDetailController.recipeNameLabel = new Label();
            recipeDetailController.recipeImageView = new ImageView();
            recipeDetailController.servingsLabel = new Label();
            recipeDetailController.caloriesLabel = new Label();
            recipeDetailController.ingredientsTextArea = new TextArea();
            recipeDetailController.methodTextArea = new TextArea();
            recipeDetailController.recipeDescriptionTextArea = new TextArea();
            recipeDetailController.unfavourited = new ImageView();
            latch.countDown();
        });
        latch.await(5, TimeUnit.SECONDS); // Ensure the JavaFX UI components are initialized

        // Set up a mock recipe
        when(mockRecipe.getRecipeName()).thenReturn("Test Recipe");
        when(mockRecipe.getDescription()).thenReturn("This is a test description.");
        when(mockRecipe.getServings()).thenReturn(2);
        when(mockRecipe.getCalories()).thenReturn(250);
        when(mockRecipe.getIngredients()).thenReturn(Arrays.asList("Ingredient 1", "Ingredient 2"));
        when(mockRecipe.getRecipeMethod()).thenReturn(Arrays.asList("Step 1", "Step 2"));
        when(mockRecipe.getPictureUrl()).thenReturn("test_picture");
    }

    /**
     * Sets the recipe data to be displayed in the UI components.
     *
     * @param recipe The Recipe object containing the details to be shown.
     */
    @Test
    void testSetRecipeData() {
        // Act: Set the recipe data
        recipeDetailController.setRecipeData(mockRecipe);

        // Assert: Verify that the UI components are updated correctly
        assertEquals("Test Recipe", recipeDetailController.recipeNameLabel.getText());
        assertEquals("Servings: 2", recipeDetailController.servingsLabel.getText());
        assertEquals("Calories: 250 kcal", recipeDetailController.caloriesLabel.getText());
        assertEquals("Ingredient 1\nIngredient 2", recipeDetailController.ingredientsTextArea.getText());
        assertEquals("Step 1\nStep 2", recipeDetailController.methodTextArea.getText());
        assertEquals("This is a test description.", recipeDetailController.recipeDescriptionTextArea.getText());
    }

    /**
     * Method to test the validation of recipe data in the RecipeDetailController.
     * This test sets a valid recipe in the controller and then validates the recipe data.
     * It confirms that the validation is successful by checking if the current recipe is not null.
     */
    @Test
    void testValidateRecipeData() {
        // Arrange: Set a valid recipe
        recipeDetailController.setRecipeData(mockRecipe);

        // Act & Assert: The validation should pass
        assertTrue(recipeDetailController.validateRecipeData());
    }
}
