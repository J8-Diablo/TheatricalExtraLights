package com.github.dumann089.theatricalextralights.client.blockentities;

import com.github.dumann089.theatricalextralights.blockentities.SpinnerBlockEntity;
import com.github.dumann089.theatricalextralights.client.particle.JetVariant;
import com.github.dumann089.theatricalextralights.client.particle.WaterJetParticleOptions;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.imabad.theatrical.TheatricalExpectPlatform;
import dev.imabad.theatrical.blocks.HangableBlock;
import dev.imabad.theatrical.client.LazyRenderers;
import com.github.dumann089.theatricalextralights.client.blockentities.ExtraLightsRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class SpinnerRenderer extends ExtraLightsRenderer<SpinnerBlockEntity> {
    private BakedModel cachedPanModel, cachedTiltModel, cachedStaticModel;

    public SpinnerRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void renderModel(SpinnerBlockEntity blockEntity, PoseStack poseStack, VertexConsumer vertexConsumer,
                            Direction facing, float partialTicks, boolean isFlipped, BlockState blockState,
                            boolean isHanging, int packedLight, int packedOverlay) {
        if(cachedStaticModel == null){
            cachedStaticModel = TheatricalExpectPlatform.getBakedModel(blockEntity.getFixture().getStaticModel());
        }
        if (cachedPanModel == null){
            cachedPanModel = TheatricalExpectPlatform.getBakedModel(blockEntity.getFixture().getPanModel());
        }
        if (cachedTiltModel == null){
            cachedTiltModel = TheatricalExpectPlatform.getBakedModel(blockEntity.getFixture().getTiltModel());
        }

        poseStack.translate(0.5F, 0, .5F);
        if(isHanging){
            Direction hangDirection = blockState.getValue(HangableBlock.HANG_DIRECTION);
            poseStack.translate(0, 0.5, 0F);
            if(hangDirection.getAxis() != Direction.Axis.Y){
                if(hangDirection.getAxis() == Direction.Axis.Z){
                    if(hangDirection == Direction.SOUTH) {
                        poseStack.mulPose(Axis.XP.rotationDegrees(90));
                    } else {
                        poseStack.mulPose(Axis.XN.rotationDegrees(90));
                    }
                } else {
                    if(hangDirection == Direction.EAST) {
                        poseStack.mulPose(Axis.ZN.rotationDegrees(90));
                    } else {
                        poseStack.mulPose(Axis.ZN.rotationDegrees(-90));
                    }
                }
            } else {

            }
            poseStack.translate(0, -0.5, 0F);
        }

        if(facing.getAxis() == Direction.Axis.X){
            poseStack.mulPose(Axis.YP.rotationDegrees(facing.toYRot()));
        } else {
            poseStack.mulPose(Axis.YP.rotationDegrees(facing.getOpposite().toYRot()));
        }
        poseStack.translate(-0.5F, 0, -.5F);

        if (isHanging) {
            Optional<BlockState> optionalSupport = blockEntity.getSupportingStructure();
            if (optionalSupport.isPresent()) {
                float[] transforms = blockEntity.getFixture().getTransforms(blockState, optionalSupport.get());
                poseStack.translate(transforms[0], transforms[1], transforms[2]);
            } else {
                poseStack.translate(0, 0.19, 0);
            }
        }

        minecraftRenderModel(poseStack, vertexConsumer, blockState, cachedStaticModel, packedLight, packedOverlay);

        float[] pans = blockEntity.getFixture().getPanRotationPosition();
        poseStack.translate(pans[0], pans[1], pans[2]);

        float prevAngle = blockEntity.getPrevSpinAngle();
        float currentAngle = blockEntity.getSpinAngle();
        float interpolatedAngle = prevAngle + (currentAngle - prevAngle) * partialTicks;

        poseStack.mulPose(Axis.YN.rotationDegrees(interpolatedAngle));
        poseStack.translate(-pans[0], -pans[1], -pans[2]);
        minecraftRenderModel(poseStack, vertexConsumer, blockState, cachedPanModel, packedLight, packedOverlay);

        float[] tilts = blockEntity.getFixture().getTiltRotationPosition();
        poseStack.translate(tilts[0], tilts[1], tilts[2]);
        int prevTilt = blockEntity.getPrevTilt();
        int tilt = blockEntity.getTilt();
        poseStack.mulPose(Axis.XP.rotationDegrees((prevTilt + (tilt - prevTilt) * partialTicks)));
        poseStack.translate(-tilts[0], -tilts[1], -tilts[2]);
        minecraftRenderModel(poseStack, vertexConsumer, blockState, cachedTiltModel,  packedLight, packedOverlay);
    }

        @Override
    public void preparePoseStack(SpinnerBlockEntity blockEntity, PoseStack poseStack, Direction facing,
                                 float partialTicks, boolean isFlipped, BlockState blockState, boolean isHanging) {
        poseStack.translate(0.5F, 0, .5F);
        if(isHanging){
            Direction hangDirection = blockState.getValue(HangableBlock.HANG_DIRECTION);
            poseStack.translate(0, 0.5, 0F);
            if(hangDirection.getAxis() != Direction.Axis.Y){
                if(hangDirection.getAxis() == Direction.Axis.Z){
                    if(hangDirection == Direction.SOUTH) {
                        poseStack.mulPose(Axis.XP.rotationDegrees(90));
                    } else {
                        poseStack.mulPose(Axis.XN.rotationDegrees(90));
                    }
                } else {
                    if(hangDirection == Direction.EAST) {
                        poseStack.mulPose(Axis.ZN.rotationDegrees(90));
                    } else {
                        poseStack.mulPose(Axis.ZN.rotationDegrees(-90));
                    }
                }
            } else {

            }
            poseStack.translate(0, -0.5, 0F);
        }

        if(facing.getAxis() == Direction.Axis.X){
            poseStack.mulPose(Axis.YP.rotationDegrees(facing.toYRot()));
        } else {
            poseStack.mulPose(Axis.YP.rotationDegrees(facing.getOpposite().toYRot()));
        }
        poseStack.translate(-0.5F, 0, -.5F);

        if (isHanging) {
            Optional<BlockState> optionalSupport = blockEntity.getSupportingStructure();
            if (optionalSupport.isPresent()) {
                float[] transforms = blockEntity.getFixture().getTransforms(blockState, optionalSupport.get());
                poseStack.translate(transforms[0], transforms[1], transforms[2]);
            } else {
                poseStack.translate(0, 0.19, 0);
            }
        }

        float[] pans = blockEntity.getFixture().getPanRotationPosition();
        poseStack.translate(pans[0], pans[1], pans[2]);

        float prevAngle = blockEntity.getPrevSpinAngle();
        float currentAngle = blockEntity.getSpinAngle();
        float interpolatedAngle = prevAngle + (currentAngle - prevAngle) * partialTicks;

        poseStack.mulPose(Axis.YN.rotationDegrees(interpolatedAngle));
        poseStack.translate(-pans[0], -pans[1], -pans[2]);

        float[] tilts = blockEntity.getFixture().getTiltRotationPosition();
        poseStack.translate(tilts[0], tilts[1], tilts[2]);
        int prevTilt = blockEntity.getPrevTilt();
        int tilt = blockEntity.getTilt();
        poseStack.mulPose(Axis.XP.rotationDegrees((prevTilt + (tilt - prevTilt) * partialTicks)));
        poseStack.translate(-tilts[0], -tilts[1], -tilts[2]);
    }
}