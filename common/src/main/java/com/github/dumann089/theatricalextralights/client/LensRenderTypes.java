package com.github.dumann089.theatricalextralights.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class LensRenderTypes {

    public static final RenderType LENS = RenderType.create(
            "Lens",
            DefaultVertexFormat.POSITION_COLOR_TEX,
            VertexFormat.Mode.QUADS,
            256,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(
                            GameRenderer::getPositionColorTexShader
                    ))
                    .setTextureState(new RenderStateShard.TextureStateShard(
                            new ResourceLocation(
                                    "theatricalextralights",
                                    "textures/misc/lens.png"
                            ),
                            false,
                            false
                    ))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .createCompositeState(false));

    public static final RenderType GLOW = RenderType.create(
            "Glow",
            DefaultVertexFormat.POSITION_COLOR_TEX,
            VertexFormat.Mode.QUADS,
            256,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(
                            GameRenderer::getPositionColorTexShader
                    ))
                    .setTextureState(new RenderStateShard.TextureStateShard(
                            new ResourceLocation(
                                    "theatricalextralights",
                                    "textures/misc/glow.png"
                            ),
                            true,
                            false
                    ))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .createCompositeState(false)
    );
}