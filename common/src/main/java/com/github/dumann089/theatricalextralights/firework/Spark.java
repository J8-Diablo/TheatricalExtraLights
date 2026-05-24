package com.github.dumann089.theatricalextralights.firework;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/**
 * One visible spark emitted by a firework. Updated each tick by the entity, drawn each
 * frame by the entity renderer as a halo quad (same visual style as the rocket).
 * Pure data + simple physics — no Minecraft particle system involved.
 */
public final class Spark {
    public double x, y, z;
    public double prevX, prevY, prevZ;
    public double vx, vy, vz;

    public final int color;
    public final float scale;
    public final int lifetime;
    public final float gravity;
    public final float drag;
    public final boolean trail;
    public final boolean strobe;

    public int age;

    public Spark(double x, double y, double z,
                 double vx, double vy, double vz,
                 int color, float scale, int lifetime,
                 float gravity, float drag,
                 boolean trail, boolean strobe) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
        this.vx = vx;
        this.vy = vy;
        this.vz = vz;
        this.color = color;
        this.scale = scale;
        this.lifetime = lifetime;
        this.gravity = gravity;
        this.drag = drag;
        this.trail = trail;
        this.strobe = strobe;
        this.age = 0;
    }

    public void tick() {
        prevX = x;
        prevY = y;
        prevZ = z;
        x += vx;
        y += vy;
        z += vz;
        vx *= drag;
        vy = (vy - gravity) * drag;
        vz *= drag;
        age++;
    }

    public boolean isDead() {
        return age >= lifetime;
    }

    public Vec3 getPosition(float partialTick) {
        return new Vec3(
                Mth.lerp(partialTick, prevX, x),
                Mth.lerp(partialTick, prevY, y),
                Mth.lerp(partialTick, prevZ, z)
        );
    }

    public float getAlpha(float partialTick) {
        if (strobe) {
            return age % 2 == 0 ? 1.0f : 0.30f;
        }
        float t = (age + partialTick) / lifetime;
        if (t >= 1.0f) {
            return 0.0f;
        }
        if (trail) {
            float remaining = 1.0f - t;
            return remaining * remaining * (3.0f - 2.0f * remaining);
        }
        return 1.0f - t;
    }

    public float getScale(float partialTick) {
        return scale;
    }
}
