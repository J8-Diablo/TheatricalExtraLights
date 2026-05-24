package com.github.dumann089.theatricalextralights.client.blockentities;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.concurrent.ConcurrentHashMap;

public class GoboRenderType extends RenderType {

    private static final ConcurrentHashMap<ResourceLocation, RenderType> CACHE =
            new ConcurrentHashMap<>();

    private static final class ClampedTextureShard extends RenderStateShard.TextureStateShard {

        ClampedTextureShard(ResourceLocation loc) {
            super(loc, false, false);
        }

        @Override
        public void setupRenderState() {
            super.setupRenderState();

            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

            GlStateManager._texParameter(
                    GL11.GL_TEXTURE_2D,
                    GL11.GL_TEXTURE_WRAP_S,
                    GL12.GL_CLAMP_TO_EDGE
            );

            GlStateManager._texParameter(
                    GL11.GL_TEXTURE_2D,
                    GL11.GL_TEXTURE_WRAP_T,
                    GL12.GL_CLAMP_TO_EDGE
            );
        }
    }

    public static RenderType goboDecal(ResourceLocation texture) {
        return CACHE.computeIfAbsent(texture, GoboRenderType::build);
    }

    private static RenderType build(ResourceLocation texture) {

        return RenderType.create(
                "gobo_decal_" + texture.getPath(),
                DefaultVertexFormat.POSITION_COLOR_TEX,
                VertexFormat.Mode.QUADS,
                1024,
                false,
                false,
                RenderType.CompositeState.builder()
                        .setShaderState(new ShaderStateShard(
                                GameRenderer::getPositionColorTexShader))
                        .setTextureState(new ClampedTextureShard(texture))
                        .setTransparencyState(LIGHTNING_TRANSPARENCY)
                        .setDepthTestState(LEQUAL_DEPTH_TEST)
                        .setCullState(NO_CULL)
                        .setWriteMaskState(COLOR_WRITE)
                        .createCompositeState(false)
        );
    }

    public static void invalidateCache() {
        CACHE.clear();
    }

    private GoboRenderType(
            String name,
            VertexFormat format,
            VertexFormat.Mode mode,
            int bufferSize,
            boolean affectsCrumbling,
            boolean sortOnUpload,
            Runnable setupState,
            Runnable clearState
    ) {
        super(
                name,
                format,
                mode,
                bufferSize,
                affectsCrumbling,
                sortOnUpload,
                setupState,
                clearState
        );
    }
}