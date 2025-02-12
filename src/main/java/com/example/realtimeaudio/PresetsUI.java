package com.example.realtimeaudio;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class PresetsUI {
    Button saveButton;
    Button saveAsButton;
    DropdownMenu presetDropdown;
    private final PresetsManager presetsManager;

    public PresetsUI(PresetsManager presetsManager) {
        this.presetsManager = presetsManager;
        presetDropdown = new DropdownMenu(presetsManager.getPresetNames().toArray(new String[0]), "Default");
        saveButton = new Button("Save");
        saveAsButton = new Button("Save as");

        saveButton.setDisable(true);

        saveButton.setOnAction(this::handleSave);
        saveAsButton.setOnAction(this::handleSaveAs);
        presetDropdown.SetOnAction(this::handleLoad);
    }

    private void handleLoad(ActionEvent event) {
        String preset = presetDropdown.getValue();
        presetsManager.loadPreset(preset);

        saveButton.setDisable(preset.equals("Default"));
    }

    private void handleSave(ActionEvent event) {
        String preset = presetDropdown.getValue();
        presetsManager.savePreset(preset);
    }

    private void handleSaveAs(ActionEvent event) {
        List<String> presetNames = presetsManager.getPresetNames();

        // Create a new stage (popup)
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block input to other windows
        popupStage.setTitle("Enter a name for the preset:");

        // Create UI components for the popup
        Label promptLabel = new Label("Enter a name for the preset:");
        Label errorLabel = new Label("");
        TextField inputField = new TextField();
        Button okButton = new Button("OK");

        // Handle OK button click
        okButton.setOnAction(e -> {
            String input = inputField.getText();
            errorLabel.textProperty().set("");
            if (input.isEmpty()) {
                errorLabel.textProperty().set("Name cannot be empty!");
                return;
            } else if (input.equals("Default")) {
                errorLabel.textProperty().set("Cannot overwrite default preset!");
                return;
            } else if (presetNames.contains(input)) {
                handleOverwrite(input, popupStage);
                return;
            }
            else {
                presetsManager.savePreset(input);
            }
            addPreset(input);
            popupStage.close(); // Close the popup window
        });

        // Arrange components in a layout
        VBox popupLayout = new VBox(10, promptLabel, inputField, errorLabel, okButton);
        popupLayout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        // Create and set the popup scene
        Scene popupScene = new Scene(popupLayout, 300, 150);
        popupStage.setScene(popupScene);

        // Show the popup window
        popupStage.showAndWait();
    }

    private void handleOverwrite(String presetName, Stage firstStage) {
        // Create a new stage for the overwrite confirmation popup
        Stage confirmationStage = new Stage();
        confirmationStage.initModality(Modality.APPLICATION_MODAL); // Block input to other windows
        confirmationStage.setTitle("Confirm Overwrite");

        // Create UI components for the confirmation popup
        Label confirmationLabel = new Label("Preset \"" + presetName + "\" already exists. Do you want to overwrite it?");
        Button yesButton = new Button("Yes");
        Button noButton = new Button("No");

        // Handle Yes button click (proceed with overwrite)
        yesButton.setOnAction(event -> {
            presetsManager.savePreset(presetName);
            presetDropdown.getDropdown().setValue(presetName);
            confirmationStage.close(); // Close the confirmation popup
            firstStage.close(); // Close previous popup
        });

        // Handle No button click (cancel overwrite)
        noButton.setOnAction(event -> {
            confirmationStage.close(); // Close the confirmation popup without doing anything
        });

        // Arrange components in a layout
        VBox confirmationLayout = new VBox(10, confirmationLabel, yesButton, noButton);
        confirmationLayout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        // Create and set the confirmation scene
        Scene confirmationScene = new Scene(confirmationLayout, 300, 150);
        confirmationStage.setScene(confirmationScene);

        // Show the confirmation popup
        confirmationStage.showAndWait();
    }


private void addPreset(String input) {
        presetDropdown.getDropdown().getItems().add(input);
        presetDropdown.getDropdown().setValue(input);
    }

}
