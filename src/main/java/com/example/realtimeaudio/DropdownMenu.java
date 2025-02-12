package com.example.realtimeaudio;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class DropdownMenu {

    private final Pane view;
    private final ComboBox<String> dropdown;

    public DropdownMenu(String[] options, String defaultOption) {
        // Observable list for dropdown options
        ObservableList<String> dropdownOptions = FXCollections.observableArrayList(options);

        // Create a ComboBox for the dropdown
        dropdown = new ComboBox<>(dropdownOptions);

        // Set default option
        if (defaultOption != null && dropdownOptions.contains(defaultOption)) {
            dropdown.setValue(defaultOption);
        }

        // Event listener for selection change
        dropdown.setOnAction(event -> {
            String selectedOption = dropdown.getValue();
            System.out.println("Selected: " + selectedOption);
        });

        // Layout: Text above ComboBox
        view = new VBox(10);
        view.getChildren().addAll(dropdown);

        // Set style class
        dropdown.getStyleClass().add("dropdown-menu");
    }

    public Pane getView() {
        return view;
    }

    public ComboBox<String> getDropdown() {
        return dropdown;
    }

    public String getValue() {
        return dropdown.getValue();
    }

    public void SetOnAction(javafx.event.EventHandler<javafx.event.ActionEvent> eventHandler) {
        dropdown.setOnAction(eventHandler);
    }
}
