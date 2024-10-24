package com.example.granny_gains_new.controller;

import com.example.granny_gains_new.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.HashSet;

/**
 * This class represents a controller for managing favorite meals related functionality.
 * It includes methods for handling navigation between pages, loading saved meals data,
 * initializing table columns, and setting up cell factories for the TableView.
 * The class uses JavaFX annotations for handling GUI components and event handling.
 */
public class FavMealsController {

    @FXML
    private TableView<MealEntry> mealTableView;

    @FXML
    private TableColumn<MealEntry, String> mealNameColumn;

    @FXML
    private TableColumn<MealEntry, String> descriptionColumn;

    @FXML
    private TableColumn<MealEntry, Integer> servingsColumn;

    @FXML
    private TableColumn<MealEntry, Integer> caloriesColumn;

    @FXML
    private Button backToHomeButton, mealsButton, fitnessButton;


    /**
     * Handles the action to navigate back to the home screen.
     * Retrieves the current stage from the backToHomeButton's scene and sets the scene to the home screen FXML.
     * The home screen FXML is loaded using FXMLLoader with a scene size of 1000x1000.
     *
     * @throws IOException if an error occurs during the loading of the home screen FXML.
     */
    @FXML
    protected void handleBackToHome() throws IOException {
        Stage stage = (Stage) backToHomeButton.getScene().getWindow(); // Use backToHomeButton instead of HomeButton
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/granny_gains_home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 1000); // Match the size used by others
        stage.setScene(scene);
    }


    /**
     * Handles the action to navigate to the Favorite Meals Page.
     * Retrieves the current stage from the backToHomeButton's scene and sets the scene to the Favorite Meals Page FXML.
     * The Favorite Meals Page FXML is loaded using FXMLLoader with a scene size of 1200x700.
     * Prints a message upon successful navigation or an error message if an IOException occurs.
     */
    @FXML
    public void handleMeals() {
        navigateToPage("/com/example/granny_gains_new/Fav_Meals_Page.fxml", "Favorite Meals Page");
    }


    /**
     * Handles the action to navigate to the Favorite Fitness Page.
     * Retrieves the current stage from the backToHomeButton's scene and sets the scene to the Favorite Fitness Page FXML.
     * The Favorite Fitness Page FXML is loaded using FXMLLoader with a scene size of 1200x700.
     * Prints a message upon successful navigation or an error message if an IOException occurs.
     */
    @FXML
    public void handleFitness() {
        navigateToPage("/com/example/granny_gains_new/Fav_Fitness_Page.fxml", "Favorite Fitness Page");
    }


    /**
     * Navigates to a specified page based on the provided FXML file path and page name.
     *
     * @param fxmlFilePath the file path to the FXML file of the page to navigate to
     * @param pageName the name of the page being navigated to
     */
    private void navigateToPage(String fxmlFilePath, String pageName) {
        try {
            Stage stage = (Stage) backToHomeButton.getScene().getWindow(); // Use backToHomeButton here as well
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFilePath));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
            stage.setScene(scene);
            stage.setMaximized(true);
            System.out.println("Navigated to " + pageName);
        } catch (IOException e) {
            System.err.println("Error loading " + pageName + ": " + e.getMessage());
        }
    }

    /**
     * Initialises the meal table view by setting up cell value factories, column widths, cell factories,
     * and loading saved meal entries to display in the table.
     */
    @FXML
    public void initialize() {
        mealNameColumn.setCellValueFactory(new PropertyValueFactory<>("mealName"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        servingsColumn.setCellValueFactory(new PropertyValueFactory<>("servings"));
        caloriesColumn.setCellValueFactory(new PropertyValueFactory<>("calories"));

        // Set preferred and minimum widths for each column
        mealNameColumn.setPrefWidth(200);
        mealNameColumn.setMinWidth(150);

        descriptionColumn.setPrefWidth(200);
        descriptionColumn.setMinWidth(150);

        servingsColumn.setPrefWidth(100);
        servingsColumn.setMinWidth(75);

        caloriesColumn.setPrefWidth(100);
        caloriesColumn.setMinWidth(75);

        // Disable automatic resizing for all columns
        mealTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Set cell factory for descriptionColumn
        descriptionColumn.setCellFactory(col -> new TableCell<MealEntry, String>() {
            private final VBox vbox = new VBox(); // Use VBox to hold the Label
            private final Label label = new Label();
            private final Tooltip tooltip = new Tooltip(); // Create Tooltip instance

            {
                label.setWrapText(true); // Enable text wrapping
                label.setMaxWidth(200); // Set the max width for wrapping
                label.setMinHeight(20); // Minimum height to maintain visibility
                label.setMaxHeight(80); // Limit the max height
                vbox.getChildren().add(label);
                vbox.setPrefWidth(200); // Set the preferred width of the VBox
                vbox.setMaxWidth(200); // Prevent the VBox from stretching
                setGraphic(vbox);
                Tooltip.install(label, tooltip); // Install the tooltip on the label
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    label.setText(null);
                    tooltip.hide(); // Hide tooltip if item is empty
                } else {
                    label.setText(item);
                    tooltip.setText(item); // Set tooltip text to full description
                }
            }
        });

        loadSavedMeals();
    }

    /**
     * Method to load saved meals from the MealTable in the database, avoiding duplicates based on meal names.
     * Retrieves recipe name, description, servings, and calories from the MealTable.
     * Creates MealEntry objects for unique meal names and populates the mealTableView with the saved meals.
     * Prints an error message if there is an issue with loading the meals from the database.
     */
    private void loadSavedMeals() {
        String query = "SELECT recipe_name, description, servings, calories FROM MealTable";

        ObservableList<MealEntry> savedMeals = FXCollections.observableArrayList();
        Set<String> uniqueMealNames = new HashSet<>(); // Set to track unique meal names

        try (Connection conn = DatabaseConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String recipeName = rs.getString("recipe_name");
                String description = rs.getString("description");
                int servings = rs.getInt("servings");
                int calories = rs.getInt("calories");

                // Check if the meal name is unique before adding
                if (!uniqueMealNames.contains(recipeName)) {
                    uniqueMealNames.add(recipeName); // Add to set to track uniqueness
                    savedMeals.add(new MealEntry(recipeName, description, servings, calories));
                }
            }

            mealTableView.setItems(savedMeals);
        } catch (SQLException e) {
            System.err.println("Error loading saved meals: " + e.getMessage());
        }
    }


    /**
     * Represents a single entry in a meal plan, containing details such as meal name, description, servings, and calories.
     */
    public static class MealEntry {
        private final String mealName;
        private final String description;
        private final int servings;
        private final int calories;

        public MealEntry(String mealName, String description, int servings, int calories) {
            this.mealName = mealName;
            this.description = description;
            this.servings = servings;
            this.calories = calories;
        }

        public String getMealName() {
            return mealName;
        }

        public String getDescription() {
            return description;
        }

        public int getServings() {
            return servings;
        }

        public int getCalories() {
            return calories;
        }
    }
}
