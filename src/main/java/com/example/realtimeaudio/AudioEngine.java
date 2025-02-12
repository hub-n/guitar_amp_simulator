package com.example.realtimeaudio;

import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicBoolean;
import com.example.realtimeaudio.LiveEq;
import com.example.realtimeaudio.Delay;

public class AudioEngine {

    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread audioThread;

    private Mixer.Info selectedInputMixer;
    private Mixer.Info selectedOutputMixer;

    private TargetDataLine inputLine;
    private SourceDataLine outputLine;

    private int bufferSize = 2048;

    // ------------------------------------------------------------------------
    // DSP modules
    // ------------------------------------------------------------------------
    private TwoBandEq twoBandEq;
    private Gain gain;
    private Volume volume;
    private LiveEq liveEq;
    private Delay delay;
    private Flanger flanger;
    private Reverb reverb;

    public AudioEngine() {
        this.twoBandEq = new TwoBandEq();
        this.gain = new Gain();
        this.volume = new Volume();
        this.liveEq = new LiveEq();
        this.delay = new Delay(1.0, 0.4f, 44100, 0.5f);
        this.flanger = new Flanger(0.07, 0.4, 44100, 0.06f);
        this.reverb = new Reverb(0.08f, 0.4f, 0.7f, 44100f);
//        delay.turnOn();
//        flanger.turnOn();
        twoBandEq.setBassGain(1.0f);
        twoBandEq.setTrebleGain(1.0f);
        twoBandEq.setLowPassAlpha(0.1f);
    }

    /**
     * Starts capturing and playing audio in real time, applying our SimpleEq.
     */
    public void startAudioLoop() {
        if (running.get()) {
            System.out.println("Audio loop is already running.");
            return;
        }
        running.set(true);

        audioThread = new Thread(() -> {
            try {
                openLines();

                // 4) Allocate a buffer
                byte[] buffer = new byte[bufferSize];

                System.out.println("Audio loop started (with SimpleEq).");

                // 5) Main loop
                while (running.get()) {
                    int bytesRead = inputLine.read(buffer, 0, buffer.length);
                    if (bytesRead > 0) {
                        // Apply DSP
                        processBuffer(buffer, bytesRead);

                        // Write to output
                        outputLine.write(buffer, 0, bytesRead);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeLines();
                System.out.println("Audio loop stopped.");
            }
        }, "Audio-Thread");

        audioThread.start();
    }

    /**
     * Stops the audio loop.
     */
    public void stopAudioLoop() {
        running.set(false);
        if (audioThread != null) {
            try {
                audioThread.join(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        audioThread = null;
    }

    /**
     * Processes an entire stereo buffer of 16-bit PCM audio in-place.
     * @param buffer    The audio data
     * @param bytesRead Number of bytes valid in buffer
     */
    public void processBuffer(byte[] buffer, int bytesRead) {
        // Each stereo frame = 4 bytes (2 bytes left + 2 bytes right).
        ByteBuffer bb = ByteBuffer.wrap(buffer, 0, bytesRead);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        int frameSize = 4; // 2 bytes per sample * 2 channels
        int sampleCount = bytesRead / frameSize;

        for (int i = 0; i < sampleCount; i++) {
            short sLeft  = bb.getShort(i * frameSize);
            short sRight = bb.getShort(i * frameSize + 2);

            // Convert to float in range [-1..1]
            float fLeft = sLeft  / 32768.0f;
            float fRight = sRight / 32768.0f;

            // ================= DSP Modules ==================
            // Apply preamp gain (can add distortion if gain>1)


            fLeft = flanger.processSample(fLeft);
            fRight = flanger.processSample(fRight);

            fLeft = delay.processSample(fLeft);
            fRight = delay.processSample(fRight);

            float[] s = reverb.processSample(fLeft, fRight);
            fLeft = s[0];
            fRight = s[1];

            fLeft = gain.processSample(fLeft);
            fRight = gain.processSample(fRight);

            // Apply EQ
            fLeft = liveEq.processSample(fLeft);
            fRight = liveEq.processSample(fRight);
            // Apply master volume
            fLeft = volume.processSample(fLeft);
            fRight = volume.processSample(fRight);

            // Clamp and convert back
            if (fLeft > 1.0f)  fLeft = 1.0f;
            if (fLeft < -1.0f) fLeft = -1.0f;
            if (fRight > 1.0f)  fRight = 1.0f;
            if (fRight < -1.0f) fRight = -1.0f;

            short oLeft  = (short) (fLeft * 32767.0f);
            short oRight = (short) (fRight * 32767.0f);

            // Write back
            bb.putShort(i * frameSize, oLeft);
            bb.putShort(i * frameSize + 2, oRight);
        }
    }

    // --------------------------------------------------------------------
    // Internal line management
    // --------------------------------------------------------------------

    private void openLines() {
        try {
            AudioFormat format = new AudioFormat(44100f, 16, 2, true, false);

            // ----------- INPUT -----------
            DataLine.Info inputInfo = new DataLine.Info(TargetDataLine.class, format);
            if (selectedInputMixer != null) {
                inputLine = (TargetDataLine) AudioSystem.getMixer(selectedInputMixer).getLine(inputInfo);
            } else {
                inputLine = (TargetDataLine) AudioSystem.getLine(inputInfo);
            }
            inputLine.open(format, bufferSize);
            inputLine.start();

            // ----------- OUTPUT -----------
            // 3) Open output line
            DataLine.Info outputInfo = new DataLine.Info(SourceDataLine.class, format);
            if (selectedOutputMixer != null) {
                outputLine = (SourceDataLine) AudioSystem.getMixer(selectedOutputMixer).getLine(outputInfo);
            } else {
                outputLine = (SourceDataLine) AudioSystem.getLine(outputInfo);
            }
            outputLine.open(format, bufferSize);
            outputLine.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes lines properly, freeing resources.
     */
    private void closeLines() {
        if (inputLine != null) {
            inputLine.stop();
            inputLine.close();
            inputLine = null;
        }
        if (outputLine != null) {
            outputLine.stop();
            outputLine.close();
            outputLine = null;
        }
        // Reset or clear DSP states
        twoBandEq.reset();
    }

    // ------------------------------------------------------------------------
    // Getters/Setters
    // ------------------------------------------------------------------------
    public Mixer.Info getSelectedInputMixer() {
        return selectedInputMixer;
    }

    public void setSelectedInputMixer(Mixer.Info selectedInputMixer) {
        this.selectedInputMixer = selectedInputMixer;
    }

    public Mixer.Info getSelectedOutputMixer() {
        return selectedOutputMixer;
    }

    public void setSelectedOutputMixer(Mixer.Info selectedOutputMixer) {
        this.selectedOutputMixer = selectedOutputMixer;
    }

    public Gain getGainModule() {
        return gain;
    }

    public void setGainModule(Gain gainModule) {
        this.gain = gainModule;
    }

    public Volume getVolumeModule() {
        return volume;
    }

    public void setVolumeModule(Volume volumeModule) {
        this.volume = volumeModule;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public TwoBandEq getSimpleEq() {
        return twoBandEq;
    }

    public LiveEq getLiveEQ() {
        return this.liveEq;
    }

    public void setSimpleEq(TwoBandEq twoBandEq) {
        this.twoBandEq = twoBandEq;
    }

    public boolean isRunning() {
        return running.get();
    }

    public Delay getDelay() {
        return this.delay;
    }

    public Flanger getFlanger() {
        return this.flanger;
    }

    public Reverb getReverb() {
        return this.reverb;
    }
}
