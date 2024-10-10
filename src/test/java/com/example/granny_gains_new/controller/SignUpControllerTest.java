package com.example.granny_gains_new.controller;

import com.example.granny_gains_new.database.DatabaseConnection;
import com.example.granny_gains_new.model.User;
import com.example.granny_gains_new.util.ButtonHandler;
import com.example.granny_gains_new.util.UITextField;
import com.example.granny_gains_new.util.UITextLabel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignUpControllerTest {

    @Mock
    private UITextField tfFirstName, tfLastName, tfEmail, tfPhone, tfPassword;

    @Mock
    private UITextLabel lblincorrectdetails;

    @Mock
    private ButtonHandler buttonHandler;

    private SignUpController signUpController;

    @BeforeEach
    void setUp() {
        signUpController = new SignUpController(buttonHandler);
        signUpController.setTextFields(tfFirstName, tfLastName, tfEmail, tfPhone, tfPassword);
        signUpController.setLabel(lblincorrectdetails);
    }

    @Test
    void testSignUpWithValidDetails() throws SQLException {
        // Arrange
        when(tfFirstName.getText()).thenReturn("John");
        when(tfLastName.getText()).thenReturn("Doe");
        when(tfEmail.getText()).thenReturn("john.doe@example.com");
        when(tfPhone.getText()).thenReturn("1234567890");
        when(tfPassword.getText()).thenReturn("password123");

        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            Connection mockConnection = mock(Connection.class);
            PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
            ResultSet mockResultSet = mock(ResultSet.class);

            mockedStatic.when(DatabaseConnection::getInstance).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false); // Email is unique

            // Act
            SignUpController spyController = spy(signUpController);
            doNothing().when(spyController).navigateToSecurityQuestion(any(User.class));

            spyController.handleSignUp();

            // Assert
            verify(spyController).navigateToSecurityQuestion(any(User.class));
            verify(lblincorrectdetails, never()).setText(anyString());
        }
    }

    @Test
    void testSignUpWithInvalidEmail() {
        // Arrange
        when(tfFirstName.getText()).thenReturn("John");
        when(tfLastName.getText()).thenReturn("Doe");
        when(tfEmail.getText()).thenReturn("invalidemail");
        when(tfPhone.getText()).thenReturn("1234567890");
        when(tfPassword.getText()).thenReturn("password123");

        // Act
        signUpController.handleSignUp();

        // Assert
        verify(lblincorrectdetails).setText("Invalid Email.");
    }

    @Test
    void testSignUpWithExistingEmail() throws SQLException {
        // Arrange
        when(tfFirstName.getText()).thenReturn("John");
        when(tfLastName.getText()).thenReturn("Doe");
        when(tfEmail.getText()).thenReturn("existing@example.com");
        when(tfPhone.getText()).thenReturn("1234567890");
        when(tfPassword.getText()).thenReturn("password123");

        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            Connection mockConnection = mock(Connection.class);
            PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
            ResultSet mockResultSet = mock(ResultSet.class);

            mockedStatic.when(DatabaseConnection::getInstance).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true); // Email already exists

            // Act
            signUpController.handleSignUp();

            // Assert
            verify(lblincorrectdetails).setText("Email already in use.");
        }
    }

    @Test
    void testSignUpWithShortPassword() {
        when(tfFirstName.getText()).thenReturn("John");
        when(tfLastName.getText()).thenReturn("Doe");
        when(tfEmail.getText()).thenReturn("john.doe@example.com");
        when(tfPhone.getText()).thenReturn("1234567890");
        when(tfPassword.getText()).thenReturn("short");

        signUpController.handleSignUp();

        verify(lblincorrectdetails).setText("Invalid Password. Password must have at least 8 characters\nand contain at least 1 number.");
    }

    @Test
    void testSignUpWithInvalidPhoneNumber() {
        when(tfFirstName.getText()).thenReturn("John");
        when(tfLastName.getText()).thenReturn("Doe");
        when(tfEmail.getText()).thenReturn("john.doe@example.com");
        when(tfPhone.getText()).thenReturn("123abc");
        when(tfPassword.getText()).thenReturn("password123");

        signUpController.handleSignUp();

        verify(lblincorrectdetails).setText("Invalid Phone Number.");
    }

    @Test
    void testSignUpWithInvalidName() {
        when(tfFirstName.getText()).thenReturn("123");
        when(tfLastName.getText()).thenReturn("456");
        when(tfEmail.getText()).thenReturn("john.doe@example.com");
        when(tfPhone.getText()).thenReturn("1234567890");
        when(tfPassword.getText()).thenReturn("password123");

        signUpController.handleSignUp();

        verify(lblincorrectdetails).setText("Invalid Name.");
    }

    @Test
    void testSignUpWithEmptyFields() {
        when(tfFirstName.getText()).thenReturn("");
        when(tfLastName.getText()).thenReturn("");
        when(tfEmail.getText()).thenReturn("");
        when(tfPhone.getText()).thenReturn("");
        when(tfPassword.getText()).thenReturn("");

        signUpController.handleSignUp();

        verify(lblincorrectdetails).setText("Please fill out all required fields.");
    }
}