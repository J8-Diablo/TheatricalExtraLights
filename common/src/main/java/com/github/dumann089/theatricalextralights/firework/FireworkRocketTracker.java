package com.github.dumann089.theatricalextralights.firework;

import com.github.dumann089.theatricalextralights.config.TheatricalExtraLightsConfig;
import net.minecraft.world.level.Level;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

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
                return false;
            }
            ACTIVE.put(level, count + 1);
            return true;
        }
    }

    public static void onRemoved(Level level) {
        synchronized (ACTIVE) {
            ACTIVE.put(level, Math.max(0, ACTIVE.getInt(level) - 1));
        }
    }
}
