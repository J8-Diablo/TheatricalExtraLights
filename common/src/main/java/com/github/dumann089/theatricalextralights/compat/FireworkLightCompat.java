package com.github.dumann089.theatricalextralights.compat;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.imabad.theatrical.api.DynamicLightProvider;
import net.minecraft.core.BlockPos;

public final class FireworkLightCompat {
    private FireworkLightCompat() {
    }

    @ExpectPlatform
    public static void sync(DynamicLightProvider provider) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void remove(DynamicLightProvider provider) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void removeAt(BlockPos pos) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void flushPending() {
        throw new AssertionError();
    }
}
