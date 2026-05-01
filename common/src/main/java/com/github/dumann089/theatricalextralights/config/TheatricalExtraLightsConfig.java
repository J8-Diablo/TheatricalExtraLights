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

    /* ================= DEFAULT VALUES ================= */

    private Float laserBeamLength = 60.0f;
    private Float rgbBarBeamLength = 9.0f;
    private Boolean renderLens = true;
    private Boolean render2DBeam = false;

    /* ================= SINGLETON ================= */

    private static TheatricalExtraLightsConfig INSTANCE = new TheatricalExtraLightsConfig();

    public static void load() {
        TheatricalExtraLightsConfig defaults = new TheatricalExtraLightsConfig();

        if (FILE.exists()) {
            try (FileReader reader = new FileReader(FILE)) {

                TheatricalExtraLightsConfig loaded =
                        GSON.fromJson(reader, TheatricalExtraLightsConfig.class);

                if (loaded != null) {

                    if (loaded.laserBeamLength != null)
                        defaults.laserBeamLength = loaded.laserBeamLength;

                    if (loaded.rgbBarBeamLength != null)
                        defaults.rgbBarBeamLength = loaded.rgbBarBeamLength;

                    if (loaded.renderLens != null)
                        defaults.renderLens = loaded.renderLens;

                    if (loaded.render2DBeam != null)
                        defaults.render2DBeam = loaded.render2DBeam;
                }

                INSTANCE = defaults;
                INSTANCE.ensureValidValues();
                save();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            INSTANCE = defaults;
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
        if (laserBeamLength == null || laserBeamLength < 20f)
            laserBeamLength = 20f;

        if (rgbBarBeamLength == null || rgbBarBeamLength < 1f)
            rgbBarBeamLength = 1f;

        if (renderLens == null)
            renderLens = true;

        if (render2DBeam == null)
            render2DBeam = true;
    }
    
    /* ================= GETTERS ================= */

    public static float getLaserBeamLength() {
        return INSTANCE.laserBeamLength;
    }

    public static float getRgbBarBeamLength() {
        return INSTANCE.rgbBarBeamLength;
    }

    public static boolean shouldRenderLens() {
        return INSTANCE.renderLens;
    }

    public static boolean shouldRender2DBeam() {
        return INSTANCE.render2DBeam;
    }

    /* ================= SETTERS ================= */

    public static void setLaserBeamLength(float value) {
        INSTANCE.laserBeamLength = Math.max(20f, value);
    }

    public static void setRgbBarBeamLength(float value) {
        INSTANCE.rgbBarBeamLength = Math.max(1f, value);
    }

    public static void setRenderLens(boolean value) {
        INSTANCE.renderLens = value;
    }

    public static void setRender2DBeam(boolean value) {
        INSTANCE.render2DBeam = value;
    }
}