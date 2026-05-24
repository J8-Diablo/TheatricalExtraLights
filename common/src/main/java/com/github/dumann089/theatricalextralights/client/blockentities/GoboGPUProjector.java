package com.github.dumann089.theatricalextralights.client.blockentities;

import com.github.dumann089.theatricalextralights.blockentities.interfaces.HasGobo;
import com.github.dumann089.theatricalextralights.config.TheatricalExtraLightsConfig;
import com.github.dumann089.theatricalextralights.client.ModShaders;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.imabad.theatrical.blocks.HangableBlock;
import dev.imabad.theatrical.client.LazyRenderers;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class GoboGPUProjector {

    // ── Reused per-instance temporaries (never allocate in hot paths) ──────────
    private final Vector4f raycastOriginVec = new Vector4f();
    private final Vector4f raycastDirVec    = new Vector4f();
    private final PoseStack projectorPoseStack = new PoseStack();

    // ── Reused Vector4f slots for the lazy-render closure ──────────────────────
    private final Vector4f tmpLightPos = new Vector4f();
    private final Vector4f tmpLightDir = new Vector4f();
    private final Vector4f tmpAxisU    = new Vector4f();
    private final Vector4f tmpAxisV    = new Vector4f();

    private static final Direction[] DIRS = Direction.values();

    // ── Geometry cache ─────────────────────────────────────────────────────────
    private int   cachedGeoHash   = Integer.MIN_VALUE;
    private final LongOpenHashSet uniqueBlocks = new LongOpenHashSet(512);

    // Flat VBO: 3 floats per corner × 4 corners = 12 floats per quad
    private float[] cachedVerts    = new float[2048 * 12];
    private int     cachedQuadCount = 0;

    // Reusable MutableBlockPos for DDA — eliminates one allocation per isOccludedFast call
    private final BlockPos.MutableBlockPos ddaCheckPos = new BlockPos.MutableBlockPos();

    // ── Tunables ───────────────────────────────────────────────────────────────
    /**
     * Hard cap on the scan radius (blocks). Keeps the triple-loop bounded even
     * at wide zoom angles.
     */
    private static final float MAX_SCAN_RADIUS = 6.0f;

    /**
     * Scan length at maximum zoom (widest cone, zoomNorm = 1.0).
     * The gobo stops being projected past this distance when fully open.
     */
    private static final float SCAN_LEN_ZOOM_MAX = 8.0f;

    /**
     * Scan length at minimum zoom (narrowest cone, zoomNorm = 0.0).
     * At this zoom the gobo can reach up to MaxGoboDistance from config.
     * This value is used as the floor so it never exceeds MaxGoboDistance.
     */
    private static final float SCAN_LEN_ZOOM_MIN_DEFAULT = 80.0f;

    // ══════════════════════════════════════════════════════════════════════════
    //  Public entry point
    // ══════════════════════════════════════════════════════════════════════════

    public <T extends BlockEntity & HasGobo> void render(
            T be, MultiBufferSource multiBufferSource, Direction facing, float partialTicks,
            boolean isFlipped, BlockState blockState, boolean isHanging, Vec3 localLensOffset,
            float[] panPivot, float[] tiltPivot, float[] structuralTransform,
            float minAngle, float maxAngle) {

        float intensity01 = be.getPartialIntensity(partialTicks) / 255f;
        if (intensity01 <= 0f || be.getLevel() == null) return;

        int   colour   = be.getColour() == 0 ? 0xFFFFFF : be.getColour();
        float panDeg   = be.getPartialPanDeg(partialTicks);
        float tiltDeg  = be.getPartialTiltDeg(partialTicks);
        float zoomNorm = be.getPartialZoom(partialTicks) / 255f;
        float coneHalfAngle = minAngle + zoomNorm * (maxAngle - minAngle);

        projectorPoseStack.setIdentity();
        applyFixtureOrientation(projectorPoseStack, facing, isFlipped, blockState, isHanging,
                panDeg, tiltDeg, panPivot, tiltPivot, structuralTransform);
        projectorPoseStack.translate(localLensOffset.x, localLensOffset.y, localLensOffset.z);

        Matrix4f m = projectorPoseStack.last().pose();
        raycastOriginVec.set(0f, 0f, 0f, 1f).mul(m);
        raycastDirVec   .set(0f, 0f, -1f, 0f).mul(m);

        Vec3 origin  = Vec3.atLowerCornerOf(be.getBlockPos())
                .add(raycastOriginVec.x, raycastOriginVec.y, raycastOriginVec.z);
        Vec3 beamDir = new Vec3(raycastDirVec.x, raycastDirVec.y, raycastDirVec.z).normalize();

        // ── Entity hit → shorten beam ──────────────────────────────────────────
        float finalLen = TheatricalExtraLightsConfig.getLaserBeamLength() * 1.5f;
        Vec3  centerEnd = origin.add(beamDir.scale(finalLen));
        AABB  beamVolume = new AABB(origin, centerEnd).inflate(3.0);

        List<LivingEntity> entities = be.getLevel().getEntitiesOfClass(
                LivingEntity.class, beamVolume, e -> !e.isSpectator());
        for (LivingEntity entity : entities) {
            Optional<Vec3> hit = entity.getBoundingBox().clip(origin, centerEnd);
            if (hit.isPresent()) {
                float d = (float) origin.distanceToSqr(hit.get());
                if (d < finalLen * finalLen) finalLen = (float) Math.sqrt(d);
            }
        }

        // ── Beam axes (with optional gobo rotation) ───────────────────────────
        Vec3 axisU, axisV;
        {
            Vec3 up = Math.abs(beamDir.y) > 0.9 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0);
            axisU = beamDir.cross(up).normalize();
            axisV = beamDir.cross(axisU).normalize();

            float goboRot = be.getGoboRotation();
            if (Math.abs(goboRot) > 0.001f) {
                double rad = Math.toRadians(goboRot);
                double cos = Math.cos(rad), sin = Math.sin(rad);
                Vec3 oldU = axisU;
                axisU = oldU.scale(cos).add(axisV.scale(sin)).normalize();
                axisV = oldU.scale(-sin).add(axisV.scale(cos)).normalize();
            }
        }

        float tanHalfAngle = (float) Math.tan(Math.toRadians(coneHalfAngle));

        // ── Snapshot values for the lazy closure (primitives / immutable refs) ─
        final int    finalColour    = colour;
        final float  finalIntensity = intensity01;
        final int    finalGoboSlot  = be.getGobo();
        final BlockPos bePos        = be.getBlockPos();
        final Level  level          = be.getLevel();
        final Vec3   finalOrigin    = origin;
        final Vec3   finalBeamDir   = beamDir;
        final Vec3   finalAxisU     = axisU;
        final Vec3   finalAxisV     = axisV;
        final float  finalMaxLen    = finalLen;
        final float  finalZoomNorm  = zoomNorm;   // 0 = narrow, 1 = wide

        LazyRenderers.addLazyRender(new LazyRenderers.LazyRenderer() {

            @Override
            public void render(MultiBufferSource.BufferSource bufferSource,
                               PoseStack poseStack, Camera camera, float partialTick) {

                Vec3 camPos = camera.getPosition();

                // ── Transform light vectors into view space ────────────────────
                poseStack.pushPose();
                poseStack.translate(-camPos.x, -camPos.y, -camPos.z);
                Matrix4f matrix = poseStack.last().pose();

                // Reuse pre-allocated Vector4f slots — zero allocations here
                tmpLightPos.set((float) finalOrigin.x, (float) finalOrigin.y,
                        (float) finalOrigin.z, 1.0f);
                matrix.transform(tmpLightPos);

                tmpLightDir.set((float) finalBeamDir.x, (float) finalBeamDir.y,
                        (float) finalBeamDir.z, 0.0f);
                matrix.transform(tmpLightDir);
                tmpLightDir.normalize();

                tmpAxisU.set((float) finalAxisU.x, (float) finalAxisU.y,
                        (float) finalAxisU.z, 0.0f);
                matrix.transform(tmpAxisU);
                tmpAxisU.normalize();

                tmpAxisV.set((float) finalAxisV.x, (float) finalAxisV.y,
                        (float) finalAxisV.z, 0.0f);
                matrix.transform(tmpAxisV);
                tmpAxisV.normalize();

                // ── Vertex buffer setup ───────────────────────────────────────
                ResourceLocation texture    = be.getGoboLibrary().getTexture(finalGoboSlot);
                RenderType       renderType = ModShaders.getGoboRenderType(texture);
                VertexConsumer   vc         = bufferSource.getBuffer(renderType);

                int   r          = (finalColour >> 16) & 0xFF;
                int   g          = (finalColour >> 8)  & 0xFF;
                int   b          = finalColour          & 0xFF;
                int   finalAlpha = (int)(Math.min(1f, finalIntensity * 1.5f) * 255f);

                // ── Zoom-driven scan length ───────────────────────────────────
                // zoomNorm=0 → narrow beam → projects far (up to MaxGoboDistance)
                // zoomNorm=1 → wide  beam → cuts off early (SCAN_LEN_ZOOM_MAX)
                //
                // Quadratic ease-in so the cut-off feels gradual at low zoom
                // and aggressive at high zoom, matching how a real gobo frosts out.
                float maxGoboDistConfig = TheatricalExtraLightsConfig.getMaxGoboDistance();
                float scanLenAtMin  = Math.min(SCAN_LEN_ZOOM_MIN_DEFAULT, maxGoboDistConfig);
                float zoomT         = finalZoomNorm * finalZoomNorm; // ease-in^2
                float scanLen       = scanLenAtMin + zoomT * (SCAN_LEN_ZOOM_MAX - scanLenAtMin);
                // Also respect the actual beam stop (entity hit, etc.)
                scanLen = Math.min(scanLen, finalMaxLen);

                // Scan radius: hard-capped to keep the triple-loop sane
                float clampedRadius = Math.min(scanLen * tanHalfAngle, MAX_SCAN_RADIUS);

                // FIX #2: geoHash no longer includes finalMaxLen.
                // finalMaxLen changes every frame when entities enter the beam volume,
                // which used to invalidate the cache and trigger rebuildGeometryCache
                // every frame. Surface geometry does not depend on entity occlusion.
                int qDirX = Math.round((float) finalBeamDir.x * 50f);
                int qDirY = Math.round((float) finalBeamDir.y * 50f);
                int qDirZ = Math.round((float) finalBeamDir.z * 50f);
                int qTan  = Math.round(tanHalfAngle * 100f);
                int qScan = Math.round(scanLen);   // scanLen is zoom-driven, bucket by block

                int geoHash = java.util.Objects.hash(
                        bePos, qDirX, qDirY, qDirZ, qTan, qScan);

                // ── Shader uniforms (after scanLen is computed) ───────────────
                // MaxLen is set to scanLen so the shader's lengthFade matches
                // the zoom-driven cutoff exactly — no geometry/shader mismatch.
                ShaderInstance shader = ModShaders.goboProjectorShader;
                if (shader != null) {
                    shader.safeGetUniform("LightPos").set(tmpLightPos.x(), tmpLightPos.y(), tmpLightPos.z());
                    shader.safeGetUniform("LightDir").set(tmpLightDir.x(), tmpLightDir.y(), tmpLightDir.z());
                    shader.safeGetUniform("AxisU")   .set(tmpAxisU.x(),    tmpAxisU.y(),    tmpAxisU.z());
                    shader.safeGetUniform("AxisV")   .set(tmpAxisV.x(),    tmpAxisV.y(),    tmpAxisV.z());
                    shader.safeGetUniform("TanHalfAngle").set(tanHalfAngle);
                    shader.safeGetUniform("MaxLen")  .set(scanLen);
                    shader.safeGetUniform("MaxGoboDist")
                            .set(TheatricalExtraLightsConfig.getMaxGoboDistance());
                }

                if (geoHash != cachedGeoHash) {
                    rebuildGeometryCache(level, bePos, finalOrigin, finalBeamDir,
                            scanLen, clampedRadius, tanHalfAngle, geoHash);
                }

                // ── Emit cached quads every frame (no block lookups) ──────────
                for (int i = 0; i < cachedQuadCount; i++) {
                    int vIdx = i * 12;
                    vc.vertex(matrix, cachedVerts[vIdx],   cachedVerts[vIdx+1], cachedVerts[vIdx+2])
                            .color(r, g, b, finalAlpha).uv(0f, 0f).endVertex();
                    vc.vertex(matrix, cachedVerts[vIdx+3], cachedVerts[vIdx+4], cachedVerts[vIdx+5])
                            .color(r, g, b, finalAlpha).uv(0f, 0f).endVertex();
                    vc.vertex(matrix, cachedVerts[vIdx+6], cachedVerts[vIdx+7], cachedVerts[vIdx+8])
                            .color(r, g, b, finalAlpha).uv(0f, 0f).endVertex();
                    vc.vertex(matrix, cachedVerts[vIdx+9], cachedVerts[vIdx+10],cachedVerts[vIdx+11])
                            .color(r, g, b, finalAlpha).uv(0f, 0f).endVertex();
                }

                poseStack.popPose();
                bufferSource.endBatch(renderType);
            }

            @Override
            public Vec3 getPos(float partialTick) { return bePos.getCenter(); }
        });
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Geometry rebuild — called only when geoHash changes
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Rebuilds {@link #cachedVerts} and {@link #cachedQuadCount}.
     * All expensive block-lookup work is here; nothing in this method is called
     * every frame — only on cache misses.
     */
    private void rebuildGeometryCache(Level level, BlockPos bePos, Vec3 finalOrigin,
                                      Vec3 finalBeamDir, float scanLen, float clampedRadius,
                                      float tanHalfAngle, int newGeoHash) {

        cachedGeoHash  = newGeoHash;
        cachedQuadCount = 0;
        uniqueBlocks.clear();

        float ox = (float) finalOrigin.x;
        float oy = (float) finalOrigin.y;
        float oz = (float) finalOrigin.z;
        float dx = (float) finalBeamDir.x;
        float dy = (float) finalBeamDir.y;
        float dz = (float) finalBeamDir.z;

        double endX = ox + dx * scanLen;
        double endY = oy + dy * scanLen;
        double endZ = oz + dz * scanLen;

        int minX = (int) Math.floor(Math.min(ox, endX) - clampedRadius) - 1;
        int maxX = (int) Math.ceil (Math.max(ox, endX) + clampedRadius) + 1;
        int minY = (int) Math.floor(Math.min(oy, endY) - clampedRadius) - 1;
        int maxY = (int) Math.ceil (Math.max(oy, endY) + clampedRadius) + 1;
        int minZ = (int) Math.floor(Math.min(oz, endZ) - clampedRadius) - 1;
        int maxZ = (int) Math.ceil (Math.max(oz, endZ) + clampedRadius) + 1;

        BlockPos.MutableBlockPos mPos  = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos adjPos = new BlockPos.MutableBlockPos();

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = minY; y <= maxY; y++) {

                    // Cone inclusion test (block centre)
                    float vx = x + 0.5f - ox;
                    float vy = y + 0.5f - oy;
                    float vz = z + 0.5f - oz;

                    float t = vx * dx + vy * dy + vz * dz;
                    if (t < -0.5f || t > scanLen + 0.5f) continue;

                    float distSq = (vx*vx + vy*vy + vz*vz) - (t*t);
                    float maxR   = t * tanHalfAngle + 1.4f;
                    if (distSq > maxR * maxR) continue;

                    mPos.set(x, y, z);
                    if (mPos.equals(bePos)) continue;

                    BlockState state = level.getBlockState(mPos);
                    if (state.isAir() || state.getShape(level, mPos).isEmpty()) continue;

                    // Skip mod/passthrough blocks
                    ResourceLocation key = BuiltInRegistries.BLOCK.getKey(state.getBlock());
                    if (key != null) {
                        String ns = key.getNamespace();
                        if (ns.equals("theatrical") || ns.equals("theatricalextralights")) continue;
                        if (TheatricalExtraLightsConfig.isLaserPassThrough(key.toString())) continue;
                    }

                    // Visibility: at least one exposed face must have line-of-sight to origin
                    boolean accepted = false;
                    for (Direction dir : DIRS) {
                        adjPos.setWithOffset(mPos, dir);
                        if (level.getBlockState(adjPos).isSolidRender(level, adjPos)) continue;

                        // Face must point toward the light
                        double cx = x + 0.5 + dir.getStepX() * 0.5;
                        double cy = y + 0.5 + dir.getStepY() * 0.5;
                        double cz = z + 0.5 + dir.getStepZ() * 0.5;
                        double rx  = cx - ox, ry = cy - oy, rz = cz - oz;
                        if ((rx * dir.getStepX() + ry * dir.getStepY() + rz * dir.getStepZ()) >= 0.01) continue;

                        // DDA occlusion test
                        double fx = x + 0.5 + dir.getStepX() * 0.49;
                        double fy = y + 0.5 + dir.getStepY() * 0.49;
                        double fz = z + 0.5 + dir.getStepZ() * 0.49;
                        if (!isOccludedFast(level, fx, fy, fz, ox, oy, oz, bePos, mPos)) {
                            accepted = true;
                            break;
                        }
                    }
                    if (!accepted) continue;

                    // Store as relative key so bePos-relative coords survive
                    int  relX = mPos.getX() - bePos.getX();
                    int  relY = mPos.getY() - bePos.getY();
                    int  relZ = mPos.getZ() - bePos.getZ();
                    long blockKey = (((long)(relX & 0xFFFF)) << 32)
                            | (((long)(relY & 0xFFFF)) << 16)
                            | ((long)(relZ & 0xFFFF));
                    uniqueBlocks.add(blockKey);
                }
            }
        }

        // Build VBO data from accepted blocks
        final float zBias = 0.02f;

        LongIterator iter = uniqueBlocks.iterator();
        while (iter.hasNext()) {
            long bk  = iter.nextLong();
            int  pdx = (short)(bk >>> 32);
            int  pdy = (short)(bk >>> 16);
            int  pdz = (short)(bk & 0xFFFF);

            BlockPos pos = bePos.offset(pdx, pdy, pdz);
            BlockState s = level.getBlockState(pos);
            if (s.isAir()) continue;

            // Per-block radial direction for back-face culling
            float cvx, cvy, cvz;
            double dirX = pos.getX() + 0.5 - finalOrigin.x;
            double dirY = pos.getY() + 0.5 - finalOrigin.y;
            double dirZ = pos.getZ() + 0.5 - finalOrigin.z;
            double dirLen = Math.sqrt(dirX*dirX + dirY*dirY + dirZ*dirZ);
            if (dirLen > 0.001) {
                cvx = (float)(dirX / dirLen);
                cvy = (float)(dirY / dirLen);
                cvz = (float)(dirZ / dirLen);
            } else {
                cvx = (float) finalBeamDir.x;
                cvy = (float) finalBeamDir.y;
                cvz = (float) finalBeamDir.z;
            }

            if (s.isCollisionShapeFullBlock(level, pos)) {
                AABB box = new AABB(pos.getX(), pos.getY(), pos.getZ(),
                        pos.getX() + 1.0, pos.getY() + 1.0, pos.getZ() + 1.0)
                        .inflate(zBias);
                for (int f = 0; f < 6; f++) {
                    Direction face = DIRS[f];
                    if ((face.getStepX() * cvx + face.getStepY() * cvy + face.getStepZ() * cvz) > 0.01f) continue;
                    BlockPos adj = pos.relative(face);
                    if (level.getBlockState(adj).isSolidRender(level, adj)) continue;
                    addQuadToVBO(box, f);
                }
            } else {
                for (AABB box : s.getShape(level, pos).toAabbs()) {
                    AABB wBox = box.move(pos).inflate(zBias);
                    for (int f = 0; f < 6; f++) {
                        Direction face = DIRS[f];
                        if ((face.getStepX() * cvx + face.getStepY() * cvy + face.getStepZ() * cvz) > 0.01f) continue;
                        BlockPos adj = pos.relative(face);
                        if (level.getBlockState(adj).isSolidRender(level, adj)) continue;
                        addQuadToVBO(wBox, f);
                    }
                }
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  DDA occlusion test
    // ══════════════════════════════════════════════════════════════════════════

    private boolean isOccludedFast(Level level,
                                   double sx, double sy, double sz,
                                   double ex, double ey, double ez,
                                   BlockPos bePos, BlockPos targetPos) {
        double ddx = ex - sx, ddy = ey - sy, ddz = ez - sz;
        double len = Math.sqrt(ddx*ddx + ddy*ddy + ddz*ddz);
        if (len < 0.001) return false;

        ddx /= len; ddy /= len; ddz /= len;

        int stepX = ddx > 0 ? 1 : (ddx < 0 ? -1 : 0);
        int stepY = ddy > 0 ? 1 : (ddy < 0 ? -1 : 0);
        int stepZ = ddz > 0 ? 1 : (ddz < 0 ? -1 : 0);

        double tDX = stepX != 0 ? Math.abs(1.0 / ddx) : Double.MAX_VALUE;
        double tDY = stepY != 0 ? Math.abs(1.0 / ddy) : Double.MAX_VALUE;
        double tDZ = stepZ != 0 ? Math.abs(1.0 / ddz) : Double.MAX_VALUE;

        int vx = (int) Math.floor(sx);
        int vy = (int) Math.floor(sy);
        int vz = (int) Math.floor(sz);

        double tMX = stepX > 0 ? (vx + 1.0 - sx) * tDX : (stepX < 0 ? (sx - vx) * tDX : Double.MAX_VALUE);
        double tMY = stepY > 0 ? (vy + 1.0 - sy) * tDY : (stepY < 0 ? (sy - vy) * tDY : Double.MAX_VALUE);
        double tMZ = stepZ > 0 ? (vz + 1.0 - sz) * tDZ : (stepZ < 0 ? (sz - vz) * tDZ : Double.MAX_VALUE);

        double t = 0;

        // Reuse the pre-allocated instance field instead of allocating a new
        // MutableBlockPos on every call.
        BlockPos.MutableBlockPos checkPos = ddaCheckPos;

        while (t < len - 0.01) {
            if (tMX < tMY && tMX < tMZ) { t = tMX; vx += stepX; tMX += tDX; }
            else if (tMY < tMZ)          { t = tMY; vy += stepY; tMY += tDY; }
            else                          { t = tMZ; vz += stepZ; tMZ += tDZ; }

            checkPos.set(vx, vy, vz);
            if (checkPos.equals(bePos) || checkPos.equals(targetPos)) continue;

            BlockState state = level.getBlockState(checkPos);
            if (!state.isAir() && state.isSolidRender(level, checkPos)) {
                ResourceLocation key = BuiltInRegistries.BLOCK.getKey(state.getBlock());
                boolean isMod  = key != null && (key.getNamespace().equals("theatrical")
                        || key.getNamespace().equals("theatricalextralights"));
                boolean isPass = key != null && TheatricalExtraLightsConfig.isLaserPassThrough(key.toString());
                if (!isMod && !isPass) return true;
            }
        }
        return false;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  VBO helpers
    // ══════════════════════════════════════════════════════════════════════════

    private void addQuadToVBO(AABB box, int faceIdx) {
        if (cachedQuadCount * 12 >= cachedVerts.length) {
            cachedVerts = Arrays.copyOf(cachedVerts, cachedVerts.length * 2);
        }

        int   vIdx = cachedQuadCount * 12;
        float mx = (float) box.minX, my = (float) box.minY, mz = (float) box.minZ;
        float Mx = (float) box.maxX, My = (float) box.maxY, Mz = (float) box.maxZ;

        switch (DIRS[faceIdx]) {
            case DOWN:  writeQuad(vIdx, mx,my,Mz, mx,my,mz, Mx,my,mz, Mx,my,Mz); break;
            case UP:    writeQuad(vIdx, mx,My,mz, mx,My,Mz, Mx,My,Mz, Mx,My,mz); break;
            case NORTH: writeQuad(vIdx, Mx,my,mz, mx,my,mz, mx,My,mz, Mx,My,mz); break;
            case SOUTH: writeQuad(vIdx, mx,my,Mz, Mx,my,Mz, Mx,My,Mz, mx,My,Mz); break;
            case WEST:  writeQuad(vIdx, mx,my,mz, mx,my,Mz, mx,My,Mz, mx,My,mz); break;
            case EAST:  writeQuad(vIdx, Mx,my,Mz, Mx,my,mz, Mx,My,mz, Mx,My,Mz); break;
        }
        cachedQuadCount++;
    }

    private void writeQuad(int i,
                           float x1, float y1, float z1,
                           float x2, float y2, float z2,
                           float x3, float y3, float z3,
                           float x4, float y4, float z4) {
        cachedVerts[i++] = x1; cachedVerts[i++] = y1; cachedVerts[i++] = z1;
        cachedVerts[i++] = x2; cachedVerts[i++] = y2; cachedVerts[i++] = z2;
        cachedVerts[i++] = x3; cachedVerts[i++] = y3; cachedVerts[i++] = z3;
        cachedVerts[i++] = x4; cachedVerts[i++] = y4; cachedVerts[i]   = z4;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Fixture orientation — unchanged, no allocations removed here because
    //  PoseStack.mulPose already pools internally in Minecraft.
    // ══════════════════════════════════════════════════════════════════════════

    private static void applyFixtureOrientation(PoseStack ps, Direction facing,
                                                boolean isFlipped, BlockState blockState,
                                                boolean isHanging, float panDeg, float tiltDeg,
                                                float[] pans, float[] tilts,
                                                float[] structuralTransform) {
        ps.translate(0.5f, 0f, 0.5f);
        if (isHanging) {
            Direction hangDir = Direction.UP;
            try { hangDir = blockState.getValue(HangableBlock.HANG_DIRECTION); } catch (Exception ignored) {}
            ps.translate(0, 0.5, 0);
            if (hangDir.getAxis() != Direction.Axis.Y) {
                if (hangDir.getAxis() == Direction.Axis.Z) {
                    ps.mulPose(Axis.ZP.rotationDegrees(90));
                    ps.mulPose(hangDir == Direction.SOUTH
                            ? Axis.XP.rotationDegrees(-90)
                            : Axis.XP.rotationDegrees(90));
                } else {
                    ps.mulPose(Axis.ZN.rotationDegrees(-90));
                }
            }
            ps.translate(0, -0.5, 0);
        }
        ps.mulPose(Axis.YP.rotationDegrees(facing.toYRot()));
        ps.translate(-0.5f, 0f, -0.5f);
        if (isHanging) {
            ps.translate(structuralTransform[0], structuralTransform[1], structuralTransform[2]);
            ps.translate(0, -0.08, 0);
        }
        if (isFlipped) {
            ps.translate(0.5f, 0.5f, 0.5f);
            ps.mulPose(Axis.ZP.rotationDegrees(180));
            ps.translate(-0.5f, -0.5f, -0.5f);
        }
        ps.translate(pans[0], pans[1], pans[2]);
        ps.mulPose(Axis.YP.rotationDegrees(panDeg));
        ps.translate(-pans[0], -pans[1], -pans[2]);
        ps.translate(tilts[0], tilts[1], tilts[2]);
        ps.mulPose(isFlipped ? Axis.XP.rotationDegrees(-180) : Axis.XP.rotationDegrees(180));
        ps.mulPose(Axis.XP.rotationDegrees(tiltDeg));
        ps.translate(-tilts[0], -tilts[1], -tilts[2]);
    }
}