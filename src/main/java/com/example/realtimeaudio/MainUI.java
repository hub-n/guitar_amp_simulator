package com.example.realtimeaudio;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;
import javafx.geometry.Pos;
import javafx.scene.image.Image;

import javax.sound.sampled.Mixer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainUI {

    private final Pane root = new BorderPane();;
    private final List<SaveableObject> savable = new ArrayList<>(); // Add to this everything that presets will save
    private ButtonSwitch ampPowerSwitch;
    private ButtonSwitch ampBypassSwitch;
    private ButtonSwitch ampDiode;
    private RotatableCircle volume;
    private RotatableCircle high;
    private RotatableCircle mid;
    private RotatableCircle bass;
    private RotatableCircle gain;
    private ButtonSwitch flangerSwitch;
    private ButtonSwitch flangerDiode;
    private RotatableCircle flangerRate;
    private RotatableCircle flangerDepth;
    private RotatableCircle flangerDelay;
    private RotatableCircle flangerWetAmount;
    private ButtonSwitch delaySwitch;
    private ButtonSwitch delayDiode;
    private RotatableCircle delayFeedback;
    private RotatableCircle delayRate;
    private RotatableCircle delayWetAmount;
    private ButtonSwitch reverbSwitch;
    private ButtonSwitch reverbDiode;
    private RotatableCircle reverbSize;
    private RotatableCircle reverbDamp;
    HBox topBar;

    private RotatableCircle reverbMix;
    private ComboBox<Mixer.Info> inputMixerComboBox;
    private ComboBox<Mixer.Info> outputMixerComboBox;

    StyleManager styleManager;

    public MainUI() {
        Pane ampControls = CreateAmpControls();
        Pane effectControls = CreateEffectControls();

        styleManager = new StyleManager(this);

        // Needs to be last because of manager
        topBar = CreateTopBar(ampControls, effectControls);

        root.getStyleClass().add("root");
        root.getChildren().addAll(ampControls, effectControls, topBar);
    }

    private Pane CreateAmpControls() {
        Pane controls = new Pane();

        final int SMALL_CIRCLE_SIZE = 50;
        final int LARGE_CIRCLE_SIZE = 70;
        final int ANGLE = -45;

        ampPowerSwitch = new ButtonSwitch("", false, "onOff");
        ampBypassSwitch = new ButtonSwitch("", false, "ampBypass");
        ampBypassSwitch.SetOnMouseClicked(null);
        ampDiode = new ButtonSwitch("", false, "ampDiode");
        ampDiode.setAsDiode("amp_on.png", "amp_off.png");
        volume = new RotatableCircle(LARGE_CIRCLE_SIZE, ANGLE, "Volume", 1.0, 0, 1.0); // from mute to full
        high = new RotatableCircle(SMALL_CIRCLE_SIZE, ANGLE, "High", 1.0, 0.1, 10.0);
        mid = new RotatableCircle(SMALL_CIRCLE_SIZE, ANGLE, "Mid", 1.0, 0.1, 10.0);
        bass = new RotatableCircle(SMALL_CIRCLE_SIZE, ANGLE, "Bass", 1.0, 0.1, 10.0);
        gain = new RotatableCircle(LARGE_CIRCLE_SIZE, ANGLE, "Gain", 1.0, 0.1, 8.0); // from 0.5x to 5x, default 1.0

        savable.addAll(List.of(high, mid, bass, gain));

        controls.getChildren().addAll(
                ampPowerSwitch.getView(), ampBypassSwitch.getView(), ampDiode.getView(), volume.getView(),
                high.getView(), mid.getView(), bass.getView(), gain.getView()
        );
        controls.getStyleClass().add("controls");
        return controls;
    }

    private HBox CreateTopBar(Pane ampControls, Pane effectControls) {
        HBox bar = new HBox();

        // Presets UI
        PresetsManager presetsManager = new PresetsManager(savable);
        PresetsUI presetsUI = new PresetsUI(presetsManager);
        HBox presetsBox = new HBox(presetsUI.presetDropdown.getView(), presetsUI.saveButton, presetsUI.saveAsButton);
        presetsBox.getStyleClass().add("presets-box");
        presetsUI.saveButton.getStyleClass().add("top-bar-button");
        presetsUI.saveAsButton.getStyleClass().add("top-bar-button");

        // Effects UI
        Button effectsButton = new Button("Effects");
        HBox effectsBox = new HBox(effectsButton);
        effectsBox.getStyleClass().add("effects-box");
        effectsButton.getStyleClass().add("top-bar-button");

        // Open window on click
        effectControls.setVisible(false);
        effectControls.setMouseTransparent(true);
        effectsButton.setOnAction(event -> {
            if (effectsButton.getText().equals("Effects")) {
                root.getStyleClass().clear();
                root.getStyleClass().add("Effects-window");
                effectControls.setVisible(true);
                effectControls.setMouseTransparent(false);
                ampControls.setVisible(false);
                ampControls.setMouseTransparent(true);
                styleManager.switchBackground(2);
                effectsButton.setText("Amplifier");
            }
            else {
                root.getStyleClass().clear();
                root.getStyleClass().add("root");
                effectControls.setVisible(false);
                effectControls.setMouseTransparent(true);
                ampControls.setVisible(true);
                ampControls.setMouseTransparent(false);
                styleManager.switchBackground(1);
                effectsButton.setText("Effects");
            }
        });

        // Devices UI
        // ================= INPUT COMBO BOX ==================
        inputMixerComboBox = new ComboBox<>();
        // 1) Get all mixers that support TargetDataLine (INPUT)
        List<Mixer.Info> inputMixers = AudioUtils.getAvailableInputMixers();
        inputMixerComboBox.getItems().addAll(inputMixers);

        // Button displays the mixer name rather than the whole object
        inputMixerComboBox.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Mixer.Info item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? null : item.getName());
            }
        });

        // The button cell is what shows when the combo is collapsed
        inputMixerComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Mixer.Info item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? null : item.getName());
            }
        });

        // Select the first available input mixer by default
        if (!inputMixers.isEmpty()) {
            inputMixerComboBox.getSelectionModel().select(0);
        }

        // ================= OUTPUT COMBO BOX ==================
        outputMixerComboBox = new ComboBox<>();
        // 2) Get all mixers that support SourceDataLine (OUTPUT)
        List<Mixer.Info> outputMixers = AudioUtils.getAvailableOutputMixers();
        outputMixerComboBox.getItems().addAll(outputMixers);

        // Same cell factory logic for output
        outputMixerComboBox.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Mixer.Info item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? null : item.getName());
            }
        });

        outputMixerComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Mixer.Info item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? null : item.getName());
            }
        });

        // Select the first available output mixer by default
        if (!outputMixers.isEmpty()) {
            outputMixerComboBox.getSelectionModel().select(0);
        }

        HBox devicesBox = new HBox(inputMixerComboBox, outputMixerComboBox);
        devicesBox.getStyleClass().add("devices-box");
        inputMixerComboBox.getStyleClass().add("dropdown-menu");
        outputMixerComboBox.getStyleClass().add("dropdown-menu");

        // Set titles
        Label presetsTitle = new Label("Presets");
        presetsTitle.getStyleClass().add("presets-title");

        VBox presets = new VBox(5, presetsTitle, presetsBox);
        presets.setAlignment(Pos.CENTER);

        Label effectsTitle = new Label("Chain");
        effectsTitle.getStyleClass().add("effects-title");

        VBox effects = new VBox(5, effectsTitle, effectsBox);
        effects.setAlignment(Pos.CENTER);

        Label inputDevicesTitle = new Label("Input");
        inputDevicesTitle.getStyleClass().add("input-devices-title");

        Label outputDevicesTitle = new Label("Output");
        outputDevicesTitle.getStyleClass().add("output-devices-title");

        HBox deviceLabels = new HBox(inputDevicesTitle, outputDevicesTitle);
        deviceLabels.getStyleClass().add("device-labels-box");

        VBox devices = new VBox(5, deviceLabels, devicesBox);
        devices.setAlignment(Pos.CENTER);

        // Piece together the bar
        bar.getChildren().addAll(presets, effects, devices);
        bar.getStyleClass().add("top-bar");
        return bar;
    }

    private Pane CreateEffectControls() {
        Pane controls = new Pane();

        final int LARGE_CIRCLE_SIZE = 70;
        final int ANGLE = -45;

        flangerSwitch = new ButtonSwitch("", false, "flangerSwitch");
        flangerSwitch.setSwitchImages();
        flangerDiode = new ButtonSwitch("", false, "flangerDiode");
        flangerDiode.setAsDiode("pedal_on.png", "pedal_off.png");
        flangerRate = new RotatableCircle(LARGE_CIRCLE_SIZE, ANGLE, "flangerRate", 0.06, 0.01, 3.0);
        flangerDepth = new RotatableCircle(LARGE_CIRCLE_SIZE, ANGLE, "flangerDepth", 0.07, 0.001, 0.5);
        flangerDelay = new RotatableCircle(LARGE_CIRCLE_SIZE, ANGLE, "flangerDelay", 1.0, 0.01, 5.0);
        flangerWetAmount = new RotatableCircle(LARGE_CIRCLE_SIZE, ANGLE, "flangerWetAmount", 0.4, 0.0, 1.0);
        delaySwitch = new ButtonSwitch("", false, "delaySwitch");
        delaySwitch.setSwitchImages();
        delayDiode = new ButtonSwitch("", false, "delayDiode");
        delayDiode.setAsDiode("pedal_on.png", "pedal_off.png");
        delayFeedback = new RotatableCircle(LARGE_CIRCLE_SIZE, ANGLE, "delayFeedback", 0.5, 0.0, 1.);
        delayRate = new RotatableCircle(LARGE_CIRCLE_SIZE, ANGLE, "delayRate", 1.0, 0.1, 3.);
        delayWetAmount = new RotatableCircle(LARGE_CIRCLE_SIZE, ANGLE, "delayWetAmount", 0.5, 0.0, 1.0);
        reverbSwitch = new ButtonSwitch("", false, "reverbSwitch");
        reverbSwitch.setSwitchImages();
        reverbDiode = new ButtonSwitch("", false, "reverbDiode");
        reverbDiode.setAsDiode("pedal_on.png", "pedal_off.png");
        reverbSize = new RotatableCircle(LARGE_CIRCLE_SIZE, ANGLE, "reverbSize", 0.08, 0.01, 0.15);
        reverbDamp = new RotatableCircle(LARGE_CIRCLE_SIZE, ANGLE, "reverbDamp", 0.5, 0.0, 0.9);
        reverbMix = new RotatableCircle(LARGE_CIRCLE_SIZE, ANGLE, "reverbMix", 0.5, 0.0, 1.0);

        savable.addAll(List.of(
                flangerRate, flangerDepth, flangerDelay, flangerWetAmount, delayFeedback, delayRate, delayWetAmount,
                reverbSize, reverbDamp, reverbMix)
        );

        Pane diodes = new Pane(
                flangerDiode.getView(), delayDiode.getView(), reverbDiode.getView()
        );
        Pane knobs = new Pane(
                flangerRate.getView(), flangerDepth.getView(), delayRate.getView(), delayWetAmount.getView(),
                reverbSize.getView(), reverbMix.getView(), flangerWetAmount.getView(), flangerDelay.getView(),
                delayFeedback.getView(), reverbDamp.getView()
        );
        Pane switches = new Pane(
                flangerSwitch.getView(), delaySwitch.getView(), reverbSwitch.getView()
        );
        controls.getChildren().addAll(
                diodes, switches, knobs
        );
        controls.getStyleClass().add("controls");
        return controls;
    }

    public Pane getRoot() {
        return root;
    }

    public ButtonSwitch getOnOffSwitch() {
        return ampPowerSwitch;
    }

    public RotatableCircle getVolumeKnob() {
        return volume;
    }

    public RotatableCircle getHighKnob() {
        return high;
    }

    public RotatableCircle getMidKnob() {
        return mid;
    }

    public RotatableCircle getBassKnob() {
        return bass;
    }

    public RotatableCircle getGainKnob() {
        return gain;
    }

    public ButtonSwitch getFlangerSwitch() {
        return flangerSwitch;
    }

    public ButtonSwitch getFlangerDiode() {
        return flangerDiode;
    }

    public RotatableCircle getFlangerRate() {
        return flangerRate;
    }

    public RotatableCircle getFlangerDepth() {
        return flangerDepth;
    }

    public RotatableCircle getFlangerDelay() {
        return flangerDelay;
    }

    public RotatableCircle getFlangerWetAmount() {
        return flangerWetAmount;
    }

    public ButtonSwitch getDelaySwitch() {
        return delaySwitch;
    }

    public ButtonSwitch getDelayDiode() {
        return delayDiode;
    }

    public RotatableCircle getDelayFeedback() {
        return delayFeedback;
    }

    public RotatableCircle getDelayRate() {
        return delayRate;
    }

    public RotatableCircle getDelayWetAmount() {
        return delayWetAmount;
    }

    public ButtonSwitch getReverbSwitch() {
        return reverbSwitch;
    }

    public ButtonSwitch getReverbDiode() {
        return reverbDiode;
    }

    public RotatableCircle getReverbSize() {
        return reverbSize;
    }

    public RotatableCircle getReverbDamp() {
        return reverbDamp;
    }

    public RotatableCircle getReverbMix() {
        return reverbMix;
    }

    public ComboBox<Mixer.Info> getInputMixerComboBox() {
        return inputMixerComboBox;
    }

    public ComboBox<Mixer.Info> getOutputMixerComboBox() {
        return outputMixerComboBox;
    }

    public void setAmpDiodeStatus(Double value) {
        ampDiode.setDiodeStatus(value);
    }

    public void setFlangerDiodeStatus(Double value) {
        flangerDiode.setDiodeStatus(value);
    }

    public void setDelayDiodeStatus(Double value) {
        delayDiode.setDiodeStatus(value);
    }

    public void setReverbDiodeStatus(Double value) {
        reverbDiode.setDiodeStatus(value);
    }

    public ButtonSwitch getAmpBypassSwitch() {
        return ampBypassSwitch;
    }

    public ButtonSwitch getAmpDiode() {
        return ampDiode;
    }
}