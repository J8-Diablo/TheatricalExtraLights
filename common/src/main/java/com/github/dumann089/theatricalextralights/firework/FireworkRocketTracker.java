package com.github.dumann089.theatricalextralights.firework;

import com.github.dumann089.theatricalextralights.config.TheatricalExtraLightsConfig;
import com.github.dumann089.theatricalextralights.entities.FireworkRocketEntity;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public final class FireworkRocketTracker {
    private static final Object2IntOpenHashMap<Level> ACTIVE = new Object2IntOpenHashMap<>();

    private FireworkRocketTracker() {
    }

    public static boolean tryRegisterLaunch(Level level) {
        int max = TheatricalExtraLightsConfig.getMaxConcurrentRockets();
        if (max <= 0) {
            return true;
        }
        synchronized (ACTIVE) {
            int count = ACTIVE.getInt(level);
            if (count >= max) {
                resyncCount(level);
                count = ACTIVE.getInt(level);
                if (count >= max) {
                    return false;
                }
            }
            ACTIVE.put(level, count + 1);
            return true;
        }
    }

    public static void cancelLaunch(Level level) {
        onRemoved(level);
    }

    public static void onRemoved(Level level) {
        synchronized (ACTIVE) {
            ACTIVE.put(level, Math.max(0, ACTIVE.getInt(level) - 1));
        }
    }

    private static void resyncCount(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        int actual = serverLevel.getEntitiesOfClass(
                FireworkRocketEntity.class,
                new AABB(-3.0E7, -64, -3.0E7, 3.0E7, 320, 3.0E7)
        ).size();
        ACTIVE.put(level, actual);
    }
}
