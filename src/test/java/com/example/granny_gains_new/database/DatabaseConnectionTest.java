package com.example.granny_gains_new.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class DatabaseConnectionTest {

    private Connection connection;

    @BeforeEach
    public void setUp() {
        connection = DatabaseConnection.getInstance();
    }

    @AfterEach
    public void tearDown() {
        DatabaseConnection.closeConnection();
    }

    @Test
    public void testConnectionEstablished() {
        assertNotNull(connection, "Connection should not be null");
        try {
            assertFalse(connection.isClosed(), "Connection should be open");
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }

    @Test
    public void testTablesCreated() {
        String checkTableSql = "SELECT name FROM sqlite_master WHERE type='table' AND name='User';";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(checkTableSql)) {
            assertTrue(rs.next(), "User table should exist in the database");
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }

    @Test
    public void testReopenConnection() {
        DatabaseConnection.closeConnection();
        try {
            assertTrue(connection.isClosed(), "Connection should be closed");
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }

        connection = DatabaseConnection.getInstance();
        try {
            assertFalse(connection.isClosed(), "Connection should be reopened");
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }

    @Test
    public void testSingletonConnection() {
        Connection secondConnection = DatabaseConnection.getInstance();
        assertSame(connection, secondConnection, "The connection instance should be the same (singleton)");
    }

    @Test
    public void testConnectionClose() {
        DatabaseConnection.closeConnection();
        try {
            assertTrue(connection.isClosed(), "Connection should be closed");
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }

    @Test
    public void testConnectionReopensAfterClose() {
        DatabaseConnection.closeConnection();
        try {
            assertTrue(connection.isClosed(), "Connection should be closed");
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }

        Connection newConnection = DatabaseConnection.getInstance();
        assertNotNull(newConnection, "New connection should be opened");
        try {
            assertFalse(newConnection.isClosed(), "New connection should be open");
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }
}