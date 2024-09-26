package com.example.granny_gains_new.database;

import com.example.granny_gains_new.model.Recipe;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeDBHandler {

    private Connection conn;

    public RecipeDBHandler() {
        // Establish connection to the database using the DatabaseConnection class
        this.conn = DatabaseConnection.getInstance();
    }

    // Add a recipe to the database if it doesn't exist
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

    // Check if a recipe with the same name already exists
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

    // Retrieve a list of all recipes from the database
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
