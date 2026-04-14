package com.github.dumann089.theatricalextralights.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class WaterJetFanParticle extends TextureSheetParticle {

    public static ParticleProvider<WaterJetParticleOptions> provider(SpriteSet spriteSet) {
        return (options, world, x, y, z, vx, vy, vz) ->
                new WaterJetFanParticle(
                        world,
                        x, y, z,
                        vx, vy, vz,
                        options.intensity,
                        options.thickness,
                        spriteSet
                );
    }

    private final SpriteSet spriteSet;
    private final float rollSpeed;

    protected WaterJetFanParticle(
            ClientLevel world,
            double x, double y, double z,
            double vx, double vy, double vz,
            float intensity,
            float thickness,
            SpriteSet spriteSet
    ) {
        super(world, x, y, z);
        this.spriteSet = spriteSet;

        thickness = Mth.clamp(thickness, 0.02F, 1.0F);
        this.setSize(0.1F, 0.1F);

        float minSize = 0.05F;
        float maxSize = 0.75F;
        this.quadSize = minSize + (maxSize - minSize) * thickness;

        this.lifetime = 70;
        this.gravity = 1.4F;
        this.hasPhysics = false;

        /* =========================
           INTENSITY
           ========================= */
        double intensityClamped = Mth.clamp(vy, 0.0, 1.0);
        double verticalSpeed = intensityClamped * 0.6;

        Vec3 dir = new Vec3(vx, 0.0, vz);
        if (dir.lengthSqr() < 1.0E-4) {
            dir = new Vec3(0, 0, 1);
        }
        dir = dir.normalize();

        Vec3 side = new Vec3(-dir.z, 0, dir.x);

        double spread = 0.4;
        double factor = (Math.random() * 2 - 1) * spread;

        Vec3 finalDir = dir.add(side.scale(factor)).normalize();

        this.xd = finalDir.x * verticalSpeed;
        this.yd = verticalSpeed;
        this.zd = finalDir.z * verticalSpeed;

        this.setSpriteFromAge(spriteSet);

        this.roll = (float) (Math.random() * 6 * Math.PI);
        this.oRoll = this.roll;
        this.rollSpeed = (float) (Math.random() * 0.4 - 0.2);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        super.tick();
        if (!removed) {
            float lifeRatio = (float) age / lifetime;
            this.alpha = 1.0F - lifeRatio;
            setSpriteFromAge(spriteSet);
        }
    }
}
