package com.example.granny_gains_new.controller;

import com.example.granny_gains_new.database.RecipeDBHandler;
import com.example.granny_gains_new.model.Recipe;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This class contains test methods to validate the functionality of the MealsController class.
 * It sets up necessary dependencies, initializes test data, and performs tests with different scenarios.
 * The tests focus on loading recipes based on different filters and verifying the expected behavior.
 */
class MealsControllerTest {

    @Mock
    private RecipeDBHandler mockDbHandler;

    @InjectMocks
    private MealsController mealsController;

    private Recipe recipe1;
    private Recipe recipe2;
    private Recipe recipe3;

    /**
     * Initializes the JavaFX toolkit before executing any tests. This method ensures that the JavaFX toolkit is initialized only once before all tests are run.
     * If the current thread is not the JavaFX application thread, it starts up the JavaFX toolkit by invoking Platform.startup() and waits for the initialization to complete.
     * This method uses a CountDownLatch to synchronize the initialization process and ensures that the toolkit is ready within a specified time limit.
     *
     * @throws InterruptedException if the thread is interrupted while waiting for the JavaFX toolkit initialization
     */
    @BeforeAll
    static void initToolkit() throws InterruptedException {
        // Initialize the JavaFX toolkit only once before all tests
        if (!Platform.isFxApplicationThread()) {
            final CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(latch::countDown); // Initializes the JavaFX toolkit
            latch.await(5, TimeUnit.SECONDS);
        }
    }

    /**
     * Set up the necessary environment before each test method is executed.
     * This method initializes the Mockito framework, sets up test data by creating Recipe objects,
     * creates a new instance of MealsController, injects a mock RecipeDBHandler, and sets up a ListView.
     */
    @BeforeEach
    void setUp() {
        // Initialise Mockito
        MockitoAnnotations.openMocks(this);

        // Initialise some test data
        recipe1 = new Recipe("Pancakes", "breakfast", "Fluffy pancakes", "pancakes");
        recipe2 = new Recipe("Grilled Cheese", "lunch", "Delicious grilled cheese", "grilled_cheese");
        recipe3 = new Recipe("Spaghetti", "dinner", "Classic spaghetti", "spaghetti");

        // Create the MealsController using no-arg constructor
        mealsController = new MealsController();

        // Inject the mock RecipeDBHandler using the setter method
        mealsController.setRecipeDBHandler(mockDbHandler);

        // Set up a real ListView and inject it into the controller
        mealsController.recipeListView = new ListView<>();
    }

    /**
     * Test method to verify the behavior of loading recipes without applying any filter.
     * This test case mocks the database call to return all recipes and validates if the expected
     * number of recipes are loaded into the ListView after calling the loadRecipes method with no filter.
     *
     * @throws InterruptedException if the thread is interrupted while waiting for JavaFX thread events
     */
    @Test
    void testLoadRecipes_NoFilter() throws InterruptedException {
        // mock the database call to return all recipes
        List<Recipe> allRecipes = Arrays.asList(recipe1, recipe2, recipe3);  // Exactly 3 recipes
        when(mockDbHandler.getAllRecipes()).thenReturn(allRecipes);          // Mock returns 3 recipes

        // Call the loadRecipes method with no filter
        Platform.runLater(() -> mealsController.loadRecipes(null));

        // Wait for JavaFX thread to complete
        waitForFxEvents();

        //  Verify that all recipes are loaded into the ListView
        ObservableList<Recipe> loadedRecipes = mealsController.recipeListView.getItems();

        // Print out the size and items in the ListView
        System.out.println("Loaded Recipes Size: " + loadedRecipes.size());
        loadedRecipes.forEach(recipe -> System.out.println("Recipe: " + recipe.getRecipeName()));

        assertEquals(3, loadedRecipes.size(), "ListView should contain exactly 3 recipes");
    }

    /**
     * Test method to verify the behavior of loading recipes with an empty filter result.
     * This test case sets up the necessary test data, mocks the database call to return all recipes,
     * loads recipes with a specified filter, waits for JavaFX thread events to complete, and then asserts
     * that the loaded recipes list is empty.
     *
     * @throws InterruptedException if the thread is interrupted while waiting for JavaFX thread events
     */
    @Test
    void testLoadRecipes_EmptyFilterResult() throws InterruptedException {

        List<Recipe> allRecipes = Arrays.asList(recipe1, recipe2, recipe3);
        when(mockDbHandler.getAllRecipes()).thenReturn(allRecipes);


        Platform.runLater(() -> mealsController.loadRecipes("snack"));


        waitForFxEvents();


        ObservableList<Recipe> loadedRecipes = mealsController.recipeListView.getItems();
        assertEquals(0, loadedRecipes.size());
    }


    /**
     * Waits for JavaFX events to complete by utilizing a CountDownLatch to synchronize the process.
     * This method runs the JavaFX code on the application thread using Platform.runLater and awaits
     * the completion for a specified time period. If the thread is interrupted during the wait,
     * it interrupts the current thread.
     */
    private void waitForFxEvents() {
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(latch::countDown);
        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
