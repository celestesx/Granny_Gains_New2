package com.example.granny_gains_new.controller;

import com.example.granny_gains_new.database.DatabaseConnection;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
    private TextArea recipeDescriptionTextArea;

    @FXML
    private ImageView unfavourited;

    private boolean isFavourited = false; // Track if the meal is favorited

    private Recipe currentRecipe; // Declare an instance variable to hold the recipe

    @FXML
    protected void FavouriteMeal() {
        if (currentRecipe == null) {
            System.out.println("Current recipe is not set.");
            return;
        }

        if (isFavourited) {
            // Remove from favorites
            boolean success = removeMealFromFavorites();
            if (success) {
                updateHeartImage(false);
                System.out.println("Meal Unfavorited");
            } else {
                System.out.println("Failed to unfavorite the meal.");
            }
        } else {
            // Save to favorites
            boolean success = saveMealToFavorites();
            if (success) {
                updateHeartImage(true);
                System.out.println("Meal Favourited");
            } else {
                System.out.println("Failed to favorite the meal due to database errors.");
            }
        }

        isFavourited = !isFavourited; // Toggle favorited status
    }

    private void updateHeartImage(boolean favorited) {
        String heartImagePath = favorited
                ? "/com/example/granny_gains_new/images/icons8-favorite-50 (1).png" // Filled heart image
                : "/com/example/granny_gains_new/images/icons8-favorite-50.png"; // Unfilled heart image
        Image heartImage = new Image(getClass().getResourceAsStream(heartImagePath));
        unfavourited.setImage(heartImage);
    }

    private boolean saveMealToFavorites() {
        // Perform validation
        if (!validateRecipeData()) {
            return false; // Validation failed
        }

        String query = "INSERT INTO MealTable (recipe_name, description, servings, calories, picture_url) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            String recipeName = recipeNameLabel.getText();
            String description = ""; // You need to get the description from the current recipe
            int servings = Integer.parseInt(servingsLabel.getText().split(": ")[1]);
            int calories = Integer.parseInt(caloriesLabel.getText().split(": ")[1].split(" ")[0]);
            String pictureUrl = currentRecipe.getPictureUrl();

            // Assuming you have a method in Recipe to get the description
            description = currentRecipe.getDescription(); // Get the description here

            stmt.setString(1, recipeName);
            stmt.setString(2, description); // Set the description value
            stmt.setInt(3, servings);
            stmt.setInt(4, calories);
            stmt.setString(5, pictureUrl);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Return true if meal saved successfully
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false on error
        }
    }

    private boolean removeMealFromFavorites() {
        if (currentRecipe == null) {
            return false; // No recipe to remove
        }

        String query = "DELETE FROM MealTable WHERE recipe_name = ?";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, currentRecipe.getRecipeName());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Return true if meal removed successfully
        } catch (SQLException e) {
            System.err.println("Error removing meal: " + e.getMessage());
            return false; // Return false on error
        }
    }

    private boolean validateRecipeData() {
        return currentRecipe != null; // Just check if currentRecipe is not null
    }

    public void setRecipeData(Recipe recipe) {
        this.currentRecipe = recipe; // Store the passed recipe in the instance variable

        recipeNameLabel.setText(recipe.getRecipeName());

        // Load the corresponding image for the recipe
        Image recipeImage = new Image(getClass().getResourceAsStream("/com/example/granny_gains_new/meals_images/" + recipe.getPictureUrl() + ".png"));
        recipeImageView.setImage(recipeImage);

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
        recipeDescriptionTextArea.setText(recipe.getDescription());

        // Update the heart image based on the favorited status
        updateHeartImage(isFavourited);
    }

    @FXML
    protected void handleBackToMeals() throws IOException {
        Stage stage = (Stage) recipeNameLabel.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/meals_page.fxml"));
        Scene scene = new Scene(loader.load(), 1000, 800);
        stage.setScene(scene);
    }
}
