package com.github.dumann089.theatricalextralights.client.blockentities;

import com.github.dumann089.theatricalextralights.blockentities.Flow2JetBlockEntity;
import com.github.dumann089.theatricalextralights.fixtures.Flow2JetFixture;
import com.github.dumann089.theatricalextralights.util.FixtureJetDirection;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.imabad.theatrical.TheatricalExpectPlatform;
import dev.imabad.theatrical.blocks.HangableBlock;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

/** Machine entière pivotée (pan + tilt), pas seulement la buse. */
public class Flow2JetRenderer extends ExtraLightsRenderer<Flow2JetBlockEntity> {
    private BakedModel wholeModel;

    public Flow2JetRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void renderModel(
            Flow2JetBlockEntity blockEntity,
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            Direction facing,
            float partialTicks,
            boolean isFlipped,
            BlockState blockState,
            boolean isHanging,
            int packedLight,
            int packedOverlay
    ) {
        if (wholeModel == null) {
            wholeModel = TheatricalExpectPlatform.getBakedModel(blockEntity.getFixture().getPanModel());
        }

        applyFixturePose(poseStack, blockEntity, facing, blockState, isHanging, partialTicks);
        minecraftRenderModel(poseStack, vertexConsumer, blockState, wholeModel, packedLight, packedOverlay);
    }

    private static void applyFixturePose(
            PoseStack poseStack,
            Flow2JetBlockEntity blockEntity,
            Direction facing,
            BlockState blockState,
            boolean isHanging,
            float partialTicks
    ) {
        poseStack.translate(0.5F, 0, 0.5F);
        if (isHanging) {
            Direction hangDirection = blockState.getValue(HangableBlock.HANG_DIRECTION);
            poseStack.translate(0, 0.5, 0F);
            if (hangDirection.getAxis() != Direction.Axis.Y) {
                if (hangDirection.getAxis() == Direction.Axis.Z) {
                    if (hangDirection == Direction.SOUTH) {
                        poseStack.mulPose(Axis.XP.rotationDegrees(90));
                    } else {
                        poseStack.mulPose(Axis.XN.rotationDegrees(90));
                    }
                } else {
                    if (hangDirection == Direction.EAST) {
                        poseStack.mulPose(Axis.ZN.rotationDegrees(90));
                    } else {
                        poseStack.mulPose(Axis.ZN.rotationDegrees(-90));
                    }
                }
            }
            poseStack.translate(0, -0.5, 0F);
        }

        if (facing.getAxis() == Direction.Axis.X) {
            poseStack.mulPose(Axis.YP.rotationDegrees(facing.toYRot()));
        } else {
            poseStack.mulPose(Axis.YP.rotationDegrees(facing.getOpposite().toYRot()));
        }
        poseStack.translate(-0.5F, 0, -0.5F);

        if (isHanging) {
            Optional<BlockState> optionalSupport = blockEntity.getSupportingStructure();
            if (optionalSupport.isPresent()) {
                float[] transforms = blockEntity.getFixture().getTransforms(blockState, optionalSupport.get());
                poseStack.translate(transforms[0], transforms[1], transforms[2]);
            } else {
                poseStack.translate(0, 0.19, 0);
            }
        }

        float[] headPivot = blockEntity.getFixture().getPanRotationPosition();
        float pan = blockEntity.getInterpolatedPan(partialTicks);
        float userTilt = blockEntity.getInterpolatedTilt(partialTicks);
        float effectiveTilt = Flow2JetFixture.effectiveTilt(userTilt, isHanging);
        float[] groundPivot = Flow2JetFixture.GROUND_PIVOT;

        if (!isHanging) {
            poseStack.translate(groundPivot[0], groundPivot[1], groundPivot[2]);
            poseStack.mulPose(Axis.XP.rotationDegrees(Flow2JetFixture.REST_LAY_DEGREES));
            poseStack.translate(-groundPivot[0], -groundPivot[1], -groundPivot[2]);
        }

        Vec3 panTiltPivot = FixtureJetDirection.flow2JetPanTiltPivot(headPivot, null, isHanging);

        poseStack.translate((float) panTiltPivot.x, (float) panTiltPivot.y, (float) panTiltPivot.z);
        poseStack.mulPose(Axis.YN.rotationDegrees(pan));
        poseStack.mulPose(Axis.XP.rotationDegrees(effectiveTilt));
        poseStack.translate(-(float) panTiltPivot.x, -(float) panTiltPivot.y, -(float) panTiltPivot.z);
    }

    @Override
    public void preparePoseStack(
            Flow2JetBlockEntity blockEntity,
            PoseStack poseStack,
            Direction facing,
            float partialTicks,
            boolean isFlipped,
            BlockState blockState,
            boolean isHanging
    ) {
        applyFixturePose(poseStack, blockEntity, facing, blockState, isHanging, partialTicks);
    }
}
