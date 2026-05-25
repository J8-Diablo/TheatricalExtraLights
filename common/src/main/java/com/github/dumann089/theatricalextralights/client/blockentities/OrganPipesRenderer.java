package com.github.dumann089.theatricalextralights.client.blockentities;

import com.github.dumann089.theatricalextralights.blockentities.OrganPipesBlockEntity;
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

public class OrganPipesRenderer extends ExtraLightsRenderer<OrganPipesBlockEntity> {
    private BakedModel cachedPanModel, cachedTiltModel, cachedStaticModel;

    private static final double[] PARTICLE_X_OFFSETS = {
            -0.9375, -0.625, -0.25, 0.125, 0.5, 0.875, 1.25, 1.625, 1.9375
    };

    private static final double[] HEIGHT_MULTIPLIERS = {
            0.6, 0.7, 0.8, 0.9, 1.0, 0.9, 0.8, 0.7, 0.6
    };

    public OrganPipesRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void renderModel(OrganPipesBlockEntity blockEntity, PoseStack poseStack, VertexConsumer vertexConsumer, Direction facing, float partialTicks, boolean isFlipped, BlockState blockState, boolean isHanging, int packedLight, int packedOverlay) {
        if(cachedStaticModel == null){
            cachedStaticModel = TheatricalExpectPlatform.getBakedModel(blockEntity.getFixture().getStaticModel());
        }
        if (cachedPanModel == null){
            cachedPanModel = TheatricalExpectPlatform.getBakedModel(blockEntity.getFixture().getPanModel());
        }
        if (cachedTiltModel == null){
            cachedTiltModel = TheatricalExpectPlatform.getBakedModel(blockEntity.getFixture().getTiltModel());
        }
        //#region Fixture Hanging
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
                //TODO: Handle hanging up
            }
            poseStack.translate(0, -0.5, 0F);
        }
        //#endregion
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
        // Static Model Render
        minecraftRenderModel(poseStack, vertexConsumer, blockState, cachedStaticModel, packedLight, packedOverlay);
        //#region Model Pan
        float[] pans = blockEntity.getFixture().getPanRotationPosition();
        poseStack.translate(pans[0], pans[1], pans[2]);
        int prevPan = blockEntity.getPrevPan();
        int pan = blockEntity.getPan();
        poseStack.mulPose(Axis.YN.rotationDegrees((prevPan + (pan - prevPan) * partialTicks)));
        poseStack.translate(-pans[0], -pans[1], -pans[2]);
        minecraftRenderModel(poseStack, vertexConsumer, blockState, cachedPanModel, packedLight, packedOverlay);
        //#endregion
        //#region Model Tilt
        float[] tilts = blockEntity.getFixture().getTiltRotationPosition();
        poseStack.translate(tilts[0], tilts[1], tilts[2]);
        int prevTilt = blockEntity.getPrevTilt();
        int tilt = blockEntity.getTilt();
        poseStack.mulPose(Axis.XP.rotationDegrees((prevTilt + (tilt - prevTilt) * partialTicks)));
        poseStack.translate(-tilts[0], -tilts[1], -tilts[2]);
        minecraftRenderModel(poseStack, vertexConsumer, blockState, cachedTiltModel,  packedLight, packedOverlay);
        //#endregion
    }

    @Override
    public void beforeRenderBeam(OrganPipesBlockEntity blockEntity, PoseStack poseStack,
                                 VertexConsumer vertexConsumer, MultiBufferSource multiBufferSource,
                                 Direction facing, float partialTicks, boolean isFlipped,
                                 BlockState blockstate, boolean isHanging, int packedLight, int packedOverlay) {

        if(blockEntity.getIntensity() > 0){
            LazyRenderers.addLazyRender(new LazyRenderers.LazyRenderer() {
                @Override
                public void render(MultiBufferSource.BufferSource bufferSource, PoseStack poseStack, Camera camera, float partialTick) {

                    if (Minecraft.getInstance().isPaused()) return;

                    Vec3 offset = Vec3.atLowerCornerOf(blockEntity.getBlockPos()).subtract(camera.getPosition());
                    poseStack.pushPose();
                    poseStack.translate(offset.x, offset.y, offset.z);

                    preparePoseStack(blockEntity, poseStack, facing, partialTicks, isFlipped, blockstate, isHanging);

                    float pan = blockEntity.getPrevPan() + (blockEntity.getPan() - blockEntity.getPrevPan()) * partialTicks;
                    float tilt = blockEntity.getPrevTilt() + (blockEntity.getTilt() - blockEntity.getPrevTilt()) * partialTicks;

                    double yaw = Math.toRadians(pan);
                    double pitch = Math.toRadians(tilt);

                    double dirX = -Math.sin(yaw) * Math.cos(pitch);
                    double dirY = Math.sin(pitch);
                    double dirZ = Math.cos(yaw) * Math.cos(pitch);

                    switch(facing) {
                        case NORTH -> { double tmp = dirX; dirX = -dirX; dirZ = -dirZ; }
                        case EAST -> { double tmp = dirX; dirX = dirZ; dirZ = -tmp; }
                        case WEST -> { double tmp = dirX; dirX = -dirZ; dirZ = tmp; }
                        case SOUTH, UP, DOWN -> {}
                    }

                    double maxHeight = blockEntity.getJetHeight();
                    double baseTargetHeight = (blockEntity.getIntensity() / 255.0) * maxHeight;

                    blockEntity.smoothedHeight += (baseTargetHeight - blockEntity.smoothedHeight) * 0.1;

                    double baseSpeed = blockEntity.smoothedHeight * 0.09;

                    if (blockEntity.getLevel() != null) {
                        float intensity = blockEntity.getIntensity() / 255.0f;
                        float thickness = blockEntity.getJetThickness();

                        for (int i = 0; i < PARTICLE_X_OFFSETS.length; i++) {
                            double xOffset = PARTICLE_X_OFFSETS[i];
                            double heightMultiplier = HEIGHT_MULTIPLIERS[i];

                            double baseX = blockEntity.getBlockPos().getX() + xOffset;
                            double baseY = blockEntity.getBlockPos().getY() + 2.0;
                            double baseZ = blockEntity.getBlockPos().getZ() + 0.51;

                            double blockCenterX = blockEntity.getBlockPos().getX() + 0.5;
                            double blockCenterZ = blockEntity.getBlockPos().getZ() + 0.5;

                            double relativeX = baseX - blockCenterX;
                            double relativeZ = baseZ - blockCenterZ;

                            double cosYaw = Math.cos(yaw);
                            double sinYaw = Math.sin(yaw);

                            double rotatedX = relativeX * cosYaw - relativeZ * sinYaw;
                            double rotatedZ = relativeX * sinYaw + relativeZ * cosYaw;

                            double finalRelX = rotatedX;
                            double finalRelZ = rotatedZ;

                            switch(facing) {
                                case NORTH -> {
                                    finalRelX = -rotatedX;
                                    finalRelZ = -rotatedZ;
                                }
                                case EAST -> {
                                    double tmp = rotatedX;
                                    finalRelX = rotatedZ;
                                    finalRelZ = -tmp;
                                }
                                case WEST -> {
                                    double tmp = rotatedX;
                                    finalRelX = -rotatedZ;
                                    finalRelZ = tmp;
                                }
                                case SOUTH, UP, DOWN -> {}
                            }

                            double particleX = blockCenterX + finalRelX;
                            double particleY = baseY;
                            double particleZ = blockCenterZ + finalRelZ;

                            double particleSpeed = baseSpeed * heightMultiplier;

                            WaterJetParticleOptions options = new WaterJetParticleOptions(
                                    intensity * (float) heightMultiplier,
                                    thickness,
                                    JetVariant.JET3
                            );

                            blockEntity.getLevel().addAlwaysVisibleParticle(
                                    options,
                                    true,
                                    particleX, particleY, particleZ,
                                    dirX * particleSpeed, dirY * particleSpeed, dirZ * particleSpeed
                            );
                        }
                    }
                    poseStack.popPose();
                }

                @Override
                public Vec3 getPos(float partialTick) {
                    return blockEntity.getBlockPos().getCenter();
                }
            });
        }
    }


    @Override
    public void preparePoseStack(OrganPipesBlockEntity blockEntity, PoseStack poseStack, Direction facing, float partialTicks, boolean isFlipped, BlockState blockState, boolean isHanging) {
        //#region Fixture Hanging
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
                //TODO: Handle hanging up
            }
            poseStack.translate(0, -0.5, 0F);
        }
        //#endregion
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
        //#region Model Pan
        float[] pans = blockEntity.getFixture().getPanRotationPosition();
        poseStack.translate(pans[0], pans[1], pans[2]);
        int prevPan = blockEntity.getPrevPan();
        int pan = blockEntity.getPan();
        poseStack.mulPose(Axis.YN.rotationDegrees((prevPan + (pan - prevPan) * partialTicks)));
        poseStack.translate(-pans[0], -pans[1], -pans[2]);
        //#endregion
        //#region Model Tilt
        float[] tilts = blockEntity.getFixture().getTiltRotationPosition();
        poseStack.translate(tilts[0], tilts[1], tilts[2]);
        int prevTilt = blockEntity.getPrevTilt();
        int tilt = blockEntity.getTilt();
        poseStack.mulPose(Axis.XP.rotationDegrees((prevTilt + (tilt - prevTilt) * partialTicks)));
        poseStack.translate(-tilts[0], -tilts[1], -tilts[2]);
        //#endregion
    }
}