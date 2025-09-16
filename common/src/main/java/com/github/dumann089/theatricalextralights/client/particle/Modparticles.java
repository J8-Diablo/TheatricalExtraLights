package com.github.dumann089.theatricalextralights.client.particle;

import net.minecraft.core.particles.SimpleParticleType;
import dev.architectury.injectables.annotations.ExpectPlatform;

public class Modparticles {
    public static SimpleParticleType WATER_JET;

    public static void register() {
        WATER_JET = register("water_jet");
    }

    @ExpectPlatform
    public static SimpleParticleType register(String name) {
        throw new AssertionError("ExpectPlatform failed");
    }
}
