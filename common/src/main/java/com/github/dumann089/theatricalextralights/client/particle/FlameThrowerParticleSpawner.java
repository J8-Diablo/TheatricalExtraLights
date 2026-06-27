package com.github.dumann089.theatricalextralights.client.particle;

import com.github.dumann089.theatricalextralights.particle.ModParticle;
import com.github.dumann089.theatricalextralights.util.DirectionOffset;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

/** Émission client des particules du lance-flammes. */
@Environment(EnvType.CLIENT)
public final class FlameThrowerParticleSpawner {
    private static final double MAX_SPAWN_DISTANCE_SQ = 128.0 * 128.0;
    private static final int BASE_PARTICLE_COUNT = 8;
    private static final int EXTRA_PARTICLE_VARIANCE = 5;

    private FlameThrowerParticleSpawner() {
    }

    public static void spawnJet(
            ClientLevel level,
            BlockPos blockPos,
            Direction facing,
            float panAngleDegrees,
            float intensity,
            RandomSource random
    ) {
        if (intensity <= 0.0f) {
            return;
        }

        float intensityFactor = Mth.clamp(intensity, 0.0f, 255.0f) / 255.0f;
        int count = Math.max(1, Math.round((BASE_PARTICLE_COUNT + random.nextInt(EXTRA_PARTICLE_VARIANCE + 1)) * intensityFactor));

        Vec3 nozzle = DirectionOffset.flameNozzlePosition(facing);
        Vec3 jetDirection = DirectionOffset.panJetDirection(facing, panAngleDegrees);

        double spawnX = blockPos.getX() + nozzle.x;
        double spawnY = blockPos.getY() + nozzle.y;
        double spawnZ = blockPos.getZ() + nozzle.z;

        for (int i = 0; i < count; i++) {
            level.addParticle(
                    ModParticle.FLAME_THROWER_JET.get(),
                    spawnX,
                    spawnY,
                    spawnZ,
                    jetDirection.x,
                    jetDirection.y,
                    jetDirection.z
            );
        }
    }

    public static double maxSpawnDistanceSq() {
        return MAX_SPAWN_DISTANCE_SQ;
    }
}
