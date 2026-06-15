package com.github.dumann089.theatricalextralights.compat.fabric;

import com.github.dumann089.theatricalextralights.compat.FireworkLightUpdateQueue;
import com.lowdragmc.shimmer.client.light.ColorPointLight;
import com.lowdragmc.shimmer.client.light.LightManager;
import dev.imabad.theatrical.api.DynamicLightProvider;
import dev.imabad.theatrical.compat.ModCompat;
import dev.imabad.theatrical.compat.ShimmerCompat;
import net.minecraft.core.BlockPos;
import org.joml.Vector3f;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class FireworkLightCompatImpl {
    private static final Map<BlockPos, ColorPointLight> FIREWORK_LIGHTS = new ConcurrentHashMap<>();

    private FireworkLightCompatImpl() {
    }

    public static void sync(DynamicLightProvider provider) {
        if (!ModCompat.SHIMMER) {
            return;
        }
        FireworkLightUpdateQueue.queueSync(provider);
    }

    public static void remove(DynamicLightProvider provider) {
        if (!ModCompat.SHIMMER) {
            return;
        }
        FireworkLightUpdateQueue.queueRemove(provider.getOwnerPos());
    }

    public static void removeAt(BlockPos pos) {
        if (!ModCompat.SHIMMER) {
            return;
        }
        FireworkLightUpdateQueue.queueRemove(pos);
    }

    public static void flushPending() {
        if (!ModCompat.SHIMMER) {
            FireworkLightUpdateQueue.drain(update -> {});
            return;
        }
        FireworkLightUpdateQueue.drain(FireworkLightCompatImpl::applyUpdate);
    }

    private static void applyUpdate(FireworkLightUpdateQueue.Update update) {
        if (update.remove()) {
            removeLightAt(update.ownerPos());
            return;
        }
        Vector3f lightPos = update.pos();
        ColorPointLight light = FIREWORK_LIGHTS.get(update.ownerPos());
        if (light != null) {
            light.setPos(lightPos.x, lightPos.y, lightPos.z);
            light.radius = update.spread();
            light.setColor(update.color());
            light.setEnable(true);
            light.update();
        } else {
            ColorPointLight newLight = LightManager.INSTANCE.addLight(lightPos, update.color(), update.spread());
            if (newLight != null) {
                FIREWORK_LIGHTS.put(update.ownerPos(), newLight);
            }
        }
    }

    private static void removeLightAt(BlockPos pos) {
        ColorPointLight light = FIREWORK_LIGHTS.remove(pos);
        if (light != null) {
            light.remove();
        }
        ShimmerCompat.removeLight(pos);
    }
}
