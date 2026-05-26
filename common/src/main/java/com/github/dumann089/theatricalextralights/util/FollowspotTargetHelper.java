package com.github.dumann089.theatricalextralights.util;

import dev.imabad.theatrical.api.dmx.DMXConsumer;
import dev.imabad.theatrical.blockentities.light.BaseLightBlockEntity;
import dev.imabad.theatrical.util.UUIDUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Optional;
import java.util.UUID;

public final class FollowspotTargetHelper {

    public static final int REQUIRED_CHANNEL_COUNT = 7;

    private FollowspotTargetHelper() {
    }

    public record TargetMatch(BlockPos pos, BaseLightBlockEntity fixture, String translationKey) {
    }

    public static Optional<TargetMatch> findTarget(Level level, UUID networkId, int universe, int address) {
        return findTarget(level, networkId, universe, address, null);
    }

    public static Optional<TargetMatch> findTarget(Level level, UUID networkId, int universe, int address,
                                                   BlockPos searchCenter) {
        if (level == null || address < 1) {
            return Optional.empty();
        }

        int chunkRadius = 8;
        BlockPos origin = searchCenter;
        if (origin == null) {
            if (level.players().isEmpty()) {
                origin = level.getSharedSpawnPos();
            } else {
                origin = level.players().get(0).blockPosition();
            }
        }

        int centerChunkX = origin.getX() >> 4;
        int centerChunkZ = origin.getZ() >> 4;

        for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
            for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                int chunkX = centerChunkX + dx;
                int chunkZ = centerChunkZ + dz;
                if (!level.hasChunk(chunkX, chunkZ)) {
                    continue;
                }
                LevelChunk chunk = level.getChunk(chunkX, chunkZ);
                for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                    if (!(blockEntity instanceof DMXConsumer consumer)) {
                        continue;
                    }
                    if (!(blockEntity instanceof BaseLightBlockEntity light)) {
                        continue;
                    }
                    if (!consumer.getNetworkId().equals(networkId)) {
                        continue;
                    }
                    if (consumer.getUniverse() != universe) {
                        continue;
                    }
                    if (consumer.getChannelStart() != address) {
                        continue;
                    }
                    if (consumer.getChannelCount() != REQUIRED_CHANNEL_COUNT) {
                        continue;
                    }
                    return Optional.of(new TargetMatch(
                            blockEntity.getBlockPos(),
                            light,
                            consumer.getTranslationKey()
                    ));
                }
            }
        }
        return Optional.empty();
    }

    public static boolean isValidNetwork(UUID networkId) {
        return networkId != null && !networkId.equals(UUIDUtil.NULL);
    }
}
