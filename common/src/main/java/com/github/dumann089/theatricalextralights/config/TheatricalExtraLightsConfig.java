package com.github.dumann089.theatricalextralights.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TheatricalExtraLightsConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File FILE = new File("config/theatricalextralights.json");

    private boolean enableOverlay = true;
    private float laserBeamLength = 60.0f; // valor por defecto

    // Singleton
    private static TheatricalExtraLightsConfig INSTANCE = new TheatricalExtraLightsConfig();

    public static void load() {
        if (FILE.exists()) {
            try (FileReader reader = new FileReader(FILE)) {
                INSTANCE = GSON.fromJson(reader, TheatricalExtraLightsConfig.class);
                // Validar valor de laserBeamLength
                if (INSTANCE.laserBeamLength < 20.0f) {
                    INSTANCE.laserBeamLength = 20.0f;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            save();
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(FILE)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void ensureValidValues() {
        if (laserBeamLength < 20f) {
            laserBeamLength = 20f;
        }
    }

    // Getters
    public static boolean enableOverlay() {
        return INSTANCE.enableOverlay;
    }

    public static float getLaserBeamLength() {
        return INSTANCE.laserBeamLength;
    }

    // Setters
    public static void setEnableOverlay(boolean value) {
        INSTANCE.enableOverlay = value;
    }

    public static void setLaserBeamLength(float value) {
        INSTANCE.laserBeamLength = Math.max(20.0f, value);
    }
}