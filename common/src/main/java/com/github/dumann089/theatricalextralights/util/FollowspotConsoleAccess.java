package com.github.dumann089.theatricalextralights.util;

import com.github.dumann089.theatricalextralights.blockentities.FollowspotConsoleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class FollowspotConsoleAccess {

    private static final double MAX_USE_DISTANCE_SQR = 8.0 * 8.0;

    private FollowspotConsoleAccess() {
    }

    public static boolean canPlayerUse(ServerPlayer player, BlockPos consolePos) {
        if (!player.level().isLoaded(consolePos)) {
            return false;
        }
        BlockEntity blockEntity = player.level().getBlockEntity(consolePos);
        if (!(blockEntity instanceof FollowspotConsoleBlockEntity)) {
            return false;
        }
        return player.distanceToSqr(
                consolePos.getX() + 0.5,
                consolePos.getY() + 0.5,
                consolePos.getZ() + 0.5
        ) <= MAX_USE_DISTANCE_SQR;
    }
}
