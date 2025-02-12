package com.example.realtimeaudio;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class ToggleSwitch extends Button {
    private boolean isOn;

    private final Image onImage;
    private final Image offImage;

    private final ImageView imageView;

    public ToggleSwitch(String onImagePath, String offImagePath, double width, double height) {
        // Load the images
        this.onImage = new Image(Objects.requireNonNull(getClass().getResource(onImagePath)).toExternalForm());
        this.offImage = new Image(Objects.requireNonNull(getClass().getResource(offImagePath)).toExternalForm());

        // Initialize the ImageView and set its size
        this.imageView = new ImageView(offImage);
        imageView.setFitWidth(width);  // Set the width of the image
        imageView.setFitHeight(height); // Set the height of the image

        // Set the ImageView as the button's graphic
        setGraphic(imageView);

        // Set button size
        setPrefWidth(width);
        setPrefHeight(height);

        // Style the button
        setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        // Initialize the toggle state
        this.isOn = false;

        // Add click behavior
        setOnAction(event -> toggle());
    }

    // Toggle the state of the switch
    private void toggle() {
        isOn = !isOn;

        // Update the graphic based on the state
        if (isOn) {
            imageView.setImage(onImage);
        } else {
            imageView.setImage(offImage);
        }
    }

    // Getter for the state
    public boolean isOn() {
        return isOn;
    }

    // Setter for the state
    public void setOn(boolean isOn) {
        this.isOn = isOn;

        // Update the graphic based on the state
        if (isOn) {
            imageView.setImage(onImage);
        } else {
            imageView.setImage(offImage);
        }
    }
}
