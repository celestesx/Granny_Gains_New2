package com.example.granny_gains_new.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseConnectionTest {

    private Connection connection;

    @BeforeEach
    public void setUp() throws SQLException {
        connection = DatabaseConnection.getInstance();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        DatabaseConnection.closeConnection();
        connection = null;
    }

    @Test
    public void testConnectionEstablished() throws SQLException {
        assertNotNull(connection, "Connection should not be null");
        assertFalse(connection.isClosed(), "Connection should be open");
    }

    @Test
    public void testTablesCreated() throws SQLException {
        String checkTableSql = "SELECT name FROM sqlite_master WHERE type='table' AND name='User';";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(checkTableSql)) {

            assertTrue(rs.next(), "User table should exist in the database");
        }
    }

    @Test
    public void testReopenConnection() throws SQLException {
        DatabaseConnection.closeConnection();
        assertTrue(connection.isClosed(), "Connection should be closed");

        connection = DatabaseConnection.getInstance();
        assertFalse(connection.isClosed(), "Connection should be reopened");
    }

    @Test
    public void testSingletonConnection() throws SQLException {
        Connection secondConnection = DatabaseConnection.getInstance();
        assertSame(connection, secondConnection, "The connection instance should be the same (singleton)");
    }

    @Test
    public void testConnectionClose() throws SQLException {
        DatabaseConnection.closeConnection();
        assertTrue(connection.isClosed(), "Connection should be closed");
    }

    @Test
    public void testConnectionReopensAfterClose() throws SQLException {
        DatabaseConnection.closeConnection();
        assertTrue(connection.isClosed(), "Connection should be closed");

        Connection newConnection = DatabaseConnection.getInstance();
        assertNotNull(newConnection, "New connection should be opened");
        assertFalse(newConnection.isClosed(), "New connection should be open");
    }
}
