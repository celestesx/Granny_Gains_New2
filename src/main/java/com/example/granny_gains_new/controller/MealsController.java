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
    private ListView<Recipe> recipeListView;

    @FXML
    private Button HomeButton, AllRecipesButton, VeganButton, GlutenFreeButton;

    @FXML
    public void initialize() {
        loadRecipes(null);

        assert HomeButton != null : "fx:id=\"HomeButton\" was not injected: check your FXML file 'Meals.fxml'.";
    }

    private void loadRecipes(String filter) {
        RecipeDBHandler dbHandler = new RecipeDBHandler();
        List<Recipe> recipeList = dbHandler.getAllRecipes();

        if (filter != null) {
            recipeList = recipeList.stream()
                    .filter(recipe -> recipe.getRecipeType().equalsIgnoreCase(filter))
                    .collect(Collectors.toList());
        }

        ObservableList<Recipe> observableRecipeList = FXCollections.observableArrayList(recipeList);
        recipeListView.setItems(observableRecipeList);
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
        loadRecipes(null);
    }

    @FXML
    protected void showVeganRecipes() {
        loadRecipes("Vegan");
    }

    @FXML
    protected void showGlutenFreeRecipes() {
        loadRecipes("Gluten-Free");
    }

    @FXML
    protected void handleBackToHome() throws IOException {
        Stage stage = (Stage) HomeButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/granny_gains_home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
        stage.setScene(scene);
    }
}