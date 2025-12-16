package com.github.dumann089.theatricalextralights.client.blockentities;

import com.github.dumann089.theatricalextralights.blockentities.MovingJetBlockEntity;
import com.github.dumann089.theatricalextralights.blockentities.MovingJetBlockEntity;
import com.github.dumann089.theatricalextralights.particle.ModParticle;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.imabad.theatrical.TheatricalExpectPlatform;
import dev.imabad.theatrical.blocks.HangableBlock;
import dev.imabad.theatrical.client.LazyRenderers;
import dev.imabad.theatrical.client.blockentities.FixtureRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class MovingJetRenderer extends FixtureRenderer<MovingJetBlockEntity> {
    private BakedModel cachedPanModel, cachedTiltModel, cachedStaticModel;
    public MovingJetRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void renderModel(MovingJetBlockEntity blockEntity, PoseStack poseStack, VertexConsumer vertexConsumer, Direction facing, float partialTicks, boolean isFlipped, BlockState blockState, boolean isHanging, int packedLight, int packedOverlay) {
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
//        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        poseStack.mulPose(Axis.XP.rotationDegrees((prevTilt + (tilt - prevTilt) * partialTicks)));
        poseStack.translate(-tilts[0], -tilts[1], -tilts[2]);
        minecraftRenderModel(poseStack, vertexConsumer, blockState, cachedTiltModel,  packedLight, packedOverlay);
        //#endregion
    }
    @Override
    public void beforeRenderBeam(MovingJetBlockEntity blockEntity, PoseStack poseStack,
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

                    double x = blockEntity.getBlockPos().getX() + 0.5;
                    double y = blockEntity.getBlockPos().getY() + 1.81f;
                    double z = blockEntity.getBlockPos().getZ() + 0.5;

                    float pan = blockEntity.getPrevPan() + (blockEntity.getPan() - blockEntity.getPrevPan()) * partialTicks;
                    float tilt = blockEntity.getPrevTilt() + (blockEntity.getTilt() - blockEntity.getPrevTilt()) * partialTicks;

                    double yaw = Math.toRadians(pan);
                    double pitch = Math.toRadians(tilt + 90.0);

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
                    double targetHeight = (blockEntity.getIntensity() / 255.0) * maxHeight;

                    blockEntity.smoothedHeight += (targetHeight - blockEntity.smoothedHeight) * 0.1;

                    double speed = blockEntity.smoothedHeight * 0.07;

                    if (blockEntity.getLevel() !=null)
                        blockEntity.getLevel().addAlwaysVisibleParticle(ModParticle.WATERMOVINGJETPARTICLE.get(),
                            true,
                            x, y, z,
                            dirX * speed, dirY * speed, dirZ * speed);
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
    public void preparePoseStack(MovingJetBlockEntity blockEntity, PoseStack poseStack, Direction facing, float partialTicks, boolean isFlipped, BlockState blockState, boolean isHanging) {
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
//        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        poseStack.mulPose(Axis.XP.rotationDegrees((prevTilt + (tilt - prevTilt) * partialTicks)));
        poseStack.translate(-tilts[0], -tilts[1], -tilts[2]);
        //#endregion
    }
}
