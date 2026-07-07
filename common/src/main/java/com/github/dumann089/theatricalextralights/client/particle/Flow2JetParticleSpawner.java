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
    private static final int BASE_CORE_COUNT = 22;
    private static final int EXTRA_CORE_VARIANCE = 12;
    private static final int BASE_PUFF_COUNT = 10;
    private static final int EXTRA_PUFF_VARIANCE = 8;
    private static final double PUFF_MIN_DISTANCE = 0.2;
    private static final double PUFF_MAX_DISTANCE = 1.2;

    private Flow2JetParticleSpawner() {
    }

    public static void spawnJet(
            ClientLevel level,
            BlockPos blockPos,
            Direction facing,
            float pan,
            float tilt,
            float[] pivot,
            float[] beamStart,
            int intensity,
            RandomSource random
    ) {
        if (intensity <= 0) {
            return;
        }

        Vec3 nozzle = FixtureJetDirection.beamWorldPosition(blockPos, facing, pan, tilt, pivot, beamStart);
        if (!FireworkRenderDistances.isWithinClientFlameRange(nozzle.x, nozzle.y, nozzle.z)) {
            return;
        }

        float intensityFactor = FixtureJetDirection.intensityFactor(intensity);
        int coreCount = Math.max(3, Math.round((BASE_CORE_COUNT + random.nextInt(EXTRA_CORE_VARIANCE + 1)) * intensityFactor));
        int puffCount = Math.max(2, Math.round((BASE_PUFF_COUNT + random.nextInt(EXTRA_PUFF_VARIANCE + 1)) * intensityFactor));
        float speedScale = 0.5f + 0.8f * intensityFactor;

        Vector3f jetDirection = FixtureJetDirection.computeDirection(pan, tilt, facing);
        double dirX = jetDirection.x();
        double dirY = jetDirection.y();
        double dirZ = jetDirection.z();

        for (int i = 0; i < coreCount; i++) {
            level.addParticle(
                    ModParticle.CO2_JET_CORE.get(),
                    true,
                    nozzle.x,
                    nozzle.y,
                    nozzle.z,
                    dirX * speedScale,
                    dirY * speedScale,
                    dirZ * speedScale
            );
        }

        for (int i = 0; i < puffCount; i++) {
            double along = Mth.lerp(random.nextDouble(), PUFF_MIN_DISTANCE, PUFF_MAX_DISTANCE) * intensityFactor;
            Vec3 puffPos = FixtureJetDirection.pointAlongJet(nozzle, jetDirection, along);
            level.addParticle(
                    ModParticle.CO2_JET_PUFF.get(),
                    true,
                    puffPos.x,
                    puffPos.y,
                    puffPos.z,
                    dirX * speedScale * 0.35,
                    dirY * speedScale * 0.35,
                    dirZ * speedScale * 0.35
            );
        }
    }
}
