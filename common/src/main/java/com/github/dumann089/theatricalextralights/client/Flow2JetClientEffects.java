package com.github.dumann089.theatricalextralights.client;

import com.github.dumann089.theatricalextralights.blockentities.Flow2JetBlockEntity;
import com.github.dumann089.theatricalextralights.client.particle.Flow2JetDissipation;
import com.github.dumann089.theatricalextralights.client.particle.Flow2JetParticleSpawner;
import com.github.dumann089.theatricalextralights.client.sfx.SoundLoopManager;
import com.github.dumann089.theatricalextralights.firework.FireworkRenderDistances;
import com.github.dumann089.theatricalextralights.sounds.ModSounds;
import com.github.dumann089.theatricalextralights.util.FixtureJetDirection;
import dev.imabad.theatrical.blocks.HangableBlock;
import dev.imabad.theatrical.blocks.light.BaseLightBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Environment(EnvType.CLIENT)
public final class Flow2JetClientEffects {
    private static final double HEAR_DISTANCE = 48.0;
    private static final double HEAR_DISTANCE_SQ = HEAR_DISTANCE * HEAR_DISTANCE;
    private static final Map<BlockPos, Boolean> WAS_ACTIVE = new ConcurrentHashMap<>();

    private Flow2JetClientEffects() {
    }

    public static void tick(Flow2JetBlockEntity blockEntity) {
        boolean active = blockEntity.getIntensity() > 0;
        Minecraft minecraft = Minecraft.getInstance();
        BlockPos pos = blockEntity.getBlockPos();
        Vec3 center = pos.getCenter();

        boolean playerCanHear = minecraft.player != null
                && minecraft.player.distanceToSqr(center) <= HEAR_DISTANCE_SQ;
        float intensityFactor = FixtureJetDirection.intensityFactor((int) blockEntity.getIntensity());
        float volume = 0.08f + 0.14f * intensityFactor;

        if (active && playerCanHear) {
            SoundLoopManager.play(
                    blockEntity.getLevel(),
                    pos,
                    null,
                    ModSounds.FLOW2JET_LOOP.get(),
                    volume,
                    1.0f
            );
        } else {
            SoundLoopManager.stopLoop(pos);
        }

        if (!(blockEntity.getLevel() instanceof ClientLevel level)) {
            return;
        }

        float partial = minecraft.getFrameTime();
        float pan = blockEntity.getInterpolatedPan(partial);
        float userTilt = blockEntity.getInterpolatedTilt(partial);
        float[] beamStart = blockEntity.getFixture().getBeamStartPosition();
        float[] headPivot = blockEntity.getFixture().getPanRotationPosition();
        Direction facing = blockEntity.getBlockState().getValue(BaseLightBlock.FACING);
        boolean isHanging = ((HangableBlock) blockEntity.getBlockState().getBlock())
                .isHanging(blockEntity.getLevel(), pos);

        Vector3f jetDirection = FixtureJetDirection.directionFromFlow2JetPose(
                pos,
                facing,
                pan,
                userTilt,
                headPivot,
                beamStart,
                isHanging
        );
        Vec3 nozzle = FixtureJetDirection.beamWorldPositionFlow2Jet(
                pos,
                facing,
                pan,
                userTilt,
                headPivot,
                beamStart,
                isHanging
        );
        nozzle = Flow2JetParticleSpawner.adjustNozzleForFacing(facing, pos, nozzle);
        jetDirection = Flow2JetParticleSpawner.adjustDirectionForFacing(facing, jetDirection);

        if (active) {
            Flow2JetDissipation.markRunning(pos);
            WAS_ACTIVE.put(pos, true);

            if (!FireworkRenderDistances.isWithinClientFlameRange(nozzle.x, nozzle.y, nozzle.z)) {
                return;
            }

            Flow2JetParticleSpawner.spawnJet(
                    level,
                    pos,
                    facing,
                    pan,
                    userTilt,
                    headPivot,
                    beamStart,
                    isHanging,
                    (int) blockEntity.getIntensity(),
                    level.random
            );
        } else if (Boolean.TRUE.equals(WAS_ACTIVE.get(pos))) {
            WAS_ACTIVE.put(pos, false);
            Flow2JetDissipation.beginStop(pos, nozzle, jetDirection, level.getGameTime());
        }
    }

    public static void stop(BlockPos pos) {
        WAS_ACTIVE.remove(pos);
        Flow2JetDissipation.clear(pos);
        SoundLoopManager.stopAll(pos);
    }
}
