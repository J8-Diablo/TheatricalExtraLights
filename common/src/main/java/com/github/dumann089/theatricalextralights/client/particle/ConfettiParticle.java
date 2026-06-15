package com.github.dumann089.theatricalextralights.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ConfettiParticle extends TextureSheetParticle {
    private static final PerlinSimplexNoise X_NOISE = noise(58637214);
    private static final PerlinSimplexNoise Z_NOISE = noise(823917);
    private static final PerlinSimplexNoise YAW_NOISE = noise(28943157);
    private static final PerlinSimplexNoise ROLL_NOISE = noise(80085);
    private static final PerlinSimplexNoise PITCH_NOISE = noise(49715286);

    private final SpriteSet sprites;
    private final int particleRandom;
    private float pitch;
    private float oPitch;
    private float yaw;
    private float oYaw;
    private float dPitch;
    private float dYaw;
    private float dRoll;

    private static PerlinSimplexNoise noise(int seed) {
        return new PerlinSimplexNoise(new LegacyRandomSource(seed),
                List.of(-7, -2, -1, 0, 1, 2));
    }

    protected ConfettiParticle(ClientLevel level, double x, double y, double z, double mx, double my, double mz, SpriteSet sprites) {
        super(level, x, y, z, mx, my, mz);
        this.sprites = sprites;
        this.particleRandom = this.random.nextInt();
        setSpriteFromAge(sprites);
        setSize(0.001F, 0.001F);
        gravity = 0.028F;
        friction = 0.982F;
        hasPhysics = true;
        lifetime = this.random.nextIntBetweenInclusive(650, 1000);
        quadSize *= 1.25F;
        // Keep original Supplementaries texture colours (no HSV tint).
        setColor(1.0F, 1.0F, 1.0F);
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        Vector3f[] corners = new Vector3f[4];
        float cx = (float) (Mth.lerp(partialTicks, xo, x) - camera.getPosition().x);
        float cy = (float) (Mth.lerp(partialTicks, yo, y) - camera.getPosition().y);
        float cz = (float) (Mth.lerp(partialTicks, zo, z) - camera.getPosition().z);

        Quaternionf rotation = new Quaternionf();
        rotation.rotateZ(Mth.lerp(partialTicks, oRoll, roll));
        rotation.rotateY(Mth.lerp(partialTicks, oYaw, yaw));
        rotation.rotateX(Mth.lerp(partialTicks, oPitch, pitch));

        float size = getQuadSize(partialTicks);
        corners[0] = new Vector3f(-1.0F, -1.0F, 0.0F);
        corners[1] = new Vector3f(-1.0F, 1.0F, 0.0F);
        corners[2] = new Vector3f(1.0F, 1.0F, 0.0F);
        corners[3] = new Vector3f(1.0F, -1.0F, 0.0F);
        for (Vector3f corner : corners) {
            corner.rotate((Quaternionfc) rotation);
            corner.mul(size);
            corner.add(cx, cy, cz);
        }

        float u0 = getU0();
        float u1 = getU1();
        float v0 = getV0();
        float v1 = getV1();
        int light = getLightColor(partialTicks);

        buffer.vertex(corners[0].x(), corners[0].y(), corners[0].z()).uv(u1, v1).color(rCol, gCol, bCol, alpha).uv2(light).endVertex();
        buffer.vertex(corners[1].x(), corners[1].y(), corners[1].z()).uv(u1, v0).color(rCol, gCol, bCol, alpha).uv2(light).endVertex();
        buffer.vertex(corners[2].x(), corners[2].y(), corners[2].z()).uv(u0, v0).color(rCol, gCol, bCol, alpha).uv2(light).endVertex();
        buffer.vertex(corners[3].x(), corners[3].y(), corners[3].z()).uv(u0, v1).color(rCol, gCol, bCol, alpha).uv2(light).endVertex();
    }

    @Override
    public void tick() {
        boolean still = xo == x && zo == z && yo == y && age > 8;
        boolean ascending = age < 35 && yd > 0.02F;
        boolean hasLanded = (onGround || still) && !ascending;

        float posChange = 0.01F;
        xd += posChange * X_NOISE.getValue(particleRandom, age, false);
        zd += posChange * Z_NOISE.getValue(particleRandom, age, false);
        oYaw = yaw;
        oPitch = pitch;
        oRoll = roll;

        if (!hasLanded) {
            float rotChange = 0.1F;
            dYaw += (float) (rotChange * YAW_NOISE.getValue(particleRandom, age, false));
            dRoll += (float) (rotChange * ROLL_NOISE.getValue(particleRandom, age, false));
            dPitch += (float) (rotChange * PITCH_NOISE.getValue(particleRandom, age, false));
            yaw += dYaw;
            pitch += dPitch;
            roll += dRoll;
        } else {
            age = Math.max(age, lifetime - 20);
        }

        float moment = 0.98F;
        dYaw *= moment;
        dRoll *= moment;
        dPitch *= moment;

        super.tick();
        setSpriteFromAge(sprites);
        alpha = Math.min(1.0F, 1.0F - (float) age / (float) lifetime);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                                     double xSpeed, double ySpeed, double zSpeed) {
            return new ConfettiParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }
    }
}
