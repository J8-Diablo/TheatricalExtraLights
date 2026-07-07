package com.github.dumann089.theatricalextralights.client.blockentities;

import com.github.dumann089.theatricalextralights.blockentities.Flow2JetBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.imabad.theatrical.TheatricalExpectPlatform;
import dev.imabad.theatrical.blocks.HangableBlock;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

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

        float[] pivot = blockEntity.getFixture().getPanRotationPosition();
        float pan = blockEntity.getInterpolatedPan(partialTicks);
        float tilt = blockEntity.getInterpolatedTilt(partialTicks);

        poseStack.translate(pivot[0], pivot[1], pivot[2]);
        poseStack.mulPose(Axis.YN.rotationDegrees(pan));
        poseStack.translate(-pivot[0], -pivot[1], -pivot[2]);

        poseStack.translate(pivot[0], pivot[1], pivot[2]);
        poseStack.mulPose(Axis.XP.rotationDegrees(tilt));
        poseStack.translate(-pivot[0], -pivot[1], -pivot[2]);
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
