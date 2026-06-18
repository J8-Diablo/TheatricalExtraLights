package com.github.dumann089.theatricalextralights.firework;

import net.minecraft.core.Direction;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Raycast along a spark step so daytime powder stops on solid blocks. */
public final class SparkBlockCollision {
    private static final double SURFACE_EPSILON = 0.02;

    private SparkBlockCollision() {
    }

    public record MoveResult(double x, double y, double z, boolean blocked) {
    }

    public static MoveResult move(Level level, double x, double y, double z, double dx, double dy, double dz) {
        Vec3 from = new Vec3(x, y, z);
        Vec3 to = new Vec3(x + dx, y + dy, z + dz);
        if (from.distanceToSqr(to) < 1.0E-10) {
            return new MoveResult(x, y, z, false);
        }

        BlockHitResult hit = level.clip(new ClipContext(
                from,
                to,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                null
        ));
        if (hit.getType() != HitResult.Type.BLOCK) {
            return new MoveResult(to.x, to.y, to.z, false);
        }

        Vec3 pos = hit.getLocation();
        Direction face = hit.getDirection();
        return new MoveResult(
                pos.x - face.getStepX() * SURFACE_EPSILON,
                pos.y - face.getStepY() * SURFACE_EPSILON,
                pos.z - face.getStepZ() * SURFACE_EPSILON,
                true
        );
    }
}
