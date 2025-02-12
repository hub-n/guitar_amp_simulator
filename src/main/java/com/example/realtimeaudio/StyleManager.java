package com.example.realtimeaudio;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class StyleManager {
    private final ImageView backgroundImageView;
    private final Image backgroundImage;

    public StyleManager(MainUI mainUI){
        backgroundImage = new Image("/background.jpg");
        backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setFitWidth(1200);
        backgroundImageView.setFitHeight(780);

        mainUI.getRoot().getChildren().add(0, backgroundImageView);

        // Get all to variables for readability
        Pane ampPowerSwitch = mainUI.getOnOffSwitch().getView();
        Pane ampBypassSwitch = mainUI.getAmpBypassSwitch().getView();
        Pane ampDiode = mainUI.getAmpDiode().getView();
        Pane volume = mainUI.getVolumeKnob().getView();
        Pane high = mainUI.getHighKnob().getView();
        Pane mid = mainUI.getMidKnob().getView();
        Pane bass = mainUI.getBassKnob().getView();
        Pane gain = mainUI.getGainKnob().getView();
        Pane flangerSwitch = mainUI.getFlangerSwitch().getView();
        Pane flangerDiode = mainUI.getFlangerDiode().getView();
        Pane flangerRate = mainUI.getFlangerRate().getView();
        Pane flangerDepth = mainUI.getFlangerDepth().getView();
        Pane flangerDelay = mainUI.getFlangerDelay().getView();
        Pane flangerWetAmount = mainUI.getFlangerWetAmount().getView();
        Pane delaySwitch = mainUI.getDelaySwitch().getView();
        Pane delayDiode = mainUI.getDelayDiode().getView();
        Pane delayFeedback = mainUI.getDelayFeedback().getView();
        Pane delayRate = mainUI.getDelayRate().getView();
        Pane delayWetAmount = mainUI.getDelayWetAmount().getView();
        Pane reverbSwitch = mainUI.getReverbSwitch().getView();
        Pane reverbDiode = mainUI.getReverbDiode().getView();
        Pane reverbSize = mainUI.getReverbSize().getView();
        Pane reverbDamp = mainUI.getReverbDamp().getView();
        Pane reverbMix = mainUI.getReverbMix().getView();

        // Set everything in position
        // Main
        setPos(volume, 0.367, 0.62);
        setPos(high, 0.455, 0.648);
        setPos(mid, 0.516, 0.648);
        setPos(bass, 0.578, 0.648);
        setPos(gain, 0.6312, 0.625);
        setPos(ampPowerSwitch, 0.178, 0.64);
        setPos(ampDiode, 0.305, 0.63);
        setPos(ampBypassSwitch, 0.224, 0.64);

        // Effects
        // Flanger
        setPos(flangerSwitch, 0.168, 0.74);
        setPos(flangerDiode, 0.168, 0.279);
        setPos(flangerRate, 0.102, 0.334);
        setPos(flangerDepth, 0.22, 0.334);
        setPos(flangerDelay, 0.102, 0.49);
        setPos(flangerWetAmount, 0.22, 0.49);

        // Delay
        setPos(delaySwitch, 0.448, 0.74);
        setPos(delayDiode, 0.448, 0.276);
        setPos(delayFeedback, 0.442, 0.49);
        setPos(delayRate, 0.384, 0.334);
        setPos(delayWetAmount, 0.5, 0.334);

        // Reverb
        setPos(reverbSwitch, 0.728, 0.74);
        setPos(reverbDiode, 0.728, 0.279);
        setPos(reverbSize, 0.66, 0.334);
        setPos(reverbDamp, 0.72, 0.49);
        setPos(reverbMix, 0.78, 0.334);
    }

    void setPos(Pane pane, double x, double y) {
        pane.layoutXProperty().bind(backgroundImageView.fitWidthProperty().multiply(x));
        pane.layoutYProperty().bind(backgroundImageView.fitHeightProperty().multiply(y));
    }

    void switchBackground(int imageNum) {
        if (imageNum == 1)
            backgroundImageView.setImage(new Image("/background.jpg"));
        if (imageNum == 2)
            backgroundImageView.setImage(new Image("/pedals.jpg"));
    }
}
