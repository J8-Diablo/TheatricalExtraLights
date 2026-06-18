package com.github.dumann089.theatricalextralights.client.firework;

import com.github.dumann089.theatricalextralights.config.TheatricalExtraLightsConfig;
import com.github.dumann089.theatricalextralights.entities.FireworkRocketEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import org.joml.Vector3f;

public final class FireworkSmokeEffects {
    private static int budgetThisTick;
    private static long budgetTick = Long.MIN_VALUE;

    private FireworkSmokeEffects() {
    }

    public static void beginClientTick(long gameTime) {
        if (gameTime != budgetTick) {
            budgetTick = gameTime;
            budgetThisTick = TheatricalExtraLightsConfig.getFireworkSmokeBudgetPerTick();
        }
    }

    public static void trySpawnFlightSmoke(FireworkRocketEntity rocket, RandomSource random, int flightLife) {
        if (!TheatricalExtraLightsConfig.isFireworkSmokeEnabled() || budgetThisTick <= 0) {
            return;
        }
        if (!rocket.getPreset().getPattern().spawnsFlightSmoke()) {
            return;
        }
        if (rocket.getPreset().getPattern().isDaytimePowder()) {
            return;
        }
        if (flightLife % TheatricalExtraLightsConfig.getFireworkSmokeSpawnInterval() != 0) {
            return;
        }
        if (!(rocket.level() instanceof ClientLevel level)) {
            return;
        }
        if (Minecraft.getInstance().player == null) {
            return;
        }
        double dx = rocket.getX() - Minecraft.getInstance().player.getX();
        double dy = rocket.getY() - Minecraft.getInstance().player.getY();
        double dz = rocket.getZ() - Minecraft.getInstance().player.getZ();
        if (dx * dx + dy * dy + dz * dz > 96.0 * 96.0) {
            return;
        }

        budgetThisTick--;
        double vx = rocket.getDeltaMovement().x * -0.04 + (random.nextDouble() - 0.5) * 0.01;
        double vy = rocket.getDeltaMovement().y * -0.04 + 0.01;
        double vz = rocket.getDeltaMovement().z * -0.04 + (random.nextDouble() - 0.5) * 0.01;
        level.addParticle(
                ParticleTypes.CAMPFIRE_COSY_SMOKE,
                rocket.getX(),
                rocket.getY(),
                rocket.getZ(),
                vx,
                vy,
                vz
        );
    }

    public static void trySpawnPowderParticle(FireworkRocketEntity rocket, RandomSource random, int flightLife) {
        if (!TheatricalExtraLightsConfig.isFireworkSmokeEnabled() || budgetThisTick <= 0) {
            return;
        }
        if (!rocket.getPreset().getPattern().usesColoredPowderParticles()) {
            return;
        }
        if (flightLife % TheatricalExtraLightsConfig.getFireworkSmokeSpawnInterval() != 0) {
            return;
        }
        if (!(rocket.level() instanceof ClientLevel level)) {
            return;
        }
        if (Minecraft.getInstance().player == null) {
            return;
        }
        double dx = rocket.getX() - Minecraft.getInstance().player.getX();
        double dy = rocket.getY() - Minecraft.getInstance().player.getY();
        double dz = rocket.getZ() - Minecraft.getInstance().player.getZ();
        if (dx * dx + dy * dy + dz * dz > 96.0 * 96.0) {
            return;
        }

        budgetThisTick--;
        int color = rocket.getLaunchColor();
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float size = 0.55f + random.nextFloat() * 0.35f;
        DustParticleOptions dust = new DustParticleOptions(new Vector3f(r, g, b), size);
        double vx = rocket.getDeltaMovement().x * -0.025 + (random.nextDouble() - 0.5) * 0.004;
        double vy = rocket.getDeltaMovement().y * -0.015;
        double vz = rocket.getDeltaMovement().z * -0.025 + (random.nextDouble() - 0.5) * 0.004;
        level.addParticle(dust, rocket.getX(), rocket.getY(), rocket.getZ(), vx, vy, vz);
    }

    /** Extra colored dust along each fan stream — visible in daylight. */
    public static void spawnDaytimeFanParticles(
            FireworkRocketEntity rocket,
            RandomSource random,
            int[] palette,
            double originX,
            double originY,
            double originZ,
            double baseYaw,
            float spreadDegrees
    ) {
        if (!TheatricalExtraLightsConfig.isFireworkSmokeEnabled()) {
            return;
        }
        if (!(rocket.level() instanceof ClientLevel level)) {
            return;
        }
        if (Minecraft.getInstance().player == null) {
            return;
        }
        double dx = originX - Minecraft.getInstance().player.getX();
        double dy = originY - Minecraft.getInstance().player.getY();
        double dz = originZ - Minecraft.getInstance().player.getZ();
        if (dx * dx + dy * dy + dz * dz > 128.0 * 128.0) {
            return;
        }

        int streams = 24;
        for (int stream = 0; stream < streams; stream++) {
            if (budgetThisTick <= 0) {
                return;
            }
            float yawOffset = (-spreadDegrees * 0.5f) + (spreadDegrees * stream / Math.max(1, streams - 1));
            double yaw = baseYaw + Math.toRadians(yawOffset);
            double pitch = Math.toRadians(34.0 + random.nextDouble() * 12.0);
            double speed = 0.55 + random.nextDouble() * 0.25;
            double horizontal = Math.cos(pitch) * speed;
            double vx = -Math.sin(yaw) * horizontal;
            double vy = Math.sin(pitch) * speed;
            double vz = Math.cos(yaw) * horizontal;

            int color = palette[Math.floorMod(stream, palette.length)];
            float r = ((color >> 16) & 0xFF) / 255.0f;
            float g = ((color >> 8) & 0xFF) / 255.0f;
            float b = (color & 0xFF) / 255.0f;
            DustParticleOptions dust = new DustParticleOptions(new Vector3f(r, g, b), 1.1f + random.nextFloat() * 0.6f);
            budgetThisTick--;
            level.addParticle(
                    dust,
                    originX + (random.nextDouble() - 0.5) * 0.08,
                    originY + (random.nextDouble() - 0.5) * 0.04,
                    originZ + (random.nextDouble() - 0.5) * 0.08,
                    vx,
                    vy,
                    vz
            );
        }
    }
}
