package com.github.dumann089.theatricalextralights.client.particle;

import com.github.dumann089.theatricalextralights.firework.FireworkRenderDistances;
import com.github.dumann089.theatricalextralights.particle.ModParticle;
import com.github.dumann089.theatricalextralights.util.FixtureJetDirection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public final class Flow2JetParticleSpawner {
    private static final int BASE_COLUMN_COUNT = 12;
    private static final int EXTRA_COLUMN_VARIANCE = 8;
    private static final int BASE_CROWN_COUNT = 5;
    private static final int EXTRA_CROWN_VARIANCE = 4;
    private static final float COLUMN_CONE_DEGREES = 8f;
    private static final float COLUMN_MIN_DISTANCE = 0.08f;
    private static final float COLUMN_MAX_DISTANCE = 1.05f;
    private static final float CROWN_MIN_DISTANCE = 0.7f;
    private static final float CROWN_MAX_DISTANCE = 1.45f;

    private Flow2JetParticleSpawner() {
    }

    public static void spawnJet(
            ClientLevel level,
            BlockPos blockPos,
            Direction facing,
            float pan,
            float userTilt,
            float[] headPivot,
            float[] beamStart,
            boolean hanging,
            int intensity,
            RandomSource random
    ) {
        if (intensity <= 0) {
            return;
        }

        Vector3f jetDirection = FixtureJetDirection.directionFromFlow2JetPose(
                blockPos,
                facing,
                pan,
                userTilt,
                headPivot,
                beamStart,
                hanging
        );
        Vec3 nozzle = FixtureJetDirection.beamWorldPositionFlow2Jet(
                blockPos,
                facing,
                pan,
                userTilt,
                headPivot,
                beamStart,
                hanging
        );

        if (facing.getAxis() == Direction.Axis.X) {
            nozzle = mirrorAroundBlockCenter(nozzle, blockPos);
            jetDirection = mirrorDirection(jetDirection);
        }

        if (!FireworkRenderDistances.isWithinClientFlameRange(nozzle.x, nozzle.y, nozzle.z)) {
            return;
        }

        float intensityFactor = FixtureJetDirection.intensityFactor(intensity);
        int columnCount = Math.max(4, Math.round((BASE_COLUMN_COUNT + random.nextInt(EXTRA_COLUMN_VARIANCE + 1)) * intensityFactor));
        int crownCount = Math.max(2, Math.round((BASE_CROWN_COUNT + random.nextInt(EXTRA_CROWN_VARIANCE + 1)) * intensityFactor));
        float columnSpeed = 0.48f + 0.38f * intensityFactor;

        for (int i = 0; i < columnCount; i++) {
            float along = Mth.lerp(random.nextFloat(), COLUMN_MIN_DISTANCE, COLUMN_MAX_DISTANCE) * intensityFactor;
            float coneRadius = along * 0.07f;
            Vec3 axisPoint = FixtureJetDirection.pointAlongJet(nozzle, jetDirection, along);
            Vec3 spawn = axisPoint.add(Co2SmokePhysics.randomDisk(jetDirection, random, coneRadius));

            float spread = COLUMN_CONE_DEGREES * (0.65f + random.nextFloat() * 0.75f);
            Vec3 velocity = Co2SmokePhysics.randomUnitCone(jetDirection, spread, random)
                    .scale(columnSpeed + random.nextFloat() * 0.12f);

            level.addParticle(
                    ModParticle.CO2_JET_PUFF.get(),
                    true,
                    spawn.x,
                    spawn.y,
                    spawn.z,
                    velocity.x,
                    velocity.y,
                    velocity.z
            );
        }

        for (int i = 0; i < crownCount; i++) {
            float along = Mth.lerp(random.nextFloat(), CROWN_MIN_DISTANCE, CROWN_MAX_DISTANCE) * intensityFactor;
            Vec3 axisPoint = FixtureJetDirection.pointAlongJet(nozzle, jetDirection, along);
            Vec3 spawn = axisPoint.add(Co2SmokePhysics.randomDisk(jetDirection, random, 0.18f + along * 0.06f));

            float drift = 0.05f + random.nextFloat() * 0.07f;
            Vec3 velocity = new Vec3(
                    jetDirection.x() * drift,
                    jetDirection.y() * drift,
                    jetDirection.z() * drift
            );

            level.addParticle(
                    ModParticle.CO2_JET_PUFF.get(),
                    true,
                    spawn.x,
                    spawn.y,
                    spawn.z,
                    velocity.x,
                    velocity.y,
                    velocity.z
            );
        }
    }

    private static Vec3 mirrorAroundBlockCenter(Vec3 world, BlockPos blockPos) {
        double cx = blockPos.getX() + 0.5;
        double cz = blockPos.getZ() + 0.5;
        return new Vec3(2.0 * cx - world.x, world.y, 2.0 * cz - world.z);
    }

    private static Vector3f mirrorDirection(Vector3f direction) {
        return new Vector3f(-direction.x(), direction.y(), -direction.z());
    }
}
