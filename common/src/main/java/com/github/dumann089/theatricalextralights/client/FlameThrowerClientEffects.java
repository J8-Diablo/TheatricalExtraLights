package com.github.dumann089.theatricalextralights.client;

import com.github.dumann089.theatricalextralights.blockentities.FlameThrowerBlockEntity;
import com.github.dumann089.theatricalextralights.client.particle.FlameThrowerParticleSpawner;
import com.github.dumann089.theatricalextralights.client.sfx.SoundLoopManager;
import com.github.dumann089.theatricalextralights.sounds.ModSounds;
import dev.imabad.theatrical.blocks.light.BaseLightBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

@Environment(EnvType.CLIENT)
public final class FlameThrowerClientEffects {
    private FlameThrowerClientEffects() {
    }

    public static void tick(FlameThrowerBlockEntity blockEntity) {
        boolean active = blockEntity.getIntensity() > 0;
        if (blockEntity.updateClientActiveState(active)) {
            if (active) {
                SoundLoopManager.play(
                        blockEntity.getLevel(),
                        blockEntity.getBlockPos(),
                        null,
                        ModSounds.FLAME_THROWER_LOOP.get(),
                        0.5f,
                        1.0f
                );
            } else {
                SoundLoopManager.stopLoop(blockEntity.getBlockPos());
            }
        }

        if (!active) {
            return;
        }

        if (!(blockEntity.getLevel() instanceof ClientLevel level)) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null
                && minecraft.player.distanceToSqr(blockEntity.getBlockPos().getCenter()) > FlameThrowerParticleSpawner.maxSpawnDistanceSq()) {
            return;
        }

        Direction facing = blockEntity.getBlockState().getValue(BaseLightBlock.FACING);
        float headRenderAngle = blockEntity.getHeadRenderAngle(minecraft.getFrameTime());

        FlameThrowerParticleSpawner.spawnJet(
                level,
                blockEntity.getBlockPos(),
                facing,
                headRenderAngle,
                blockEntity.getIntensity(),
                level.random
        );
    }

    public static void stop(BlockPos pos) {
        SoundLoopManager.stopAll(pos);
    }
}
