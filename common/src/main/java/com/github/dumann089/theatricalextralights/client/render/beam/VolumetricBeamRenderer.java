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
    private static final int SLICE_MULTIPLIER = 2;

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

        int currentHash = 1;
        currentHash = 31 * currentHash + data.generateStateHash(dynamicSlices);
        currentHash = 31 * currentHash + Float.floatToIntBits(scanLen);
        currentHash = 31 * currentHash + Float.floatToIntBits(density);
        currentHash = 31 * currentHash + Float.floatToIntBits(maxAlpha);
        currentHash = 31 * currentHash + Float.floatToIntBits(fadeLen);
        currentHash = 31 * currentHash + Float.floatToIntBits(TheatricalExtraLightsConfig.getVolumetricBeamBrightness());
        currentHash = 31 * currentHash + dynamicSlices;
        currentHash = 31 * currentHash + (hitBlock ? 1231 : 1237);

        if (currentHash != cachedHashSlots[slot]) {
            rebuildGeometry(slot, data, dynamicSlices, scanLen, density, maxAlpha, fadeLen, hitBlock);
            cachedHashSlots[slot] = currentHash;
        }

        this.beamR[slot] = (data.color() >> 16) & 0xFF;
        this.beamG[slot] = (data.color() >> 8)  & 0xFF;
        this.beamB[slot] =  data.color()         & 0xFF;

        float rawIntensity = Math.min(data.intensity() * TheatricalExtraLightsConfig.getVolumetricBeamBrightness(), 1.0f);
        this.beamAlphaScale[slot] = (float) Math.pow(rawIntensity, 0.5);

        if (IrisCompat.isShadersActive()) {
            this.beamRenderTypes[slot] = ModShaders.getVolumetricFallbackRenderType(data.goboTexture());
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
                    boolean sVisible = (sDot >= -4.0 && sDot < 0) || (sDot >= 0 && (sDot * sDot) >= sDistSq * 0.05);

                    int lastOffset = (quadCount - 1) * 24;
                    double edx = (blockX + verts[lastOffset]) - camX;
                    double edy = (blockY + verts[lastOffset+1]) - camY;
                    double edz = (blockZ + verts[lastOffset+2]) - camZ;
                    double eDot = edx*lx + edy*ly + edz*lz;
                    double eDistSq = edx*edx + edy*edy + edz*edz;
                    boolean eVisible = (eDot >= -4.0 && eDot < 0) || (eDot >= 0 && (eDot * eDot) >= eDistSq * 0.05);

                    if (!sVisible && !eVisible) continue;

                    int r  = beamR[k];
                    int g  = beamG[k];
                    int bl = beamB[k];
                    float alphaScale = beamAlphaScale[k];

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

                        int alphaInt = Math.min((int)(finalAlpha * 255.0f), 255);

                        for (int v = 0; v < 4; v++) {
                            int vOffset = offsetVert + (v * 6);
                            vc.vertex(mat, verts[vOffset], verts[vOffset + 1], verts[vOffset + 2])
                                    .color(r, g, bl, alphaInt)
                                    .uv(verts[vOffset + 3], verts[vOffset + 4])
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

    private void rebuildGeometry(int slot, BeamRenderData data, int slices, float scanLen,
                                 float density, float maxAlpha, float fadeLen, boolean hitBlock) {

        int totalSlices = slices * SLICE_MULTIPLIER;
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