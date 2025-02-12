package com.example.realtimeaudio;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.jvm.AudioPlayer;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;

import javax.sound.sampled.*;

public class LiveEq {
    private final Filter lowFilter;
    private final Filter midFilter;
    private final Filter highFilter;

    LiveEq() {
        lowFilter = new Filter(44100f);
        midFilter = new Filter(44100f);
        highFilter = new Filter(44100f);

        lowFilter.setParameters(100.0f, 80.0f,  1.0f);
        midFilter.setParameters(800.0f, 400.0f, 1.0f);
        highFilter.setParameters(6000.0f, 2000f,   1.0f);
    }

    public float processSample(float sample) {
        float low = lowFilter.update(sample);
        float mid = midFilter.update(low);
        float high = highFilter.update(mid);
        return high;
    }

    public Filter getLowFilter() {
        return this.lowFilter;
    }
    public Filter getMidFilter() {
        return this.midFilter;
    }
    public Filter getHighFilter() {
        return this.highFilter;
    }
    public void setLowFilterGain(float gain) {
        this.lowFilter.setParameters(100.0f, 80.0f,  gain);
    }
    public void setMidFilterGain(float gain) {
        this.midFilter.setParameters(800.0f, 400.0f, gain);
    }
    public void setHighFilterGain(float gain) {
        this.highFilter.setParameters(6000.0f, 2000f,   gain);
    }
    public float getLowFilterGain() {
        return this.lowFilter.gain;
    }
    public float getMidFilterGain() {
        return this.midFilter.gain;
    }
    public float getHighFilterGain() {
        return this.highFilter.gain;
    }

//    @Override
//    public boolean process(AudioEvent audioEvent) {
//        float[] buffer = audioEvent.getFloatBuffer();
//
//        for (int i = 0; i < buffer.length; i++) {
//            float low = lowFilter.update(buffer[i]);
//            float mid = midFilter.update(low);
//            float high = highFilter.update(mid);
//            buffer[i] = high;
//        }
//        return true;
//    }
//    @Override
//    public void processingFinished() {
//        System.out.println("Processing finished!");
//    }
//
//    public static void main(String[] args) {
//        AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
//        try {
//            TargetDataLine line = AudioSystem.getTargetDataLine(format);
//            line.open(format);
//            line.start();
//
//            AudioDispatcher dispatcher = new AudioDispatcher(
//                    new JVMAudioInputStream(new AudioInputStream(line)),  2048, 0);
//
//            dispatcher.addAudioProcessor(new LiveEq());
//            AudioPlayer player = new AudioPlayer(dispatcher.getFormat());
//            dispatcher.addAudioProcessor(player);
//            new Thread(dispatcher, "Audio Dispatcher").start();
//        }
//        catch (LineUnavailableException e) {
//            e.printStackTrace();
//        }
//    }
}
