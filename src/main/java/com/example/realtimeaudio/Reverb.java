package com.example.realtimeaudio;

import javax.sound.sampled.Clip;
import javax.sound.sampled.ReverbType;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

public class Reverb {
    private final AtomicBoolean running = new AtomicBoolean(false);
    private float sampleRate = 44100;
    private float wet;
    private float decay;
    private float size;
    private Delay delayLeft;
    private Delay delayRight;
    private Delay delayLeft2;
    private Delay delayRight2;
    Reverb() {
        wet = 1f;
        decay = 0.7f;
        size = 0.08f;
        delayLeft = new Delay(0.08, 0.7f, 44100, 1.f);
        delayRight = new Delay(0.06, 0.75f, 44100, 1.f);
        delayLeft2 = new Delay(0.03, 0.7f, 44100, 1.f);
        delayRight2 = new Delay(0.04, 0.75f, 44100, 1.f);
        delayLeft.turnOn();
        delayRight.turnOn();
        delayLeft2.turnOn();
        delayRight2.turnOn();
    }
    Reverb(float size, float wet, float decay, float sampleRate) {
        delayLeft = new Delay(size, decay, sampleRate, wet);
        delayRight = new Delay(size * 0.75, decay * 1.07142f, sampleRate, wet);
        delayLeft2 = new Delay(size * 0.375, decay, sampleRate, wet);
        delayRight2 = new Delay(size * 1/2, decay * 1.07142f, sampleRate, wet);
        delayLeft.turnOn();
        delayRight.turnOn();
        delayLeft2.turnOn();
        delayRight2.turnOn();
        this.wet = wet;
        this.decay = decay;
        this.sampleRate = sampleRate;
        this.size = size;
    }

    public float processLeft(float leftSample) {
        float sample = this.delayLeft.processSample(leftSample);
        return sample;
    }

    public float processRight(float rightSample) {
        float sample = this.delayRight.processSample(rightSample);
        return sample;
    }

    public float[] processSample(float leftSample, float rightSample) {
        if (!running.get()) {
            return new float[]{leftSample, rightSample};
        }
        float d1 = delayLeft.processSample(leftSample);
        float d2 = delayRight.processSample(rightSample);
        float d3 = delayLeft2.processSample(leftSample);
        float d4 = delayRight2.processSample(rightSample);

        float h1 = d1 + d2 + d3 + d4;
        float h2 = d1 - d2 + d3 - d4;
        float h3 = d1 + d2 - d3 - d4;
        float h4 = d1 - d2 - d3 + d4;

        // Normalize by dividing by the square root of the number of channels
        float scale = 2;
        h1 /= scale;
        h2 /= scale;
        h3 /= scale;
        h4 /= scale;

        float leftOutput = (h1 + h3) / (float) Math.sqrt(2);
        float rightOutput = (h2 + 2 * h4) / (float) Math.sqrt(2);

        leftOutput = leftOutput * wet + leftSample * (1 - wet);
        rightOutput = rightOutput * wet + rightSample * (1 - wet);

        return new float[]{leftOutput, rightOutput};
    }

    public void setWet(float wet) {
        this.wet = wet;
        this.delayLeft.setWet(wet);
        this.delayRight.setWet(wet);
        this.delayLeft2.setWet(wet);
        this.delayRight2.setWet(wet);
    }

    public void setSize(float size) {
        this.size = size;
        delayLeft.setEchoLength(size);
        delayRight.setEchoLength(size * 0.75);
        delayLeft2.setEchoLength(size * 0.375);
        delayRight2.setEchoLength(size * 1/2);
    }

    public void setDecay(float decay) {
        this.decay = decay;
        this.delayLeft.setDecay(decay);
        this.delayRight.setDecay(decay * 1.07142f);
        this.delayLeft2.setDecay(decay);
        this.delayRight2.setDecay(decay * 1.07142f);
    }

    public float getValueWet() {
        return wet;
    }

    public float getDecay() {
        return decay;
    }

    public float getSize() { return this.size; }

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
