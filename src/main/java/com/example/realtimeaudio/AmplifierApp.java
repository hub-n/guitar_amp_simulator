package com.example.realtimeaudio;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Objects;

public class AmplifierApp extends Application {

    private AudioEngine audioEngine;

    @Override
    public void start(Stage primaryStage) {
        // Instantiate audio engine
        audioEngine = new AudioEngine();

        // Create UI
        MainUI mainUI = new MainUI();

        // ============== Connect Amp knobs ==============
        // ============ Bass ============
        mainUI.getBassKnob().getNormalizedValue().addListener((obs, oldVal, newVal) -> {
            audioEngine.getLiveEQ().setLowFilterGain(newVal.floatValue());
        });
        // ============ Mid ============
        mainUI.getMidKnob().getNormalizedValue().addListener((obs, oldVal, newVal) -> {
            audioEngine.getLiveEQ().setMidFilterGain(newVal.floatValue());
        });
        // ============ Treble ============
        mainUI.getHighKnob().getNormalizedValue().addListener((obs, oldVal, newVal) -> {
            audioEngine.getLiveEQ().setHighFilterGain(newVal.floatValue());
        });
        // ============ Gain ============
        mainUI.getGainKnob().getNormalizedValue().addListener((obs, oldVal, newVal) -> {
            audioEngine.getGainModule().setGain(newVal.floatValue());
        });
        // ============ Volume ============
        mainUI.getVolumeKnob().getNormalizedValue().addListener((obs, oldVal, newVal) -> {
            audioEngine.getVolumeModule().setVolume(newVal.floatValue());
        });

        // ============== ON/OFF BUTTON ==============
        mainUI.getOnOffSwitch().getState().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                audioEngine.setSelectedInputMixer(mainUI.getInputMixerComboBox().getValue());
                audioEngine.setSelectedOutputMixer(mainUI.getOutputMixerComboBox().getValue());
                audioEngine.startAudioLoop();
                mainUI.setAmpDiodeStatus(1.0);
            } else {
                audioEngine.stopAudioLoop();
                mainUI.setAmpDiodeStatus(0.0);
            }
        });

        // ============== Connect Effect buttons ==============
        mainUI.getFlangerSwitch().getState().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                audioEngine.getFlanger().turnOn();
                mainUI.setFlangerDiodeStatus(1.0);
            } else {
                audioEngine.getFlanger().turnOff();
                mainUI.setFlangerDiodeStatus(0.0);
            }
        });

        mainUI.getDelaySwitch().getState().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                audioEngine.getDelay().turnOn();
                mainUI.setDelayDiodeStatus(1.0);
            } else {
                audioEngine.getDelay().turnOff();
                mainUI.setDelayDiodeStatus(0.0);
            }
        });

        mainUI.getReverbSwitch().getState().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                audioEngine.getReverb().turnOn();
                mainUI.setReverbDiodeStatus(1.0);
            } else {
                audioEngine.getReverb().turnOff();
                mainUI.setReverbDiodeStatus(0.0);
            }
        });

        // ============== Connect Effect knobs ==============
        // ============ Flanger ============
        mainUI.getFlangerRate().getNormalizedValue().addListener((obs, oldVal, newVal) -> {
            audioEngine.getFlanger().setLfoFrequency(newVal.floatValue());
        });

        mainUI.getFlangerDepth().getNormalizedValue().addListener((obs, oldVal, newVal) -> {
            audioEngine.getFlanger().setMaxFlangerLength(newVal.floatValue());
        });

        mainUI.getFlangerDelay().getNormalizedValue().addListener((obs, oldVal, newVal) -> {
            audioEngine.getFlanger().setTime(newVal.floatValue());
        });

        mainUI.getFlangerWetAmount().getNormalizedValue().addListener((obs, oldVal, newVal) -> {
            audioEngine.getFlanger().setWet(newVal.floatValue());
        });

        // ============ Delay ============
        mainUI.getDelayFeedback().getNormalizedValue().addListener((obs, oldVal, newVal) -> {
            audioEngine.getDelay().setDecay(newVal.floatValue());
        });

        mainUI.getDelayRate().getNormalizedValue().addListener((obs, oldVal, newVal) -> {
            audioEngine.getDelay().setEchoLength(newVal.floatValue());
        });

        mainUI.getDelayWetAmount().getNormalizedValue().addListener((obs, oldVal, newVal) -> {
            audioEngine.getDelay().setWet(newVal.floatValue());
        });

        // ============ Reverb ============
        mainUI.getReverbSize().getNormalizedValue().addListener((obs, oldVal, newVal) -> {
            audioEngine.getReverb().setSize(newVal.floatValue());
        });

        mainUI.getReverbDamp().getNormalizedValue().addListener((obs, oldVal, newVal) -> {
            audioEngine.getReverb().setDecay(newVal.floatValue());
        });

        mainUI.getReverbMix().getNormalizedValue().addListener((obs, oldVal, newVal) -> {
            audioEngine.getReverb().setWet(newVal.floatValue());
        });

        // ============== Change device when selected new ==========
        mainUI.getInputMixerComboBox().getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                audioEngine.setSelectedInputMixer(newVal);
                audioEngine.stopAudioLoop();
                audioEngine.startAudioLoop();
            }
        });

        mainUI.getOutputMixerComboBox().valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                audioEngine.setSelectedOutputMixer(newVal);
                audioEngine.stopAudioLoop();
                audioEngine.startAudioLoop();
            }
        });

        // Setup scene
        Pane root = mainUI.getRoot();
        Scene scene = new Scene(root, 1200, 780);
        primaryStage.setTitle("PAP AMP");
        primaryStage.setScene(scene);
        // Set style
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style1.css")).toExternalForm());
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void stop() {
        // Ensure we stop the audio engine if the user closes the window.
        audioEngine.stopAudioLoop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
