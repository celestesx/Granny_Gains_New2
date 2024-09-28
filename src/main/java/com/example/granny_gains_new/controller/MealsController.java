package com.example.granny_gains_new.controller;

import com.example.granny_gains_new.database.RecipeDBHandler;
import com.example.granny_gains_new.model.Recipe;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class MealsController {

    @FXML
    private ListView<Recipe> recipeListView;  // ListView to display recipes

    @FXML
    private Button backButton, AllRecipesButton, VeganButton, GlutenFreeButton;

    @FXML
    public void initialize() {
        // Load and display all recipes on initialization
        loadRecipes(null);

        // Verify UI components
        assert backButton != null : "fx:id=\"backButton\" was not injected: check your FXML file 'Meals.fxml'.";
    }

    // Method to load and display recipes based on a filter
    private void loadRecipes(String filter) {
        // Load recipes from the database using RecipeDBHandler
        RecipeDBHandler dbHandler = new RecipeDBHandler();
        List<Recipe> recipeList = dbHandler.getAllRecipes();

        // If a filter is provided (e.g., "Vegan"), filter the recipes
        if (filter != null) {
            recipeList = recipeList.stream()
                    .filter(recipe -> recipe.getRecipeType().equalsIgnoreCase(filter))
                    .collect(Collectors.toList());
        }

        // Convert the list to an ObservableList
        ObservableList<Recipe> observableRecipeList = FXCollections.observableArrayList(recipeList);

        // Set the ListView's items
        recipeListView.setItems(observableRecipeList);

        // Customize the ListView to display recipe details
        recipeListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Recipe recipe, boolean empty) {
                super.updateItem(recipe, empty);
                if (empty || recipe == null) {
                    setText(null);
                } else {
                    setText(recipe.getRecipeName() + " (" + recipe.getRecipeType() + ")\n" + recipe.getDescription());
                }
            }
        });
    }

    @FXML
    protected void showAllRecipes() {
        loadRecipes(null);  // Show all recipes
    }

    @FXML
    protected void showVeganRecipes() {
        loadRecipes("Vegan");  // Show only Vegan recipes
    }

    @FXML
    protected void showGlutenFreeRecipes() {
        loadRecipes("Gluten-Free");  // Show only Gluten-Free recipes
    }

    @FXML
    protected void handleBackToHome() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/granny_gains_home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
        stage.setScene(scene);
    }
}