package com.github.dumann089.theatricalextralights.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.multiplayer.ClientLevel;

public class WaterJetParticle extends TextureSheetParticle {
    protected final SpriteSet spriteSet;
    protected final float rollSpeed;

    protected WaterJetParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
        super(world, x, y, z);
        this.spriteSet = spriteSet;
        this.setSize(0.1F, 0.1F);
        this.quadSize *= 2.0F;
        this.lifetime = 45;
        this.gravity = 0.9F;
        this.hasPhysics = false;

        double spread = 0.025;
        this.xd = vx + (Math.random() - 0.012) * spread;
        this.yd = vy + (Math.random() * 0.69);
        this.zd = vz + (Math.random() - 0.012) * spread;

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
            this.setSpriteFromAge(this.spriteSet);
            this.oRoll = this.roll;
            this.roll += this.rollSpeed;
        }
    }

	public class Provider {
	}

    public static void register() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'register'");
    }
}
