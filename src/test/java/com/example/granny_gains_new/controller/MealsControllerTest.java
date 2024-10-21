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

class MealsControllerTest {

    @Mock
    private RecipeDBHandler mockDbHandler;

    @InjectMocks
    private MealsController mealsController;

    private Recipe recipe1;
    private Recipe recipe2;
    private Recipe recipe3;

    @BeforeAll
    static void initToolkit() throws InterruptedException {
        // Initialize the JavaFX toolkit only once before all tests
        if (!Platform.isFxApplicationThread()) {
            final CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(latch::countDown); // Initializes the JavaFX toolkit
            latch.await(5, TimeUnit.SECONDS);
        }
    }

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




    @Test
    void testLoadRecipes_EmptyFilterResult() throws InterruptedException {

        List<Recipe> allRecipes = Arrays.asList(recipe1, recipe2, recipe3);
        when(mockDbHandler.getAllRecipes()).thenReturn(allRecipes);


        Platform.runLater(() -> mealsController.loadRecipes("snack"));


        waitForFxEvents();


        ObservableList<Recipe> loadedRecipes = mealsController.recipeListView.getItems();
        assertEquals(0, loadedRecipes.size());
    }



    // Helper method to wait for JavaFX thread events to complete
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
