package com.github.dumann089.theatricalextralights.firework;

import com.github.dumann089.theatricalextralights.config.TheatricalExtraLightsConfig;
import com.github.dumann089.theatricalextralights.entities.FireworkRocketEntity;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Limits concurrent firework rockets and reclaims orphans that stack in sky chunks.
 */
public final class FireworkRocketTracker {
    private static final AABB WORLD_BOUNDS = new AABB(-3.0E7, -64, -3.0E7, 3.0E7, 320, 3.0E7);
    private static final int ABSOLUTE_MAX_TICKS = 420;
    private static final double PLAYER_KEEP_RANGE = 160.0;

    private FireworkRocketTracker() {
    }

    public static void registerEvents() {
        TickEvent.SERVER_LEVEL_POST.register(FireworkRocketTracker::onServerLevelTick);
    }

    private static void onServerLevelTick(ServerLevel level) {
        if (level.getGameTime() % 20L != 0L) {
            return;
        }
        cleanupOrphans(level);
    }

    public static boolean tryRegisterLaunch(Level level) {
        int max = TheatricalExtraLightsConfig.getMaxConcurrentRockets();
        if (max <= 0) {
            return true;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return true;
        }

        int count = countActive(serverLevel);
        if (count >= max) {
            purgeExpired(serverLevel);
        } else if (count > max / 2) {
            cleanupOrphans(serverLevel);
        }

        return countActive(serverLevel) < max;
    }

    public static void cancelLaunch(Level level) {
    }

    public static void onRemoved(Level level) {
    }

    public static int countActive(ServerLevel level) {
        return level.getEntitiesOfClass(FireworkRocketEntity.class, WORLD_BOUNDS).size();
    }

    /** Reclaim rockets that flew too far/high or linger without nearby players. */
    public static void cleanupOrphans(ServerLevel level) {
        for (FireworkRocketEntity rocket : List.copyOf(level.getEntitiesOfClass(FireworkRocketEntity.class, WORLD_BOUNDS))) {
            if (rocket.shouldForceCleanup(level)) {
                rocket.discard();
            }
        }
    }

    public static void purgeFinished(ServerLevel level) {
        for (FireworkRocketEntity rocket : level.getEntitiesOfClass(FireworkRocketEntity.class, WORLD_BOUNDS)) {
            int hold = rocket.getPreset().getPattern().getServerHoldTicks();
            if (rocket.tickCount > hold || rocket.tickCount > ABSOLUTE_MAX_TICKS || rocket.shouldForceCleanup(level)) {
                rocket.discard();
            }
        }
    }

    public static void purgeExpired(ServerLevel level) {
        purgeFinished(level);

        int max = TheatricalExtraLightsConfig.getMaxConcurrentRockets();
        if (max <= 0) {
            return;
        }

        List<FireworkRocketEntity> rockets = new ArrayList<>(
                level.getEntitiesOfClass(FireworkRocketEntity.class, WORLD_BOUNDS)
        );
        while (rockets.size() > max) {
            rockets.sort(Comparator.comparingInt(r -> r.tickCount));
            if (rockets.isEmpty()) {
                break;
            }
            rockets.remove(0).discard();
            rockets = new ArrayList<>(level.getEntitiesOfClass(FireworkRocketEntity.class, WORLD_BOUNDS));
        }
    }

    public static boolean hasNearbyPlayer(ServerLevel level, FireworkRocketEntity rocket) {
        AABB box = rocket.getBoundingBox().inflate(PLAYER_KEEP_RANGE);
        for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, box)) {
            if (!player.isSpectator() && !player.isDeadOrDying()) {
                return true;
            }
        }
        return false;
    }
}
