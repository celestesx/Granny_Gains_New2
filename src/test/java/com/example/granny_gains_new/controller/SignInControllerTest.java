package com.example.granny_gains_new.controller;

import com.example.granny_gains_new.database.DatabaseConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class represents the test suite for the SignInController class. It contains test methods
 * to validate the functionality of the SignInController class for credential validation scenarios.
 */
class SignInControllerTest {

    private SignInController signInController;
    private Connection connection;

    /**
     * This method is executed before each test. It sets up the test environment
     * by initializing the controller, establishing a database connection,
     * and creating the necessary table and test data if it doesn't exist.
     */
    @BeforeEach
    void setUp() throws SQLException {
        // Initialize the SignInController
        signInController = new SignInController();

        // Get a connection to the database
        connection = DatabaseConnection.getInstance();

        // Create the User table if it doesn't already exist
        try (PreparedStatement stmt = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS User (" +
                        "email TEXT PRIMARY KEY, " +
                        "password TEXT NOT NULL, " +
                        "first_name TEXT, " +
                        "last_name TEXT)")) {
            stmt.executeUpdate();
        }

        // Check if the test user already exists in the database
        try (PreparedStatement checkStmt = connection.prepareStatement(
                "SELECT COUNT(*) FROM User WHERE email = ?")) {
            checkStmt.setString(1, "testuser@example.com");
            ResultSet rs = checkStmt.executeQuery();
            // If the test user doesn't exist, insert it
            if (rs.next() && rs.getInt(1) == 0) {
                try (PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO User (email, password, first_name, last_name) VALUES (?, ?, ?, ?)")) {
                    stmt.setString(1, "testuser@example.com");
                    stmt.setString(2, "password123");
                    stmt.setString(3, "Test");
                    stmt.setString(4, "User");
                    stmt.executeUpdate();
                }
            }
        }
    }

    /**
     * This method is executed after each test. It ensures that any test data created
     * during the tests is cleaned up, preventing data contamination between test cases.
     */
    @AfterEach
    void tearDown() throws SQLException {
        // If the connection is closed, reopen it
        if (connection == null || connection.isClosed()) {
            connection = DatabaseConnection.getInstance();
        }

        // Delete the test user from the database to clean up
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM User WHERE email = ?")) {
            stmt.setString(1, "testuser@example.com");
            stmt.executeUpdate();
        }
    }

    /**
     * Test case for successful validation of credentials.
     * Verifies that the system returns true for valid email and password.
     */
    @Test
    void testValidateCredentialsSuccess() {
        // Arrange: Valid email and password
        String email = "testuser@example.com";
        String password = "password123";

        // Act & Assert: Validate credentials should return true for correct credentials
        assertTrue(signInController.validateCredentials(email, password),
                "Valid credentials should return true.");
    }

    /**
     * Test case for invalid password validation.
     * Verifies that the system returns false when an incorrect password is provided.
     */
    @Test
    void testValidateCredentialsInvalidPassword() {
        // Arrange: Valid email but incorrect password
        String email = "testuser@example.com";
        String password = "wrongpassword";

        // Act & Assert: Validate credentials should return false for incorrect password
        assertFalse(signInController.validateCredentials(email, password),
                "Invalid password should return false.");
    }

    /**
     * Test case for user not found.
     * Verifies that the system returns false when the user is not present in the database.
     */
    @Test
    void testValidateCredentialsUserNotFound() {
        // Arrange: Non-existent email
        String email = "nonexistent@example.com";
        String password = "password123";

        // Act & Assert: Validate credentials should return false for a non-existent user
        assertFalse(signInController.validateCredentials(email, password),
                "Non-existent user should return false.");
    }

    /**
     * Test case for empty email or password.
     * Verifies that the system returns false when email or password is missing.
     */
    @Test
    void testValidateCredentialsEmptyFields() {
        // Arrange: Empty email and valid password
        String emptyEmail = "";
        String password = "password123";

        // Act & Assert: Validate credentials should return false for empty email
        assertFalse(signInController.validateCredentials(emptyEmail, password),
                "Empty email should return false.");

        // Arrange: Valid email and empty password
        String email = "testuser@example.com";
        String emptyPassword = "";

        // Act & Assert: Validate credentials should return false for empty password
        assertFalse(signInController.validateCredentials(email, emptyPassword),
                "Empty password should return false.");
    }

    /**
     * Test case for basic SQL injection.
     * Verifies that SQL injection attempts are handled and do not bypass security.
     */
    @Test
    void testValidateCredentialsSQLInjection() {
        // Arrange: Attempt to bypass validation with SQL injection
        String email = "testuser@example.com' OR '1' = '1";
        String password = "password123";

        // Act & Assert: Validate credentials should return false for SQL injection attempt
        assertFalse(signInController.validateCredentials(email, password),
                "SQL Injection should not bypass validation.");
    }

    /**
     * Test case for long input validation.
     * Verifies that the system can handle unusually long email and password inputs.
     */
    @Test
    void testValidateCredentialsLongInput() {
        // Arrange: Unusually long email and password
        String longEmail = "a".repeat(300) + "@example.com";  // 300-character email
        String longPassword = "b".repeat(300);                // 300-character password

        // Act & Assert: Validate credentials should return false for long inputs
        assertFalse(signInController.validateCredentials(longEmail, longPassword),
                "Unusually long input should not be accepted.");
    }
}

