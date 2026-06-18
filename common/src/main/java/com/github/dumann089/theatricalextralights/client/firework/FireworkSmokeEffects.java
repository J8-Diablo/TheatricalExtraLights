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

    private static boolean canSpawnNearPlayer(double x, double y, double z, double maxRange) {
        if (Minecraft.getInstance().player == null) {
            return false;
        }
        double dx = x - Minecraft.getInstance().player.getX();
        double dy = y - Minecraft.getInstance().player.getY();
        double dz = z - Minecraft.getInstance().player.getZ();
        return dx * dx + dy * dy + dz * dz <= maxRange * maxRange;
    }

    private static boolean consumeBudget() {
        if (!TheatricalExtraLightsConfig.isFireworkSmokeEnabled() || budgetThisTick <= 0) {
            return false;
        }
        budgetThisTick--;
        return true;
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
        if (!canSpawnNearPlayer(rocket.getX(), rocket.getY(), rocket.getZ(), 96.0)) {
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
        if (!canSpawnNearPlayer(rocket.getX(), rocket.getY(), rocket.getZ(), 96.0)) {
            return;
        }

        budgetThisTick--;
        spawnColoredDust(level, rocket.getLaunchColor(), rocket.getX(), rocket.getY(), rocket.getZ(),
                rocket.getDeltaMovement().x * -0.025 + (random.nextDouble() - 0.5) * 0.004,
                rocket.getDeltaMovement().y * -0.015,
                rocket.getDeltaMovement().z * -0.025 + (random.nextDouble() - 0.5) * 0.004,
                0.55f + random.nextFloat() * 0.35f);
    }

    /** Phase 1 — white launch plume at the tube mouth. */
    public static void trySpawnDaytimeLaunchPlume(FireworkRocketEntity rocket, RandomSource random, int flightLife) {
        if (flightLife > 6 || flightLife % 2 != 0) {
            return;
        }
        if (!(rocket.level() instanceof ClientLevel level)) {
            return;
        }
        if (!canSpawnNearPlayer(rocket.getX(), rocket.getY(), rocket.getZ(), 128.0)) {
            return;
        }
        for (int i = 0; i < 3; i++) {
            if (!consumeBudget()) {
                return;
            }
            double spread = 0.12;
            level.addParticle(
                    ParticleTypes.CLOUD,
                    rocket.getX() + (random.nextDouble() - 0.5) * spread,
                    rocket.getY() + (random.nextDouble() - 0.5) * spread * 0.4,
                    rocket.getZ() + (random.nextDouble() - 0.5) * spread,
                    (random.nextDouble() - 0.5) * 0.04,
                    0.06 + random.nextDouble() * 0.08,
                    (random.nextDouble() - 0.5) * 0.04
            );
        }
        if (consumeBudget()) {
            level.addParticle(
                    ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    rocket.getX(),
                    rocket.getY(),
                    rocket.getZ(),
                    (random.nextDouble() - 0.5) * 0.02,
                    0.12,
                    (random.nextDouble() - 0.5) * 0.02
            );
        }
    }

    /** Phase 2 — thick colored smoke trail with turbulence during ascent. */
    public static void trySpawnDaytimeFlightTrail(FireworkRocketEntity rocket, RandomSource random, int flightLife) {
        if (!rocket.getPreset().getPattern().isDaytimePowder()) {
            return;
        }
        if (flightLife % 2 != 0) {
            return;
        }
        if (!(rocket.level() instanceof ClientLevel level)) {
            return;
        }
        if (!canSpawnNearPlayer(rocket.getX(), rocket.getY(), rocket.getZ(), 128.0)) {
            return;
        }

        int[] palette = rocket.getColors();
        int color = palette[Math.floorMod(flightLife, palette.length)];
        for (int i = 0; i < 2; i++) {
            if (!consumeBudget()) {
                return;
            }
            double turb = 0.08;
            spawnColoredDust(
                    level,
                    color,
                    rocket.getX() + (random.nextDouble() - 0.5) * turb,
                    rocket.getY() + (random.nextDouble() - 0.5) * turb * 0.35,
                    rocket.getZ() + (random.nextDouble() - 0.5) * turb,
                    rocket.getDeltaMovement().x * -0.03 + (random.nextDouble() - 0.5) * 0.03,
                    rocket.getDeltaMovement().y * -0.02 + (random.nextDouble() - 0.5) * 0.02,
                    rocket.getDeltaMovement().z * -0.03 + (random.nextDouble() - 0.5) * 0.03,
                    1.0f + random.nextFloat() * 0.8f
            );
        }
    }

    /** Phase 3 — Holi / color-burst cloud at apex. */
    public static void spawnDaytimeBurstParticles(
            FireworkRocketEntity rocket,
            RandomSource random,
            int[] palette,
            double originX,
            double originY,
            double originZ,
            int count
    ) {
        if (!(rocket.level() instanceof ClientLevel level)) {
            return;
        }
        if (!canSpawnNearPlayer(originX, originY, originZ, 160.0)) {
            return;
        }

        for (int i = 0; i < count; i++) {
            if (!consumeBudget()) {
                return;
            }
            double theta = random.nextDouble() * Math.PI * 2.0;
            double cosPhi = random.nextDouble() * 0.75 + 0.05;
            double sinPhi = Math.sqrt(Math.max(0.0, 1.0 - cosPhi * cosPhi));
            double speed = 0.08 + random.nextDouble() * 0.35;
            double vx = sinPhi * Math.cos(theta) * speed;
            double vy = cosPhi * speed + 0.02;
            double vz = sinPhi * Math.sin(theta) * speed;
            int color = palette[random.nextInt(palette.length)];
            spawnColoredDust(
                    level,
                    color,
                    originX + (random.nextDouble() - 0.5) * 0.25,
                    originY + (random.nextDouble() - 0.5) * 0.25,
                    originZ + (random.nextDouble() - 0.5) * 0.25,
                    vx,
                    vy,
                    vz,
                    1.2f + random.nextFloat() * 1.0f
            );
        }
    }

    /** Phase 3 — ground fan / cone burst (rainbow powder fan). */
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
        if (!(rocket.level() instanceof ClientLevel level)) {
            return;
        }
        if (!canSpawnNearPlayer(originX, originY, originZ, 160.0)) {
            return;
        }

        int streams = 32;
        for (int stream = 0; stream < streams; stream++) {
            if (!consumeBudget()) {
                return;
            }
            float yawOffset = (-spreadDegrees * 0.5f) + (spreadDegrees * stream / Math.max(1, streams - 1));
            double yaw = baseYaw + Math.toRadians(yawOffset);
            double pitch = Math.toRadians(28.0 + random.nextDouble() * 38.0);
            double speed = 0.35 + random.nextDouble() * 0.45;
            double horizontal = Math.cos(pitch) * speed;
            double vx = -Math.sin(yaw) * horizontal;
            double vy = Math.sin(pitch) * speed;
            double vz = Math.cos(yaw) * horizontal;

            int color = palette[Math.floorMod(stream, palette.length)];
            spawnColoredDust(
                    level,
                    color,
                    originX + (random.nextDouble() - 0.5) * 0.1,
                    originY + (random.nextDouble() - 0.5) * 0.05,
                    originZ + (random.nextDouble() - 0.5) * 0.1,
                    vx,
                    vy,
                    vz,
                    1.3f + random.nextFloat() * 0.9f
            );
        }
    }

    private static void spawnColoredDust(
            ClientLevel level,
            int color,
            double x, double y, double z,
            double vx, double vy, double vz,
            float size
    ) {
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        DustParticleOptions dust = new DustParticleOptions(new Vector3f(r, g, b), size);
        level.addParticle(dust, x, y, z, vx, vy, vz);
    }
}
