package com.github.dumann089.theatricalextralights.firework;

import com.github.dumann089.theatricalextralights.entities.FireworkRocketEntity;
import net.minecraft.util.RandomSource;

/**
 * Defines a firework's behavior across all phases. Concrete subclasses populate the
 * rocket's spark list at the appropriate moments and tune light/halo parameters.
 */
public abstract class BurstPattern {

    public abstract boolean isBurst();

    public boolean hasCrackleSound() {
        return false;
    }

    public abstract int getBurstDuration();

    public int getCometFadeTicks() {
        return 0;
    }

    /** Called client-side every tick during flight. Spawn trail sparks here. */
    public void onFlightTick(FireworkRocketEntity rocket, RandomSource random) {
    }

    /** Called client-side once when the burst starts. Populate initial sparks. */
    public void onBurstStart(FireworkRocketEntity rocket, RandomSource random) {
    }

    /** Called client-side every burst tick (tickIndex starts at 0, increments). */
    public void onBurstTick(FireworkRocketEntity rocket, RandomSource random, int tickIndex) {
    }

    /** Called client-side every fade tick for non-burst comets. */
    public void onFadeTick(FireworkRocketEntity rocket, RandomSource random, int remainingTicks) {
    }

    public abstract int getFlightLifetime();

    public double getGravity() {
        return 0.025;
    }

    public double getDrag() {
        return 0.99;
    }

    public float getLaunchSpeedMultiplier() {
        return 1.0f;
    }

    public int getFlightLuminance() {
        return 6;
    }

    public float getFlightLightSpread() {
        return 22.0f;
    }

    public int getBurstLuminance(int tickIndex) {
        if (tickIndex >= 3) {
            return 0;
        }
        return Math.max(0, 8 - tickIndex * 3);
    }

    public float getBurstLightSpread(int tickIndex) {
        if (tickIndex >= 3) {
            return 0.0f;
        }
        float fade = 1.0f - tickIndex / 3.0f;
        return 50.0f * fade;
    }

    public float getFlightHaloInnerSize() {
        return 1.0f;
    }

    public float getFlightHaloOuterSize() {
        return 1.5f;
    }

    public float getBurstHaloInnerSize(int tickIndex) {
        if (tickIndex < 4) {
            return 5.0f;
        }
        return Math.max(0.0f, 5.0f - (tickIndex - 4) * 0.6f);
    }

    public float getBurstHaloOuterSize(int tickIndex) {
        if (tickIndex < 4) {
            return 8.0f;
        }
        return Math.max(0.0f, 8.0f - (tickIndex - 4) * 0.9f);
    }

    /**
     * Lateral drift amplitude added each tick during flight. Real rockets are never
     * perfectly straight — small wobble adds organic feel. Whistler / spinner override
     * with much higher values to convey spinning.
     */
    public double getFlightWobble() {
        return 0.005;
    }

    /**
     * If true, apex detection is skipped — the rocket keeps flying past its peak,
     * falling due to gravity, until it reaches its flight lifetime or collides.
     * Used by patterns that should leave a continuous trail across the full
     * ascent + descent (whistler, etc.) rather than stopping at the peak.
     */
    public boolean continuesAfterApex() {
        return false;
    }

    /**
     * Helper: ambient drifting embers added on top of a main burst. Tiny slow sparks
     * with random direction, long lifetime, palette-color variation. Gives the burst
     * a "natural firework" trailing-dust feel.
     */
    protected static void spawnEmbers(FireworkRocketEntity rocket, RandomSource random, int count) {
        int[] palette = rocket.getColors();
        double cx = rocket.getX();
        double cy = rocket.getY();
        double cz = rocket.getZ();
        for (int i = 0; i < count; i++) {
            double theta = random.nextDouble() * Math.PI * 2.0;
            double cosPhi = 2.0 * random.nextDouble() - 1.0;
            double sinPhi = Math.sqrt(Math.max(0.0, 1.0 - cosPhi * cosPhi));
            double speed = 0.20 + random.nextDouble() * 0.55;
            double vx = sinPhi * Math.cos(theta) * speed;
            double vy = cosPhi * speed + 0.04;
            double vz = sinPhi * Math.sin(theta) * speed;
            float scale = 0.14f + random.nextFloat() * 0.10f;
            int lifetime = 90 + random.nextInt(80);
            int color = palette[random.nextInt(palette.length)];
            rocket.addSpark(new Spark(cx, cy, cz, vx, vy, vz, color, scale, lifetime, 0.003f, 0.998f, false, false));
        }
    }
}
