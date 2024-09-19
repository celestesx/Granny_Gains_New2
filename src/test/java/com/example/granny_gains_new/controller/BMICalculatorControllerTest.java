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

class BMICalculatorControllerTest {

    private BMICalculatorController bmiController;
    private Connection connection;

    private TextField heightField;
    private TextField weightField;
    private Label bmiLabel;
    private DatePicker dpDateOfBirth;
    private ToggleGroup genderGroup;


    @BeforeAll
    static void initToolkit() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(() -> {
            latch.countDown();
        });
        latch.await();
    }

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
     * Test case for a correct BMI calculation
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
     * system shows an error when inputs are invalid.
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
