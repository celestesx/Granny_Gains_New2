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

/**
 * Controller class for handling the details of a Recipe.
 */
public class RecipeDetailController {

    @FXML
    Label recipeNameLabel;

    @FXML
    ImageView recipeImageView;

    @FXML
    Label servingsLabel;

    @FXML
    Label caloriesLabel;

    @FXML
    TextArea ingredientsTextArea;

    @FXML
    TextArea methodTextArea;

    @FXML
    TextArea recipeDescriptionTextArea;

    @FXML
    ImageView unfavourited;

    boolean isFavourited = false; // Track if the meal is favorited

    private Recipe currentRecipe; // Declare an instance variable to hold the recipe

    /**
     * This method handles toggling the favorite status of the current recipe. If the current recipe
     * is not set, it will display a message and return. If the current recipe is favorited,
     * it will remove it from the favorites. If the current recipe is not favorited, it will
     * save it to the favorites. It will update the heart image accordingly.
     */
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

    /**
     * Updates the heart image based on the favorited status.
     *
     * @param favorited a boolean value indicating whether the heart image should be filled or unfilled
     */
    private void updateHeartImage(boolean favorited) {
        String heartImagePath = favorited
                ? "/com/example/granny_gains_new/images/icons8-favorite-50 (1).png" // Filled heart image
                : "/com/example/granny_gains_new/images/icons8-favorite-50.png"; // Unfilled heart image
        Image heartImage = new Image(getClass().getResourceAsStream(heartImagePath));
        unfavourited.setImage(heartImage);
    }

    /**
     * Saves the current meal to the favorites table in the database.
     *
     * @return true if the meal was saved successfully, false otherwise
     */
    private boolean saveMealToFavorites() {
        // Perform validation
        if (!validateRecipeData()) {
            return false; // Validation failed
        }

        String query = "INSERT INTO MealTable (recipe_name, description, servings, calories, picture_url) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            String recipeName = recipeNameLabel.getText();
            String description = "";
            int servings = Integer.parseInt(servingsLabel.getText().split(": ")[1]);
            int calories = Integer.parseInt(caloriesLabel.getText().split(": ")[1].split(" ")[0]);
            String pictureUrl = currentRecipe.getPictureUrl();

            description = currentRecipe.getDescription();

            stmt.setString(1, recipeName);
            stmt.setString(2, description);
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

    /**
     * Removes the current meal from the favorites list in the database.
     * If the current recipe is not set, the method will return false with an indication message.
     * The meal is removed by executing a DELETE SQL query on the MealTable using the recipe name.
     *
     * @return true if the meal is successfully removed from favorites, false otherwise
     */
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

    /**
     * Validates the recipe data by checking if the current recipe is not null.
     *
     * @return true if currentRecipe is not null, false otherwise
     */
    boolean validateRecipeData() {
        return currentRecipe != null; // Just check if currentRecipe is not null
    }

    /**
     * Sets the recipe data to be displayed in the UI components.
     *
     * @param recipe The Recipe object containing the details to be shown.
     */
    public void setRecipeData(Recipe recipe) {
        this.currentRecipe = recipe; // Store the passed recipe in the instance variable

        recipeNameLabel.setText(recipe.getRecipeName());

        // Load the corresponding image for the recipe
        Image recipeImage = loadImage("/com/example/granny_gains_new/meals_images/" + recipe.getPictureUrl() + ".png");
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

    /**
     * Loads an image based on the provided image path. If the image is not found at the specified path,
     * a default image will be returned.
     *
     * @param imagePath The path to the image to be loaded. Must start with a forward slash ("/").
     * @return The loaded Image object. If the specified image is not found, a default image will be returned.
     */
    protected Image loadImage(String imagePath) {
        try {
            return new Image(getClass().getResourceAsStream(imagePath));
        } catch (NullPointerException e) {
            // Return a default image if the resource is not found
            return new Image(getClass().getResourceAsStream("/com/example/granny_gains_new/images/HIIT1.png"));
        }
    }

    /**
     * This method handles the action for navigating back to the meals page.
     * It retrieves the current window stage, loads the meals_page.fxml file, creates a new scene with the loaded content,
     * and sets the scene to the stage for displaying.
     *
     * @throws IOException if an I/O error occurs during the loading of the meals_page.fxml file
     */
    @FXML
    protected void handleBackToMeals() throws IOException {
        Stage stage = (Stage) recipeNameLabel.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/meals_page.fxml"));
        Scene scene = new Scene(loader.load(), 1000, 800);
        stage.setScene(scene);
    }
}
