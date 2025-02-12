package com.example.realtimeaudio;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.effects.FlangerEffect;
import be.tarsos.dsp.io.TarsosDSPAudioFloatConverter;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.TarsosDSPAudioInputStream;
import be.tarsos.dsp.io.jvm.AudioPlayer;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.util.AudioResourceUtils;
import com.example.realtimeaudio.LiveEq;
import com.example.realtimeaudio.AudioUtils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class Flanger {
    private final AtomicBoolean running = new AtomicBoolean(false);
    private double maxFlangerLength; // knob needed
    private float sampleRate; // no knob
    private float wet; // knob needed
    private double time = 0; // no knob
    private float lfoFrequency; // knob needed
    private float[] flangerBuffer; // no knob
    private int writeIndex = 0; // no knob

    // total of 3 knobs needed: maxFlangerLength (Length), wet (Wet / Mix), lfoFrequency (Flanger Frequency)

    Flanger(double maxFlangerLength, double wet,
            float sampleRate, float lfoFrequency) {
        this.sampleRate = sampleRate;
        this.lfoFrequency = lfoFrequency;
        this.wet = (float) wet;
        this.maxFlangerLength = maxFlangerLength;
        flangerBuffer = new float[(int) (sampleRate * this.maxFlangerLength)];
        Arrays.fill(flangerBuffer, 0.0f);
    }

    public void setLfoFrequency(float lfoFrequency) {
        this.lfoFrequency = lfoFrequency;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public void setWet(float wet) {
        this.wet = wet;
    }

    public void setMaxFlangerLength(double flangerLength) {
        this.maxFlangerLength = maxFlangerLength;
        flangerBuffer = new float[(int) (sampleRate * flangerLength)];
    }

    public double getTime() {
        return time;
    }

    public float getValueWet() {
        return wet;
    }

    public float getLfoFrequency() {
        return lfoFrequency;
    }

    public boolean isRunning() {
        return running.get();
    }

    public float processSample(float sample) {
        if (!running.get()) {
            return sample;
        }
        double twoPIf = 2 * Math.PI * lfoFrequency;
        double timeStep = 1.0 / sampleRate;
        time += timeStep;
        double lfoValue = (flangerBuffer.length - 1) * Math.sin(twoPIf * time);
        int delay = (int) (Math.round(Math.abs(lfoValue)));
        if (writeIndex >= flangerBuffer.length) {
            writeIndex = 0;
        }
        flangerBuffer[writeIndex] = sample;
        int readPosition = writeIndex - delay;
        if (readPosition < 0) {
            readPosition += flangerBuffer.length;
        }
        writeIndex++;

        return (1.0f - wet) * sample + wet * flangerBuffer[readPosition];
    }
    public void turnOn() {
        if (running.get()) {
            System.out.println("Audio loop is already running.");
            return;
        }
        running.set(true);
    }

    public void turnOff() {
        if (!running.get()) {
            System.out.println("Audio loop is not running.");
            return;
        }
        running.set(false);
    }
}
