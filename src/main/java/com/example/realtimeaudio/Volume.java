package com.example.realtimeaudio;

/**
 * A simple module for controlling the final (master) volume of the signal.
 * Typically applies a linear scaling.
 */
public class Volume {

    // volume in range 0.0 ~ 1.0 or possibly above 1.0 if we allow "boost"
    private float volume = 1.0f;

    /**
     * Process one sample. Just scales by volume factor,
     * then clamps to [-1..1].
     */
    public float processSample(float sample) {
        float out = sample * volume;
        // Hard clamp just to be safe
        if (out > 1.0f)  out = 1.0f;
        if (out < -1.0f) out = -1.0f;
        return out;
    }

    public float getVolume() {
        return volume;
    }

    /**
     * Set volume factor. 1.0 => no change; 0.0 => mute;
     * > 1.0 => boost.
     */
    public void setVolume(float volume) {
        this.volume = volume;
    }
}
