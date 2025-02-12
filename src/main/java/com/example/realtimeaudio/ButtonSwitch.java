package com.example.realtimeaudio;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ButtonSwitch extends SaveableObject {

    BooleanProperty state = new SimpleBooleanProperty();
    private final Pane view;
    private final Button button;
    private final String id;
    private ImageView imageView;
    private Image imageOn;
    private Image imageOff;

    public ButtonSwitch(String title, boolean initialState, String id) {
        state.set(initialState);
        this.id = id;

        // Set up images
        imageOn = new Image("/switch_on.png");
        imageOff = new Image("/switch_off.png");

        imageView = new ImageView(imageOff);
        imageView.setFitHeight(50);
        imageView.setFitWidth(100);

        button = new Button();
        button.setStyle("-fx-background-color: transparent;");
        button.setGraphic(imageView);

        button.setOnMouseClicked(event -> {
            state.set(!state.get());
            if (state.get()) {
                imageView.setImage(imageOn);  // Change to image2
            } else {
                imageView.setImage(imageOff);  // Change to image1
            }
        });

        Text text = new Text(title);
        text.setMouseTransparent(true);
        text.getStyleClass().add("button-switch-text");

        view = new VBox();
        view.getChildren().addAll(text, button);

        view.getStyleClass().add("button-switch");
        view.setId(id);
    }

    public Pane getView() {
        return view;
    }

    public BooleanProperty getState() {
        return state;
    }

    public void SetOnMouseClicked(EventHandler<MouseEvent> eventHandler) {
        button.setOnMouseClicked(eventHandler);
    }

    public void setAsDiode(String on_path, String off_path) {
        button.setOnMouseClicked(null);
        imageOn = new Image(on_path);
        imageOff = new Image(off_path);
        imageView.setImage(imageOff);
    }

    public void setDiodeStatus(Double value) {
        state.set(value.equals(1.0));
        if (state.get()) {
            imageView.setImage(imageOn);
        }
        else {
            imageView.setImage(imageOff);
        }
    }

    public void setSwitchImages() {
        imageView.setImage(null);
        button.setOnMouseClicked(event -> {
            state.set(!state.get());
        });
    }

    @Override
    public Double getValue() {
        if (state.get()) {
            return 1.0;
        }
        return 0.0;
    }

    @Override
    public void setValue(Double value) {
        state.set(value.equals(1.0));
    }

    @Override
    public String getId() {
        return id;
    }
}
