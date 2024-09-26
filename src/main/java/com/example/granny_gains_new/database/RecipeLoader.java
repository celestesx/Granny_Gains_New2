package com.example.granny_gains_new.database;

import com.example.granny_gains_new.model.Recipe;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RecipeLoader {

    // Load recipes from a CSV file and add them to the database (no duplicates)
    public void loadRecipesFromCSV(String filePath, RecipeDBHandler dbHandler) {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] line;
            boolean isFirstRow = true;  // Flag to skip the header row
            while ((line = reader.readNext()) != null) {
                if (isFirstRow) {
                    isFirstRow = false;  // Skip the first row (header)
                    continue;
                }

                // Convert ingredients and recipe_method from string to List<String>
                List<String> ingredients = parseListFromString(line[5]);
                List<String> recipeMethod = parseListFromString(line[6]);

                // Assuming CSV structure: recipe_type, recipe_name, servings, calories, description, ingredients, recipe_method, picture_url
                Recipe recipe = new Recipe(
                        0,  // recipe_id is auto-incremented in the DB
                        line[0],  // recipe_type
                        line[1],  // recipe_name
                        (int) Float.parseFloat(line[2]),  // servings, convert from float to integer
                        (int) Float.parseFloat(line[3]),  // calories, convert from float to integer
                        line[4],  // description
                        ingredients,  // parsed List<String> for ingredients
                        recipeMethod,  // parsed List<String> for recipe method
                        line[7]  // picture_url (you can replace with actual image URLs if needed)
                );

                // Add recipe to the database only if it does not exist
                dbHandler.addRecipe(recipe);
            }
        } catch (IOException e) {
            System.err.println("Error loading recipes from CSV: " + e.getMessage());
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

    // Helper method to parse a list from a string like ["item1", "item2"]
    private List<String> parseListFromString(String listString) {
        // Remove square brackets and split by commas
        String[] items = listString.replaceAll("\\[|\\]", "").split(",");

        // Trim spaces and return as a List<String>
        return Arrays.asList(items).stream()
                .map(String::trim)  // Remove any leading/trailing spaces
                .toList();
    }
}
