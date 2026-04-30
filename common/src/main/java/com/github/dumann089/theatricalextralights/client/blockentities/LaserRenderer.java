package com.github.dumann089.theatricalextralights.client.blockentities;

import com.github.dumann089.theatricalextralights.TheatricalExtraLights;
import com.github.dumann089.theatricalextralights.blockentities.LaserBlockEntity;
import com.github.dumann089.theatricalextralights.config.TheatricalExtraLightsConfig;
import com.github.dumann089.theatricalextralights.laser.LaserBeam;
import com.github.dumann089.theatricalextralights.laser.LaserPattern;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.imabad.theatrical.TheatricalExpectPlatform;
import dev.imabad.theatrical.blocks.HangableBlock;
import dev.imabad.theatrical.client.LazyRenderers;
import dev.imabad.theatrical.client.TheatricalRenderTypes;
import dev.imabad.theatrical.client.blockentities.FixtureRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import dev.imabad.theatrical.blockentities.light.BaseLightBlockEntity;
import dev.imabad.theatrical.blocks.light.BaseLightBlock;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.Deque;
import java.util.List;
import java.util.Optional;

public class LaserRenderer extends FixtureRenderer<LaserBlockEntity> {
    private BakedModel cachedPanModel, cachedTiltModel, cachedStaticModel;
    // DEBUG: throttle render-side logs to one print every ~120 frames
    private int renderLogTick = 0;

    public LaserRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void renderModel(LaserBlockEntity blockEntity, PoseStack poseStack, VertexConsumer vertexConsumer, Direction facing, float partialTicks, boolean isFlipped, BlockState blockState, boolean isHanging, int packedLight, int packedOverlay) {
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
    }

    @Override
    public void beforeRenderBeam(LaserBlockEntity blockEntity, PoseStack poseStack, VertexConsumer vertexConsumer,
                                 MultiBufferSource multiBufferSource, Direction facing, float partialTicks, boolean isFlipped,
                                 BlockState blockstate, boolean isHanging, int packedLight, int packedOverlay) {
        if (blockEntity.getIntensity() <= 0) {
            // Drain trail when fixture is off so it doesn't look frozen
            blockEntity.getTrailBuffer().clear();
            return;
        }

        LazyRenderers.addLazyRender(new LazyRenderers.LazyRenderer() {
            @Override
            public void render(MultiBufferSource.BufferSource bufferSource, PoseStack poseStack, Camera camera, float partialTick) {
                poseStack.pushPose();
                Vec3 offset = Vec3.atLowerCornerOf(blockEntity.getBlockPos()).subtract(camera.getPosition());
                poseStack.translate(offset.x, offset.y, offset.z);

                preparePoseStack(blockEntity, poseStack, facing, partialTick, isFlipped, blockstate, isHanging);

                VertexConsumer beamConsumer = bufferSource.getBuffer(TheatricalRenderTypes.BEAM);

                float intensity = blockEntity.getPrevIntensity() + ((blockEntity.getIntensity() - blockEntity.getPrevIntensity()) * partialTick);
                float intensity01 = intensity / 255f;

                int c1 = blockEntity.getColour();
                int c2 = blockEntity.getColour2();
                int c3 = blockEntity.getColour3();
                // If C2/C3 are unset (pure black), fall back to the previous color so a
                // monochromatic pattern (only C1 set on the controller) renders fully
                // visible instead of fading to invisible mid-pattern.
                if (c2 == 0) c2 = c1;
                if (c3 == 0) c3 = c2;

                double animTimeSec = (blockEntity.getLevel().getGameTime() + partialTick) / 20.0;
                LaserPattern pattern = blockEntity.getPattern();
                List<LaserBeam> beams = pattern.generate(
                        blockEntity.getSizeRaw(),
                        blockEntity.getAmplitudeRaw(),
                        blockEntity.getSpeedRaw(),
                        blockEntity.getRotationRaw(),
                        animTimeSec,
                        c1, c2, c3
                );
                // DEBUG: print every ~120 frames what the renderer sees client-side
                renderLogTick++;
                if (renderLogTick % 120 == 0) {
                    TheatricalExtraLights.LOGGER.info(
                            "[LaserRenderer@{}] patternRaw={} selected={} sizeRaw={} ampRaw={} beams={} c1={} c2={} c3={}",
                            blockEntity.getBlockPos(), blockEntity.getPatternRaw(), pattern.name(),
                            blockEntity.getSizeRaw(), blockEntity.getAmplitudeRaw(), beams.size(),
                            Integer.toHexString(c1), Integer.toHexString(c2), Integer.toHexString(c3));
                }

                int focus = blockEntity.getFocus();
                float beamWidth = 0.02f + (focus / 255f) * 0.06f;
                float baseLength = TheatricalExtraLightsConfig.getLaserBeamLength();

                Vec3 baseOrigin = new Vec3(0.5F, 0.5F, 0.0F);
                if (isHanging) {
                    baseOrigin = new Vec3(baseOrigin.x, 1.0 - baseOrigin.y, baseOrigin.z);
                }

                // ----- Single raycast on the laser's main aim direction. Every beam in
                // the pattern uses this same effective length so the polyline shape
                // stays uniform (drawn on whatever surface the laser is pointing at,
                // or projected to max length in open space). Per-beam clipping caused
                // ugly artifacts in complex scenes where beams hit nearby truss/decor.
                float baseEffLen = raycastBeamLength(blockEntity, 0f, 0f, baseLength);
                float[] effLengths = new float[beams.size()];
                for (int i = 0; i < beams.size(); i++) {
                    LaserBeam beam = beams.get(i);
                    effLengths[i] = baseEffLen * (beam.length / 32f);
                }

                int persistenceRaw = blockEntity.getPersistenceRaw();
                Deque<LaserBlockEntity.TrailFrame> trail = blockEntity.getTrailBuffer();

                // ----- Scanning beam mode for polyline patterns -----
                // Real lasers project a SINGLE beam that scans the pattern at high speed.
                // Persistence (eye/trail) makes the full shape visible. We replicate this:
                // each beam in the pattern is rendered with a fade-out alpha based on its
                // distance from the current "scan position" (which moves over time).
                // - Persistence DMX low  → narrow fade window → only 1-2 bright beams visible
                //   = clearly looks like one scanning beam
                // - Persistence DMX high → wide fade window → most/all of the pattern visible
                //   = the full pattern's shape is drawn
                // Scan rate is half the render frame rate (~30 sweeps/sec at 60 fps).
                // Faster than this aliases badly; slower makes motion look laggy.
                // Real galvos run at 30-100k pps which equals hundreds of sweeps/sec,
                // but at MC's 60 fps that's not perceivable — 30 Hz here matches the
                // "feels like a fast continuous laser" sweet spot.
                final float scanRate = 15.0f;
                int patternN = beams.size();
                float scanPos = (float) (((animTimeSec * scanRate) % 1.0) * patternN);
                // Wide fade window by default so the pattern stays readable at high
                // scan rate. Persistence still controls the trail length, but the
                // floor (patternN * 0.6f) ensures the scan motion never makes the
                // pattern unreadable.
                float fadeWindow = Math.max(patternN * 0.6f,
                        (persistenceRaw / 255f) * patternN + 1f);
                boolean scanning = pattern.usesPolyline() && patternN >= 2;

                LaserBlockEntity.TrailFrame snapshot = new LaserBlockEntity.TrailFrame(beams.size());
                for (int i = 0; i < beams.size(); i++) {
                    LaserBeam beam = beams.get(i);
                    snapshot.yaws[i] = beam.yawDeg;
                    snapshot.pitches[i] = beam.pitchDeg;
                    snapshot.lengths[i] = effLengths[i];
                    snapshot.colors[i] = beam.color;
                }

                if (scanning) {
                    // Render each beam with a fading alpha relative to the scan position.
                    float[] alphasByBeam = new float[patternN];
                    for (int i = 0; i < patternN; i++) {
                        // Distance "behind" the scan (pattern wraps around)
                        float dist = scanPos - i;
                        if (dist < 0) dist += patternN;
                        if (dist < fadeWindow) {
                            float t = dist / fadeWindow;
                            alphasByBeam[i] = (1.0f - t) * intensity01;
                        } else {
                            alphasByBeam[i] = 0f;
                        }
                    }
                    for (int i = 0; i < patternN; i++) {
                        if (alphasByBeam[i] < 0.005f) continue;
                        LaserBeam beam = beams.get(i);
                        renderOneBeam(beamConsumer, poseStack, baseOrigin,
                                beam.yawDeg, beam.pitchDeg, effLengths[i], beamWidth, beam.color, alphasByBeam[i]);
                    }

                    // Polyline ribbons between consecutive endpoints, with the same scan-fade alpha.
                    float ribbonHalfWidth = beamWidth * 1.4f;
                    Vec3 prevPt = computeEndpoint(baseOrigin, beams.get(0).yawDeg, beams.get(0).pitchDeg, effLengths[0]);
                    for (int i = 1; i < patternN; i++) {
                        LaserBeam beam = beams.get(i);
                        Vec3 currPt = computeEndpoint(baseOrigin, beam.yawDeg, beam.pitchDeg, effLengths[i]);
                        // Use the smaller of the two beams' fade alphas so segments behind the
                        // scan front are dimmed consistently.
                        float segAlpha = Math.min(alphasByBeam[i - 1], alphasByBeam[i]) * 0.75f;
                        if (segAlpha > 0.005f) {
                            renderRibbon(beamConsumer, poseStack, prevPt, currPt, ribbonHalfWidth, beam.color, segAlpha);
                        }
                        prevPt = currPt;
                    }
                    if (pattern.isClosed()) {
                        Vec3 firstPt = computeEndpoint(baseOrigin, beams.get(0).yawDeg, beams.get(0).pitchDeg, effLengths[0]);
                        float segAlpha = Math.min(alphasByBeam[patternN - 1], alphasByBeam[0]) * 0.75f;
                        if (segAlpha > 0.005f) {
                            renderRibbon(beamConsumer, poseStack, prevPt, firstPt, ribbonHalfWidth, beams.get(0).color, segAlpha);
                        }
                    }
                } else {
                    // Non-polyline patterns (BEAM_SIMPLE, SCATTER, BURST): render every beam
                    // at full intensity. They're inherently radial/random so scanning doesn't apply.
                    for (int i = 0; i < beams.size(); i++) {
                        LaserBeam beam = beams.get(i);
                        renderOneBeam(beamConsumer, poseStack, baseOrigin,
                                beam.yawDeg, beam.pitchDeg, effLengths[i], beamWidth, beam.color, intensity01);
                    }
                }

                // Trail buffer is no longer used (scanning + fade-window approach
                // computes alpha analytically from animTimeSec and persistenceRaw).
                // Keep it cleared so old code paths don't accumulate stale frames.
                if (!trail.isEmpty()) {
                    trail.clear();
                }

                poseStack.popPose();
            }

            @Override
            public Vec3 getPos(float partialTick) {
                return blockEntity.getBlockPos().getCenter();
            }
        });
    }

    private void renderOneBeam(VertexConsumer beamConsumer, PoseStack poseStack, Vec3 baseOrigin,
                               float yawDeg, float pitchDeg, float length, float beamWidth,
                               int color, float alpha) {
        poseStack.pushPose();
        poseStack.translate(baseOrigin.x, baseOrigin.y, baseOrigin.z);
        poseStack.mulPose(Axis.YP.rotationDegrees(yawDeg));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitchDeg));
        renderLightBeam(beamConsumer, poseStack, alpha, beamWidth, length, color);
        poseStack.popPose();
    }

    /**
     * Cast a ray in world space along the beam direction, returning the effective
     * length until the first solid block hit (or {@code maxLen} if no hit). The
     * world direction is computed analytically from the laser's pan/tilt + per-beam
     * offsets, NOT from the pose stack matrix — the latter is camera-relative and
     * would give different results from different camera angles.
     */
    /** Hits within this distance of the laser are treated as the laser's own
     *  mounting structure (truss, frame, the laser block itself) and skipped.
     *  Real walls beyond this distance still block beams. */
    private static final float RAYCAST_SKIP_RADIUS = 2.5f;

    private float raycastBeamLength(LaserBlockEntity be, float yawDeg, float pitchDeg, float maxLen) {
        if (be == null || be.getLevel() == null || maxLen <= 0.001f) return maxLen;
        Vec3 origin = be.getBlockPos().getCenter();
        Vec3 dir = getBeamWorldDir(be, yawDeg, pitchDeg);
        if (dir.lengthSqr() < 1e-8) return maxLen;
        Vec3 endWorld = origin.add(dir.scale(maxLen));

        // Iteratively skip hits that fall within the mounting-structure radius
        // around the laser. Once the ray finds a hit beyond this radius, that's
        // a real wall and we stop there. Cap iterations so we never loop forever
        // on weird geometry.
        Vec3 rayStart = origin;
        int safety = 16;
        while (safety-- > 0) {
            BlockHitResult hit = be.getLevel().clip(new ClipContext(rayStart, endWorld,
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
            if (hit.getType() == HitResult.Type.MISS) {
                return maxLen;
            }
            float dist = (float) hit.getLocation().distanceTo(origin);
            if (dist >= RAYCAST_SKIP_RADIUS) {
                return Math.min(maxLen, dist);
            }
            // Skip this hit, continue past it
            rayStart = hit.getLocation().add(dir.scale(0.01));
            if (rayStart.distanceToSqr(endWorld) < 1e-4) {
                return maxLen;
            }
        }
        return maxLen;
    }

    /**
     * Compute a beam's world-space direction by combining the laser's effective
     * pan/tilt (which already accounts for facing/hanging/upside-down) with per-beam
     * yaw/pitch offsets, then converting to a unit vector via the same view-vector
     * formula used by Theatrical's {@code rayTraceDir}.
     */
    private static Vec3 getBeamWorldDir(LaserBlockEntity be, float beamYawDeg, float beamPitchDeg) {
        BlockState bs = be.getBlockState();
        net.minecraft.core.Direction hangDir = bs.getValue(BaseLightBlock.HANG_DIRECTION);
        net.minecraft.core.Direction facing = bs.getValue(BaseLightBlock.FACING);
        boolean isHangingNonVertically = BaseLightBlockEntity.isHangingNonVertically(
                hangDir, bs.getValue(BaseLightBlock.HANGING));

        if (!isHangingNonVertically) {
            float tilt = be.getTilt();
            if (be.isUpsideDown() || be.getFixture().invertTilt()) {
                tilt = -tilt;
            }
            float pan = (facing.toYRot() - be.getPan());
            if (facing.getAxis() == net.minecraft.core.Direction.Axis.X) {
                pan -= 180;
            }
            if (be.getFixture().invertPan()) {
                pan *= -1;
            }
            if (be.isUpsideDown()) {
                if (facing.getAxis() == net.minecraft.core.Direction.Axis.X) {
                    pan = facing.getOpposite().toYRot() + be.getPan();
                } else {
                    pan = facing.toYRot() + be.getPan();
                }
            }
            // Per-beam offsets: panPerBeam = panBase - beamYaw, tiltPerBeam = tiltBase + beamPitch
            return BaseLightBlockEntity.calculateViewVector(tilt + beamPitchDeg, pan - beamYawDeg);
        }
        // Non-vertical hanging is rare for lasers; fall back to base direction
        // (per-beam offsets ignored — the pattern won't raycast individually but at
        // least it stays consistent regardless of camera angle).
        return BaseLightBlockEntity.rayTraceDir(be);
    }

    /**
     * Compute the 3D endpoint of a beam in fixture-local space, given (yaw, pitch, length).
     * Mirrors the rotation chain in {@link #renderOneBeam} (translate baseOrigin, rotate Y, rotate X,
     * extend +Z by length).
     */
    private Vec3 computeEndpoint(Vec3 baseOrigin, float yawDeg, float pitchDeg, float length) {
        double y = Math.toRadians(yawDeg);
        double p = Math.toRadians(pitchDeg);
        double cy = Math.cos(y), sy = Math.sin(y);
        double cp = Math.cos(p), sp = Math.sin(p);
        double x = length * sy * cp;
        double yc = -length * sp;
        double z = length * cy * cp;
        return new Vec3(baseOrigin.x + x, baseOrigin.y + yc, baseOrigin.z + z);
    }

    /**
     * Draw a thin 4-sided ribbon (tube) connecting two 3D points in fixture-local space.
     * The current pose stack is used as-is (no extra transform); world transform is
     * already applied by preparePoseStack on the caller's side.
     */
    private void renderRibbon(VertexConsumer builder, PoseStack stack,
                              Vec3 a, Vec3 b, float halfWidth, int color, float alpha) {
        Vec3 dir = b.subtract(a);
        if (dir.lengthSqr() < 1e-8) return;
        dir = dir.normalize();
        Vec3 up = Math.abs(dir.y) > 0.95 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0);
        Vec3 right = dir.cross(up).normalize().scale(halfWidth);
        Vec3 perp = right.cross(dir).normalize().scale(halfWidth);

        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int bC = color & 0xFF;
        int aV = (int) (Math.max(0f, Math.min(1f, alpha)) * 255);

        Matrix4f m = stack.last().pose();
        Matrix3f normal = stack.last().normal();

        float ax1 = (float) (a.x + right.x), ay1 = (float) (a.y + right.y), az1 = (float) (a.z + right.z);
        float ax2 = (float) (a.x - right.x), ay2 = (float) (a.y - right.y), az2 = (float) (a.z - right.z);
        float ax3 = (float) (a.x + perp.x),  ay3 = (float) (a.y + perp.y),  az3 = (float) (a.z + perp.z);
        float ax4 = (float) (a.x - perp.x),  ay4 = (float) (a.y - perp.y),  az4 = (float) (a.z - perp.z);
        float bx1 = (float) (b.x + right.x), by1 = (float) (b.y + right.y), bz1 = (float) (b.z + right.z);
        float bx2 = (float) (b.x - right.x), by2 = (float) (b.y - right.y), bz2 = (float) (b.z - right.z);
        float bx3 = (float) (b.x + perp.x),  by3 = (float) (b.y + perp.y),  bz3 = (float) (b.z + perp.z);
        float bx4 = (float) (b.x - perp.x),  by4 = (float) (b.y - perp.y),  bz4 = (float) (b.z - perp.z);

        // 4 sides forming a thin rectangular tube around the segment
        addVertex(builder, m, normal, r, g, bC, aV, ax1, ay1, az1);
        addVertex(builder, m, normal, r, g, bC, aV, ax3, ay3, az3);
        addVertex(builder, m, normal, r, g, bC, aV, bx3, by3, bz3);
        addVertex(builder, m, normal, r, g, bC, aV, bx1, by1, bz1);

        addVertex(builder, m, normal, r, g, bC, aV, ax3, ay3, az3);
        addVertex(builder, m, normal, r, g, bC, aV, ax2, ay2, az2);
        addVertex(builder, m, normal, r, g, bC, aV, bx2, by2, bz2);
        addVertex(builder, m, normal, r, g, bC, aV, bx3, by3, bz3);

        addVertex(builder, m, normal, r, g, bC, aV, ax2, ay2, az2);
        addVertex(builder, m, normal, r, g, bC, aV, ax4, ay4, az4);
        addVertex(builder, m, normal, r, g, bC, aV, bx4, by4, bz4);
        addVertex(builder, m, normal, r, g, bC, aV, bx2, by2, bz2);

        addVertex(builder, m, normal, r, g, bC, aV, ax4, ay4, az4);
        addVertex(builder, m, normal, r, g, bC, aV, ax1, ay1, az1);
        addVertex(builder, m, normal, r, g, bC, aV, bx1, by1, bz1);
        addVertex(builder, m, normal, r, g, bC, aV, bx4, by4, bz4);
    }

    private void renderLightBeam(VertexConsumer builder, PoseStack stack, float alpha, float beamSize, float length, int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (int) (Math.max(0f, Math.min(1f, alpha)) * 255);
        // Far end keeps 40% of the start alpha so the beam tip stays visible
        // (instead of fading to nothing) — like a real laser hitting fog/surface.
        int aFar = Math.max(0, (int) (a * 0.4f));
        Matrix4f m = stack.last().pose();
        Matrix3f normal = stack.last().normal();

        float endMultiplier = beamSize;

        // R Face
        addVertex(builder, m, normal, r, g, b, aFar, beamSize * endMultiplier, beamSize * endMultiplier, length);
        addVertex(builder, m, normal, r, g, b, a, beamSize, beamSize, 0);
        addVertex(builder, m, normal, r, g, b, a, beamSize, -beamSize, 0);
        addVertex(builder, m, normal, r, g, b, aFar, beamSize * endMultiplier, -beamSize * endMultiplier, length);

        // L Face
        addVertex(builder, m, normal, r, g, b, aFar, -beamSize * endMultiplier, -beamSize * endMultiplier, length);
        addVertex(builder, m, normal, r, g, b, a, -beamSize, -beamSize, 0);
        addVertex(builder, m, normal, r, g, b, a, -beamSize, beamSize, 0);
        addVertex(builder, m, normal, r, g, b, aFar, -beamSize * endMultiplier, beamSize * endMultiplier, length);

        // Top Face
        addVertex(builder, m, normal, r, g, b, aFar, -beamSize * endMultiplier, beamSize * endMultiplier, length);
        addVertex(builder, m, normal, r, g, b, a, -beamSize, beamSize, 0);
        addVertex(builder, m, normal, r, g, b, a, beamSize, beamSize, 0);
        addVertex(builder, m, normal, r, g, b, aFar, beamSize * endMultiplier, beamSize * endMultiplier, length);

        // Down Face
        addVertex(builder, m, normal, r, g, b, aFar, beamSize * endMultiplier, -beamSize * endMultiplier, length);
        addVertex(builder, m, normal, r, g, b, a, beamSize, -beamSize, 0);
        addVertex(builder, m, normal, r, g, b, a, -beamSize, -beamSize, 0);
        addVertex(builder, m, normal, r, g, b, aFar, -beamSize * endMultiplier, -beamSize * endMultiplier, length);
    }

    @Override
    public void preparePoseStack(LaserBlockEntity blockEntity, PoseStack poseStack, Direction facing, float partialTicks, boolean isFlipped, BlockState blockState, boolean isHanging) {
        poseStack.translate(0.5F, 0, .5F);
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
