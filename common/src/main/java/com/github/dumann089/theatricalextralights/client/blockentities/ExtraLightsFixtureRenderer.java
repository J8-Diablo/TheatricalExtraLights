package com.github.dumann089.theatricalextralights.client.blockentities;

import com.github.dumann089.theatricalextralights.client.Beam2DRenderTypes;
import com.github.dumann089.theatricalextralights.client.LensRenderTypes;
import com.github.dumann089.theatricalextralights.config.TheatricalExtraLightsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.imabad.theatrical.blockentities.light.BaseLightBlockEntity;
import dev.imabad.theatrical.client.blockentities.FixtureRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public abstract class ExtraLightsFixtureRenderer<T extends BaseLightBlockEntity> extends FixtureRenderer<T> {

    public ExtraLightsFixtureRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    /** Faisceau géré dans {@code beforeRenderBeam} — évite le double rendu Theatrical. */
    @Override
    public boolean shouldRenderBeam(T blockEntity) {
        return false;
    }

    // ── Vertex helpers ──────────────────────────────────────────────────────

    // BEAM_VANILLA
    private static void addVertexPC(VertexConsumer vc, Matrix4f m,
                                    int r, int g, int b, int a,
                                    float x, float y, float z) {
        vc.vertex(m, x, y, z)
                .color(r, g, b, a)
                .endVertex();
    }

     // BEAM_SHADERS
     private static void addVertexPCTL(VertexConsumer vc, Matrix4f m,
                                       int r, int g, int b, int a,
                                       float x, float y, float z) {
         vc.vertex(m, x, y, z)
                 .color(r, g, b, a)
                 .endVertex();
     }

    private static void addBeamVertex(VertexConsumer vc, Matrix4f m,
                                      int r, int g, int b, int a,
                                      float x, float y, float z) {
        if (Beam2DRenderTypes.isShadersActive()) {
            addVertexPCTL(vc, m, r, g, b, a, x, y, z);
        } else {
            addVertexPC(vc, m, r, g, b, a, x, y, z);
        }
    }

    private static void addLensVertex(VertexConsumer vc, Matrix4f m,
                                      int r, int g, int b, int a,
                                      float x, float y, float z,
                                      float u, float v) {
        vc.vertex(m, x, y, z)
                .color(r, g, b, a)
                .uv(u, v)
                .uv2(LightTexture.FULL_BRIGHT)
                .endVertex();
    }

    // ── Beam 2D ─────────────────────────────────────────────────────────────

    protected void renderLightBeam2D(VertexConsumer builder, PoseStack stack, T tileEntityFixture, Camera camera, float alpha, float beamSize, float length, int color, float focusMultiplier) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        float intensity = 0.65f;
        int a = (int) (alpha * 255 * intensity);

        length += 2.5f;
        float endSize = beamSize + (tileEntityFixture.getFocus() * focusMultiplier);

        stack.pushPose();

        Matrix4f inverseMatrix = new Matrix4f(stack.last().pose()).invert();
        org.joml.Vector4f toCameraLocal = inverseMatrix.transform(new org.joml.Vector4f(0, 0, 0, 1));

        float angle = 0;
        if (Math.abs(toCameraLocal.x) > 0.001f || Math.abs(toCameraLocal.y) > 0.001f) {
            angle = (float) Math.atan2(toCameraLocal.y, toCameraLocal.x);
        }

        stack.mulPose(new org.joml.Quaternionf().rotateZ(angle - (float)(Math.PI / 2)));

        Matrix4f m = stack.last().pose();

        // Cara frontal
        addBeamVertex(builder, m, r, g, b, a, -beamSize, 0,  0);
        addBeamVertex(builder, m, r, g, b, a,  beamSize, 0,  0);
        addBeamVertex(builder, m, r, g, b, 0,  endSize,  0, -length);
        addBeamVertex(builder, m, r, g, b, 0, -endSize,  0, -length);

        // Cara trasera
        addBeamVertex(builder, m, r, g, b, 0, -endSize,  0, -length);
        addBeamVertex(builder, m, r, g, b, 0,  endSize,  0, -length);
        addBeamVertex(builder, m, r, g, b, a,  beamSize, 0,  0);
        addBeamVertex(builder, m, r, g, b, a, -beamSize, 0,  0);

        stack.popPose();
    }

    // ── Beam 4D ─────────────────────────────────────────────────────────────

    protected void renderLightBeam4D(VertexConsumer builder, PoseStack stack, T tileEntityFixture,
                                     float partialTicks, float alpha, float beamSize,
                                     float length, int color, float focusMultiplier) {

        float endMultiplier = 1 + tileEntityFixture.getFocus() * length * focusMultiplier;

        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        float intensity = 0.65f;
        int a = (int) (alpha * 255 * intensity);

        Matrix4f m = stack.last().pose();

        length += 4.0f;
        float end = endMultiplier;

        // Cara +X
        addBeamVertex(builder, m, r, g, b, 0,  beamSize * end,  beamSize * end, -length);
        addBeamVertex(builder, m, r, g, b, a,  beamSize,        beamSize,        0);
        addBeamVertex(builder, m, r, g, b, a,  beamSize,       -beamSize,        0);
        addBeamVertex(builder, m, r, g, b, 0,  beamSize * end, -beamSize * end, -length);

        // Cara -X
        addBeamVertex(builder, m, r, g, b, 0, -beamSize * end, -beamSize * end, -length);
        addBeamVertex(builder, m, r, g, b, a, -beamSize,       -beamSize,        0);
        addBeamVertex(builder, m, r, g, b, a, -beamSize,        beamSize,        0);
        addBeamVertex(builder, m, r, g, b, 0, -beamSize * end,  beamSize * end, -length);

        // Cara +Y
        addBeamVertex(builder, m, r, g, b, 0, -beamSize * end,  beamSize * end, -length);
        addBeamVertex(builder, m, r, g, b, a, -beamSize,        beamSize,        0);
        addBeamVertex(builder, m, r, g, b, a,  beamSize,        beamSize,        0);
        addBeamVertex(builder, m, r, g, b, 0,  beamSize * end,  beamSize * end, -length);

        // Cara -Y
        addBeamVertex(builder, m, r, g, b, 0,  beamSize * end, -beamSize * end, -length);
        addBeamVertex(builder, m, r, g, b, a,  beamSize,       -beamSize,        0);
        addBeamVertex(builder, m, r, g, b, a, -beamSize,       -beamSize,        0);
        addBeamVertex(builder, m, r, g, b, 0, -beamSize * end, -beamSize * end, -length);
    }

    // ── Gobo ────────────────────────────────────────────────────────────────

    protected void renderGoboBeams(VertexConsumer builder, PoseStack stack,
                                   T tileEntityFixture, Camera camera,
                                   float alpha, float beamSize, float length, int color,
                                   float focusMultiplier, int beamCount, float spreadAngle) {

        float goboBeamSize = beamSize * 0.4f;

        for (int i = 0; i < beamCount; i++) {
            float baseAngle = ((float) i / beamCount) * (float)(Math.PI * 2);

            stack.pushPose();
            stack.mulPose(new org.joml.Quaternionf().rotateZ(baseAngle));
            stack.mulPose(new org.joml.Quaternionf().rotateX(spreadAngle));

            if (TheatricalExtraLightsConfig.shouldRender2DBeam()) {
                renderLightBeam2D(builder, stack, tileEntityFixture, camera, alpha, goboBeamSize, length, color, focusMultiplier);
            } else {
                renderLightBeam4D(builder, stack, tileEntityFixture, 0f, alpha, goboBeamSize, length, color, focusMultiplier);
            }

            stack.popPose();
        }
    }

    // ── Lens ────────────────────────────────────────────────────────────────

    protected void renderLens(MultiBufferSource multiBufferSource, PoseStack poseStack, float alpha, int color, float size, float translateX, float translateY, float translateZ) {
        if (!TheatricalExtraLightsConfig.shouldRenderLens()) return;

        VertexConsumer lensConsumer = multiBufferSource.getBuffer(LensRenderTypes.LENS);

        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (int) (alpha * 255 * 0.90f);
        int lr = (int)(r * 0.90f);
        int lg = (int)(g * 0.90f);
        int lb = (int)(b * 0.90f);

        poseStack.pushPose();
        poseStack.translate(translateX, translateY, translateZ);

        Matrix4f m1 = poseStack.last().pose();

        addLensVertex(lensConsumer, m1, lr, lg, lb, a, -size,  size, 0f, 0f, 0f);
        addLensVertex(lensConsumer, m1, lr, lg, lb, a,  size,  size, 0f, 1f, 0f);
        addLensVertex(lensConsumer, m1, lr, lg, lb, a,  size, -size, 0f, 1f, 1f);
        addLensVertex(lensConsumer, m1, lr, lg, lb, a, -size, -size, 0f, 0f, 1f);

        poseStack.popPose();
    }

    // ── Lens Glow ───────────────────────────────────────────────────────────

    protected void renderLensGlow(VertexConsumer builder, PoseStack stack, int color, float size) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        Matrix4f m = stack.last().pose();

        addBeamVertex(builder, m, r, g, b, 255, -size,  size, 0f);
        addBeamVertex(builder, m, r, g, b, 255,  size,  size, 0f);
        addBeamVertex(builder, m, r, g, b, 255,  size, -size, 0f);
        addBeamVertex(builder, m, r, g, b, 255, -size, -size, 0f);
    }
}