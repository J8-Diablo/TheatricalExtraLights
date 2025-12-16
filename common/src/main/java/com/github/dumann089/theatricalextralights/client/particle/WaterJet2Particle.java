package com.github.dumann089.theatricalextralights.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;


@Environment(EnvType.CLIENT)
public class WaterJet2Particle extends TextureSheetParticle {

    public static WaterJet2ParticleProvider provider(SpriteSet spriteSet) {
        return new WaterJet2ParticleProvider(spriteSet);
    }

    public static class WaterJet2ParticleProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public WaterJet2ParticleProvider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new WaterJet2Particle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }

    private final SpriteSet spriteSet;
    private final float rollSpeed;
    private float targetIntensity;
    private float currentIntensity;

    protected WaterJet2Particle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
        super(world, x, y, z);
        this.spriteSet = spriteSet;
        this.setSize((float) 0.1, (float) 0.1);

        this.targetIntensity = (float) vy;
        this.currentIntensity = this.targetIntensity;

        float minSize = 0.10F;
        float maxSize = 0.20F;
        this.quadSize = minSize + (maxSize - minSize) * this.currentIntensity;

        this.lifetime = 45;
        this.gravity = (float) 1.4;
        this.hasPhysics = false;
        double spread = 0.012;
        this.xd = (vx + (Math.random() - 0.08) * spread);
        this.yd = vy + (Math.random() * 0.10);
        this.zd = (vz + (Math.random() - 0.08) * spread);
        this.setSpriteFromAge(spriteSet);

        this.roll = (float)(Math.random() * 6 * Math.PI);
        this.oRoll = this.roll;
        this.rollSpeed = (float)(Math.random() * 0.4 - 0.2);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.removed) {
            float lifeRatio = (float)this.age / (float)this.lifetime;
            float alpha = 1.0F - lifeRatio;

            this.alpha = alpha;

            this.oRoll = this.roll;
            this.roll += this.rollSpeed;

            this.setSpriteFromAge(this.spriteSet);
        }
    }
}