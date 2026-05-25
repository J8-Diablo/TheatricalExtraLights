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
    public void beforeRenderBeam(SpinnerBlockEntity blockEntity, PoseStack poseStack,
                                 VertexConsumer vertexConsumer,
                                 MultiBufferSource multiBufferSource,
                                 Direction facing, float partialTicks,
                                 boolean isFlipped, BlockState blockstate,
                                 boolean isHanging, int packedLight,
                                 int packedOverlay) {

        if (blockEntity.getIntensity() <= 0) return;

        SpinnerBlockEntity.tick(blockEntity);

        LazyRenderers.addLazyRender(new LazyRenderers.LazyRenderer() {

            @Override
            public void render(MultiBufferSource.BufferSource bufferSource,
                               PoseStack poseStack, Camera camera, float partialTick) {

                if (Minecraft.getInstance().isPaused()) return;

                Vec3 camOffset = Vec3.atLowerCornerOf(blockEntity.getBlockPos())
                        .subtract(camera.getPosition());

                poseStack.pushPose();
                poseStack.translate(camOffset.x, camOffset.y, camOffset.z);

                float intensity = blockEntity.getIntensity() / 255.0f;
                float thickness = blockEntity.getJetThickness();

                double maxHeight = blockEntity.getJetHeight();
                double targetHeight = intensity * maxHeight;
                blockEntity.smoothedHeight += (targetHeight - blockEntity.smoothedHeight) * 0.15;

                double speed = blockEntity.smoothedHeight * 0.10;

                float angle = blockEntity.getSpinAngle() % 360.0f;
                double rad = Math.toRadians(angle);

                float nozzleAngle = blockEntity.getNozzleAngle();
                double nozzleRad = Math.toRadians(nozzleAngle);

                double baseX = blockEntity.getBlockPos().getX();
                double baseY = blockEntity.getBlockPos().getY();
                double baseZ = blockEntity.getBlockPos().getZ();

                double[][] jetPositions = {
                        { 0.96,  2.0, 0.501 },
                        { 0.5,   2.0, 0.968 },
                        { 0.5,   2.0, 0.034 },
                        { 0.034, 2.0, 0.501 }
                };

                if (blockEntity.getLevel() != null) {

                    WaterJetParticleOptions options =
                            new WaterJetParticleOptions(intensity, thickness, JetVariant.JET3);

                    for (double[] jet : jetPositions) {

                        double localX = jet[0];
                        double localY = jet[1];
                        double localZ = jet[2];

                        double offsetX = localX - 0.5;
                        double offsetZ = localZ - 0.5;

                        double rotatedX = offsetX * Math.cos(rad) - offsetZ * Math.sin(rad);
                        double rotatedZ = offsetX * Math.sin(rad) + offsetZ * Math.cos(rad);

                        double worldX = baseX + 0.5 + rotatedX;
                        double worldY = baseY + localY;
                        double worldZ = baseZ + 0.5 + rotatedZ;

                        double dirX = rotatedX;
                        double dirZ = rotatedZ;
                        double dirLength = Math.sqrt(dirX * dirX + dirZ * dirZ);

                        if (dirLength > 0.001) {
                            dirX /= dirLength;
                            dirZ /= dirLength;
                        } else {
                            dirX = 0;
                            dirZ = 0;
                        }

                        double velY = speed * Math.cos(nozzleRad);

                        double horizontalSpeed = speed * Math.sin(nozzleRad);
                        double velX = dirX * horizontalSpeed;
                        double velZ = dirZ * horizontalSpeed;

                        blockEntity.getLevel().addAlwaysVisibleParticle(
                                options,
                                true,
                                worldX, worldY, worldZ,
                                velX, velY, velZ
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