package com.github.dumann089.theatricalextralights.compat.fabric;

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
        BlockPos ownerPos = provider.getOwnerPos();
        if (provider.getLightLuminance() <= 0) {
            removeLightAt(ownerPos);
            return;
        }
        Vector3f lightPos = provider.getLightPos();
        ColorPointLight light = FIREWORK_LIGHTS.get(ownerPos);
        if (light != null) {
            light.setPos(lightPos.x, lightPos.y, lightPos.z);
            light.radius = provider.getLightSpread();
            light.setColor(provider.getLightColour());
            light.setEnable(true);
            light.update();
        } else {
            ColorPointLight newLight = LightManager.INSTANCE.addLight(
                    lightPos, provider.getLightColour(), provider.getLightSpread());
            if (newLight != null) {
                FIREWORK_LIGHTS.put(ownerPos, newLight);
            }
        }
    }

    public static void remove(DynamicLightProvider provider) {
        if (!ModCompat.SHIMMER) {
            return;
        }
        removeLightAt(provider.getOwnerPos());
    }

    public static void removeAt(BlockPos pos) {
        if (!ModCompat.SHIMMER) {
            return;
        }
        removeLightAt(pos);
    }

    private static void removeLightAt(BlockPos pos) {
        ColorPointLight light = FIREWORK_LIGHTS.remove(pos);
        if (light != null) {
            light.remove();
        }
        ShimmerCompat.removeLight(pos);
    }
}
