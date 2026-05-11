package com.github.dumann089.theatricalextralights.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TheatricalExtraLightsConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File FILE = new File("config/theatricalextralights.json");

    /* ================= DEFAULT VALUES ================= */

    private Float laserBeamLength = 400.0f;
    private Float rgbBarBeamLength = 9.0f;
    private Boolean renderLens = true;
    /**
     * Block IDs (e.g. "minecraft:black_concrete") that lasers pass through
     * instead of stopping on. Use this for scenic decor blocks (backdrops,
     * trusses made of regular blocks, etc.) so beams continue to a real wall
     * behind them. Mod blocks from "theatrical" and "theatricalextralights"
     * are always skipped — no need to list them.
     */
    private List<String> laserPassThroughBlocks = new java.util.ArrayList<>(Arrays.asList(
            "minecraft:glass",
            "minecraft:tinted_glass",
            "minecraft:iron_bars",
            "minecraft:barrier"
    ));

    private transient Set<String> laserPassThroughSet = null;

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

                    if (loaded.laserPassThroughBlocks != null)
                        defaults.laserPassThroughBlocks = loaded.laserPassThroughBlocks;
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

        if (laserPassThroughBlocks == null)
            laserPassThroughBlocks = new java.util.ArrayList<>();
        laserPassThroughSet = new HashSet<>(laserPassThroughBlocks);
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

    public static boolean isLaserPassThrough(String blockId) {
        if (INSTANCE.laserPassThroughSet == null) {
            INSTANCE.laserPassThroughSet = INSTANCE.laserPassThroughBlocks == null
                    ? new HashSet<>()
                    : new HashSet<>(INSTANCE.laserPassThroughBlocks);
        }
        return INSTANCE.laserPassThroughSet.contains(blockId);
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
}