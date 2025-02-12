package com.example.realtimeaudio;

/**
 * A simple module for applying "preamp gain" to an audio signal,
 * potentially driving it into soft clipping.
 */
public class Gain {

    // How much we boost the signal before clipping
    // For instance, gain = 1.0 => no change
    // gain = 5.0 => big boost
    private float gain = 1.0f;

    // Whether to apply soft clipping
    private boolean softClipping = true;

    /**
     * Process one sample in [-1..1].
     * @param sample the input sample
     * @return the processed sample
     */
    public float processSample(float sample) {
        // 1. Apply gain
        float boosted = sample * gain;

        // 2. Optional soft clipping
        if (softClipping) {
            boosted = (float) Math.tanh(boosted);
        } else {
            // Hard clip at [-1, 1]
            if (boosted > 1.0f)  boosted = 1.0f;
            if (boosted < -1.0f) boosted = -1.0f;
        }

        return boosted;
    }

    public float getGain() {
        return gain;
    }

    public void setGain(float gain) {
        this.gain = gain;
    }

    public boolean isSoftClipping() {
        return softClipping;
    }

    public void setSoftClipping(boolean softClipping) {
        this.softClipping = softClipping;
    }
}
