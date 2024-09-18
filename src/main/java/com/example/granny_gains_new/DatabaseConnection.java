package com.example.granny_gains_new;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Singleton instance
    private static Connection instance = null;

    // Private constructor to prevent instantiation
    private DatabaseConnection() { }

    // Thread-safe method to get the singleton instance of the connection
    public static synchronized Connection getInstance() {
        if (instance == null) {
            try {
                String url = "jdbc:sqlite:database.db";
                instance = DriverManager.getConnection(url);
                System.out.println("Database connected successfully!");
            } catch (SQLException sqlEx) {
                System.err.println("Failed to connect to the database: " + sqlEx.getMessage());
            }
        }
        return instance;
    }
}

