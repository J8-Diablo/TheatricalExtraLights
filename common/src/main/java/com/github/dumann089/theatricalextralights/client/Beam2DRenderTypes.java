package com.github.dumann089.theatricalextralights.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

import static net.minecraft.client.renderer.RenderStateShard.*;

public class Beam2DRenderTypes {

    public static final RenderType FADER = RenderType.create(
            "Fader",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            256,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorShader))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .createCompositeState(false)
    );

    public static final RenderType BEAM = RenderType.create(
            "Beam",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            256,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorShader))
                    .setTransparencyState(RenderStateShard.LIGHTNING_TRANSPARENCY)
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .createCompositeState(false)
    );
}