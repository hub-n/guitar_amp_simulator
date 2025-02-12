package com.example.realtimeaudio;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class RotatableCircle extends SaveableObject {

    // Value that updates with rotation
    private final DoubleProperty rotationValue = new SimpleDoubleProperty(0);
    private double savedX = 0;
    private double savedY = 0;
    private final StackPane view;
    private final int maxRotate;
    private final int spacing;
    private final DoubleProperty percentValue = new SimpleDoubleProperty(0);
    private final DoubleProperty normalizedValue = new SimpleDoubleProperty(0);
    private final double minValue;
    private final double maxValue;
    private final String id;
    private final Region circle;

    public RotatableCircle(double radius, int spacing, String id, double initialValue, double minValue, double maxValue) {
        boolean topPointer = true;
        if (spacing < 0){
            spacing = Math.abs(spacing);
            topPointer = false;
        }
        this.maxRotate = 360-2*spacing;
        this.spacing = spacing;
        this.id = id;
        this.minValue = minValue;
        this.maxValue = maxValue;

        // Create a circle
        circle = new Region();
        circle.getStyleClass().add("rotatable-circle");
        circle.setMinSize(radius*2, radius*2); // Set size to make it circular
        circle.setMaxSize(radius*2, radius*2);

        // Create label
//        Text label = new Text(id);
//        label.setMouseTransparent(true);
//        label.getStyleClass().add("rotatable-circle-label");

        // Set up view
        view = new StackPane();
        view.setAlignment(Pos.CENTER);
        view.getChildren().addAll(circle);

        // Rotate so 0 is on left
        if (!topPointer) {
            circle.setScaleY(-1);
        }

        // Rotate handling
        circle.setOnMouseDragged(this::handleRotate);
        circle.setOnMousePressed(event -> {
            savedX = event.getSceneX();
            savedY = event.getSceneY();
        });

        // Set spacing - convert initial value to percent
        circle.setRotate(spacing);

        // Set initial value - convert to degrees
        updateRotation(((initialValue - minValue) / (maxValue - minValue)) * maxRotate);

        // Set style class
        view.getStyleClass().add("rotatable-circle-view");
        view.setId(id);
    }

    private void handleRotate(MouseEvent event) {
        double move = event.getSceneX() - savedX;
        move += -event.getSceneY() + savedY;
        move *= 0.5;
        move += rotationValue.get();
        move = Math.max(0, Math.min(maxRotate, move));
        savedX = event.getSceneX();
        savedY = event.getSceneY();

        updateRotation(move);
    }

    /**
     *
     * @param rotation This needs to be in degrees
     */
    private void updateRotation(double rotation) {
        rotationValue.set(rotation);
        circle.setRotate(rotation+spacing);
        percentValue.set(100*rotation/maxRotate);
        normalizedValue.set(minValue + (percentValue.getValue() / 100) * (maxValue - minValue));
    }

    public DoubleProperty getRotationValue() {
        return rotationValue;
    }

    public DoubleProperty getPercentValue() {
        return percentValue;
    }

    public DoubleProperty getNormalizedValue() {
        return normalizedValue;
    }

    public StackPane getView() {
        return view;
    }

    public Double getValue() {
        return rotationValue.getValue();
    }

    public void setValue(Double value) {
        updateRotation(value);
    }

    public String getId() {
        return this.id;
    }
}
