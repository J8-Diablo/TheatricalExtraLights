package com.github.dumann089.theatricalextralights.client.render.beam;

import com.github.dumann089.theatricalextralights.config.TheatricalExtraLightsConfig;
import com.github.dumann089.theatricalextralights.client.IrisCompat;
import com.github.dumann089.theatricalextralights.client.ModShaders;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.imabad.theatrical.client.LazyRenderers;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class VolumetricBeamRenderer extends LazyRenderers.LazyRenderer {

    private static final int MAX_BEAMS_PER_FIXTURE = 32;
    /** Vanilla volumetric shader fills density; Iris fallback only draws shell quads. */
    private static final int SLICE_MULTIPLIER = 2;
    /** Length subdivisions for Iris shell geometry (4 walls × N) — not disc slices. */
    private static final int SHELL_SEGMENTS_SHADERS = 24;
    /** cos² FOV threshold — lower = wider (shader fallback needs wider or horizon beams vanish). */
    private static final double BEAM_FOV_COS2 = 0.05;
    private static final double BEAM_FOV_COS2_SHADERS = 0.0004; // ~almost full sphere
    /** Flat albedo so gobo UV noise doesn't print a mesh into the shaft under Complementary. */
    private static final net.minecraft.resources.ResourceLocation SHADER_BEAM_TEXTURE =
            new net.minecraft.resources.ResourceLocation("minecraft", "textures/block/white_concrete.png");

    private final float[][] cachedVertsSlots = new float[MAX_BEAMS_PER_FIXTURE][16384];
    private final int[] cachedQuadCountSlots = new int[MAX_BEAMS_PER_FIXTURE];
    private final int[] cachedHashSlots = new int[MAX_BEAMS_PER_FIXTURE];

    private final int[] beamR = new int[MAX_BEAMS_PER_FIXTURE];
    private final int[] beamG = new int[MAX_BEAMS_PER_FIXTURE];
    private final int[] beamB = new int[MAX_BEAMS_PER_FIXTURE];
    private final float[] beamAlphaScale = new float[MAX_BEAMS_PER_FIXTURE];
    private final RenderType[] beamRenderTypes = new RenderType[MAX_BEAMS_PER_FIXTURE];

    private final boolean[] processedSlots = new boolean[MAX_BEAMS_PER_FIXTURE];

    private int activeBeamCount = 0;
    private net.minecraft.core.BlockPos currentPos;

    public void render(BeamRenderData data, PoseStack poseStack) {
        if (!TheatricalExtraLightsConfig.isVolumetricBeamEnabled() || data.intensity() <= 0.0f) return;

        if (this.activeBeamCount == 0) {
            this.currentPos = data.fixturePos();
            LazyRenderers.addLazyRender(this);
        }

        int slot = this.activeBeamCount;
        if (slot >= MAX_BEAMS_PER_FIXTURE) return;

        int baseSlices = TheatricalExtraLightsConfig.getVolumetricBeamSlices();
        float maxDist = TheatricalExtraLightsConfig.getVolumetricBeamDistance();

        boolean hitBlock = data.scanLen() < maxDist;
        float scanLen = hitBlock ? data.scanLen() + 2.5f : maxDist;
        if (scanLen <= 0.0f) return;

        float slicesPerMeter = (float) baseSlices / maxDist;
        int dynamicSlices = Math.round(scanLen * slicesPerMeter);
        dynamicSlices = Math.max(32, Math.min(baseSlices, dynamicSlices));

        float density  = TheatricalExtraLightsConfig.getVolumetricBeamDensity();
        float maxAlpha = TheatricalExtraLightsConfig.getVolumetricBeamMaxAlpha();
        float fadeLen  = TheatricalExtraLightsConfig.getVolumetricBeamFadeLength();
        boolean shaders = IrisCompat.isShadersActive();
        int sliceMul = shaders ? 1 : SLICE_MULTIPLIER;

        int currentHash = 1;
        currentHash = 31 * currentHash + data.generateStateHash(dynamicSlices);
        currentHash = 31 * currentHash + Float.floatToIntBits(scanLen);
        currentHash = 31 * currentHash + Float.floatToIntBits(density);
        currentHash = 31 * currentHash + Float.floatToIntBits(maxAlpha);
        currentHash = 31 * currentHash + Float.floatToIntBits(fadeLen);
        currentHash = 31 * currentHash + Float.floatToIntBits(TheatricalExtraLightsConfig.getVolumetricBeamBrightness());
        currentHash = 31 * currentHash + dynamicSlices;
        currentHash = 31 * currentHash + sliceMul;
        currentHash = 31 * currentHash + (hitBlock ? 1231 : 1237);
        currentHash = 31 * currentHash + (shaders ? 2 : 0); // 2 = shell geometry

        if (currentHash != cachedHashSlots[slot]) {
            if (shaders) {
                rebuildShellGeometry(slot, data, scanLen, density, maxAlpha, fadeLen, hitBlock);
            } else {
                rebuildGeometry(slot, data, dynamicSlices, scanLen, density, maxAlpha, fadeLen, hitBlock, sliceMul);
            }
            cachedHashSlots[slot] = currentHash;
        }

        this.beamR[slot] = (data.color() >> 16) & 0xFF;
        this.beamG[slot] = (data.color() >> 8)  & 0xFF;
        this.beamB[slot] =  data.color()         & 0xFF;

        float rawIntensity = Math.min(data.intensity() * TheatricalExtraLightsConfig.getVolumetricBeamBrightness(), 1.0f);
        // Complementary/IPBR punches beaconbeam emission — keep shaft softer
        this.beamAlphaScale[slot] = (float) Math.pow(rawIntensity, 0.5) * (shaders ? 0.55f : 1.0f);

        if (shaders) {
            this.beamRenderTypes[slot] = ModShaders.getVolumetricFallbackRenderType(SHADER_BEAM_TEXTURE);
        } else {
            this.beamRenderTypes[slot] = ModShaders.getVolumetricRenderType(data.goboTexture());
        }
        this.activeBeamCount++;
    }

    @Override
    public void render(MultiBufferSource.BufferSource bufferSource, PoseStack ps, Camera camera, float partialTick) {
        if (activeBeamCount == 0) return;

        final double camX = camera.getPosition().x;
        final double camY = camera.getPosition().y;
        final double camZ = camera.getPosition().z;
        final org.joml.Vector3f look = camera.getLookVector();
        final float lx = look.x();
        final float ly = look.y();
        final float lz = look.z();
        final double fovCos2 = IrisCompat.isShadersActive() ? BEAM_FOV_COS2_SHADERS : BEAM_FOV_COS2;

        final double blockX = this.currentPos.getX();
        final double blockY = this.currentPos.getY();
        final double blockZ = this.currentPos.getZ();

        ps.pushPose();
        Vec3 offset = Vec3.atLowerCornerOf(this.currentPos).subtract(camera.getPosition());
        ps.translate(offset.x, offset.y, offset.z);

        Matrix4f mat = ps.last().pose();

        java.util.Arrays.fill(this.processedSlots, 0, this.activeBeamCount, false);

        for (int b = 0; b < activeBeamCount; b++) {
            if (processedSlots[b]) continue;

            RenderType targetRenderType = beamRenderTypes[b];
            VertexConsumer vc = bufferSource.getBuffer(targetRenderType);

            for (int k = b; k < activeBeamCount; k++) {
                if (!processedSlots[k] && beamRenderTypes[k] == targetRenderType) {
                    processedSlots[k] = true;

                    int quadCount = cachedQuadCountSlots[k];
                    if (quadCount == 0) continue;

                    float[] verts = cachedVertsSlots[k];

                    double sdx = (blockX + verts[0]) - camX;
                    double sdy = (blockY + verts[1]) - camY;
                    double sdz = (blockZ + verts[2]) - camZ;
                    double sDot = sdx*lx + sdy*ly + sdz*lz;
                    double sDistSq = sdx*sdx + sdy*sdy + sdz*sdz;
                    boolean sVisible = (sDot >= -8.0 && sDot < 0) || (sDot >= 0 && (sDot * sDot) >= sDistSq * fovCos2);

                    int lastOffset = (quadCount - 1) * 24;
                    double edx = (blockX + verts[lastOffset]) - camX;
                    double edy = (blockY + verts[lastOffset+1]) - camY;
                    double edz = (blockZ + verts[lastOffset+2]) - camZ;
                    double eDot = edx*lx + edy*ly + edz*lz;
                    double eDistSq = edx*edx + edy*edy + edz*edz;
                    boolean eVisible = (eDot >= -8.0 && eDot < 0) || (eDot >= 0 && (eDot * eDot) >= eDistSq * fovCos2);

                    if (!sVisible && !eVisible) continue;

                    int r  = beamR[k];
                    int g  = beamG[k];
                    int bl = beamB[k];
                    float alphaScale = beamAlphaScale[k];
                    boolean shellSoft = IrisCompat.isShadersActive();

                    for (int i = 0; i < quadCount; i++) {
                        int offsetVert = i * 24;

                        double sliceX = blockX + verts[offsetVert];
                        double sliceY = blockY + verts[offsetVert + 1];
                        double sliceZ = blockZ + verts[offsetVert + 2];

                        double dx = sliceX - camX;
                        double dy = sliceY - camY;
                        double dz = sliceZ - camZ;

                        if (dx*lx + dy*ly + dz*lz < -0.4) continue;

                        double dSq = dx*dx + dy*dy + dz*dz;
                        float proximityFactor = 1.0f;

                        if (dSq < 25.0) {
                            if (dSq <= 2.25) continue;
                            proximityFactor = (float) ((Math.sqrt(dSq) - 1.5) / 3.5);
                        }

                        float baseAlpha = verts[offsetVert + 5];
                        float finalAlpha = baseAlpha * proximityFactor * alphaScale;

                        if (finalAlpha <= 0.001f) continue;

                        for (int v = 0; v < 4; v++) {
                            int vOffset = offsetVert + (v * 6);
                            float u = verts[vOffset + 3];
                            float vv = verts[vOffset + 4];
                            float soft = 1.0f;
                            if (shellSoft) {
                                // Soften wall seams (U) + lengthwise banding (V)
                                float across = u * 2.0f - 1.0f;
                                soft = (float) Math.exp(-across * across * 2.2)
                                        * (0.75f + 0.25f * (float) Math.sin(Math.PI * vv));
                            }
                            int alphaInt = Math.min((int) (finalAlpha * soft * 255.0f), 255);
                            if (alphaInt <= 1) {
                                continue;
                            }
                            vc.vertex(mat, verts[vOffset], verts[vOffset + 1], verts[vOffset + 2])
                                    .color(r, g, bl, alphaInt)
                                    .uv(u, vv)
                                    .endVertex();
                        }
                    }
                }
            }
        }

        ps.popPose();
        this.activeBeamCount = 0;
    }

    @Override
    public Vec3 getPos(float partialTick) {
        return this.currentPos != null ? Vec3.atCenterOf(this.currentPos) : Vec3.ZERO;
    }

    /**
     * Iris/Complementary path: 4 longitudinal walls (frustum shell).
     * The old single diagonal quad per slice cut through the volume and looked like a cross mesh.
     */
    private void rebuildShellGeometry(int slot, BeamRenderData data, float scanLen,
                                      float density, float maxAlpha, float fadeLen, boolean hitBlock) {
        int segments = SHELL_SEGMENTS_SHADERS;
        int requiredSize = segments * 4 * 4 * 6; // segments × 4 walls × 4 verts × 6 floats
        if (cachedVertsSlots[slot].length < requiredSize) {
            cachedVertsSlots[slot] = new float[requiredSize + 512];
        }
        cachedQuadCountSlots[slot] = 0;

        double ox = data.origin().x, oy = data.origin().y, oz = data.origin().z;
        double bx = data.beamDir().x, by = data.beamDir().y, bz = data.beamDir().z;

        double rad = Math.toRadians(-data.goboRotation());
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        double ux = (data.axisU().x * cos - data.axisV().x * sin);
        double uy = (data.axisU().y * cos - data.axisV().y * sin);
        double uz = (data.axisU().z * cos - data.axisV().z * sin);

        double vx = (data.axisU().x * sin + data.axisV().x * cos);
        double vy = (data.axisU().y * sin + data.axisV().y * cos);
        double vz = (data.axisU().z * sin + data.axisV().z * cos);

        float[] verts = cachedVertsSlots[slot];
        int idx = 0;
        float alphaFactor = 1.35f / segments;

        for (int i = 0; i < segments; i++) {
            float tCurr = (float) i / segments;
            float tNext = (float) (i + 1) / segments;
            float distCurr = tCurr * scanLen;
            float distNext = tNext * scanLen;

            float alphaC = shellAlpha(distCurr, scanLen, density, maxAlpha, fadeLen, hitBlock, alphaFactor);
            float alphaN = shellAlpha(distNext, scanLen, density, maxAlpha, fadeLen, hitBlock, alphaFactor);
            if (alphaC <= 0.001f && alphaN <= 0.001f) {
                continue;
            }

            float radiusC = (data.tanHalfAngle() < 0.001f)
                    ? data.baseRadius()
                    : Math.max(data.baseRadius(), distCurr * data.tanHalfAngle());
            float radiusN = (data.tanHalfAngle() < 0.001f)
                    ? data.baseRadius()
                    : Math.max(data.baseRadius(), distNext * data.tanHalfAngle());
            float rwC = radiusC * data.widthScale();
            float rhC = radiusC * data.heightScale();
            float rwN = radiusN * data.widthScale();
            float rhN = radiusN * data.heightScale();

            double cxC = ox + bx * distCurr, cyC = oy + by * distCurr, czC = oz + bz * distCurr;
            double cxN = ox + bx * distNext, cyN = oy + by * distNext, czN = oz + bz * distNext;

            // Rectangle corners: 0=-- 1=+- 2=++ 3=-+  (u,v)
            double[][] c = {
                    {cxC - ux * rwC - vx * rhC, cyC - uy * rwC - vy * rhC, czC - uz * rwC - vz * rhC},
                    {cxC + ux * rwC - vx * rhC, cyC + uy * rwC - vy * rhC, czC + uz * rwC - vz * rhC},
                    {cxC + ux * rwC + vx * rhC, cyC + uy * rwC + vy * rhC, czC + uz * rwC + vz * rhC},
                    {cxC - ux * rwC + vx * rhC, cyC - uy * rwC + vy * rhC, czC - uz * rwC + vz * rhC},
            };
            double[][] n = {
                    {cxN - ux * rwN - vx * rhN, cyN - uy * rwN - vy * rhN, czN - uz * rwN - vz * rhN},
                    {cxN + ux * rwN - vx * rhN, cyN + uy * rwN - vy * rhN, czN + uz * rwN - vz * rhN},
                    {cxN + ux * rwN + vx * rhN, cyN + uy * rwN + vy * rhN, czN + uz * rwN + vz * rhN},
                    {cxN - ux * rwN + vx * rhN, cyN - uy * rwN + vy * rhN, czN - uz * rwN + vz * rhN},
            };

            for (int face = 0; face < 4; face++) {
                int a = face;
                int b = (face + 1) & 3;
                idx = putVert(verts, idx, c[a][0], c[a][1], c[a][2], 0.0f, tCurr, alphaC);
                idx = putVert(verts, idx, c[b][0], c[b][1], c[b][2], 1.0f, tCurr, alphaC);
                idx = putVert(verts, idx, n[b][0], n[b][1], n[b][2], 1.0f, tNext, alphaN);
                idx = putVert(verts, idx, n[a][0], n[a][1], n[a][2], 0.0f, tNext, alphaN);
                cachedQuadCountSlots[slot]++;
            }
        }
    }

    private static float shellAlpha(float dist, float scanLen, float density, float maxAlpha,
                                    float fadeLen, boolean hitBlock, float alphaFactor) {
        float alpha = (float) Math.exp(-(dist / scanLen) * density) * maxAlpha * alphaFactor;
        if (fadeLen > 0.0f && !hitBlock) {
            float distanceLeft = scanLen - dist;
            if (distanceLeft < fadeLen) {
                float fadeRatio = distanceLeft / fadeLen;
                alpha *= fadeRatio * fadeRatio * fadeRatio;
            }
        }
        return alpha;
    }

    private static int putVert(float[] verts, int idx, double x, double y, double z,
                               float u, float v, float alpha) {
        verts[idx++] = (float) x;
        verts[idx++] = (float) y;
        verts[idx++] = (float) z;
        verts[idx++] = u;
        verts[idx++] = v;
        verts[idx++] = alpha;
        return idx;
    }

    private void rebuildGeometry(int slot, BeamRenderData data, int slices, float scanLen,
                                 float density, float maxAlpha, float fadeLen, boolean hitBlock,
                                 int sliceMul) {

        int totalSlices = slices * sliceMul;
        int totalSegments = totalSlices - 1;
        int requiredSize = totalSegments * 4 * 6;

        if (cachedVertsSlots[slot].length < requiredSize) {
            cachedVertsSlots[slot] = new float[requiredSize + 512];
        }
        cachedQuadCountSlots[slot] = 0;

        double ox = data.origin().x, oy = data.origin().y, oz = data.origin().z;
        double bx = data.beamDir().x, by = data.beamDir().y, bz = data.beamDir().z;

        double rad = Math.toRadians(-data.goboRotation());
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        double ux = (data.axisU().x * cos - data.axisV().x * sin);
        double uy = (data.axisU().y * cos - data.axisV().y * sin);
        double uz = (data.axisU().z * cos - data.axisV().z * sin);

        double vx = (data.axisU().x * sin + data.axisV().x * cos);
        double vy = (data.axisU().y * sin + data.axisV().y * cos);
        double vz = (data.axisU().z * sin + data.axisV().z * cos);

        float[] verts = cachedVertsSlots[slot];
        int idx = 0;

        float originalStep = scanLen / (slices - 1);
        float uniformStep = scanLen / (totalSlices - 1);
        float alphaFactor = uniformStep / originalStep;

        for (int i = 0; i < totalSegments; i++) {
            float tCurr = (float) i / (totalSlices - 1);
            float distCurr = tCurr * scanLen;

            float tNext = (float) (i + 1) / (totalSlices - 1);
            float distNext = tNext * scanLen;

            double cxC = ox + bx * distCurr;
            double cyC = oy + by * distCurr;
            double czC = oz + bz * distCurr;

            float sliceAlphaC = (float) Math.exp(-(distCurr / scanLen) * density) * maxAlpha * alphaFactor;
            if (fadeLen > 0.0f && !hitBlock) {
                float distanceLeft = scanLen - distCurr;
                if (distanceLeft < fadeLen) {
                    float fadeRatio = distanceLeft / fadeLen;
                    sliceAlphaC *= (fadeRatio * fadeRatio * fadeRatio);
                }
            }

            float radiusC = (data.tanHalfAngle() < 0.001f) ? data.baseRadius() : Math.max(data.baseRadius(), distCurr * data.tanHalfAngle());
            float radiusWC = radiusC * data.widthScale();
            float radiusHC = radiusC * data.heightScale();

            double cxN = ox + bx * distNext;
            double cyN = oy + by * distNext;
            double czN = oz + bz * distNext;

            float sliceAlphaN = (float) Math.exp(-(distNext / scanLen) * density) * maxAlpha * alphaFactor;
            if (fadeLen > 0.0f && !hitBlock) {
                float distanceLeft = scanLen - distNext;
                if (distanceLeft < fadeLen) {
                    float fadeRatio = distanceLeft / fadeLen;
                    sliceAlphaN *= (fadeRatio * fadeRatio * fadeRatio);
                }
            }

            float radiusN = (data.tanHalfAngle() < 0.001f) ? data.baseRadius() : Math.max(data.baseRadius(), distNext * data.tanHalfAngle());
            float radiusWN = radiusN * data.widthScale();
            float radiusHN = radiusN * data.heightScale();

            if (sliceAlphaC <= 0.001f && sliceAlphaN <= 0.001f) continue;

            // Vanilla path keeps legacy single-panel segments (custom volumetric shader fills them)
            verts[idx++] = (float)(cxC - ux*radiusWC + vx*radiusHC); verts[idx++] = (float)(cyC - uy*radiusWC + vy*radiusHC); verts[idx++] = (float)(czC - uz*radiusWC + vz*radiusHC);
            verts[idx++] = 0.0f; verts[idx++] = 0.0f; verts[idx++] = sliceAlphaC;

            verts[idx++] = (float)(cxC + ux*radiusWC + vx*radiusHC); verts[idx++] = (float)(cyC + uy*radiusWC + vy*radiusHC); verts[idx++] = (float)(czC + uz*radiusWC + vz*radiusHC);
            verts[idx++] = 1.0f; verts[idx++] = 0.0f; verts[idx++] = sliceAlphaC;

            verts[idx++] = (float)(cxN + ux*radiusWN - vx*radiusHN); verts[idx++] = (float)(cyN + uy*radiusWN - vy*radiusHN); verts[idx++] = (float)(czN + uz*radiusWN - vz*radiusHN);
            verts[idx++] = 1.0f; verts[idx++] = 1.0f; verts[idx++] = sliceAlphaN;

            verts[idx++] = (float)(cxN - ux*radiusWN - vx*radiusHN); verts[idx++] = (float)(cyN - uy*radiusWN - vy*radiusHN); verts[idx++] = (float)(czN - uz*radiusWN - vz*radiusHN);
            verts[idx++] = 0.0f; verts[idx++] = 1.0f; verts[idx++] = sliceAlphaN;

            cachedQuadCountSlots[slot]++;
        }
    }
}