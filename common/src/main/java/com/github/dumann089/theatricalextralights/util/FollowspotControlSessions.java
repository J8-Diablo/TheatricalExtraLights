package com.github.dumann089.theatricalextralights.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class FollowspotControlSessions {

    public record SavedState(Vec3 position, float yaw, float pitch, boolean flying, boolean noPhysics) {
    }

    private static final Map<UUID, SavedState> RETURN_STATES = new HashMap<>();

    private FollowspotControlSessions() {
    }

    public static void begin(ServerPlayer player) {
        RETURN_STATES.put(player.getUUID(), new SavedState(
                player.position(),
                player.getYRot(),
                player.getXRot(),
                player.getAbilities().flying,
                player.isNoGravity()
        ));
        player.getAbilities().flying = true;
        player.getAbilities().mayfly = true;
        player.setNoGravity(true);
        player.onUpdateAbilities();
    }

    public static void end(ServerPlayer player) {
        SavedState state = RETURN_STATES.remove(player.getUUID());
        if (state == null) {
            return;
        }
        player.teleportTo(state.position.x, state.position.y, state.position.z);
        player.setYRot(state.yaw);
        player.setXRot(state.pitch);
        player.yRotO = state.yaw;
        player.xRotO = state.pitch;
        player.getAbilities().flying = state.flying;
        player.getAbilities().mayfly = player.gameMode.getGameModeForPlayer() == GameType.CREATIVE
                || player.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
        player.setNoGravity(state.noPhysics);
        player.onUpdateAbilities();
    }

    public static boolean isActive(UUID playerId) {
        return RETURN_STATES.containsKey(playerId);
    }
}
