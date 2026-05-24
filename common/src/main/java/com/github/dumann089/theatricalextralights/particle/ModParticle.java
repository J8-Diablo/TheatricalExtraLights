// ModParticle.java
package com.github.dumann089.theatricalextralights.particle;

import com.github.dumann089.theatricalextralights.TheatricalExtraLights;
import com.github.dumann089.theatricalextralights.client.particle.*;
import com.mojang.serialization.Codec;
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

    public static final RegistrySupplier<SimpleParticleType> WATERFANPARTICLE = PARTICLE_TYPES.register("water_fan_particle", () ->
            new SimpleParticleType(false) {});
    public static final RegistrySupplier<SimpleParticleType> WATERJET2PARTICLE = PARTICLE_TYPES.register("water_jet2_particle", () ->
            new SimpleParticleType(false) {});

    public static final RegistrySupplier<SimpleParticleType> WATERMOVINGJETPARTICLE = PARTICLE_TYPES.register("water_moving_jet_particle", () ->
            new SimpleParticleType(false) {});

    public static final RegistrySupplier<ParticleType<WaterJetParticleOptions>>
            WATERJET_OPTIONS = PARTICLE_TYPES.register(
            "waterjet",
            () -> new ParticleType<WaterJetParticleOptions>(false, WaterJetParticleOptions.DESERIALIZER) {
                @Override
                public Codec<WaterJetParticleOptions> codec() {
                    return WaterJetParticleOptions.CODEC;
                }
            }
    );

    public static final RegistrySupplier<ParticleType<FireworkSparkParticleOptions>>
            FIREWORK_SPARK = PARTICLE_TYPES.register(
            "firework_spark",
            () -> new ParticleType<FireworkSparkParticleOptions>(false, FireworkSparkParticleOptions.DESERIALIZER) {
                @Override
                public Codec<FireworkSparkParticleOptions> codec() {
                    return FireworkSparkParticleOptions.CODEC;
                }
            }
    );

    public static void initialize() {
        PARTICLE_TYPES.register();

        if (Platform.getEnvironment() == Env.CLIENT) {

            ParticleProviderRegistry.register(
                    WATERJET_OPTIONS,
                    WaterJetParticleProvider::new
            );

            ParticleProviderRegistry.register(
                    WATERJETPARTICLE,
                    WaterJetParticle::provider
            );
            ParticleProviderRegistry.register(
                    FIREWORK_SPARK,
                    FireworkSparkParticle.Provider::new
            );

            ParticleProviderRegistry.register(
                    WATERFANPARTICLE,
                    WaterFanParticle::provider
            );

            ParticleProviderRegistry.register(
                    WATERMOVINGJETPARTICLE,
                    WaterMovingJetParticle::provider
            );
        }
    }
}
