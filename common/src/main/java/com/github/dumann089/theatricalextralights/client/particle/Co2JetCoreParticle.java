package com.github.dumann089.theatricalextralights.client.particle;

import com.github.dumann089.theatricalextralights.firework.FireworkRenderDistances;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

/** Coeur du jet CO₂ — colonne dense blanche. */
@Environment(EnvType.CLIENT)
public class Co2JetCoreParticle extends TextureSheetParticle {
    private static final float CONE_SPREAD = 0.048f;
    private static final float DRAG = 0.94f;

    private final SpriteSet sprites;
    private final float startSize;
    private final float peakSize;

    protected Co2JetCoreParticle(
            ClientLevel level,
            double x,
            double y,
            double z,
            double dirX,
            double dirY,
            double dirZ,
            SpriteSet sprites,
            RandomSource random
    ) {
        super(level, x, y, z);
        this.sprites = sprites;

        Vec3 axis = normalizeDirection(dirX, dirY, dirZ);
        float speed = (0.9f + random.nextFloat() * 0.5f);
        Vec3 lateral = randomDiskOffset(axis, random, CONE_SPREAD * (0.7f + random.nextFloat() * 0.5f));

        xd = axis.x * speed + lateral.x;
        yd = axis.y * speed + lateral.y;
        zd = axis.z * speed + lateral.z;

        hasPhysics = false;
        gravity = 0.0f;
        lifetime = 14 + random.nextInt(11);
        float distanceScale = FireworkRenderDistances.flameParticleSizeScale(x, y, z);
        startSize = (0.22f + random.nextFloat() * 0.12f) * distanceScale;
        peakSize = startSize * (2.5f + random.nextFloat() * 1.0f);
        quadSize = startSize * 0.85f;
        alpha = 0.85f;
        rCol = 1.0f;
        gCol = 1.0f;
        bCol = 1.0f;
        pickSprite(sprites);
    }

    private static Vec3 normalizeDirection(double dx, double dy, double dz) {
        Vec3 dir = new Vec3(dx, dy, dz);
        if (dir.lengthSqr() < 1.0e-8) {
            return new Vec3(0.0, 1.0, 0.0);
        }
        return dir.normalize();
    }

    private static Vec3 randomDiskOffset(Vec3 axis, RandomSource random, float radius) {
        Vec3 helper = Math.abs(axis.y) < 0.92 ? new Vec3(0.0, 1.0, 0.0) : new Vec3(1.0, 0.0, 0.0);
        Vec3 tangent = axis.cross(helper);
        if (tangent.lengthSqr() < 1.0e-8) {
            tangent = new Vec3(1.0, 0.0, 0.0);
        }
        tangent = tangent.normalize();
        Vec3 bitangent = axis.cross(tangent).normalize();
        double angle = random.nextDouble() * Math.PI * 2.0;
        double dist = random.nextDouble() * radius;
        return tangent.scale(Math.cos(angle) * dist).add(bitangent.scale(Math.sin(angle) * dist));
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ExtraLightsRenderTypes.co2JetRenderType();
    }

    @Override
    public void tick() {
        xo = x;
        yo = y;
        zo = z;
        if (age++ >= lifetime) {
            remove();
            return;
        }

        xd *= DRAG;
        yd *= DRAG;
        zd *= DRAG;
        move(xd, yd, zd);

        float life = (float) age / (float) lifetime;
        quadSize = Mth.lerp(life, startSize, peakSize);
        alpha = (1.0f - life) * (1.0f - life) * 0.85f;
        setSpriteFromAge(sprites);
    }

    @Environment(EnvType.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(
                SimpleParticleType type,
                ClientLevel level,
                double x,
                double y,
                double z,
                double dirX,
                double dirY,
                double dirZ
        ) {
            return new Co2JetCoreParticle(level, x, y, z, dirX, dirY, dirZ, sprites, level.random);
        }
    }
}
