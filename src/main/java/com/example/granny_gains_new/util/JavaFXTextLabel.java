package com.example.granny_gains_new.util;

import javafx.scene.control.Label;

public class JavaFXTextLabel implements UITextLabel {
    private final Label label;

    public JavaFXTextLabel(Label label) {
        this.label = label;
    }

    @Override
    public void setText(String text) {
        label.setText(text);
    }
}


