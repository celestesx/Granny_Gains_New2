package com.example.granny_gains_new.model;

import java.util.List;

public class Recipe {
    private int recipeId;            // Primary key (auto-increment)
    private String recipeType;       // e.g., Breakfast, Lunch, Dinner, Snack
    private String recipeName;       // Name of the recipe
    private int servings;            // Number of servings
    private int calories;            // Calories per serving
    private String description;      // Description of the recipe
    private List<String> ingredients;  // Ingredients list
    private List<String> recipeMethod; // Cooking/preparation method
    private String pictureUrl;       // URL to the recipe image

    // Constructor
    public Recipe(int recipeId, String recipeType, String recipeName, int servings, int calories,
                  String description, List<String> ingredients, List<String> recipeMethod, String pictureUrl) {
        this.recipeId = recipeId;
        this.recipeType = recipeType;
        this.recipeName = recipeName;
        this.servings = servings;
        this.calories = calories;
        this.description = description;
        this.ingredients = ingredients;
        this.recipeMethod = recipeMethod;
        this.pictureUrl = pictureUrl;
    }

    // Getters and Setters
    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public String getRecipeType() {
        return recipeType;
    }

    public void setRecipeType(String recipeType) {
        this.recipeType = recipeType;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getRecipeMethod() {
        return recipeMethod;
    }

    public void setRecipeMethod(List<String> recipeMethod) {
        this.recipeMethod = recipeMethod;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "recipeId=" + recipeId +
                ", recipeType='" + recipeType + '\'' +
                ", recipeName='" + recipeName + '\'' +
                ", servings=" + servings +
                ", calories=" + calories +
                ", description='" + description + '\'' +
                ", ingredients=" + ingredients +
                ", recipeMethod=" + recipeMethod +
                ", pictureUrl='" + pictureUrl + '\'' +
                '}';
    }
}
