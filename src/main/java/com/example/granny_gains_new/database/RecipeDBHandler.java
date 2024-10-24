package com.example.granny_gains_new.database;

import com.example.granny_gains_new.model.Recipe;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Handles interactions with the database for Recipe objects.
 *
 * Provides methods to add recipes, check if a recipe already exists, and retrieve all recipes from the database.
 */
public class RecipeDBHandler {

    private Connection conn;

    /**
     * The RecipeDBHandler class is responsible for handling interactions with the recipe database table.
     * Upon instantiation, it establishes a connection to the database using the DatabaseConnection class.
     */
    public RecipeDBHandler() {
        // Establish connection to the database using the DatabaseConnection class
        this.conn = (Connection) DatabaseConnection.getInstance();
    }

    /**
     * Adds a new recipe to the database if it does not already exist.
     *
     * @param recipe The Recipe object to be added to the database.
     */
    public void addRecipe(Recipe recipe) {
        if (!recipeExists(recipe.getRecipeName())) {
            String sql = "INSERT INTO Recipe(recipe_type, recipe_name, servings, calories, description, ingredients, recipe_method, picture_url) "
                    + "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, recipe.getRecipeType());
                pstmt.setString(2, recipe.getRecipeName());
                pstmt.setInt(3, recipe.getServings());
                pstmt.setInt(4, recipe.getCalories());
                pstmt.setString(5, recipe.getDescription());

                // Convert List<String> to a comma-separated string
                pstmt.setString(6, String.join(",", recipe.getIngredients()));
                pstmt.setString(7, String.join(",", recipe.getRecipeMethod()));

                pstmt.setString(8, recipe.getPictureUrl());

                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error adding recipe: " + e.getMessage());
            }
        } else {
            System.out.println("Recipe '" + recipe.getRecipeName() + "' already exists. Skipping insert.");
        }
    }

    /**
     * Checks if a recipe with the given name exists in the database.
     *
     * @param recipeName The name of the recipe to check*/
    public boolean recipeExists(String recipeName) {
        String sql = "SELECT recipe_id FROM Recipe WHERE recipe_name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, recipeName);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // Returns true if a result is found, meaning the recipe exists
        } catch (SQLException e) {
            System.err.println("Error checking if recipe exists: " + e.getMessage());
        }
        return false;
    }

    /**
     * Retrieves all recipes from the database and returns them as a list of Recipe objects.
     *
     * @return A list of Recipe objects containing all recipes from the database.
     */
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        String sql = "SELECT * FROM Recipe";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Convert comma-separated strings back to List<String>
                List<String> ingredients = Arrays.asList(rs.getString("ingredients").split(","));
                List<String> recipeMethod = Arrays.asList(rs.getString("recipe_method").split(","));

                Recipe recipe = new Recipe(
                        rs.getInt("recipe_id"),
                        rs.getString("recipe_type"),
                        rs.getString("recipe_name"),
                        rs.getInt("servings"),
                        rs.getInt("calories"),
                        rs.getString("description"),
                        ingredients,  // Store as List<String>
                        recipeMethod,  // Store as List<String>
                        rs.getString("picture_url")
                );
                recipes.add(recipe);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving recipes: " + e.getMessage());
        }

        return recipes;
    }
}
