package com.github.dumann089.theatricalextralights.client.blockentities;

import com.github.dumann089.theatricalextralights.client.LensRenderTypes;
import com.github.dumann089.theatricalextralights.config.TheatricalExtraLightsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.imabad.theatrical.blockentities.light.BaseLightBlockEntity;
import dev.imabad.theatrical.client.blockentities.FixtureRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public abstract class ExtraLightsFixtureRenderer<T extends BaseLightBlockEntity> extends FixtureRenderer<T> {

    public ExtraLightsFixtureRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    protected void renderLightBeam2D(VertexConsumer builder, PoseStack stack, T tileEntityFixture, Camera camera, float alpha, float beamSize, float length, int color, float focusMultiplier) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        float intensity = 0.45f;
        int a = (int) (alpha * 255 * intensity);

        length += 2.5f;
        float endSize = beamSize + (tileEntityFixture.getFocus() * focusMultiplier);

        stack.pushPose();

        Matrix4f inverseMatrix = new Matrix4f(stack.last().pose()).invert();
        org.joml.Vector4f toCameraLocal = inverseMatrix.transform(
                new org.joml.Vector4f(0, 0, 0, 1)
        );

        float angle = 0;
        if (Math.abs(toCameraLocal.x) > 0.001f || Math.abs(toCameraLocal.y) > 0.001f) {
            angle = (float) Math.atan2(toCameraLocal.y, toCameraLocal.x);
        }

        stack.mulPose(new org.joml.Quaternionf().rotateZ(angle - (float)(Math.PI / 2)));

        Matrix4f m = stack.last().pose();
        Matrix3f normal = stack.last().normal();

        addVertex(builder, m, normal, r, g, b, a, -beamSize,  0,  0);
        addVertex(builder, m, normal, r, g, b, a,  beamSize,  0,  0);
        addVertex(builder, m, normal, r, g, b, 0,  endSize,   0, -length); // Alpha 0
        addVertex(builder, m, normal, r, g, b, 0, -endSize,   0, -length); // Alpha 0

        addVertex(builder, m, normal, r, g, b, 0, -endSize,   0, -length);
        addVertex(builder, m, normal, r, g, b, 0,  endSize,   0, -length);
        addVertex(builder, m, normal, r, g, b, a,  beamSize,  0,  0);
        addVertex(builder, m, normal, r, g, b, a, -beamSize,  0,  0);

        stack.popPose();
    }
    /**
     * @param beamCount
     * @param spreadAngle
     */
    protected void renderGoboBeams(
            VertexConsumer builder, PoseStack stack,
            T tileEntityFixture, Camera camera,
            float alpha, float beamSize, float length, int color,
            float focusMultiplier, int beamCount, float spreadAngle) {

        float goboBeamSize = beamSize * 0.4f;

        for (int i = 0; i < beamCount; i++) {
            float baseAngle = ((float) i / beamCount) * (float)(Math.PI * 2);

            stack.pushPose();
            stack.mulPose(new org.joml.Quaternionf().rotateZ(baseAngle));
            stack.mulPose(new org.joml.Quaternionf().rotateX(spreadAngle));

            renderLightBeam2D(builder, stack, tileEntityFixture, camera,
                    alpha, goboBeamSize, length, color, focusMultiplier);

            stack.popPose();
        }
    }

    protected void renderLens(MultiBufferSource multiBufferSource, PoseStack poseStack, float alpha, int color, float size, float translateX, float translateY, float translateZ) {
        if (!TheatricalExtraLightsConfig.shouldRenderLens()) return;

        VertexConsumer lensConsumer = multiBufferSource.getBuffer(LensRenderTypes.LENS);

        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (int) (alpha * 255);

        float lensAlphaMul = 0.90f;
        float lensColorMul = 0.90f;

        int la = (int)(a * lensAlphaMul);
        int lr = (int)(r * lensColorMul);
        int lg = (int)(g * lensColorMul);
        int lb = (int)(b * lensColorMul);

        poseStack.pushPose();
        poseStack.translate(translateX, translateY, translateZ);

        Matrix4f m1 = poseStack.last().pose();

        addLensVertex(lensConsumer, m1, lr, lg, lb, la, -size,  size, 0f, 0f, 0f);
        addLensVertex(lensConsumer, m1, lr, lg, lb, la,  size,  size, 0f, 1f, 0f);
        addLensVertex(lensConsumer, m1, lr, lg, lb, la,  size, -size, 0f, 1f, 1f);
        addLensVertex(lensConsumer, m1, lr, lg, lb, la, -size, -size, 0f, 0f, 1f);

        poseStack.popPose();
    }

    private static void addLensVertex(VertexConsumer vc, Matrix4f m, int r, int g, int b, int a, float x, float y, float z, float u, float v) {
        vc.vertex(m, x, y, z).color(r, g, b, a).uv(u, v).endVertex();
    }

    protected void renderLensGlow(VertexConsumer builder, PoseStack stack, int color, float size) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = 255;

        Matrix4f m = stack.last().pose();
        Matrix3f normal = stack.last().normal();

        addVertex(builder, m, normal, r, g, b, a, -size,  size, 0f);
        addVertex(builder, m, normal, r, g, b, a,  size,  size, 0f);
        addVertex(builder, m, normal, r, g, b, a,  size, -size, 0f);
        addVertex(builder, m, normal, r, g, b, a, -size, -size, 0f);
    }
}
