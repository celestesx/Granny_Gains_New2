package com.example.granny_gains_new.database;

import com.example.granny_gains_new.model.Recipe;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;

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

                // Assuming CSV structure: recipe_type, recipe_name, servings, calories, description, ingredients, recipe_method, picture_url
                Recipe recipe = new Recipe(
                        0,                      // recipe_id is auto-incremented in the DB
                        line[0].replaceAll("\"", ""),  // recipe_type
                        line[1].replaceAll("\"", ""),  // recipe_name
                        Integer.parseInt(line[2]),     // servings
                        Integer.parseInt(line[3]),     // calories
                        line[4].replaceAll("\"", ""),  // description
                        line[5].replaceAll("\"", ""),  // ingredients (you might want to handle this as a JSON string or split it)
                        line[6].replaceAll("\"", ""),  // recipe_method
                        line[7]                       // picture_url (you can replace with actual image URLs if needed)
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
}
