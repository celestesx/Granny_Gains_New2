package com.example.granny_gains_new.controller;

import com.example.granny_gains_new.database.RecipeDBHandler;
import com.example.granny_gains_new.model.Recipe;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label; // Import Label for displaying recipe data
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox; // Import HBox for layout
import javafx.scene.layout.VBox; // Import VBox for layout
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
    private Button HomeButton, AllRecipesButton, BreakfastButton, LunchButton, DinnerButton;

    @FXML
    private Label categoryHeader; // Label for the category header

    @FXML
    public void initialize() {
        loadRecipes(null);
        categoryHeader.setText("All Recipes"); // Default header for all recipes
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
                    setGraphic(null);
                } else {
                    // Create layout for each cell with an Image and Labels
                    HBox hBox = new HBox(10); // 10px spacing between image and text
                    VBox vBox = new VBox(5); // 5px spacing between title and description

                    // Create the image
                    Image recipeImage;
                    try {
                        String imagePath = "/com/example/granny_gains_new/meals_images/" + recipe.getPictureUrl() + ".png";
                        recipeImage = new Image(getClass().getResourceAsStream(imagePath));
                    } catch (NullPointerException e) {
                        // If the image is missing, use a default image
                        recipeImage = new Image(getClass().getResourceAsStream("/com/example/granny_gains_new/meals_images/default.png"));
                    }
                    ImageView imageView = new ImageView(recipeImage);
                    imageView.setFitWidth(100); // Adjust the width as per your UI
                    imageView.setPreserveRatio(true);

                    // Create the title label (bold and wrap text)
                    Label titleLabel = new Label(recipe.getRecipeName());
                    titleLabel.setStyle("-fx-font-weight: bold;");
                    titleLabel.setWrapText(true);

                    // Limit the description to 10 words and add "..." after 10 words
                    String description = recipe.getDescription();
                    String[] words = description.split("\\s+");
                    if (words.length > 10) {
                        description = String.join(" ", java.util.Arrays.copyOfRange(words, 0, 10)) + "...";
                    }
                    // Create the description label (wrap text enabled)
                    Label descriptionLabel = new Label(description);
                    descriptionLabel.setWrapText(true);

                    // Add title and description to the VBox
                    vBox.getChildren().addAll(titleLabel, descriptionLabel);

                    // Add the image and VBox to the HBox
                    hBox.getChildren().addAll(imageView, vBox);

                    // Set the HBox as the graphic for the cell
                    setGraphic(hBox);
                }
            }
        });

        recipeListView.setOnMouseClicked(event -> {
            Recipe selectedRecipe = recipeListView.getSelectionModel().getSelectedItem();
            if (selectedRecipe != null) {
                try {
                    showRecipeDetailPage(selectedRecipe);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showRecipeDetailPage(Recipe recipe) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/recipiesview.fxml"));
        Stage stage = (Stage) recipeListView.getScene().getWindow();
        Scene scene = new Scene(loader.load(), 1500, 1000);

        RecipeDetailController controller = loader.getController();
        controller.setRecipeData(recipe);

        stage.setScene(scene);
    }

    @FXML
    protected void showAllRecipes() {
        categoryHeader.setText("All Recipes"); // Update header when showing all recipes
        loadRecipes(null);
    }

    @FXML
    protected void showBreakfastRecipes() {
        categoryHeader.setText("Breakfast Recipes"); // Update header when showing breakfast recipes
        loadRecipes("breakfast");
    }

    @FXML
    protected void showLunchRecipes() {
        categoryHeader.setText("Lunch Recipes"); // Update header when showing lunch recipes
        loadRecipes("lunch");
    }

    @FXML
    protected void showDinnerRecipes() {
        categoryHeader.setText("Dinner Recipes"); // Update header when showing dinner recipes
        loadRecipes("dinner");
    }

    @FXML
    protected void handleBackToHome() throws IOException {
        Stage stage = (Stage) HomeButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/granny_gains_home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
        stage.setScene(scene);
    }
}
