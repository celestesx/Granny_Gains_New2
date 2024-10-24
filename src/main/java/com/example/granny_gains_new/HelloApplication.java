package com.example.granny_gains_new;

import com.example.granny_gains_new.database.DatabaseConnection;
import com.example.granny_gains_new.database.RecipeDBHandler;
import com.example.granny_gains_new.database.RecipeLoader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;

/**
 * Represents the main application class for Granny Gains, a recipe management system.
 * Extends Application class to initialize the JavaFX application.
 */
public class HelloApplication extends Application {

    public static final String TITLE = "Granny Gains";
    public static final int WIDTH = 680;
    public static final int HEIGHT = 360;

    /**
     * Loads the Granny Gains application with the specified Stage.
     *
     * @param stage the primary stage for the application
     * @throws IOException if an error occurs during loading
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("granny_gains.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Initializes the Granny Gains recipe management system by loading recipes from a CSV file
     * and launching the JavaFX application.
     *
     * @param args The command line arguments passed to the main method.
     */
    public static void main(String[] args) {

        Connection connection = DatabaseConnection.getInstance();


        RecipeDBHandler dbHandler = new RecipeDBHandler();

        // Initialize the recipe loader
        RecipeLoader loader = new RecipeLoader();


        String csvFilePath = "src/main/resources/com/example/granny_gains_new/database/meals.csv";


        loader.loadRecipesFromCSV(csvFilePath, dbHandler);
        //Launch JavaFX application

        launch();

    }
}
