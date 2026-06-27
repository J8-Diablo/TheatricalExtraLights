package com.github.dumann089.theatricalextralights.client.forge;

import com.github.dumann089.theatricalextralights.client.particle.ConfettiParticle;
import com.github.dumann089.theatricalextralights.client.particle.FireworkSparkParticle;
import com.github.dumann089.theatricalextralights.client.particle.FlameThrowerJetParticle;
import com.github.dumann089.theatricalextralights.client.particle.WaterFanParticle;
import com.github.dumann089.theatricalextralights.client.particle.WaterJetParticle;
import com.github.dumann089.theatricalextralights.client.particle.WaterJetParticleProvider;
import com.github.dumann089.theatricalextralights.client.particle.WaterMovingJetParticle;
import com.github.dumann089.theatricalextralights.particle.ModParticle;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;

@SuppressWarnings("unused")
public class ModParticleClientImpl {
    private ModParticleClientImpl() {
    }

    public static void registerPlatformProviders() {
        // Forge registers particle providers from RegisterParticleProvidersEvent.
    }

    public static void registerForgeProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticle.WATERJET_OPTIONS.get(), WaterJetParticleProvider::new);
        event.registerSpriteSet(ModParticle.WATERJETPARTICLE.get(), WaterJetParticle::provider);
        event.registerSpriteSet(ModParticle.CONFETTI.get(), ConfettiParticle.Provider::new);
        event.registerSpriteSet(ModParticle.FIREWORK_SPARK.get(), FireworkSparkParticle.Provider::new);
        event.registerSpriteSet(ModParticle.FLAME_THROWER_JET.get(), FlameThrowerJetParticle.Provider::new);
        event.registerSpriteSet(ModParticle.WATERFANPARTICLE.get(), WaterFanParticle::provider);
        event.registerSpriteSet(ModParticle.WATERMOVINGJETPARTICLE.get(), WaterMovingJetParticle::provider);
    }
}
