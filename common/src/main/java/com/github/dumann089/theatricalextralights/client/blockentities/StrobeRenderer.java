package com.github.dumann089.theatricalextralights.client.blockentities;



import com.github.dumann089.theatricalextralights.blockentities.StrobeBlockEntity;

import com.github.dumann089.theatricalextralights.client.Beam2DRenderTypes;

import com.github.dumann089.theatricalextralights.client.StrobeRenderHelper;

import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.blaze3d.vertex.VertexConsumer;

import com.mojang.math.Axis;

import dev.imabad.theatrical.TheatricalExpectPlatform;

import dev.imabad.theatrical.blocks.HangableBlock;

import dev.imabad.theatrical.client.LazyRenderers;

import net.minecraft.client.Camera;

import net.minecraft.client.renderer.MultiBufferSource;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

import net.minecraft.client.resources.model.BakedModel;

import net.minecraft.core.BlockPos;

import net.minecraft.core.Direction;

import net.minecraft.util.Mth;

import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.phys.Vec3;

import org.joml.Matrix4f;



import java.util.Optional;



public class StrobeRenderer extends ExtraLightsFixtureRenderer<StrobeBlockEntity> {

    private static final int FLOOR_PATCH_SEGMENTS = 24;

    private static final float FLOOR_PATCH_Y_OFFSET = 0.02f;



    private BakedModel cachedPanModel;

    private BakedModel cachedTiltModel;

    private BakedModel cachedStaticModel;



    public StrobeRenderer(BlockEntityRendererProvider.Context context) {

        super(context);

    }



    @Override

    public void renderModel(

            StrobeBlockEntity blockEntity,

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

        if (cachedStaticModel == null) {

            cachedStaticModel = TheatricalExpectPlatform.getBakedModel(blockEntity.getFixture().getStaticModel());

        }

        if (cachedPanModel == null) {

            cachedPanModel = TheatricalExpectPlatform.getBakedModel(blockEntity.getFixture().getPanModel());

        }

        if (cachedTiltModel == null) {

            cachedTiltModel = TheatricalExpectPlatform.getBakedModel(blockEntity.getFixture().getTiltModel());

        }



        poseStack.translate(0.5F, 0, .5F);

        if (isHanging) {

            Direction hangDirection = blockState.getValue(HangableBlock.HANG_DIRECTION);

            poseStack.translate(0, 0.5, 0F);

            if (hangDirection.getAxis() != Direction.Axis.Y) {

                if (hangDirection.getAxis() == Direction.Axis.Z) {

                    if (hangDirection == Direction.SOUTH) {

                        poseStack.mulPose(Axis.XP.rotationDegrees(-90));

                    } else {

                        poseStack.mulPose(Axis.XP.rotationDegrees(90));

                    }

                    poseStack.mulPose(Axis.YP.rotationDegrees(180));

                } else {

                    if (hangDirection == Direction.EAST) {

                        poseStack.mulPose(Axis.ZN.rotationDegrees(-90));

                    } else {

                        poseStack.mulPose(Axis.ZN.rotationDegrees(90));

                    }

                }

            }

            poseStack.translate(0, -0.5, 0F);

        }



        poseStack.mulPose(Axis.YP.rotationDegrees(facing.toYRot()));

        poseStack.translate(-0.5F, 0, -.5F);

        if (isHanging) {

            Optional<BlockState> optionalSupport = blockEntity.getSupportingStructure();

            if (optionalSupport.isPresent()) {

                float[] transforms = blockEntity.getFixture().getTransforms(blockState, optionalSupport.get());

                poseStack.translate(transforms[0], transforms[1], transforms[2]);

            } else {

                poseStack.translate(0, 0.19, 0);

            }

            poseStack.translate(0, -0.08, 0);

        }

        if (isFlipped) {

            poseStack.translate(0.5F, 0.5, .5F);

            poseStack.mulPose(Axis.ZP.rotationDegrees(180));

            poseStack.translate(-0.5F, -0.5, -.5F);

        }



        minecraftRenderModel(poseStack, vertexConsumer, blockState, cachedStaticModel, packedLight, packedOverlay);



        float[] pans = blockEntity.getFixture().getPanRotationPosition();

        poseStack.translate(pans[0], pans[1], pans[2]);

        int prevPan = blockEntity.getPrevPan();

        int pan = blockEntity.getPan();

        poseStack.mulPose(Axis.YP.rotationDegrees((prevPan + (pan - prevPan) * partialTicks)));

        poseStack.translate(-pans[0], -pans[1], -pans[2]);

        minecraftRenderModel(poseStack, vertexConsumer, blockState, cachedPanModel, packedLight, packedOverlay);



        float[] tilts = blockEntity.getFixture().getTiltRotationPosition();

        poseStack.translate(tilts[0], tilts[1], tilts[2]);

        if (isFlipped) {

            poseStack.mulPose(Axis.XP.rotationDegrees(-180));

        } else {

            poseStack.mulPose(Axis.XP.rotationDegrees(180));

        }

        int prevTilt = blockEntity.getPrevTilt();

        int tilt = blockEntity.getTilt();

        poseStack.mulPose(Axis.XP.rotationDegrees((prevTilt + (tilt - prevTilt) * partialTicks)));

        poseStack.translate(-tilts[0], -tilts[1], -tilts[2]);

        minecraftRenderModel(poseStack, vertexConsumer, blockState, cachedTiltModel, packedLight, packedOverlay);

    }



    @Override

    public void beforeRenderBeam(

            StrobeBlockEntity blockEntity,

            PoseStack poseStack,

            VertexConsumer vertexConsumer,

            MultiBufferSource multiBufferSource,

            Direction facing,

            float partialTicks,

            boolean isFlipped,

            BlockState blockstate,

            boolean isHanging,

            int packedLight,

            int packedOverlay

    ) {

        if (blockEntity.getIntensity() <= 0 || blockEntity.getEmissionBlock() == null) {

            return;

        }



        LazyRenderers.addLazyRender(new LazyRenderers.LazyRenderer() {

            @Override

            public void render(MultiBufferSource.BufferSource bufferSource, PoseStack poseStack, Camera camera, float partialTick) {

                BlockPos emission = blockEntity.getEmissionBlock();

                if (emission == null) {

                    return;

                }



                float radius = blockEntity.getInterpolatedFloorPatchRadius(partialTick);

                if (radius <= 0f) {

                    return;

                }



                float intensityNorm = StrobeRenderHelper.renderedIntensity(blockEntity, partialTick) / 255f;

                if (intensityNorm <= 0f) {

                    return;

                }



                int color = blockEntity.getColour();

                int r = (color >> 16) & 0xFF;

                int g = (color >> 8) & 0xFF;

                int b = color & 0xFF;



                float cx = emission.getX() + 0.5f;

                float cy = emission.getY() + FLOOR_PATCH_Y_OFFSET;

                float cz = emission.getZ() + 0.5f;



                poseStack.pushPose();

                Vec3 offset = camera.getPosition();

                poseStack.translate(cx - offset.x, cy - offset.y, cz - offset.z);



                VertexConsumer floorConsumer = bufferSource.getBuffer(Beam2DRenderTypes.FLOOR_PATCH);

                renderFloorPatch(floorConsumer, poseStack.last().pose(), radius, r, g, b, intensityNorm);



                poseStack.popPose();

            }



            @Override

            public Vec3 getPos(float partialTick) {

                BlockPos emission = blockEntity.getEmissionBlock();

                if (emission == null) {

                    return blockEntity.getBlockPos().getCenter();

                }

                return new Vec3(emission.getX() + 0.5, emission.getY() + FLOOR_PATCH_Y_OFFSET, emission.getZ() + 0.5);

            }

        });

    }



    /** Disque horizontal au sol — le rayon suit le focus DMX (pas de cône volumétrique). */

    private static void renderFloorPatch(

            VertexConsumer consumer,

            Matrix4f matrix,

            float radius,

            int r,

            int g,

            int b,

            float intensityNorm

    ) {

        int centerAlpha = (int) (intensityNorm * 200f);

        int edgeAlpha = 0;



        for (int i = 0; i < FLOOR_PATCH_SEGMENTS; i++) {

            float angle0 = (float) (Math.PI * 2 * i / FLOOR_PATCH_SEGMENTS);

            float angle1 = (float) (Math.PI * 2 * (i + 1) / FLOOR_PATCH_SEGMENTS);



            float x0 = Mth.cos(angle0) * radius;

            float z0 = Mth.sin(angle0) * radius;

            float x1 = Mth.cos(angle1) * radius;

            float z1 = Mth.sin(angle1) * radius;



            consumer.vertex(matrix, 0f, 0f, 0f).color(r, g, b, centerAlpha).endVertex();

            consumer.vertex(matrix, x0, 0f, z0).color(r, g, b, edgeAlpha).endVertex();

            consumer.vertex(matrix, x1, 0f, z1).color(r, g, b, edgeAlpha).endVertex();

        }

    }



    @Override

    public void preparePoseStack(

            StrobeBlockEntity blockEntity,

            PoseStack poseStack,

            Direction facing,

            float partialTicks,

            boolean isFlipped,

            BlockState blockState,

            boolean isHanging

    ) {

        poseStack.translate(0.5F, 0, .5F);

        if (isHanging) {

            Direction hangDirection = blockState.getValue(HangableBlock.HANG_DIRECTION);

            poseStack.translate(0, 0.5, 0F);

            if (hangDirection.getAxis() != Direction.Axis.Y) {

                if (hangDirection.getAxis() == Direction.Axis.Z) {

                    if (hangDirection == Direction.SOUTH) {

                        poseStack.mulPose(Axis.XP.rotationDegrees(-90));

                    } else {

                        poseStack.mulPose(Axis.XP.rotationDegrees(90));

                    }

                    poseStack.mulPose(Axis.YP.rotationDegrees(180));

                } else {

                    if (hangDirection == Direction.EAST) {

                        poseStack.mulPose(Axis.ZN.rotationDegrees(-90));

                    } else {

                        poseStack.mulPose(Axis.ZN.rotationDegrees(90));

                    }

                }

            }

            poseStack.translate(0, -0.5, 0F);

        }



        poseStack.mulPose(Axis.YP.rotationDegrees(facing.toYRot()));

        poseStack.translate(-0.5F, 0, -.5F);

        if (isHanging) {

            Optional<BlockState> optionalSupport = blockEntity.getSupportingStructure();

            if (optionalSupport.isPresent()) {

                float[] transforms = blockEntity.getFixture().getTransforms(blockState, optionalSupport.get());

                poseStack.translate(transforms[0], transforms[1], transforms[2]);

            } else {

                poseStack.translate(0, 0.19, 0);

            }

            poseStack.translate(0, -0.08, 0);

        }

        if (isFlipped) {

            poseStack.translate(0.5F, 0.5, .5F);

            poseStack.mulPose(Axis.ZP.rotationDegrees(180));

            poseStack.translate(-0.5F, -0.5, -.5F);

        }



        float[] pans = blockEntity.getFixture().getPanRotationPosition();

        poseStack.translate(pans[0], pans[1], pans[2]);

        int prevPan = blockEntity.getPrevPan();

        int pan = blockEntity.getPan();

        poseStack.mulPose(Axis.YP.rotationDegrees((prevPan + (pan - prevPan) * partialTicks)));

        poseStack.translate(-pans[0], -pans[1], -pans[2]);



        float[] tilts = blockEntity.getFixture().getTiltRotationPosition();

        poseStack.translate(tilts[0], tilts[1], tilts[2]);

        if (isFlipped) {

            poseStack.mulPose(Axis.XP.rotationDegrees(-180));

        } else {

            poseStack.mulPose(Axis.XP.rotationDegrees(180));

        }

        int prevTilt = blockEntity.getPrevTilt();

        int tilt = blockEntity.getTilt();

        poseStack.mulPose(Axis.XP.rotationDegrees((prevTilt + (tilt - prevTilt) * partialTicks)));

        poseStack.translate(-tilts[0], -tilts[1], -tilts[2]);

    }

}


