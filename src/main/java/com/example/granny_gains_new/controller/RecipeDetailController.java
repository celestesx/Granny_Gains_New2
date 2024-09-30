package com.example.granny_gains_new.controller;

import com.example.granny_gains_new.model.Recipe;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.List;

public class RecipeDetailController {

    @FXML
    private Label recipeNameLabel;

    @FXML
    private ImageView recipeImageView;

    @FXML
    private Label servingsLabel;

    @FXML
    private Label caloriesLabel;

    @FXML
    private TextArea ingredientsTextArea;

    @FXML
    private TextArea methodTextArea;

    @FXML
    private TextArea recipeDescription;

    public void setRecipeData(Recipe recipe) {
        recipeNameLabel.setText(recipe.getRecipeName());

        // Load the corresponding image for the recipe
        Image recipeImage = new Image(getClass().getResourceAsStream("/com/example/granny_gains_new/meals_images/" + recipe.getPictureUrl() + ".png"));
        recipeImageView.setImage(recipeImage);

        recipeDescription.setText(recipe.getDescription());

        // Set servings and calories
        servingsLabel.setText("Servings: " + recipe.getServings());
        caloriesLabel.setText("Calories: " + recipe.getCalories() + " kcal");

        // Set ingredients (join them with line breaks)
        List<String> ingredients = recipe.getIngredients();
        String ingredientsText = String.join("\n", ingredients);
        ingredientsTextArea.setText(ingredientsText);

        // Set method (join steps with line breaks)
        List<String> recipeMethod = recipe.getRecipeMethod();
        String methodText = String.join("\n", recipeMethod);
        methodTextArea.setText(methodText);
    }

    @FXML
    protected void handleBackToMeals() throws IOException {
        Stage stage = (Stage) recipeNameLabel.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/meals_page.fxml"));
        Scene scene = new Scene(loader.load(), 1000, 800);
        stage.setScene(scene);
    }
}
