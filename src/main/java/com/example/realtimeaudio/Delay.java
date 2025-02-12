package com.example.realtimeaudio;

import java.util.concurrent.atomic.AtomicBoolean;

public class Delay {
    private final AtomicBoolean running = new AtomicBoolean(false);
    private double sampleRate; // no knob
    private float[] echoBuffer; // no knob
    private int position; // no knob
    private float decay; // knob needed
    private float wet; // knob needed
    private double newEchoLength; // knob needed

    // total of 3 knobs needed: decay (Decay), wet (Wet / Mix), newEchoLength (Delay Rate)

    public Delay(double echoLength, float decay,double sampleRate, float wet) {
        this.sampleRate = sampleRate;
        setDecay(decay);
        setEchoLength(echoLength);
        applyNewEchoLength();
        setWet(wet);
    }

    public float getDecay() {
        return decay;
    }

    public double getSampleRate() {
        return sampleRate;
    }

    public float getValueWet() {
        return wet;
    }

    public double getEchoLength() {
        return newEchoLength;
    }

    public void setDecay(float decay) {
        this.decay = decay;
    }

    public void setWet(float wet) {
        this.wet = wet;
    }

    public void setEchoLength(double echoLength) {
        this.newEchoLength = echoLength;
    }

    private void applyNewEchoLength(){
        if(newEchoLength != -1){
            //create a new buffer with the information of the previous buffer
            float[] newEchoBuffer = new float[(int) (sampleRate * newEchoLength)];
            if(echoBuffer != null){
                for(int i = 0 ; i < newEchoBuffer.length; i++){
                    if(position >= echoBuffer.length){
                        position = 0;
                    }
                    newEchoBuffer[i] = echoBuffer[position];
                    position++;
                }
            }
            this.echoBuffer = newEchoBuffer;
            newEchoLength = -1;
        }
    }

    public float processSample(float sample) {
        if (!running.get()) {
            return sample;
        }
        if(position >= echoBuffer.length){
            position = 0;
        }

        //output is the input added with the decayed echo
        float out = sample + echoBuffer[position] * decay;
        //store the sample in the buffer;
        echoBuffer[position] = out;

        position++;

        applyNewEchoLength();
        return wet * out + sample * (1.0f - wet);
    }
    void turnOn() {
        if (running.get()) {
            System.out.println("Audio loop is already running.");
            return;
        }
        running.set(true);
    }

    void turnOff() {
        if (!running.get()) {
            System.out.println("Audio loop is not running.");
            return;
        }
        running.set(false);
    }
}
