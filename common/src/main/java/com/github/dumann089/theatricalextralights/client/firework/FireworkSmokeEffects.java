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
        if (flightLife % (TheatricalExtraLightsConfig.getFireworkSmokeSpawnInterval() * 2) != 0) {
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
        float size = 1.6f + random.nextFloat() * 0.6f;
        DustParticleOptions dust = new DustParticleOptions(new Vector3f(r, g, b), size);
        double vx = rocket.getDeltaMovement().x * -0.02 + (random.nextDouble() - 0.5) * 0.008;
        double vy = rocket.getDeltaMovement().y * -0.02 + 0.004;
        double vz = rocket.getDeltaMovement().z * -0.02 + (random.nextDouble() - 0.5) * 0.008;
        level.addParticle(dust, rocket.getX(), rocket.getY(), rocket.getZ(), vx, vy, vz);
    }
}
