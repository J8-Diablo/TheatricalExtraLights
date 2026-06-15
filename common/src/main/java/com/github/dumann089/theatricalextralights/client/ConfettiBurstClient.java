package com.github.dumann089.theatricalextralights.client;

import com.github.dumann089.theatricalextralights.particle.ModParticle;
import com.github.dumann089.theatricalextralights.pyro.ConfettiCannonOrientation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class ConfettiBurstClient {
    /** Vertical speed (blocks/tick) — decoupled from horizontal spread. */
    private static final float VERTICAL_SPEED_LOW = 12.5F;
    private static final float VERTICAL_SPEED_HIGH = 14.75F;
    private static final float HORIZONTAL_SPEED_LOW = 1.8F;
    private static final float HORIZONTAL_SPEED_HIGH = 2.8F;
    /** Clear the block hitbox before spawning. */
    private static final float SPAWN_OFFSET = 0.85F;
    private static final float SPREAD = 0.07F;

    private ConfettiBurstClient() {
    }

    public static void spawn(Vec3 origin, Vec3 direction, float intensityNorm) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || intensityNorm <= 0.0F) {
            return;
        }

        RandomSource random = level.random;
        Vec3 axis = direction.normalize();
        Vec3 spawn = origin.add(axis.scale(SPAWN_OFFSET)).add(0.0D, 0.1D, 0.0D);

        float verticalSpeed = Mth.lerp(intensityNorm, VERTICAL_SPEED_LOW, VERTICAL_SPEED_HIGH)
                * Mth.randomBetween(random, 0.94F, 1.06F);
        float horizontalSpeed = Mth.lerp(intensityNorm, HORIZONTAL_SPEED_LOW, HORIZONTAL_SPEED_HIGH)
                * Mth.randomBetween(random, 0.88F, 1.12F);
        int count = 180 + Math.round(intensityNorm * 180.0F);

        for (int i = 0; i < count; i++) {
            Vector3f velocity = ConfettiCannonOrientation.spreadDirection(random, axis, SPREAD);
            applyHorizontalSpeed(velocity, horizontalSpeed);
            velocity.y = verticalSpeed;
            level.addParticle(
                    (ParticleOptions) ModParticle.CONFETTI.get(),
                    spawn.x, spawn.y, spawn.z,
                    velocity.x, velocity.y, velocity.z
            );
        }
    }

    private static void applyHorizontalSpeed(Vector3f velocity, float speed) {
        float hx = velocity.x;
        float hz = velocity.z;
        float len = Mth.sqrt(hx * hx + hz * hz);
        if (len > 1.0E-4F) {
            velocity.x = hx / len * speed;
            velocity.z = hz / len * speed;
        } else {
            velocity.x = 0.0F;
            velocity.z = 0.0F;
        }
    }
}
