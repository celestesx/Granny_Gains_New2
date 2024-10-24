package com.example.granny_gains_new.model;

/**
 * Class representing a User with basic information such as email, password, first name, last name, and phone number.
 */
public class User {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;

    /**
     * Constructs a new User with the provided details.
     *
     * @param email The email address of the user.
     * @param password The password for the user account.
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @param phone The phone number of the user.
     */
    public User(String email, String password, String firstName, String lastName, String phone) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

