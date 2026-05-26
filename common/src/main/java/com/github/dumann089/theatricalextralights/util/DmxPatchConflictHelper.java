package com.github.dumann089.theatricalextralights.util;

import dev.imabad.theatrical.api.dmx.DMXConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Détecte les chevauchements d'adresses DMX entre fixtures chargées (avertissement non bloquant).
 */
public final class DmxPatchConflictHelper {

    public record DmxConflict(String fixtureName, int startAddress, int endAddress) {
    }

    private DmxPatchConflictHelper() {
    }

    public static boolean rangesOverlap(int startA, int countA, int startB, int countB) {
        if (countA <= 0 || countB <= 0 || startA < 1 || startB < 1) {
            return false;
        }
        int endA = startA + countA - 1;
        int endB = startB + countB - 1;
        return startA <= endB && startB <= endA;
    }

    /**
     * Parcourt les chunks chargés autour du joueur. Les fixtures hors chunk ne sont pas vérifiées.
     */
    public static List<DmxConflict> findConflicts(
            Level level,
            BlockPos excludePos,
            UUID networkId,
            int universe,
            int address,
            int channelCount
    ) {
        if (level == null || channelCount <= 0 || address < 1) {
            return List.of();
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return List.of();
        }

        List<DmxConflict> conflicts = new ArrayList<>();
        BlockPos center = minecraft.player.blockPosition();
        int chunkRadius = minecraft.options.getEffectiveRenderDistance() + 2;
        int centerChunkX = center.getX() >> 4;
        int centerChunkZ = center.getZ() >> 4;

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
                    BlockPos otherPos = blockEntity.getBlockPos();
                    if (otherPos.equals(excludePos)) {
                        continue;
                    }
                    if (!consumer.getNetworkId().equals(networkId)) {
                        continue;
                    }
                    if (consumer.getUniverse() != universe) {
                        continue;
                    }
                    int otherStart = consumer.getChannelStart();
                    int otherCount = consumer.getChannelCount();
                    if (!rangesOverlap(address, channelCount, otherStart, otherCount)) {
                        continue;
                    }
                    int otherEnd = otherStart + Math.max(otherCount, 1) - 1;
                    String name = Component.translatable(consumer.getTranslationKey()).getString();
                    conflicts.add(new DmxConflict(name, otherStart, otherEnd));
                }
            }
        }
        return conflicts;
    }
}
