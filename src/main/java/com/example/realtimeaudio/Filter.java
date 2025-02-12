package com.example.realtimeaudio;

public class Filter {
    float sampleTime;
    float gain = 1.0f;
    float[] x = new float[3];
    float[] y = new float[3];
    float[] a = new float[3];
    float[] b = new float[3];

    Filter(float sampleRateHz) {
        // calculate sample time
        sampleTime = 1.0f / sampleRateHz;
        // initialize "no data" floats inside both the three sample input table, and three sample output
        for (int n = 0; n < 3; n++) {
            x[n] = 0.0f;
            y[n] = 0.0f;
        }
        // set default parameters for the filter
        setParameters(1.0f, 0.0f, 1.0f); // allPass filter after init
    }

    void setParameters(float centerFreqHz, float bandwidthHz, float boostCutLinear) {
        // precompute wcT - variable used in every coefficient calculation
        float wcT = (float) (2.0f * Math.tan(Math.PI * centerFreqHz * sampleTime));
        float Q = centerFreqHz / bandwidthHz;
        gain = boostCutLinear;
        // lines below are for calculating input sample coefficients
        a[0] = 4.0f + 2.0f * (boostCutLinear / Q) * wcT + wcT * wcT;
        a[1] = 2.0f * wcT * wcT - 8.0f;
        a[2] = 4.0f - 2.0f * (boostCutLinear / Q) * wcT + wcT * wcT;

        // this code calculates output sample coefficients
        // b0 is calculated this way to use this during update (difference equation)
        // since I'm not using b0 alone, I precompute 1 / b0

        b[0] = 1.0f / (4.0f + 2.0f / Q * wcT + wcT * wcT);
        b[1] = -(2.0f * wcT * wcT - 8.0f);
        b[2] = -(4.0f - 2.0f / Q * wcT + wcT * wcT);
    }

    float update(float in) {
        // shift samples from the three sample input
        x[2] = x[1];
        x[1] = x[0];
        x[0] = in;

        // shift samples from three sample output, and output result in y[0]
        y[2] = y[1];
        y[1] = y[0];
        // this is a simplified version of difference equation for a 2nd order IIR filter
        // actual difference equation: y_n = 1 / b0 ((a0x_n + a1x_n-1 + a2x_n-2) + (b1y_n-1 + b2y_n-2))
        // x_n = current input sample, y_n = current filter output sample, a and b - filter coefficients
        y[0] = (a[0] * x[0] + a[1] * x[1] + a[2] * x[2] + (b[1] * y[1] + b[2] * y[2])) * b[0];

        return y[0];
    }
}