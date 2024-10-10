package com.example.granny_gains_new.util;

import javafx.scene.control.TextField;

public class JavaFXTextField implements UITextField {
    private final TextField textField;

    public JavaFXTextField(TextField textField) {
        this.textField = textField;
    }

    @Override
    public String getText() {
        return textField.getText();
    }

    @Override
    public void setText(String text) {
        textField.setText(text);
    }
}

