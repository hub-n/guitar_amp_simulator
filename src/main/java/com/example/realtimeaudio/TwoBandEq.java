package com.example.realtimeaudio;

/**
 * A simple 2-band EQ with bass/treble controls and a first-order low-pass.
 * Applies the effect to 16-bit, stereo PCM data in place.
 */
public class TwoBandEq {

    // Gains for low (bass) and high (treble) bands
    private float bassGain = 1.0f;
    private float trebleGain = 1.0f;

    // Filter coefficient for the low-pass (0..1).
    // Smaller alpha => lower cutoff => more "low" portion.
    private float lowPassAlpha = 0.1f;

    // State for the low-pass filter on each channel
    private float lowLeft = 0.0f;
    private float lowRight = 0.0f;

    public TwoBandEq() {
        // Default constructor
    }

    public TwoBandEq(float bassGain, float trebleGain, float lowPassAlpha) {
        this.bassGain = bassGain;
        this.trebleGain = trebleGain;
        this.lowPassAlpha = lowPassAlpha;
    }

    /**
     * Process a single sample through the 2-band EQ.
     * @param inSample The input sample in [-1..1]
     * @param isLeft   Whether it's for the left channel
     * @return The processed sample
     */
    public float processSample(float inSample, boolean isLeft) {
        if (isLeft) {
            // Low-pass filter
            lowLeft = lowPassAlpha * inSample + (1 - lowPassAlpha) * lowLeft;
            // High component
            float high = inSample - lowLeft;
            // Apply gains
            return bassGain * lowLeft + trebleGain * high;
        } else {
            lowRight = lowPassAlpha * inSample + (1 - lowPassAlpha) * lowRight;
            float high = inSample - lowRight;
            return bassGain * lowRight + trebleGain * high;
        }
    }

    /**
     * Reset filter states (e.g. when stopping).
     */
    public void reset() {
        lowLeft = 0.0f;
        lowRight = 0.0f;
    }

    // ------------------------------------------------------------------------
    // Getters & Setters
    // ------------------------------------------------------------------------
    public float getBassGain() {
        return bassGain;
    }

    public void setBassGain(float bassGain) {
        this.bassGain = bassGain;
    }

    public float getTrebleGain() {
        return trebleGain;
    }

    public void setTrebleGain(float trebleGain) {
        this.trebleGain = trebleGain;
    }

    public float getLowPassAlpha() {
        return lowPassAlpha;
    }

    public void setLowPassAlpha(float lowPassAlpha) {
        this.lowPassAlpha = lowPassAlpha;
    }
}
