package com.github.dumann089.theatricalextralights.client;

import com.github.dumann089.theatricalextralights.particle.ModParticle;
import com.github.dumann089.theatricalextralights.pyro.ConfettiCannonOrientation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Bursts received during the same client tick are flushed together in {@link #tick()} so every
 * cannon pops at once. When many fire together, each gets fewer particles instead of a staggered
 * multi-tick spawn.
 */
public final class ConfettiBurstClient {
    /** Vertical speed (blocks/tick) — decoupled from horizontal spread. */
    private static final float VERTICAL_SPEED_LOW = 12.5F;
    private static final float VERTICAL_SPEED_HIGH = 14.75F;
    private static final float HORIZONTAL_SPEED_LOW = 1.8F;
    private static final float HORIZONTAL_SPEED_HIGH = 2.8F;
    /** Clear the block hitbox before spawning. */
    private static final float SPAWN_OFFSET = 0.85F;
    private static final float SPREAD = 0.07F;
    private static final double MAX_SPAWN_DISTANCE = 56.0D;
    private static final double MAX_SPAWN_DISTANCE_SQ = MAX_SPAWN_DISTANCE * MAX_SPAWN_DISTANCE;
    private static final double FULL_DETAIL_DISTANCE = 24.0D;
    private static final int BASE_PARTICLES_LOW = 180;
    private static final int BASE_PARTICLES_HIGH = 360;
    private static final int MIN_BURST_PARTICLES = 16;
    /** Max particles spawned in one synchronized wave (same client tick). */
    private static final int MAX_PARTICLES_PER_WAVE = 2000;

    private static final List<PendingBurst> SAME_TICK = new ArrayList<>();
    private static ClientLevel batchedLevel;
    /** Read by {@link com.github.dumann089.theatricalextralights.client.particle.ConfettiParticle} during spawn. */
    private static float spawnLifetimeScale = 1.0F;

    private ConfettiBurstClient() {
    }

    public static float spawnLifetimeScale() {
        return spawnLifetimeScale;
    }

    /** Buffers the burst until end of client tick — all cannons flush together in {@link #tick()}. */
    public static void queue(Vec3 origin, Vec3 direction, float intensityNorm) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || intensityNorm <= 0.0F) {
            return;
        }

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.distanceToSqr(origin) > MAX_SPAWN_DISTANCE_SQ) {
            return;
        }

        if (batchedLevel != level) {
            SAME_TICK.clear();
            batchedLevel = level;
        }

        PendingBurst burst = createBurst(origin, direction, intensityNorm, player.distanceToSqr(origin));
        if (burst != null) {
            SAME_TICK.add(burst);
        }
    }

    /** Spawns every buffered burst at once (same frame). */
    public static void tick() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || Minecraft.getInstance().isPaused()) {
            SAME_TICK.clear();
            batchedLevel = null;
            return;
        }

        if (batchedLevel != level) {
            SAME_TICK.clear();
            batchedLevel = level;
        }

        if (SAME_TICK.isEmpty()) {
            return;
        }

        int cannonCount = SAME_TICK.size();
        int waveBudget = MAX_PARTICLES_PER_WAVE;
        int evenShare = Math.max(MIN_BURST_PARTICLES, waveBudget / cannonCount);
        RandomSource random = level.random;

        try {
            for (PendingBurst burst : SAME_TICK) {
                int count = Math.min(burst.desiredCount, evenShare);
                if (count < MIN_BURST_PARTICLES) {
                    continue;
                }
                spawnLifetimeScale = burst.lifetimeScale;
                emitParticles(level, random, burst, count);
            }
        } finally {
            spawnLifetimeScale = 1.0F;
            SAME_TICK.clear();
        }
    }

    private static PendingBurst createBurst(Vec3 origin, Vec3 direction, float intensityNorm, double distSq) {
        float distanceScale = distanceScale(distSq);
        float settingsScale = particleSettingsScale();
        int desired = Math.round((BASE_PARTICLES_LOW + Math.round(intensityNorm * (BASE_PARTICLES_HIGH - BASE_PARTICLES_LOW)))
                * distanceScale * settingsScale);
        if (desired < MIN_BURST_PARTICLES) {
            return null;
        }

        Vec3 axis = direction.normalize();
        Vec3 spawn = origin.add(axis.scale(SPAWN_OFFSET)).add(0.0D, 0.1D, 0.0D);
        RandomSource random = Minecraft.getInstance().level.random;
        float verticalSpeed = Mth.lerp(intensityNorm, VERTICAL_SPEED_LOW, VERTICAL_SPEED_HIGH)
                * Mth.randomBetween(random, 0.94F, 1.06F);
        float horizontalSpeed = Mth.lerp(intensityNorm, HORIZONTAL_SPEED_LOW, HORIZONTAL_SPEED_HIGH)
                * Mth.randomBetween(random, 0.88F, 1.12F);
        float lifetimeScale = Mth.clamp(distanceScale * settingsScale, 0.35F, 1.0F);

        return new PendingBurst(spawn, axis, verticalSpeed, horizontalSpeed, lifetimeScale, desired);
    }

    private static void emitParticles(ClientLevel level, RandomSource random, PendingBurst burst, int count) {
        for (int i = 0; i < count; i++) {
            Vector3f velocity = ConfettiCannonOrientation.spreadDirection(random, burst.axis, SPREAD);
            applyHorizontalSpeed(velocity, burst.horizontalSpeed);
            velocity.y = burst.verticalSpeed;
            level.addParticle(
                    (ParticleOptions) ModParticle.CONFETTI.get(),
                    burst.spawn.x, burst.spawn.y, burst.spawn.z,
                    velocity.x, velocity.y, velocity.z
            );
        }
    }

    private static float distanceScale(double distSq) {
        double dist = Math.sqrt(distSq);
        if (dist <= FULL_DETAIL_DISTANCE) {
            return 1.0F;
        }
        return (float) Mth.clamp(
                1.0D - (dist - FULL_DETAIL_DISTANCE) / (MAX_SPAWN_DISTANCE - FULL_DETAIL_DISTANCE),
                0.25D,
                1.0D
        );
    }

    private static float particleSettingsScale() {
        ParticleStatus status = Minecraft.getInstance().options.particles().get();
        return switch (status) {
            case DECREASED -> 0.55F;
            case MINIMAL -> 0.3F;
            default -> 1.0F;
        };
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

    private static final class PendingBurst {
        private final Vec3 spawn;
        private final Vec3 axis;
        private final float verticalSpeed;
        private final float horizontalSpeed;
        private final float lifetimeScale;
        private final int desiredCount;

        private PendingBurst(Vec3 spawn, Vec3 axis, float verticalSpeed, float horizontalSpeed, float lifetimeScale, int desiredCount) {
            this.spawn = spawn;
            this.axis = axis;
            this.verticalSpeed = verticalSpeed;
            this.horizontalSpeed = horizontalSpeed;
            this.lifetimeScale = lifetimeScale;
            this.desiredCount = desiredCount;
        }
    }
}
