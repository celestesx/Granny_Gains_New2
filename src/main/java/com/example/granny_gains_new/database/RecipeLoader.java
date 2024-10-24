package com.example.granny_gains_new.database;

import com.example.granny_gains_new.model.Recipe;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * The RecipeLoader class is responsible for loading recipes from a CSV file and adding them to the database.
 */
public class RecipeLoader {

    /**
     * Loads recipes from a CSV file and adds them to the database.
     *
     * @param filePath The path to the CSV file containing recipes.
     * @param dbHandler The RecipeDBHandler object for interacting with the database.
     */
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

    /**
     * Parses a comma-separated string into a List of Strings.
     *
     * @param listString The comma-separated string to be parsed.
     * @return A List of Strings containing the parsed items after trimming any leading or trailing spaces.
     */
    private List<String> parseListFromString(String listString) {
        String[] items = listString.replaceAll("\\[|\\]", "").split(",");

        return Arrays.asList(items).stream()
                .map(String::trim)
                .toList();
    }
}
