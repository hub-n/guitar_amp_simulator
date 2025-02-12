package com.example.realtimeaudio;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class PresetsManager {
    final private List<SaveableObject> monitoredItems= new ArrayList<>();
    private Map<String, Map<String, Double>> presets = new HashMap<>();
    private final String filename = "presets.json";

    public PresetsManager(List<SaveableObject> itemsToMonitor) {
        monitoredItems.addAll(itemsToMonitor);

        Gson gson = new Gson();

        try (FileReader reader = new FileReader(filename)) {
            Type type = new TypeToken<Map<String, Map<String, Double>>>(){}.getType();
            presets = gson.fromJson(reader, type);
        } catch (FileNotFoundException e) {
            savePreset("Default");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (presets == null) {
            presets = new HashMap<>();
            savePreset("Default");
        }
    }

    public void savePreset(String name) {
        Map<String, Double> preset = new HashMap<>();
        for (SaveableObject item : monitoredItems) {
            preset.put(item.getId(), item.getValue());
        }

        presets.put(name, preset);

        Gson gson = new Gson();

        String json = gson.toJson(presets);

        try(FileWriter writer = new FileWriter(filename)) {
            writer.write(json);
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    public void loadPreset(String name) {
        Map<String, Double> preset = presets.get(name);
        if (preset == null) {
            return;
        }

        for (SaveableObject item : monitoredItems) {
            item.setValue(preset.get(item.getId()));
        }
    }

    public List<String> getPresetNames() {
        return new ArrayList<>(presets.keySet());
    }
}
