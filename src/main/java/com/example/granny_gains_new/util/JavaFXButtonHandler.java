package com.example.granny_gains_new.util;

import javafx.scene.control.Button;

public class JavaFXButtonHandler implements ButtonHandler {
    private final Button button;

    public JavaFXButtonHandler(Button button) {
        this.button = button;
    }

    @Override
    public void handleButtonClick() {
        button.fire();
    }
}
