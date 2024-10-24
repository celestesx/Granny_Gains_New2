package com.example.granny_gains_new.controller;

import com.example.granny_gains_new.database.DatabaseConnection;
import javafx.application.Platform;
import javafx.scene.control.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class is responsible for testing the functionality of the BMICalculatorController class.
 */
class BMICalculatorControllerTest {

    private BMICalculatorController bmiController;
    private Connection connection;

    private TextField heightField;
    private TextField weightField;
    private Label bmiLabel;
    private DatePicker dpDateOfBirth;
    private ToggleGroup genderGroup;


    /**
     * Initializes the JavaFX toolkit before running any tests.
     * This method starts the JavaFX platform in a separate thread to allow JavaFX operations.
     * It uses a CountDownLatch to wait for the JavaFX platform initialization before proceeding.
     *
     * @throws Exception if any errors occur during the initialization process.
     */
    @BeforeAll
    static void initToolkit() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(() -> {
            latch.countDown();
        });
        latch.await();
    }

    /**
     * Set up method executed before each test in BMICalculatorControllerTest.
     * Initializes the BMIController and UI components for testing.
     * Creates a database connection and prepares a User table in the database.
     *
     * @throws SQLException if an SQL exception occurs during setup
     */
    @BeforeEach
    void setUp() throws SQLException {

        bmiController = new BMICalculatorController();

        // Simulate the UI components
        heightField = new TextField();
        weightField = new TextField();
        bmiLabel = new Label();
        dpDateOfBirth = new DatePicker();
        genderGroup = new ToggleGroup();

        // Assign simulated fields to the controller
        bmiController.tfHeight = heightField;
        bmiController.tfWeight = weightField;
        bmiController.lblBMI = bmiLabel;
        bmiController.dpDateOfBirth = dpDateOfBirth;
        bmiController.gender = genderGroup;

        // Database
        connection = DatabaseConnection.getInstance();


        try (PreparedStatement stmt = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS User (" +
                        "email TEXT PRIMARY KEY, " +
                        "date_of_birth TEXT, " +
                        "gender TEXT, " +
                        "height REAL, " +
                        "weight REAL, " +
                        "bmi REAL)")) {
            stmt.executeUpdate();
        }
    }

    /**
     * Performs clean-up tasks after each test execution. This method is annotated with @AfterEach
     * to denote that it should be run after each test method in the test class.
     * It deletes a specific user record from the database with the email address "testuser@example.com"
     * using a prepared statement. After deletion, it closes the database connection.
     *
     * @throws SQLException if an SQL exception occurs during the clean-up process
     */
    @AfterEach
    void tearDown() throws SQLException {

        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM User WHERE email = ?")) {
            stmt.setString(1, "testuser@example.com");
            stmt.executeUpdate();
        }
        DatabaseConnection.closeConnection();
    }

    /**
     * This test method is used to verify the success scenario of calculating the Body Mass Index (BMI) based on user input.
     * It sets predefined values for height and weight fields, calls the calculateBMI() method from the BMIController, and checks
     * if the calculated BMI matches the expected value displayed on the BMI label.
     * If the input values are valid (numeric), the BMI is calculated and displayed correctly.
     * If the input values are invalid (non-numeric), an error message is displayed on the UI label.
     */
    @Test
    void testCalculateBMISuccess() {
        // Input for height and weight
        heightField.setText("180");
        weightField.setText("75");

        // Act: Call the method
        bmiController.calculateBMI();


        assertEquals("Your BMI is: 23.1", bmiLabel.getText());
    }

    /**
     * This test method is designed to validate the behavior of the calculateBMI() method when provided with invalid input
     * for the height and weight fields. The test sets non-numeric values in the heightField and weightField of the UI,
     * then calls the calculateBMI() method from bmiController. The expected outcome is to display an error message
     * "Please enter valid numbers for height and weight." on the bmiLabel. The test uses JUnit's assertEquals assertion.
     */
    @Test
    void testCalculateBMIInvalidInput() {
        // invalid input for height and weight
        heightField.setText("abc");
        weightField.setText("xyz");

        // Act: Call the method to calculate BMI
        bmiController.calculateBMI();


        assertEquals("Please enter valid numbers for height and weight.", bmiLabel.getText());
    }


}
