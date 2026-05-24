package com.github.dumann089.theatricalextralights.client.blockentities;

import com.github.dumann089.theatricalextralights.blockentities.MovingVL2CBeamsBlockEntity;
import com.github.dumann089.theatricalextralights.client.Beam2DRenderTypes;
import com.github.dumann089.theatricalextralights.client.blockentities.GoboGPUProjector;
import com.github.dumann089.theatricalextralights.client.gobo.FakeVolumetricBeamPattern;
import com.github.dumann089.theatricalextralights.client.gobo.GoboLibrary;
import com.github.dumann089.theatricalextralights.config.TheatricalExtraLightsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.imabad.theatrical.TheatricalExpectPlatform;
import dev.imabad.theatrical.blocks.HangableBlock;
import dev.imabad.theatrical.client.LazyRenderers;
import dev.imabad.theatrical.config.TheatricalConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import java.util.Optional;
import java.util.Map;
import java.util.WeakHashMap;

public class MovingVL2CBeamsRenderer extends ExtraLightsFixtureRenderer<MovingVL2CBeamsBlockEntity> {
    private BakedModel cachedPanModel, cachedTiltModel, cachedStaticModel;
    private final Double beamOpacity = TheatricalConfig.INSTANCE.CLIENT.beamOpacity;
    private final GoboGPUProjector goboProjector = new GoboGPUProjector();

    private final Map<MovingVL2CBeamsBlockEntity, float[]> structuralCache = new WeakHashMap<>();
    private final Map<MovingVL2CBeamsBlockEntity, Long> structuralCacheTicks = new WeakHashMap<>();

    public MovingVL2CBeamsRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    private float[] getThrottledStructuralTransforms(MovingVL2CBeamsBlockEntity be, BlockState state) {
        long currentTick = be.getLevel().getGameTime();
        Long lastTick = structuralCacheTicks.get(be);

        if (lastTick == null || currentTick != lastTick || !structuralCache.containsKey(be)) {
            float[] transforms;
            Optional<BlockState> optionalSupport = be.getSupportingStructure();
            if (optionalSupport.isPresent() && be.getFixture() != null) {
                transforms = be.getFixture().getTransforms(state, optionalSupport.get());
            } else {
                transforms = new float[]{0f, 0.19f, 0f};
            }
            structuralCache.put(be, transforms);
            structuralCacheTicks.put(be, currentTick);
            return transforms;
        }
        return structuralCache.get(be);
    }

    @Override
    public void renderModel(MovingVL2CBeamsBlockEntity blockEntity, PoseStack poseStack, VertexConsumer vertexConsumer, Direction facing, float partialTicks, boolean isFlipped, BlockState blockState, boolean isHanging, int packedLight, int packedOverlay) {
        if(cachedStaticModel == null){
            cachedStaticModel = TheatricalExpectPlatform.getBakedModel(blockEntity.getFixture().getStaticModel());
        }
        if (cachedPanModel == null){
            cachedPanModel = TheatricalExpectPlatform.getBakedModel(blockEntity.getFixture().getPanModel());
        }
        if (cachedTiltModel == null){
            cachedTiltModel = TheatricalExpectPlatform.getBakedModel(blockEntity.getFixture().getTiltModel());
        }

        poseStack.pushPose();
        poseStack.translate(0.5F, 0, .5F);
        if(isHanging){
            Direction hangDirection = blockState.getValue(HangableBlock.HANG_DIRECTION);
            poseStack.translate(0, 0.5, 0F);
            if(hangDirection.getAxis() != Direction.Axis.Y){
                if(hangDirection.getAxis() == Direction.Axis.Z){
                    if(hangDirection == Direction.SOUTH) {
                        poseStack.mulPose(Axis.ZP.rotationDegrees(90));
                        poseStack.mulPose(Axis.XP.rotationDegrees(-90));
                    } else {
                        poseStack.mulPose(Axis.ZP.rotationDegrees(90));
                        poseStack.mulPose(Axis.XP.rotationDegrees(90));
                    }
                } else {
                    if(hangDirection == Direction.EAST) {
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
            float[] transforms = getThrottledStructuralTransforms(blockEntity, blockState);
            poseStack.translate(transforms[0], transforms[1], transforms[2]);
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
        int prevTilt = blockEntity.getPrevTilt();
        int tilt = blockEntity.getTilt();
        if (isFlipped) {
            poseStack.mulPose(Axis.XP.rotationDegrees(-180));
        } else {
            poseStack.mulPose(Axis.XP.rotationDegrees(180));
        }
        poseStack.mulPose(Axis.XP.rotationDegrees((prevTilt + (tilt - prevTilt) * partialTicks)));
        poseStack.translate(-tilts[0], -tilts[1], -tilts[2]);
        minecraftRenderModel(poseStack, vertexConsumer, blockState, cachedTiltModel, packedLight, packedOverlay);
        poseStack.popPose();
    }

    @Override
    public void beforeRenderBeam(
            MovingVL2CBeamsBlockEntity blockEntity,
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
        if (blockEntity.getIntensity() <= 0) return;

        if (blockEntity.getGobo() >= 0) {
            Vec3 localLensOffset = new Vec3(0.5f, 0.781f, 0.2f);
            float[] panPivot = blockEntity.getFixture().getPanRotationPosition();
            float[] tiltPivot = blockEntity.getFixture().getTiltRotationPosition();
            float[] structuralTransform = getThrottledStructuralTransforms(blockEntity, blockstate);

            goboProjector.render(
                    blockEntity,
                    multiBufferSource,
                    facing,
                    partialTicks,
                    isFlipped,
                    blockstate,
                    isHanging,
                    localLensOffset,
                    panPivot,
                    tiltPivot,
                    structuralTransform,
                    1.0f,   // minAngle: zoom gobo
                    14.0f   // maxAngle: zoom gobo
            );
        }
        // ==========================================================================================

        LazyRenderers.addLazyRender(new LazyRenderers.LazyRenderer() {

            @Override
            public void render(MultiBufferSource.BufferSource bufferSource,
                               PoseStack poseStack, Camera camera, float partialTick) {

                poseStack.pushPose();
                Vec3 offset = Vec3.atLowerCornerOf(blockEntity.getBlockPos())
                        .subtract(camera.getPosition());
                poseStack.translate(offset.x, offset.y, offset.z);

                preparePoseStack(blockEntity, poseStack, facing, partialTick,
                        isFlipped, blockstate, isHanging);

                float intensity = blockEntity.getPrevIntensity()
                        + (blockEntity.getIntensity() - blockEntity.getPrevIntensity()) * partialTick;
                int   color     = blockEntity.getColour();
                float alpha     = (intensity / 255f) * beamOpacity.floatValue();

                VertexConsumer builder  = multiBufferSource.getBuffer(Beam2DRenderTypes.getBeam());
                int            goboSlot = blockEntity.getGobo();

                // ── Beam principal (gobo 0 = open) ──────────────────────────────
                if (goboSlot == 0) {
                    poseStack.pushPose();
                    poseStack.translate(0.5f, 0.781f, 0.2f);
                    if (TheatricalExtraLightsConfig.shouldRender2DBeam()) {
                        renderLightBeam2D(builder, poseStack, blockEntity, camera,
                                alpha, 0.07f, (float) blockEntity.getDistance(), color, 0.007f);
                    } else {
                        renderLightBeam4D(builder, poseStack, blockEntity, partialTick,
                                alpha, 0.07f, (float) blockEntity.getDistance(), color, 0.007f);
                    }
                    poseStack.popPose();
                }

                // ── Fake Volumetric Beams según el patrón del gobo activo ───────
                renderFakeVolumetricBeams(builder, poseStack, blockEntity, camera,
                        partialTick, alpha, color, goboSlot);

                // ── Lens glow & lens cap ─────────────────────────────────────────
                poseStack.pushPose();
                poseStack.translate(0.5f, 0.781f, 0.2f);
                renderLensGlow(builder, poseStack, color, 0.12f);
                poseStack.popPose();

                renderLens(bufferSource, poseStack, alpha, color,
                        0.12f, 0.5f, 0.781f, 0.2f);

                poseStack.popPose();
            }

            @Override
            public Vec3 getPos(float partialTick) {
                return blockEntity.getBlockPos().getCenter();
            }
        });
    }

    private void renderFakeVolumetricBeams(
            VertexConsumer builder,
            PoseStack poseStack,
            MovingVL2CBeamsBlockEntity blockEntity,
            Camera camera,
            float partialTick,
            float alpha,
            int color,
            int goboSlot
    ) {
        FakeVolumetricBeamPattern pattern = GoboLibrary.VL2C.getPattern(goboSlot);

        final float LENS_X = 0.5f;
        final float LENS_Y = 0.781f;
        final float LENS_Z = 0.2f;

        float beamLength = (float) blockEntity.getDistance();

        poseStack.pushPose();
        poseStack.translate(LENS_X, LENS_Y, LENS_Z);
        poseStack.mulPose(new org.joml.Quaternionf().rotateZ((float) Math.toRadians(blockEntity.getGoboRotation())));
        float zoomFactor = (blockEntity.getPartialZoom(partialTick) / 255f) * 2f;

        for (FakeVolumetricBeamPattern.BeamTransform t : pattern.getTransforms()) {

            float beamAlpha     = alpha * t.alphaMult();
            float beamThickness = 0.07f * t.thicknessMult();
            float startThick    = 0.007f * t.thicknessMult();

            poseStack.pushPose();
            poseStack.mulPose(new org.joml.Quaternionf().rotateZ((float) Math.toRadians(t.panDeg())));
            poseStack.mulPose(new org.joml.Quaternionf().rotateX((float) Math.toRadians(t.tiltDeg() * zoomFactor)));

            if (TheatricalExtraLightsConfig.shouldRender2DBeam()) {
                renderLightBeam2D(builder, poseStack, blockEntity, camera,
                        beamAlpha, beamThickness, beamLength, color, startThick);
            } else {
                renderLightBeam4D(builder, poseStack, blockEntity, partialTick,
                        beamAlpha, beamThickness, beamLength, color, startThick);
            }

            poseStack.popPose();
        }

        poseStack.popPose();
    }

    @Override
    public void preparePoseStack(MovingVL2CBeamsBlockEntity blockEntity, PoseStack poseStack, Direction facing, float partialTicks, boolean isFlipped, BlockState blockState, boolean isHanging) {
        poseStack.translate(0.5F, 0, .5F);
        if(isHanging){
            Direction hangDirection = blockState.getValue(HangableBlock.HANG_DIRECTION);
            poseStack.translate(0, 0.5, 0F);
            if(hangDirection.getAxis() != Direction.Axis.Y){
                if(hangDirection.getAxis() == Direction.Axis.Z){
                    if(hangDirection == Direction.SOUTH) {
                        poseStack.mulPose(Axis.ZP.rotationDegrees(90));
                        poseStack.mulPose(Axis.XP.rotationDegrees(-90));
                    } else {
                        poseStack.mulPose(Axis.ZP.rotationDegrees(90));
                        poseStack.mulPose(Axis.XP.rotationDegrees(90));
                    }
                } else {
                    if(hangDirection == Direction.EAST) {
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
            float[] transforms = getThrottledStructuralTransforms(blockEntity, blockState);
            poseStack.translate(transforms[0], transforms[1], transforms[2]);
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
        int prevTilt = blockEntity.getPrevTilt();
        int tilt = blockEntity.getTilt();
        if (isFlipped) {
            poseStack.mulPose(Axis.XP.rotationDegrees(-180));
        } else {
            poseStack.mulPose(Axis.XP.rotationDegrees(180));
        }
        poseStack.mulPose(Axis.XP.rotationDegrees((prevTilt + (tilt - prevTilt) * partialTicks)));
        poseStack.translate(-tilts[0], -tilts[1], -tilts[2]);
    }
}