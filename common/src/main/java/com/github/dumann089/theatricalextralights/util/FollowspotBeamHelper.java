package com.github.dumann089.theatricalextralights.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.imabad.theatrical.api.Fixture;
import dev.imabad.theatrical.blockentities.light.BaseLightBlockEntity;
import dev.imabad.theatrical.blocks.HangableBlock;
import dev.imabad.theatrical.blocks.light.BaseLightBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.Optional;

public final class FollowspotBeamHelper {

    /** Local X — operator stands beside the head (avoids truss behind the fixture). */
    private static final float DEFAULT_OPERATOR_EYE_SIDE = -0.85f;
    /** Local Y — middle of the head, relative to the lens origin. */
    private static final float OPERATOR_EYE_UP = 0.05f;
    /** Local Z — slightly in front of the side position, looking toward the beam. */
    private static final float OPERATOR_EYE_FORWARD = 0.2f;

    private FollowspotBeamHelper() {
    }

    public static Vec3 getBeamOrigin(BaseLightBlockEntity fixture) {
        return getBeamOrigin(fixture, fixture.getPan(), fixture.getTilt());
    }

    public static Vec3 getBeamOrigin(BaseLightBlockEntity fixture, float pan, float tilt) {
        BlockPos blockPos = fixture.getBlockPos();
        PoseStack poseStack = createFixturePose(fixture, pan, tilt);
        float[] beam = fixture.getFixture().getBeamStartPosition();
        poseStack.translate(beam[0], beam[1], beam[2]);
        Vector3f local = new Vector3f(0f, 0f, 0f);
        local.mulPosition(poseStack.last().pose());

        return new Vec3(
                blockPos.getX() + local.x,
                blockPos.getY() + local.y,
                blockPos.getZ() + local.z
        );
    }

    public static Vec3 getBeamDirection(BaseLightBlockEntity fixture, float pan, float tilt) {
        Vector3f forward = getBeamForward(createFixturePose(fixture, pan, tilt));
        return new Vec3(forward.x, forward.y, forward.z);
    }

    public static float[] getLookAngles(BaseLightBlockEntity fixture, float pan, float tilt) {
        Vector3f forward = getBeamForward(createFixturePose(fixture, pan, tilt));
        return directionToLookAngles(new Vec3(forward.x, forward.y, forward.z));
    }

    /** Side of the fixture head — operator POV beside the unit (clear of overhead truss). */
    public static Vec3 getCameraPosition(BaseLightBlockEntity fixture, float pan, float tilt) {
        float eyeSide = FollowspotOrientationHelper.getOperatorEyeSide(fixture, pan, tilt);
        return getCameraPosition(fixture, pan, tilt, eyeSide);
    }

    public static Vec3 getCameraPosition(BaseLightBlockEntity fixture, float pan, float tilt, float eyeSideX) {
        BlockPos blockPos = fixture.getBlockPos();
        PoseStack poseStack = createFixturePose(fixture, pan, tilt);
        float[] beam = fixture.getFixture().getBeamStartPosition();
        poseStack.translate(beam[0], beam[1], beam[2]);
        Vector3f localEye = new Vector3f(eyeSideX, OPERATOR_EYE_UP, OPERATOR_EYE_FORWARD);
        localEye.mulPosition(poseStack.last().pose());
        return new Vec3(
                blockPos.getX() + localEye.x,
                blockPos.getY() + localEye.y,
                blockPos.getZ() + localEye.z
        );
    }

    public static float getBeamLength(BaseLightBlockEntity fixture, float pan, float tilt) {
        if (fixture.getLevel() == null) {
            return (float) (fixture.getFixture().getLightRadius() * 4.0);
        }
        Vec3 origin = getBeamOrigin(fixture, pan, tilt);
        Vec3 direction = getBeamDirection(fixture, pan, tilt);
        double maxReach = fixture.getFixture().getLightRadius() * 6.0;
        Vec3 end = origin.add(direction.scale(maxReach));
        BlockHitResult hit = fixture.getLevel().clip(new ClipContext(
                origin,
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                null
        ));
        if (hit.getType() == HitResult.Type.BLOCK) {
            return (float) Math.max(origin.distanceTo(hit.getLocation()), 1.0);
        }
        return (float) maxReach;
    }

    public static void applyFixtureTransforms(
            PoseStack poseStack,
            BaseLightBlockEntity fixture,
            BlockState blockState,
            float pan,
            float tilt
    ) {
        applyFixtureTransformsInternal(poseStack, fixture, blockState, pan, tilt);
    }

    private static PoseStack createFixturePose(BaseLightBlockEntity fixture, float pan, float tilt) {
        PoseStack poseStack = new PoseStack();
        applyFixtureTransformsInternal(poseStack, fixture, fixture.getBlockState(), pan, tilt);
        return poseStack;
    }

    private static Vector3f getBeamForward(PoseStack poseStack) {
        Vector3f forward = new Vector3f(0f, 0f, -1f);
        poseStack.last().normal().transform(forward);
        forward.normalize();
        return forward;
    }

    private static float[] directionToLookAngles(Vec3 direction) {
        Vec3 d = direction.normalize();
        float pitch = (float) Math.toDegrees(-Math.asin(Mth.clamp(d.y, -1.0, 1.0)));
        float yaw = (float) Math.toDegrees(Math.atan2(-d.x, d.z));
        return new float[]{yaw, pitch};
    }

    private static void applyFixtureTransformsInternal(
            PoseStack poseStack,
            BaseLightBlockEntity fixture,
            BlockState blockState,
            float pan,
            float tilt
    ) {
        Fixture fixtureDef = fixture.getFixture();
        boolean isHanging = blockState.getValue(BaseLightBlock.HANGING);
        boolean isFlipped = fixture.isUpsideDown();
        Direction facing = blockState.getValue(HangableBlock.FACING);

        poseStack.translate(0.5F, 0, 0.5F);
        if (isHanging) {
            Direction hangDirection = blockState.getValue(HangableBlock.HANG_DIRECTION);
            poseStack.translate(0, 0.5, 0F);
            if (hangDirection.getAxis() != Direction.Axis.Y) {
                if (hangDirection.getAxis() == Direction.Axis.Z) {
                    if (hangDirection == Direction.SOUTH) {
                        poseStack.mulPose(Axis.ZP.rotationDegrees(90));
                        poseStack.mulPose(Axis.XP.rotationDegrees(-90));
                    } else {
                        poseStack.mulPose(Axis.ZP.rotationDegrees(90));
                        poseStack.mulPose(Axis.XP.rotationDegrees(90));
                    }
                } else {
                    if (hangDirection == Direction.EAST) {
                        poseStack.mulPose(Axis.ZN.rotationDegrees(-90));
                    } else {
                        poseStack.mulPose(Axis.ZN.rotationDegrees(90));
                    }
                }
            } else if (hangDirection == Direction.UP) {
                poseStack.mulPose(Axis.XP.rotationDegrees(180));
            }
            poseStack.translate(0, -0.5, 0F);
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(facing.toYRot()));
        poseStack.translate(-0.5F, 0, -0.5F);
        if (isHanging) {
            Optional<BlockState> optionalSupport = fixture.getSupportingStructure();
            if (optionalSupport.isPresent()) {
                float[] transforms = fixtureDef.getTransforms(blockState, optionalSupport.get());
                poseStack.translate(transforms[0], transforms[1], transforms[2]);
            } else {
                poseStack.translate(0, 0.19, 0);
            }
            poseStack.translate(0, -0.08, 0);
        }
        if (isFlipped) {
            poseStack.translate(0.5F, 0.5, 0.5F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));
            poseStack.translate(-0.5F, -0.5, -0.5F);
        }

        float[] pans = fixtureDef.getPanRotationPosition();
        poseStack.translate(pans[0], pans[1], pans[2]);
        poseStack.mulPose(Axis.YP.rotationDegrees(pan));
        poseStack.translate(-pans[0], -pans[1], -pans[2]);

        float[] tilts = fixtureDef.getTiltRotationPosition();
        poseStack.translate(tilts[0], tilts[1], tilts[2]);
        if (isFlipped) {
            poseStack.mulPose(Axis.XP.rotationDegrees(-180));
        } else {
            poseStack.mulPose(Axis.XP.rotationDegrees(180));
        }
        poseStack.mulPose(Axis.XP.rotationDegrees(tilt));
        poseStack.translate(-tilts[0], -tilts[1], -tilts[2]);
    }
}
