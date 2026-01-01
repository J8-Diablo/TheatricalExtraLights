package com.github.dumann089.theatricalextralights.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;

public class WaterJetParticleProvider
        implements ParticleProvider<WaterJetParticleOptions> {

    private final SpriteSet spriteSet;

    public WaterJetParticleProvider(SpriteSet spriteSet) {
        this.spriteSet = spriteSet;
    }

    @Override
    public Particle createParticle(
            WaterJetParticleOptions options,
            ClientLevel level,
            double x,
            double y,
            double z,
            double vx,
            double vy,
            double vz
    ) {
        return switch (options.variant) {


            case JETCone -> new WaterJetConeParticle(
                    level,
                    x, y, z,
                    vx, vy, vz,
                    options.intensity,
                    options.thickness,
                    options.coneAngle,
                    spriteSet
            );
            case JETFan -> new WaterJetFanParticle(
                    level,
                    x, y, z,
                    vx, vy, vz,
                    options.intensity,
                    options.thickness,
                    spriteSet
            );
            case JETBloom -> new WaterJetBloomParticle(
                    level,
                    x, y, z,
                    vx, vy, vz,
                    options.intensity,
                    options.thickness,
                    options.coneAngle,
                    spriteSet
            );
            case JETFog -> new WaterJetFogParticle(
                    level,
                    x, y, z,
                    vx, vy, vz,
                    options.intensity,
                    options.thickness,
                    options.coneAngle,
                    spriteSet
            );
            case JET4 -> new WaterJet4Particle(
                    level,
                    x, y, z,
                    vx, vy, vz,
                    options.intensity,
                    options.thickness,
                    spriteSet
            );
            case JET3 -> new WaterJet3Particle(
                    level,
                    x, y, z,
                    vx, vy, vz,
                    options.intensity,
                    options.thickness,
                    spriteSet
            );

            case JET2 -> new WaterJet2Particle(
                    level,
                    x, y, z,
                    vx, vy, vz,
                    options.intensity,
                    options.thickness,
                    spriteSet
            );
            case JET1 -> new WaterJet1Particle(
                    level,
                    x, y, z,
                    vx, vy, vz,
                    options.intensity,
                    options.thickness,
                    spriteSet
            );

        };
    }
}