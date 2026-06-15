package com.github.dumann089.theatricalextralights.client;

import dev.architectury.injectables.annotations.ExpectPlatform;

public final class ModParticleClient {
    private ModParticleClient() {
    }

    public static void registerProviders() {
        registerPlatformProviders();
    }

    @ExpectPlatform
    private static void registerPlatformProviders() {
        throw new AssertionError();
    }
}
