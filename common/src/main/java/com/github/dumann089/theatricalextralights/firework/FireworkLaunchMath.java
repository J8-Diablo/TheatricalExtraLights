package com.github.dumann089.theatricalextralights.firework;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public final class FireworkLaunchMath {
    private static final float BASE_LAUNCH_SPEED = 2.0f;
    private static final float MIN_LAUNCH_POWER = 0.7f;
    private static final float MAX_LAUNCH_POWER = 2.6f;

    private FireworkLaunchMath() {
    }

    public static float launchPowerFromDmx(int focus) {
        int dmx = Mth.clamp(focus, 0, 255);
        return Mth.lerp(dmx / 255.0f, MIN_LAUNCH_POWER, MAX_LAUNCH_POWER);
    }

    public static Vec3 computeVelocity(
            Direction launchFacing,
            float pitchDegrees,
            float yawOffsetDegrees,
            float launchPowerMultiplier,
            float patternSpeedMultiplier,
            RandomSource random
    ) {
        float yawRad = (launchFacing.toYRot() + yawOffsetDegrees) * Mth.DEG_TO_RAD;
        float pitchRad = pitchDegrees * Mth.DEG_TO_RAD;
        float launchSpeed = BASE_LAUNCH_SPEED * launchPowerMultiplier * patternSpeedMultiplier;

        double horizontal = Math.cos(pitchRad) * launchSpeed;
        double x = -Mth.sin(yawRad) * horizontal;
        double y = Mth.sin(pitchRad) * launchSpeed;
        double z = Mth.cos(yawRad) * horizontal;

        double driftX = (random.nextDouble() * 2.0 - 1.0) * 0.12;
        double driftZ = (random.nextDouble() * 2.0 - 1.0) * 0.12;
        return new Vec3(x + driftX, y, z + driftZ);
    }
}
