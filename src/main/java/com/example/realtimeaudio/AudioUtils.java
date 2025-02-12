package com.example.realtimeaudio;

import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for audio-related settings and helper methods.
 */
public class AudioUtils {

    /**
     * Returns a default audio format.
     */
    public static AudioFormat getDefaultAudioFormat() {
        float sampleRate = 44100f; // 44.1 kHz
        int sampleSizeInBits = 16;
        int channels = 2;         // stereo
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    /**
     * Enumerates and returns all Mixer.Info that support a TargetDataLine (microphone or other input).
     */
    public static List<Mixer.Info> getAvailableInputMixers() {
        List<Mixer.Info> result = new ArrayList<>();
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();

        AudioFormat format = getDefaultAudioFormat();
        DataLine.Info targetDataLineInfo = new DataLine.Info(TargetDataLine.class, format);

        for (Mixer.Info mixerInfo : mixers) {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            // Check if this mixer supports the TargetDataLine with our format
            if (mixer.isLineSupported(targetDataLineInfo)) {
                result.add(mixerInfo);
            }
        }
        return result;
    }

    /**
     * Enumerates and returns all Mixer.Info that support a SourceDataLine (speakers / output).
     */
    public static List<Mixer.Info> getAvailableOutputMixers() {
        List<Mixer.Info> result = new ArrayList<>();
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();

        // Same default format you use for capturing
        AudioFormat format = getDefaultAudioFormat();
        DataLine.Info sourceDataLineInfo = new DataLine.Info(SourceDataLine.class, format);

        for (Mixer.Info mixerInfo : mixers) {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            if (mixer.isLineSupported(sourceDataLineInfo)) {
                result.add(mixerInfo);
            }
        }
        return result;
    }

}
