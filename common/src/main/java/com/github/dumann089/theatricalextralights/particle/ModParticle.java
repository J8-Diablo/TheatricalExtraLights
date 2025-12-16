// ModParticle.java
package com.github.dumann089.theatricalextralights.particle;

import com.github.dumann089.theatricalextralights.TheatricalExtraLights;
import com.github.dumann089.theatricalextralights.client.particle.WaterFanParticle;
import com.github.dumann089.theatricalextralights.client.particle.WaterJet2Particle;
import com.github.dumann089.theatricalextralights.client.particle.WaterMovingJetParticle;
import com.github.dumann089.theatricalextralights.client.particle.WaterJetParticle;
import dev.architectury.platform.Platform;
import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.architectury.utils.Env;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;

public class ModParticle {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(TheatricalExtraLights.MOD_ID, Registries.PARTICLE_TYPE);

    public static final RegistrySupplier<SimpleParticleType> WATERJETPARTICLE = PARTICLE_TYPES.register("water_jet_particle", () ->
            new SimpleParticleType(false) {});

    public static final RegistrySupplier<SimpleParticleType> WATERJET2PARTICLE = PARTICLE_TYPES.register("water_jet2_particle", () ->
            new SimpleParticleType(false) {});

    public static final RegistrySupplier<SimpleParticleType> WATERFANPARTICLE = PARTICLE_TYPES.register("water_fan_particle", () ->
            new SimpleParticleType(false) {});

    public static final RegistrySupplier<SimpleParticleType> WATERMOVINGJETPARTICLE = PARTICLE_TYPES.register("water_moving_jet_particle", () ->
            new SimpleParticleType(false) {});

    public static void initialize() {
        PARTICLE_TYPES.register();
        if (Platform.getEnvironment() == Env.CLIENT) {
            ParticleProviderRegistry.register(WATERJETPARTICLE, WaterJetParticle::provider);
            ParticleProviderRegistry.register(WATERJET2PARTICLE, WaterJet2Particle::provider);
            ParticleProviderRegistry.register(WATERFANPARTICLE, WaterFanParticle::provider);
            ParticleProviderRegistry.register(WATERMOVINGJETPARTICLE, WaterMovingJetParticle::provider);

        }
    }
}
