package com.example.granny_gains_new.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;

public class BMICalculatorController {

    @FXML
    private TextField tfAge;

    @FXML
    private ToggleGroup gender;

    @FXML
    private TextField tfHeight;

    @FXML
    private TextField tfWeight;

    @FXML
    private Label lblBMI;

    @FXML
    private Button btnCalculate;

    @FXML
    private Button btnSave;

    @FXML
    protected void initialize() {
        btnCalculate.setOnAction(event -> calculateBMI());
        btnSave.setOnAction(event -> saveProfile());
    }

    private void calculateBMI() {
        try {
            int age = Integer.parseInt(tfAge.getText());
            double height = Double.parseDouble(tfHeight.getText()) / 100; // convert cm to m
            double weight = Double.parseDouble(tfWeight.getText());

            double bmi = weight / (height * height);
            lblBMI.setText(String.format("Your BMI is: %.1f", bmi));
        } catch (NumberFormatException e) {
            lblBMI.setText("Please enter valid numbers for age, height, and weight.");
        }
    }

    private void saveProfile() {
        // Here you would typically save the profile data to your database
        System.out.println("Profile saved successfully!");

        // Navigate to the home page
        try {
            Stage stage = (Stage) btnSave.getScene().getWindow();
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sign_in_page.fxml"));
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/granny_gains_new/sign_in_page.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 650);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
