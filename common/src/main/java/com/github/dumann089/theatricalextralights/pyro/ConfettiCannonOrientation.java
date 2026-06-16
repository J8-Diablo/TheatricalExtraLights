package com.github.dumann089.theatricalextralights.pyro;

import com.github.dumann089.theatricalextralights.blockentities.ConfettiCannonBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.imabad.theatrical.blocks.HangableBlock;
import dev.imabad.theatrical.blocks.light.BaseLightBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * Barrel aim derived from the same transforms as {@code ConfettiCannonRenderer}.
 */
public final class ConfettiCannonOrientation {
    /** Blockbench barrel part rotation (Z -37.5°). */
    private static final float BARREL_Z_ROT = -0.6545F;
    /** Barrel mouth / tip in Blockbench pixels (root space). */
    private static final float MOUTH_X = -3.0F;
    private static final float MOUTH_Y = 13.0F;
    private static final float TIP_ALONG_BARREL = 7.5F;

    private ConfettiCannonOrientation() {
    }

    public static Vec3 getLaunchDirection(BlockState state, ConfettiCannonBlockEntity blockEntity) {
        PoseStack pose = new PoseStack();
        applyRenderingTransforms(pose, state, blockEntity);

        Vector3f mouth = modelPointToBlockLocal(MOUTH_X, MOUTH_Y, 0.0F);
        Vector3f tip = modelPointToBlockLocal(barrelTipX(), barrelTipY(), 0.0F);
        mouth.mulPosition(pose.last().pose());
        tip.mulPosition(pose.last().pose());

        Vector3f dir = new Vector3f(tip).sub(mouth);
        if (dir.lengthSquared() < 1.0E-6F) {
            dir.set(0.0F, 1.0F, 0.0F);
        } else {
            dir.normalize();
        }
        if (dir.y < 0.0F) {
            dir.y = -dir.y;
        }
        return new Vec3(dir.x, dir.y, dir.z);
    }

    public static Vec3 getNozzlePosition(BlockPos pos, BlockState state, ConfettiCannonBlockEntity blockEntity) {
        PoseStack pose = new PoseStack();
        applyRenderingTransforms(pose, state, blockEntity);

        Vector3f mouth = modelPointToBlockLocal(MOUTH_X, MOUTH_Y, 0.0F);
        mouth.mulPosition(pose.last().pose());
        return new Vec3(pos.getX() + mouth.x, pos.getY() + mouth.y, pos.getZ() + mouth.z);
    }

    /** Shared with {@code ConfettiCannonRenderer}. */
    public static void applyRenderingTransforms(PoseStack poseStack, BlockState state, ConfettiCannonBlockEntity blockEntity) {
        Direction facing = state.getValue(HangableBlock.FACING);
        boolean isFlipped = blockEntity.isUpsideDown();
        boolean isHanging = state.getValue(BaseLightBlock.HANGING);

        poseStack.translate(0.5F, 0.0F, 0.5F);
        if (isHanging) {
            Direction hangDirection = state.getValue(HangableBlock.HANG_DIRECTION);
            poseStack.translate(0.0F, 0.5F, 0.0F);
            if (hangDirection.getAxis() != Direction.Axis.Y) {
                if (hangDirection.getAxis() == Direction.Axis.Z) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(hangDirection == Direction.SOUTH ? -90.0F : 90.0F));
                    poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                } else {
                    poseStack.mulPose(Axis.ZN.rotationDegrees(hangDirection == Direction.EAST ? -90.0F : 90.0F));
                }
            }
            poseStack.translate(0.0F, -0.5F, 0.0F);
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(facing.toYRot()));
        poseStack.translate(-0.5F, 0.0F, -0.5F);

        if (isHanging) {
            var support = blockEntity.getSupportingStructure();
            if (support.isPresent()) {
                float[] transforms = blockEntity.getFixture().getTransforms(state, support.get());
                poseStack.translate(transforms[0], transforms[1], transforms[2]);
            } else {
                poseStack.translate(0.0F, 0.19F, 0.0F);
            }
            poseStack.translate(0.0F, -0.08F, 0.0F);
        }
        if (isFlipped) {
            poseStack.translate(0.5F, 0.5F, 0.5F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            poseStack.translate(-0.5F, -0.5F, -0.5F);
        }

        applyBlockbenchEntityTransform(poseStack);
    }

    /** Item / inventory preview — blockbench entity transform only. */
    public static void applyBlockbenchEntityTransform(PoseStack poseStack) {
        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
    }

    public static Vector3f spreadDirection(RandomSource random, Vec3 axis, float spread) {
        Vector3f facing = axis.toVector3f();
        Vector3f orth = orthogonal(facing);
        orth.rotateAxis(random.nextFloat() * Mth.TWO_PI, facing.x, facing.y, facing.z);
        orth.mul((float) (random.nextGaussian() * spread));
        facing.add((Vector3fc) orth).normalize();
        return facing;
    }

    private static Vector3f modelPointToBlockLocal(float px, float py, float pz) {
        return new Vector3f(px / 16.0F, py / 16.0F, pz / 16.0F);
    }

    private static float barrelTipX() {
        Vector3f along = new Vector3f(-TIP_ALONG_BARREL, 0.0F, 0.0F);
        along.rotateZ(BARREL_Z_ROT);
        return MOUTH_X + along.x;
    }

    private static float barrelTipY() {
        Vector3f along = new Vector3f(-TIP_ALONG_BARREL, 0.0F, 0.0F);
        along.rotateZ(BARREL_Z_ROT);
        return MOUTH_Y + along.y;
    }

    private static Vector3f orthogonal(Vector3f v) {
        if (Math.abs(v.x) > Math.abs(v.y)) {
            return new Vector3f(-v.z, 0.0F, v.x).normalize();
        }
        return new Vector3f(0.0F, v.z, -v.y).normalize();
    }
}
