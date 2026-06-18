package com.github.dumann089.theatricalextralights.firework;

import com.github.dumann089.theatricalextralights.entities.FireworkRocketEntity;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

/**
 * Concrete burst patterns. Each one is a fundamentally different algorithm — not
 * shared spawn helper with parameter tweaks. Each populates the rocket's spark list
 * with its own characteristic geometry. Speeds and lifetimes tuned for big visible
 * explosions (50-100 block diameter typical).
 */
public final class BurstPatterns {
    private BurstPatterns() {
    }

    private static final int FLIGHT_LIFETIME = 160;

    private static int paletteAt(int[] palette, int index) {
        return palette[Math.floorMod(index, palette.length)];
    }

    private static void emitFlightTrail(FireworkRocketEntity rocket, RandomSource random, int color, float scale, int lifetime, boolean trail) {
        Vec3 motion = rocket.getDeltaMovement().scale(-0.05);
        double jitter = 0.06;
        double jx = (random.nextDouble() - 0.5) * jitter;
        double jy = (random.nextDouble() - 0.5) * jitter * 0.4;
        double jz = (random.nextDouble() - 0.5) * jitter;
        rocket.addSpark(new Spark(
                rocket.getX() + jx, rocket.getY() + jy, rocket.getZ() + jz,
                motion.x + jx * 0.4, motion.y + jy * 0.4, motion.z + jz * 0.4,
                color, scale, lifetime, 0.0f, 0.95f, trail, false));
    }

    /**
     * Uniform expanding sphere. Brief, clean, no gravity bias. The benchmark "boule"
     * burst — sparks shoot outward in all directions and fade in place.
     */
    public static class Peony extends BurstPattern {
        @Override public boolean isBurst() { return true; }
        @Override public int getBurstDuration() { return 64; }
        @Override public int getFlightLifetime() { return FLIGHT_LIFETIME; }

        @Override
        public void onFlightTick(FireworkRocketEntity rocket, RandomSource random) {
            emitFlightTrail(rocket, random, rocket.getLaunchColor(), 0.42f, 10, false);
        }

        @Override
        public void onBurstStart(FireworkRocketEntity rocket, RandomSource random) {
            int color = rocket.getLaunchColor();
            int count = 260;
            for (int i = 0; i < count; i++) {
                double theta = random.nextDouble() * Math.PI * 2.0;
                double cosPhi = 2.0 * random.nextDouble() - 1.0;
                double sinPhi = Math.sqrt(Math.max(0.0, 1.0 - cosPhi * cosPhi));
                double speed = 1.6 + random.nextDouble() * 0.55;
                double vx = sinPhi * Math.cos(theta) * speed;
                double vy = cosPhi * speed;
                double vz = sinPhi * Math.sin(theta) * speed;
                rocket.addSpark(new Spark(rocket.getX(), rocket.getY(), rocket.getZ(),
                        vx, vy, vz, color, 0.45f, 60, 0.0025f, 0.992f, false, false));
            }
            spawnEmbers(rocket, random, 110);
        }
    }

    /**
     * Sphere with immediate heavy droop. Sparks emit radially then fall hard, drawing
     * long downward streaks like willow branches.
     */
    public static class Willow extends BurstPattern {
        @Override public boolean isBurst() { return true; }
        @Override public boolean hasCrackleSound() { return true; }
        @Override public int getBurstDuration() { return 110; }
        @Override public int getFlightLifetime() { return FLIGHT_LIFETIME; }

        @Override
        public void onFlightTick(FireworkRocketEntity rocket, RandomSource random) {
            emitFlightTrail(rocket, random, rocket.getLaunchColor(), 0.40f, 10, false);
        }

        @Override
        public void onBurstStart(FireworkRocketEntity rocket, RandomSource random) {
            int color = rocket.getLaunchColor();
            int count = 280;
            for (int i = 0; i < count; i++) {
                double theta = random.nextDouble() * Math.PI * 2.0;
                double phi = Math.acos(2.0 * random.nextDouble() - 1.0);
                double speed = 0.85 + random.nextDouble() * 0.30;
                double vx = Math.sin(phi) * Math.cos(theta) * speed;
                double vy = Math.cos(phi) * speed + 0.10;
                double vz = Math.sin(phi) * Math.sin(theta) * speed;
                rocket.addSpark(new Spark(rocket.getX(), rocket.getY(), rocket.getZ(),
                        vx, vy, vz, color, 0.40f, 105, 0.015f, 0.992f, true, false));
            }
            spawnEmbers(rocket, random, 150);
        }
    }

    /**
     * Chaotic flickering scatter. Very wide speed range, short lifetime, every spark
     * strobes between bright and dim.
     */
    public static class Strobe extends BurstPattern {
        @Override public boolean isBurst() { return true; }
        @Override public boolean hasCrackleSound() { return true; }
        @Override public int getBurstDuration() { return 40; }
        @Override public int getFlightLifetime() { return FLIGHT_LIFETIME; }

        @Override
        public void onFlightTick(FireworkRocketEntity rocket, RandomSource random) {
            emitFlightTrail(rocket, random, rocket.getLaunchColor(), 0.42f, 10, false);
        }

        @Override
        public void onBurstStart(FireworkRocketEntity rocket, RandomSource random) {
            int color = rocket.getLaunchColor();
            int count = 260;
            for (int i = 0; i < count; i++) {
                double theta = random.nextDouble() * Math.PI * 2.0;
                double cosPhi = 2.0 * random.nextDouble() - 1.0;
                double sinPhi = Math.sqrt(Math.max(0.0, 1.0 - cosPhi * cosPhi));
                double speed = 0.90 + random.nextDouble() * 1.95;
                double vx = sinPhi * Math.cos(theta) * speed;
                double vy = cosPhi * speed;
                double vz = sinPhi * Math.sin(theta) * speed;
                rocket.addSpark(new Spark(rocket.getX(), rocket.getY(), rocket.getZ(),
                        vx, vy, vz, color, 0.45f, 32 + random.nextInt(10), 0.001f, 0.992f, false, true));
            }
            spawnEmbers(rocket, random, 110);
        }
    }

    /**
     * Three concentric color shells expanding at different speeds. Each shell is a
     * full uniform sphere in its own color from the preset palette, so three rings
     * are visible separating outward.
     */
    public static class Multicolor extends BurstPattern {
        @Override public boolean isBurst() { return true; }
        @Override public int getBurstDuration() { return 70; }
        @Override public int getFlightLifetime() { return FLIGHT_LIFETIME; }

        @Override
        public void onFlightTick(FireworkRocketEntity rocket, RandomSource random) {
            emitFlightTrail(rocket, random, rocket.getLaunchColor(), 0.42f, 10, false);
        }

        @Override
        public void onBurstStart(FireworkRocketEntity rocket, RandomSource random) {
            int[] palette = rocket.getColors();
            double[] shellSpeeds = {0.85, 1.45, 2.05};
            int[] shellCounts = {100, 115, 130};
            for (int shell = 0; shell < shellSpeeds.length; shell++) {
                int color = paletteAt(palette, shell);
                double baseSpeed = shellSpeeds[shell];
                int count = shellCounts[shell];
                for (int i = 0; i < count; i++) {
                    double theta = random.nextDouble() * Math.PI * 2.0;
                    double cosPhi = 2.0 * random.nextDouble() - 1.0;
                    double sinPhi = Math.sqrt(Math.max(0.0, 1.0 - cosPhi * cosPhi));
                    double speed = baseSpeed + random.nextDouble() * 0.10;
                    double vx = sinPhi * Math.cos(theta) * speed;
                    double vy = cosPhi * speed;
                    double vz = sinPhi * Math.sin(theta) * speed;
                    rocket.addSpark(new Spark(rocket.getX(), rocket.getY(), rocket.getZ(),
                            vx, vy, vz, color, 0.42f, 64, 0.005f, 0.992f, false, false));
                }
            }
            spawnEmbers(rocket, random, 130);
        }
    }

    /**
     * Sphere of sparks each leaving a long visible streak (long lifetime, smooth alpha
     * curve from trail flag). Looks like a flower made of streaks rather than dots.
     */
    public static class Chrysanthemum extends BurstPattern {
        @Override public boolean isBurst() { return true; }
        @Override public boolean hasCrackleSound() { return true; }
        @Override public int getBurstDuration() { return 90; }
        @Override public int getFlightLifetime() { return FLIGHT_LIFETIME; }

        @Override
        public void onFlightTick(FireworkRocketEntity rocket, RandomSource random) {
            emitFlightTrail(rocket, random, rocket.getLaunchColor(), 0.42f, 10, false);
        }

        @Override
        public void onBurstStart(FireworkRocketEntity rocket, RandomSource random) {
            int[] palette = rocket.getColors();
            int count = 280;
            for (int i = 0; i < count; i++) {
                double theta = random.nextDouble() * Math.PI * 2.0;
                double cosPhi = 2.0 * random.nextDouble() - 1.0;
                double sinPhi = Math.sqrt(Math.max(0.0, 1.0 - cosPhi * cosPhi));
                double speed = 1.50 + random.nextDouble() * 0.55;
                double vx = sinPhi * Math.cos(theta) * speed;
                double vy = cosPhi * speed + 0.08;
                double vz = sinPhi * Math.sin(theta) * speed;
                int color = paletteAt(palette, i);
                rocket.addSpark(new Spark(rocket.getX(), rocket.getY(), rocket.getZ(),
                        vx, vy, vz, color, 0.45f, 85, 0.009f, 0.992f, true, false));
            }
            spawnEmbers(rocket, random, 170);
        }
    }

    /**
     * Two-phase silhouette: a thin vertical trunk shoots up at burst start, then over
     * the next several ticks the apex blooms with falling fronds. The result is a
     * recognisable palm-tree shape.
     */
    public static class Palm extends BurstPattern {
        private static final double APEX_Y_OFFSET = 14.0;

        @Override public boolean isBurst() { return true; }
        @Override public int getBurstDuration() { return 110; }
        @Override public int getFlightLifetime() { return FLIGHT_LIFETIME; }
        @Override public float getLaunchSpeedMultiplier() { return 0.90f; }

        @Override
        public void onFlightTick(FireworkRocketEntity rocket, RandomSource random) {
            emitFlightTrail(rocket, random, rocket.getLaunchColor(), 0.46f, 12, true);
        }

        @Override
        public void onBurstStart(FireworkRocketEntity rocket, RandomSource random) {
            int color = rocket.getLaunchColor();
            int trunkCount = 50;
            for (int i = 0; i < trunkCount; i++) {
                double sx = (random.nextDouble() - 0.5) * 0.06;
                double sz = (random.nextDouble() - 0.5) * 0.06;
                double sy = 1.30 + random.nextDouble() * 0.55;
                rocket.addSpark(new Spark(rocket.getX(), rocket.getY(), rocket.getZ(),
                        sx, sy, sz, color, 0.50f, 55, 0.012f, 0.993f, true, false));
            }
            spawnEmbers(rocket, random, 60);
        }

        @Override
        public void onBurstTick(FireworkRocketEntity rocket, RandomSource random, int tickIndex) {
            if (tickIndex == 14 || tickIndex == 22 || tickIndex == 30 || tickIndex == 38) {
                int color = rocket.getLaunchColor();
                int frondCount = 50;
                double apexX = rocket.getX();
                double apexY = rocket.getY() + APEX_Y_OFFSET;
                double apexZ = rocket.getZ();
                for (int i = 0; i < frondCount; i++) {
                    double theta = random.nextDouble() * Math.PI * 2.0;
                    double horiz = 0.55 + random.nextDouble() * 0.30;
                    double drop = -0.65 - random.nextDouble() * 0.30;
                    Vec3 dir = new Vec3(Math.cos(theta) * horiz, drop, Math.sin(theta) * horiz);
                    Vec3 motion = dir.normalize().scale(1.55 + random.nextDouble() * 0.45);
                    rocket.addSpark(new Spark(apexX, apexY, apexZ,
                            motion.x, motion.y, motion.z, color, 0.45f, 80, 0.015f, 0.992f, true, false));
                }
            }
        }
    }

    /**
     * Tight downward cone — sparks only travel below the burst point in near-parallel
     * lines. Long lifetime, heavy falloff. Looks like a long horse tail of falling
     * silver streaks.
     */
    public static class Horsetail extends BurstPattern {
        @Override public boolean isBurst() { return true; }
        @Override public int getBurstDuration() { return 130; }
        @Override public int getFlightLifetime() { return FLIGHT_LIFETIME; }

        @Override
        public void onFlightTick(FireworkRocketEntity rocket, RandomSource random) {
            emitFlightTrail(rocket, random, rocket.getLaunchColor(), 0.44f, 11, true);
        }

        @Override
        public void onBurstStart(FireworkRocketEntity rocket, RandomSource random) {
            int color = rocket.getLaunchColor();
            int count = 260;
            for (int i = 0; i < count; i++) {
                double theta = random.nextDouble() * Math.PI * 2.0;
                double horiz = 0.18 + random.nextDouble() * 0.25;
                double down = -(0.85 + random.nextDouble() * 0.40);
                Vec3 dir = new Vec3(Math.cos(theta) * horiz, down, Math.sin(theta) * horiz);
                Vec3 motion = dir.normalize().scale(1.30 + random.nextDouble() * 0.40);
                rocket.addSpark(new Spark(rocket.getX(), rocket.getY(), rocket.getZ(),
                        motion.x, motion.y, motion.z, color, 0.42f, 110, 0.010f, 0.996f, true, false));
            }
            spawnEmbers(rocket, random, 100);
        }
    }

    /**
     * Flat horizontal ring expansion — every spark travels on a plane perpendicular
     * to the vertical axis. Tight Y range so the ring stays clean and visible.
     */
    public static class Ring extends BurstPattern {
        @Override public boolean isBurst() { return true; }
        @Override public int getBurstDuration() { return 50; }
        @Override public int getFlightLifetime() { return FLIGHT_LIFETIME; }

        @Override
        public void onFlightTick(FireworkRocketEntity rocket, RandomSource random) {
            emitFlightTrail(rocket, random, rocket.getLaunchColor(), 0.42f, 10, false);
        }

        @Override
        public void onBurstStart(FireworkRocketEntity rocket, RandomSource random) {
            int color = rocket.getLaunchColor();
            int count = 220;
            double speed = 1.85;
            for (int i = 0; i < count; i++) {
                double theta = random.nextDouble() * Math.PI * 2.0;
                double yJitter = (random.nextDouble() - 0.5) * 0.05;
                double vx = Math.cos(theta) * speed;
                double vy = yJitter;
                double vz = Math.sin(theta) * speed;
                rocket.addSpark(new Spark(rocket.getX(), rocket.getY(), rocket.getZ(),
                        vx, vy, vz, color, 0.45f, 48, 0.005f, 0.993f, false, false));
            }
            spawnEmbers(rocket, random, 70);
        }
    }

    /**
     * Bell-shape comet that flies high, lingers, then explodes into a dense gold
     * sphere. Combines pure comet flight (long bright trail) with a final burst.
     */
    public static class BellComet extends BurstPattern {
        @Override public boolean isBurst() { return true; }
        @Override public boolean hasCrackleSound() { return true; }
        @Override public int getBurstDuration() { return 95; }
        @Override public int getFlightLifetime() { return FLIGHT_LIFETIME; }
        @Override public double getGravity() { return 0.018; }
        @Override public double getDrag() { return 0.99; }
        @Override public float getLaunchSpeedMultiplier() { return 1.05f; }
        @Override public float getFlightLightSpread() { return 38.0f; }
        @Override public float getFlightHaloInnerSize() { return 2.5f; }
        @Override public float getFlightHaloOuterSize() { return 4.2f; }
        @Override public boolean spawnsFlightSmoke() { return true; }

        @Override
        public void onFlightTick(FireworkRocketEntity rocket, RandomSource random) {
            int color = rocket.getLaunchColor();
            for (int i = 0; i < 2; i++) {
                emitFlightTrail(rocket, random, color, 0.55f, 14, true);
            }
        }

        @Override
        public void onBurstStart(FireworkRocketEntity rocket, RandomSource random) {
            int color = rocket.getLaunchColor();
            int count = 320;
            for (int i = 0; i < count; i++) {
                double theta = random.nextDouble() * Math.PI * 2.0;
                double cosPhi = 2.0 * random.nextDouble() - 1.0;
                double sinPhi = Math.sqrt(Math.max(0.0, 1.0 - cosPhi * cosPhi));
                double speed = 1.20 + random.nextDouble() * 1.20;
                double vx = sinPhi * Math.cos(theta) * speed;
                double vy = cosPhi * speed + 0.10;
                double vz = sinPhi * Math.sin(theta) * speed;
                rocket.addSpark(new Spark(rocket.getX(), rocket.getY(), rocket.getZ(),
                        vx, vy, vz, color, 0.55f, 90, 0.011f, 0.992f, true, false));
            }
            spawnEmbers(rocket, random, 140);
        }
    }

    /**
     * Spirals upward — emits trail sparks in a rotating circle around the rocket so
     * the flight trail forms a corkscrew/helix. Standard sphere burst at apex.
     */
    public static class Spinner extends BurstPattern {
        @Override public boolean isBurst() { return true; }
        @Override public int getBurstDuration() { return 60; }
        @Override public int getFlightLifetime() { return FLIGHT_LIFETIME; }

        @Override
        public void onFlightTick(FireworkRocketEntity rocket, RandomSource random) {
            int color = rocket.getLaunchColor();
            double baseAngle = rocket.tickCount * 0.55;
            double radius = 0.55;
            for (int i = 0; i < 3; i++) {
                double a = baseAngle + i * (Math.PI * 2.0 / 3.0);
                double dx = Math.cos(a) * radius;
                double dz = Math.sin(a) * radius;
                rocket.addSpark(new Spark(rocket.getX() + dx, rocket.getY(), rocket.getZ() + dz,
                        dx * 0.18, -0.03, dz * 0.18, color, 0.36f, 18, 0.003f, 0.96f, true, false));
            }
        }

        @Override
        public void onBurstStart(FireworkRocketEntity rocket, RandomSource random) {
            int color = rocket.getLaunchColor();
            int count = 220;
            for (int i = 0; i < count; i++) {
                double theta = random.nextDouble() * Math.PI * 2.0;
                double cosPhi = 2.0 * random.nextDouble() - 1.0;
                double sinPhi = Math.sqrt(Math.max(0.0, 1.0 - cosPhi * cosPhi));
                double speed = 1.4 + random.nextDouble() * 0.5;
                double vx = sinPhi * Math.cos(theta) * speed;
                double vy = cosPhi * speed;
                double vz = sinPhi * Math.sin(theta) * speed;
                rocket.addSpark(new Spark(rocket.getX(), rocket.getY(), rocket.getZ(),
                        vx, vy, vz, color, 0.42f, 50, 0.004f, 0.992f, false, false));
            }
            spawnEmbers(rocket, random, 90);
        }
    }

    /**
     * Crossette: each main spark explodes again into a small cross. The main burst
     * spawns ~30 sparks outward, then at tick 12 every still-living main spark
     * detonates into 6 perpendicular sub-sparks (±X, ±Y, ±Z) creating the classic
     * crisscross-pattern signature of a real crossette.
     */
    public static class Crossette extends BurstPattern {
        private static final float MAIN_SPARK_SCALE = 0.55f;

        @Override public boolean isBurst() { return true; }
        @Override public int getBurstDuration() { return 70; }
        @Override public int getFlightLifetime() { return FLIGHT_LIFETIME; }

        @Override
        public void onFlightTick(FireworkRocketEntity rocket, RandomSource random) {
            emitFlightTrail(rocket, random, rocket.getLaunchColor(), 0.42f, 10, false);
        }

        @Override
        public void onBurstStart(FireworkRocketEntity rocket, RandomSource random) {
            int color = rocket.getLaunchColor();
            int count = 30;
            for (int i = 0; i < count; i++) {
                double theta = random.nextDouble() * Math.PI * 2.0;
                double cosPhi = 2.0 * random.nextDouble() - 1.0;
                double sinPhi = Math.sqrt(Math.max(0.0, 1.0 - cosPhi * cosPhi));
                double speed = 0.95 + random.nextDouble() * 0.30;
                double vx = sinPhi * Math.cos(theta) * speed;
                double vy = cosPhi * speed;
                double vz = sinPhi * Math.sin(theta) * speed;
                rocket.addSpark(new Spark(rocket.getX(), rocket.getY(), rocket.getZ(),
                        vx, vy, vz, color, MAIN_SPARK_SCALE, 60, 0.005f, 0.992f, true, false));
            }
            spawnEmbers(rocket, random, 50);
        }

        @Override
        public void onBurstTick(FireworkRocketEntity rocket, RandomSource random, int tickIndex) {
            if (tickIndex != 12) {
                return;
            }
            int color = rocket.getLaunchColor();
            double subSpeed = 0.55;
            double[][] dirs = {
                    { subSpeed, 0, 0}, {-subSpeed, 0, 0},
                    {0,  subSpeed, 0}, {0, -subSpeed, 0},
                    {0, 0,  subSpeed}, {0, 0, -subSpeed}
            };
            java.util.List<Spark> sparks = rocket.getSparks();
            int snapshotSize = sparks.size();
            for (int i = 0; i < snapshotSize; i++) {
                Spark main = sparks.get(i);
                if (main.scale < MAIN_SPARK_SCALE - 0.01f || main.isDead() || main.age > 13) {
                    continue;
                }
                for (double[] d : dirs) {
                    double jitter = 0.05;
                    double vx = d[0] + (random.nextDouble() - 0.5) * jitter;
                    double vy = d[1] + (random.nextDouble() - 0.5) * jitter;
                    double vz = d[2] + (random.nextDouble() - 0.5) * jitter;
                    rocket.addSpark(new Spark(main.x, main.y, main.z, vx, vy, vz,
                            color, 0.34f, 28, 0.004f, 0.992f, false, false));
                }
            }
        }
    }

    /**
     * Mine: a ground-level upward fountain. The launcher fires no rocket — instead
     * the burst happens immediately at the launcher position, throwing many sparks
     * into a tall narrow cone of fire above the device. Used for stage mine effects.
     */
    public static class Mine extends BurstPattern {
        @Override public boolean isBurst() { return true; }
        @Override public boolean hasCrackleSound() { return true; }
        @Override public int getBurstDuration() { return 90; }
        @Override public int getFlightLifetime() { return 1; }
        @Override public float getLaunchSpeedMultiplier() { return 0.0f; }

        @Override
        public void onBurstStart(FireworkRocketEntity rocket, RandomSource random) {
            int[] palette = rocket.getColors();
            int count = 200;
            for (int i = 0; i < count; i++) {
                double theta = random.nextDouble() * Math.PI * 2.0;
                double horizontal = 0.10 + random.nextDouble() * 0.30;
                double upward = 1.00 + random.nextDouble() * 0.50;
                double speed = 1.50 + random.nextDouble() * 1.00;
                double dirLen = Math.sqrt(horizontal * horizontal + upward * upward);
                double vx = (Math.cos(theta) * horizontal / dirLen) * speed;
                double vy = (upward / dirLen) * speed;
                double vz = (Math.sin(theta) * horizontal / dirLen) * speed;
                int color = paletteAt(palette, i);
                rocket.addSpark(new Spark(rocket.getX(), rocket.getY(), rocket.getZ(),
                        vx, vy, vz, color, 0.46f, 70 + random.nextInt(20), 0.040f, 0.985f, true, false));
            }
            spawnEmbers(rocket, random, 70);
        }
    }

    /**
     * Wide flat fan — sparks spread mostly horizontally with a tight Y range. Quick
     * and dramatic, like a spider web exploding outward.
     */
    public static class Spider extends BurstPattern {
        @Override public boolean isBurst() { return true; }
        @Override public int getBurstDuration() { return 32; }
        @Override public int getFlightLifetime() { return FLIGHT_LIFETIME; }

        @Override
        public void onFlightTick(FireworkRocketEntity rocket, RandomSource random) {
            emitFlightTrail(rocket, random, rocket.getLaunchColor(), 0.42f, 10, false);
        }

        @Override
        public void onBurstStart(FireworkRocketEntity rocket, RandomSource random) {
            int color = rocket.getLaunchColor();
            int count = 240;
            for (int i = 0; i < count; i++) {
                double theta = random.nextDouble() * Math.PI * 2.0;
                double yJitter = (random.nextDouble() - 0.5) * 0.35;
                double speed = 2.10 + random.nextDouble() * 0.45;
                double vx = Math.cos(theta) * speed;
                double vy = yJitter * speed;
                double vz = Math.sin(theta) * speed;
                rocket.addSpark(new Spark(rocket.getX(), rocket.getY(), rocket.getZ(),
                        vx, vy, vz, color, 0.40f, 30, 0.004f, 0.993f, false, false));
            }
            spawnEmbers(rocket, random, 90);
        }
    }

    /**
     * Slow lingering sphere — sparks barely fall, holding their shape for a long
     * fade. Looks like a glowing crown of stars suspended in the sky.
     */
    public static class Diadem extends BurstPattern {
        @Override public boolean isBurst() { return true; }
        @Override public int getBurstDuration() { return 110; }
        @Override public int getFlightLifetime() { return FLIGHT_LIFETIME; }

        @Override
        public void onFlightTick(FireworkRocketEntity rocket, RandomSource random) {
            emitFlightTrail(rocket, random, rocket.getLaunchColor(), 0.42f, 10, false);
        }

        @Override
        public void onBurstStart(FireworkRocketEntity rocket, RandomSource random) {
            int color = rocket.getLaunchColor();
            int count = 280;
            for (int i = 0; i < count; i++) {
                double theta = random.nextDouble() * Math.PI * 2.0;
                double cosPhi = 2.0 * random.nextDouble() - 1.0;
                double sinPhi = Math.sqrt(Math.max(0.0, 1.0 - cosPhi * cosPhi));
                double speed = 0.90 + random.nextDouble() * 0.25;
                double vx = sinPhi * Math.cos(theta) * speed;
                double vy = cosPhi * speed;
                double vz = sinPhi * Math.sin(theta) * speed;
                rocket.addSpark(new Spark(rocket.getX(), rocket.getY(), rocket.getZ(),
                        vx, vy, vz, color, 0.50f, 100, 0.0010f, 0.997f, false, false));
            }
            spawnEmbers(rocket, random, 140);
        }
    }

    /**
     * Pure flash — a brief, very bright detonation with minimal trailing sparks.
     * Loud bang, big halo, scattered embers. Classic salute / aerial bomb feel.
     */
    public static class Salute extends BurstPattern {
        @Override public boolean isBurst() { return true; }
        @Override public boolean hasCrackleSound() { return true; }
        @Override public int getBurstDuration() { return 16; }
        @Override public int getFlightLifetime() { return FLIGHT_LIFETIME; }

        @Override
        public void onFlightTick(FireworkRocketEntity rocket, RandomSource random) {
            emitFlightTrail(rocket, random, rocket.getLaunchColor(), 0.42f, 10, false);
        }

        @Override
        public void onBurstStart(FireworkRocketEntity rocket, RandomSource random) {
            int color = rocket.getLaunchColor();
            int count = 80;
            for (int i = 0; i < count; i++) {
                double theta = random.nextDouble() * Math.PI * 2.0;
                double cosPhi = 2.0 * random.nextDouble() - 1.0;
                double sinPhi = Math.sqrt(Math.max(0.0, 1.0 - cosPhi * cosPhi));
                double speed = 3.0 + random.nextDouble() * 1.5;
                double vx = sinPhi * Math.cos(theta) * speed;
                double vy = cosPhi * speed;
                double vz = sinPhi * Math.sin(theta) * speed;
                rocket.addSpark(new Spark(rocket.getX(), rocket.getY(), rocket.getZ(),
                        vx, vy, vz, color, 0.55f, 14, 0.003f, 0.99f, false, false));
            }
            spawnEmbers(rocket, random, 130);
        }
    }

    /**
     * Heart-shaped burst — sparks emit along a parametric heart curve in the camera
     * plane. Uses the classic 16sin³(t)/13cos(t)-... heart equation.
     */
    public static class Heart extends BurstPattern {
        @Override public boolean isBurst() { return true; }
        @Override public int getBurstDuration() { return 70; }
        @Override public int getFlightLifetime() { return FLIGHT_LIFETIME; }

        @Override
        public void onFlightTick(FireworkRocketEntity rocket, RandomSource random) {
            emitFlightTrail(rocket, random, rocket.getLaunchColor(), 0.42f, 10, false);
        }

        @Override
        public void onBurstStart(FireworkRocketEntity rocket, RandomSource random) {
            int color = rocket.getLaunchColor();
            int count = 220;
            double scale = 0.11;
            for (int i = 0; i < count; i++) {
                double t = (i / (double) count) * Math.PI * 2.0 + random.nextDouble() * 0.04;
                double hx = 16.0 * Math.pow(Math.sin(t), 3);
                double hy = 13.0 * Math.cos(t) - 5.0 * Math.cos(2.0 * t) - 2.0 * Math.cos(3.0 * t) - Math.cos(4.0 * t);
                double zJitter = (random.nextDouble() - 0.5) * 0.06;
                double vx = hx * scale;
                double vy = hy * scale;
                double vz = zJitter;
                rocket.addSpark(new Spark(rocket.getX(), rocket.getY(), rocket.getZ(),
                        vx, vy, vz, color, 0.42f, 65, 0.0025f, 0.995f, false, false));
            }
            spawnEmbers(rocket, random, 90);
        }
    }

    /**
     * Two distinct expansion phases: a small inner sphere immediately, then a larger
     * outer sphere at tickIndex 14 in a different palette color. Layered explosion.
     */
    public static class DoubleBurst extends BurstPattern {
        @Override public boolean isBurst() { return true; }
        @Override public int getBurstDuration() { return 75; }
        @Override public int getFlightLifetime() { return FLIGHT_LIFETIME; }

        @Override
        public void onFlightTick(FireworkRocketEntity rocket, RandomSource random) {
            emitFlightTrail(rocket, random, rocket.getLaunchColor(), 0.42f, 10, false);
        }

        @Override
        public void onBurstStart(FireworkRocketEntity rocket, RandomSource random) {
            int[] palette = rocket.getColors();
            int color1 = palette[0];
            int count = 130;
            for (int i = 0; i < count; i++) {
                double theta = random.nextDouble() * Math.PI * 2.0;
                double cosPhi = 2.0 * random.nextDouble() - 1.0;
                double sinPhi = Math.sqrt(Math.max(0.0, 1.0 - cosPhi * cosPhi));
                double speed = 0.85 + random.nextDouble() * 0.20;
                double vx = sinPhi * Math.cos(theta) * speed;
                double vy = cosPhi * speed;
                double vz = sinPhi * Math.sin(theta) * speed;
                rocket.addSpark(new Spark(rocket.getX(), rocket.getY(), rocket.getZ(),
                        vx, vy, vz, color1, 0.42f, 38, 0.004f, 0.992f, false, false));
            }
        }

        @Override
        public void onBurstTick(FireworkRocketEntity rocket, RandomSource random, int tickIndex) {
            if (tickIndex == 14) {
                int[] palette = rocket.getColors();
                int color2 = palette.length > 1 ? palette[1] : palette[0];
                int count = 220;
                for (int i = 0; i < count; i++) {
                    double theta = random.nextDouble() * Math.PI * 2.0;
                    double cosPhi = 2.0 * random.nextDouble() - 1.0;
                    double sinPhi = Math.sqrt(Math.max(0.0, 1.0 - cosPhi * cosPhi));
                    double speed = 1.75 + random.nextDouble() * 0.45;
                    double vx = sinPhi * Math.cos(theta) * speed;
                    double vy = cosPhi * speed;
                    double vz = sinPhi * Math.sin(theta) * speed;
                    rocket.addSpark(new Spark(rocket.getX(), rocket.getY(), rocket.getZ(),
                            vx, vy, vz, color2, 0.50f, 60, 0.005f, 0.992f, false, false));
                }
                spawnEmbers(rocket, random, 110);
            }
        }
    }

    /**
     * Whistler — pure flying comet (no burst) with a thicker oscillating trail and a
     * crackle sound. Side sparks alternate left/right as it climbs.
     */
    public static class Whistler extends BurstPattern {
        @Override public boolean isBurst() { return false; }
        @Override public boolean hasCrackleSound() { return true; }
        @Override public int getBurstDuration() { return 0; }
        @Override public int getCometFadeTicks() { return 4; }
        @Override public int getFlightLifetime() { return FLIGHT_LIFETIME; }
        @Override public float getFlightLightSpread() { return 34.0f; }
        @Override public float getFlightHaloInnerSize() { return 1.9f; }
        @Override public float getFlightHaloOuterSize() { return 2.85f; }
        @Override public double getFlightWobble() { return 0.025; }

        @Override
        public void onFlightTick(FireworkRocketEntity rocket, RandomSource random) {
            int color = rocket.getLaunchColor();
            for (int i = 0; i < 3; i++) {
                emitFlightTrail(rocket, random, color, 0.50f, 12, true);
            }
            double baseAngle = rocket.tickCount * 0.65;
            Vec3 baseVel = rocket.getDeltaMovement().scale(-0.05);
            int sparkCount = 4;
            for (int i = 0; i < sparkCount; i++) {
                double angle = baseAngle + i * (Math.PI * 2.0 / sparkCount) + (random.nextDouble() - 0.5) * 0.4;
                double radius = 0.50 + random.nextDouble() * 0.80;
                double dx = Math.cos(angle) * radius;
                double dz = Math.sin(angle) * radius;
                double outwardSpeed = 0.18 + random.nextDouble() * 0.15;
                double inv = 1.0 / Math.max(0.001, radius);
                double ovx = dx * inv * outwardSpeed;
                double ovz = dz * inv * outwardSpeed;
                int life = 14 + random.nextInt(6);
                float scale = 0.30f + random.nextFloat() * 0.10f;
                rocket.addSpark(new Spark(
                        rocket.getX() + dx, rocket.getY() + (random.nextDouble() - 0.5) * 0.4, rocket.getZ() + dz,
                        baseVel.x + ovx, baseVel.y, baseVel.z + ovz,
                        color, scale, life, 0.003f, 0.96f, true, false));
            }
        }

        @Override
        public void onFadeTick(FireworkRocketEntity rocket, RandomSource random, int remainingTicks) {
            // Silent fade — no new sparks. Rocket despawns shortly after apex.
        }
    }

    /**
     * Pure flying comet with a soft fade — never explodes. Used for the basic
     * single-color comets: red, blue, green, gold.
     */
    public static class Comet extends BurstPattern {
        @Override public boolean isBurst() { return false; }
        @Override public int getBurstDuration() { return 0; }
        @Override public int getCometFadeTicks() { return 7; }
        @Override public int getFlightLifetime() { return FLIGHT_LIFETIME; }
        @Override public float getFlightLightSpread() { return 32.0f; }
        @Override public float getFlightHaloInnerSize() { return 1.7f; }
        @Override public float getFlightHaloOuterSize() { return 2.55f; }
        @Override public boolean spawnsFlightSmoke() { return true; }

        @Override
        public void onFlightTick(FireworkRocketEntity rocket, RandomSource random) {
            int color = rocket.getLaunchColor();
            emitFlightTrail(rocket, random, color, 0.50f, 12, true);
        }

        @Override
        public void onFadeTick(FireworkRocketEntity rocket, RandomSource random, int remainingTicks) {
            int color = rocket.getLaunchColor();
            int fadeTicks = getCometFadeTicks();
            float fade = remainingTicks / (float) Math.max(1, fadeTicks);
            if (fade <= 0.0f || random.nextFloat() > fade * 0.85f) {
                return;
            }
            Vec3 motion = rocket.getDeltaMovement().scale(-0.05);
            double scatter = 0.06;
            double vx = motion.x + (random.nextDouble() - 0.5) * scatter;
            double vy = motion.y + (random.nextDouble() - 0.5) * scatter * 0.5;
            double vz = motion.z + (random.nextDouble() - 0.5) * scatter;
            rocket.addSpark(new Spark(rocket.getX(), rocket.getY(), rocket.getZ(),
                    vx, vy, vz, color, 0.30f + random.nextFloat() * 0.12f, 14, 0.006f, 0.97f, true, false));
        }
    }

    /**
     * Long ascending comet with dense trail, then a slow falling cascade after apex.
     */
    public static class LongTrailComet extends BurstPattern {
        @Override public boolean isBurst() { return false; }
        @Override public int getBurstDuration() { return 0; }
        @Override public int getCometFadeTicks() { return 52; }
        @Override public int getFlightLifetime() { return 220; }
        @Override public boolean continuesAfterApex() { return true; }
        @Override public float getLaunchSpeedMultiplier() { return 1.12f; }
        @Override public float getFlightLightSpread() { return 34.0f; }
        @Override public float getFlightHaloInnerSize() { return 1.8f; }
        @Override public float getFlightHaloOuterSize() { return 2.7f; }
        @Override public boolean spawnsFlightSmoke() { return true; }

        @Override
        public void onFlightTick(FireworkRocketEntity rocket, RandomSource random) {
            int[] palette = rocket.getColors();
            int primary = rocket.getLaunchColor();
            int secondary = palette.length > 1 ? palette[1] : primary;
            emitFlightTrail(rocket, random, primary, 0.56f, 18, true);
            if (random.nextFloat() < 0.45f) {
                emitFlightTrail(rocket, random, secondary, 0.42f, 14, true);
            }
        }

        @Override
        public void onFadeTick(FireworkRocketEntity rocket, RandomSource random, int remainingTicks) {
            int[] palette = rocket.getColors();
            int primary = rocket.getLaunchColor();
            int secondary = palette.length > 1 ? palette[1] : primary;
            float fade = remainingTicks / (float) Math.max(1, getCometFadeTicks());
            if (fade <= 0.0f) {
                return;
            }
            int count = 2 + random.nextInt(3);
            for (int i = 0; i < count; i++) {
                double scatter = 0.10 * fade;
                double vx = (random.nextDouble() - 0.5) * scatter;
                double vy = -0.04 - random.nextDouble() * 0.10 * fade;
                double vz = (random.nextDouble() - 0.5) * scatter;
                int color = random.nextBoolean() ? primary : secondary;
                rocket.addSpark(new Spark(
                        rocket.getX(), rocket.getY(), rocket.getZ(),
                        vx, vy, vz,
                        color,
                        0.28f + random.nextFloat() * 0.14f,
                        24 + random.nextInt(18),
                        0.012f,
                        0.975f,
                        true,
                        false
                ));
            }
        }
    }
}
