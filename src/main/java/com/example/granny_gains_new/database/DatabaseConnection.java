package com.example.granny_gains_new.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class is responsible for managing the database connection using the Singleton pattern.
 * It ensures that only one instance of the database connection is created and used across the application.
 * The database schema is executed on the first connection to create the required tables.
 */
public class DatabaseConnection {

    // Singleton instance of the Connection object.
    // This ensures that only one connection to the database is created.
    private static Connection instance = null;

    /**
     * SQL schema for creating the necessary tables in the database.
     * The CREATE TABLE IF NOT EXISTS statement ensures that the tables are only created if they do not exist.
     */
    private static final String CREATE_TABLES_SQL =
            // User Table
            "CREATE TABLE IF NOT EXISTS User (" +
            " email TEXT PRIMARY KEY, " +               // Primary key (unique identifier) for the user (email)
            " password TEXT NOT NULL, " +               // User's password
            " secret_answer TEXT, " +                   // Secret answer for security questions (missing comma fixed)
            " date_of_birth DATE, " +                   // User's date of birth
            " gender TEXT, " +                          // User's gender
            " height REAL, " +                          // User's height
            " bmi REAL " +                              // User's body mass index
            "); " +

            // Workout Table
            "CREATE TABLE IF NOT EXISTS Workout (" +
            " workout_id INTEGER PRIMARY KEY AUTOINCREMENT, " +  // Unique ID for each workout (auto-incremented)
            " user_id TEXT, " +                                   // Foreign key referencing the User table (email)
            " date DATE, " +                                      // Date of the workout
            " workout_type TEXT, " +                              // Type of workout (e.g., cardio, strength)
            " duration INTEGER, " +                               // Duration of the workout in minutes
            " calories_burned INTEGER, " +                        // Calories burned during the workout
            " FOREIGN KEY (user_id) REFERENCES User(email) " +    // Foreign key to associate the workout with a user
            "); " +

            // Program Table
            "CREATE TABLE IF NOT EXISTS Program (" +
            " program_id INTEGER PRIMARY KEY AUTOINCREMENT, " +   // Unique ID for each program (auto-incremented)
            " name TEXT, " +                                      // Program name
            " description TEXT, " +                               // Description of the program
            " program_video_url TEXT, " +                         // URL for instructional video related to the program
            " difficulty_level TEXT, " +                          // Difficulty level of the program (e.g., beginner, advanced)
            " program_type TEXT, " +                              // Type of program (e.g., strength, cardio)
            " instructions TEXT " +                               // Instructions for the program
            "); " +

            // User Program Session Table
            "CREATE TABLE IF NOT EXISTS User_program_session (" +
            " user_program_id INTEGER PRIMARY KEY AUTOINCREMENT, " + // Unique ID for each user program session
            " user_id TEXT, " +                                       // Foreign key referencing the User table (email)
            " program_id INTEGER, " +                                 // Foreign key referencing the Program table
            " start_date DATE, " +                                    // Start date of the program session
            " end_date DATE, " +                                      // End date of the program session
            " status TEXT, " +                                        // Status of the session (e.g., active, completed)
            " FOREIGN KEY (user_id) REFERENCES User(email), " +       // Foreign key to associate session with a user
            " FOREIGN KEY (program_id) REFERENCES Program(program_id) " + // Foreign key to associate session with a program
            "); " +

            // Meal Plan Table
            "CREATE TABLE IF NOT EXISTS Meal_plan (" +
            " meal_plan_id INTEGER PRIMARY KEY AUTOINCREMENT, " +  // Unique ID for each meal plan
            " user_id TEXT, " +                                   // Foreign key referencing the User table (email)
            " date DATE, " +                                      // Date the meal plan was created
            " FOREIGN KEY (user_id) REFERENCES User(email) " +    // Foreign key to associate meal plan with a user
            "); " +

            // Recipe Table
            "CREATE TABLE IF NOT EXISTS Recipe (" +
            " recipe_id INTEGER PRIMARY KEY AUTOINCREMENT, " +    // Unique ID for each recipe
            " name TEXT, " +                                      // Name of the recipe
            " recipe_image_url TEXT, " +                          // URL for the recipe image
            " ingredients TEXT, " +                               // List of ingredients for the recipe
            " instructions TEXT, " +                              // Instructions to prepare the recipe
            " calories INTEGER, " +                               // Number of calories in the recipe
            " recipe_type TEXT " +                                // Type of recipe (e.g., breakfast, lunch, dinner)
            "); " +

            // Meal Plan Recipe Table (for many-to-many relationship between Meal Plans and Recipes)
            "CREATE TABLE IF NOT EXISTS Meal_plan_recipe (" +
            " meal_plan_id INTEGER, " +                           // Foreign key referencing the Meal Plan table
            " recipe_id INTEGER, " +                              // Foreign key referencing the Recipe table
            " FOREIGN KEY (meal_plan_id) REFERENCES Meal_plan(meal_plan_id), " +  // Meal plan reference
            " FOREIGN KEY (recipe_id) REFERENCES Recipe(recipe_id) " +            // Recipe reference
            ");";

    // Private constructor to prevent instantiation
    private DatabaseConnection() { }

    /**
     * This method returns the singleton instance of the Connection.
     * It establishes a new connection if one does not already exist and creates the necessary tables.
     *
     * @return Connection instance - the singleton database connection.
     */
    public static synchronized Connection getInstance() {
        if (instance == null) {
            try {
                // Establish connection to SQLite database (create the file if it doesn't exist)
                String url = "jdbc:sqlite:database.db";  // Specify the path to your SQLite database file
                instance = DriverManager.getConnection(url);

                // Print success message
                System.out.println("Database connected successfully!");

                // Create tables by executing the SQL schema
                createTables(instance);

            } catch (SQLException sqlEx) {
                // Print error message if connection fails
                System.err.println("Failed to connect to the database: " + sqlEx.getMessage());
            }
        }
        return instance;
    }

    /**
     * This method executes the SQL schema to create the necessary tables in the database.
     * It is called after the connection to the database is established.
     *
     * @param connection The connection to the SQLite database.
     */
    private static void createTables(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            // Execute the SQL schema to create tables
            statement.executeUpdate(CREATE_TABLES_SQL);
            System.out.println("Database schema executed, tables created.");
        } catch (SQLException e) {
            // Print error message if table creation fails
            System.err.println("Failed to create tables: " + e.getMessage());
        }
    }
}
