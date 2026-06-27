package com.github.dumann089.theatricalextralights.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/** Block-space offsets and fixture rotations for Extra Lights fixtures. */
public final class DirectionOffset {
    public static final Vec3 FLAME_HEAD_PIVOT = new Vec3(0.0, -0.12, 0.19);
    /** Pivot tête BER (aligné sur le modèle Extra Lights). */
    public static final Vec3 FLAME_HEAD_RENDER_PIVOT = new Vec3(0.0, -0.12, -0.19);
    public static final Vec3 FLAME_NOZZLE_OFFSET = new Vec3(0.0, 0.13 - 5.0 / 16.0, 0.18);

    private DirectionOffset() {
    }

    public static Vec3 correctedOffset(Direction dir, Vec3 offset) {
        if (dir == null || offset == null) {
            return Vec3.ZERO;
        }
        double x = offset.x + 0.5;
        double y = offset.y + 0.5;
        double z = offset.z + 0.5;
        return switch (dir) {
            case SOUTH -> new Vec3(x, y, z);
            case WEST -> new Vec3(1.0 - z, y, x);
            case NORTH -> new Vec3(1.0 - x, y, 1.0 - z);
            case EAST -> new Vec3(z, y, 1.0 - x);
            default -> Vec3.ZERO;
        };
    }

    /** DMX pan 0-255, 128 = centre vertical, 0/255 = horizontal ±90° (180° total). */
    public static float panToAngle(int panValue) {
        return (Mth.clamp(panValue, 0, 255) - 128) / 128.0f * 90.0f;
    }

    /** Point d'émission fixe au bec (le pan incline le jet, pas le spawn). */
    public static Vec3 flameNozzlePosition(Direction facing) {
        return correctedOffset(facing, FLAME_NOZZLE_OFFSET);
    }

    /** Même signe que {@link #panJetDirection} pour que la tête suive le jet. */
    public static float panToHeadRenderAngle(int panValue) {
        return -panToAngle(panValue);
    }

    /**
     * 128 = vertical, 0/255 = quasi horizontal gauche/droite.
     */
    public static Vec3 panJetDirection(Direction facing, float panAngleDegrees) {
        double rad = Math.toRadians(panAngleDegrees);
        double sin = Math.sin(rad);
        double cos = Math.cos(rad);
        Vec3 localJet = new Vec3(-sin, cos, 0.0);
        return localDirectionToWorld(facing, localJet);
    }

    private static Vec3 localDirectionToWorld(Direction facing, Vec3 local) {
        Vec3 world = switch (facing) {
            case SOUTH -> new Vec3(local.x, local.y, local.z);
            case NORTH -> new Vec3(-local.x, local.y, -local.z);
            case EAST -> new Vec3(-local.z, local.y, local.x);
            case WEST -> new Vec3(local.z, local.y, -local.x);
            default -> local;
        };
        return world.lengthSqr() > 1.0e-6 ? world.normalize() : new Vec3(0.0, 1.0, 0.0);
    }

    public static void applyFixtureRotation(PoseStack poseStack, Direction facing, Vec3 rotation) {
        switch (facing) {
            case NORTH -> {
                poseStack.mulPose(Axis.XP.rotationDegrees((float) rotation.x));
                poseStack.mulPose(Axis.YP.rotationDegrees((float) rotation.y));
                poseStack.mulPose(Axis.ZP.rotationDegrees((float) rotation.z));
            }
            case SOUTH -> {
                poseStack.mulPose(Axis.XP.rotationDegrees((float) -rotation.x));
                poseStack.mulPose(Axis.YP.rotationDegrees((float) -rotation.y));
                poseStack.mulPose(Axis.ZP.rotationDegrees((float) rotation.z));
            }
            case EAST -> {
                poseStack.mulPose(Axis.XP.rotationDegrees((float) rotation.x));
                poseStack.mulPose(Axis.ZP.rotationDegrees((float) -rotation.y));
                poseStack.mulPose(Axis.YP.rotationDegrees((float) rotation.z));
            }
            case WEST -> {
                poseStack.mulPose(Axis.XP.rotationDegrees((float) -rotation.x));
                poseStack.mulPose(Axis.ZP.rotationDegrees((float) rotation.y));
                poseStack.mulPose(Axis.YP.rotationDegrees((float) rotation.z));
            }
            default -> poseStack.mulPose(Axis.YP.rotationDegrees((float) rotation.y));
        }
    }
}
