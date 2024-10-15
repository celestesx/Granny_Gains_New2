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

    // Method to go back to the Home page
    @FXML
    protected void handleBackToHome() throws IOException {
        Stage stage = (Stage) backToHomeButton.getScene().getWindow(); // Use backToHomeButton instead of HomeButton
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/granny_gains_home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 1000); // Match the size used by others
        stage.setScene(scene);
    }

    // Method to display favorited Meals
    @FXML
    public void handleMeals() {
        navigateToPage("/com/example/granny_gains_new/Fav_Meals_Page.fxml", "Favorite Meals Page");
    }

    // Method to display favorited Fitness items
    @FXML
    public void handleFitness() {
        navigateToPage("/com/example/granny_gains_new/Fav_Fitness_Page.fxml", "Favorite Fitness Page");
    }

    // Generic method to navigate between pages
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
