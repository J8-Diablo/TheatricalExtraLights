package com.github.dumann089.theatricalextralights.compat.forge;

import dev.imabad.theatrical.api.DynamicLightProvider;
import dev.imabad.theatrical.compat.ModCompat;
import dev.imabad.theatrical.compat.ShimmerCompat;
import net.minecraft.core.BlockPos;

public final class FireworkLightCompatImpl {
    private FireworkLightCompatImpl() {
    }

    public static void sync(DynamicLightProvider provider) {
        if (!ModCompat.SHIMMER) {
            return;
        }
        ShimmerCompat.removeLight(provider.getOwnerPos());
        if (provider.getLightLuminance() > 0) {
            ShimmerCompat.addLight(provider);
        }
    }

    public static void remove(DynamicLightProvider provider) {
        if (!ModCompat.SHIMMER) {
            return;
        }
        ShimmerCompat.removeLight(provider.getOwnerPos());
    }

    public static void removeAt(BlockPos pos) {
        if (!ModCompat.SHIMMER) {
            return;
        }
        ShimmerCompat.removeLight(pos);
    }
}
