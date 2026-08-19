package com.github.dumann089.theatricalextralights.client.render.beam.raymarch;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import net.minecraft.client.Minecraft;

/**
 * One depth-buffer snapshot per frame for screen-space raymarch occlusion.
 */
public final class SceneDepthCopy {

    private static TextureTarget depthCopy;
    private static boolean capturedThisFrame;
    private static int lastWidth = -1;
    private static int lastHeight = -1;

    private SceneDepthCopy() {
    }

    public static void beginFrame() {
        capturedThisFrame = false;
    }

    public static void capture() {
        if (capturedThisFrame) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        RenderTarget main = mc.getMainRenderTarget();
        ensureSize(main.width, main.height);
        depthCopy.copyDepthFrom(main);
        main.bindWrite(false);
        capturedThisFrame = true;
    }

    public static int getDepthTextureId() {
        if (depthCopy == null) {
            return 0;
        }
        return depthCopy.getDepthTextureId();
    }

    public static boolean hasDepth() {
        return depthCopy != null && capturedThisFrame;
    }

    private static void ensureSize(int width, int height) {
        if (depthCopy == null || lastWidth != width || lastHeight != height) {
            if (depthCopy != null) {
                depthCopy.destroyBuffers();
            }
            depthCopy = new TextureTarget(width, height, true, Minecraft.ON_OSX);
            depthCopy.setClearColor(0f, 0f, 0f, 0f);
            lastWidth = width;
            lastHeight = height;
        }
    }
}
