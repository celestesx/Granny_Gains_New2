package com.example.granny_gains_new.controller;

import com.example.granny_gains_new.database.RecipeDBHandler;
import com.example.granny_gains_new.model.Recipe;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.List;

public class MealsController {

    @FXML
    private ListView<Recipe> recipeListView;  // ListView to display recipes

    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        // Load recipes from the database and display them in the ListView
        loadRecipes();
    }

    private void loadRecipes() {
        // Load recipes from the database using the RecipeDBHandler
        RecipeDBHandler dbHandler = new RecipeDBHandler();
        List<Recipe> recipeList = dbHandler.getAllRecipes();

        // Convert the list of recipes to an ObservableList
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
                    // Display recipe name, type, and description
                    setText(recipe.getRecipeName() + " (" + recipe.getRecipeType() + ")\n" + recipe.getDescription());
                }
            }
        });
    }

    @FXML
    protected void handleBackToHome() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/granny_gains_home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
        stage.setScene(scene);
    }
}
