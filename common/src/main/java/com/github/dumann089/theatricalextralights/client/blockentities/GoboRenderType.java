package com.github.dumann089.theatricalextralights.client.blockentities;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class GoboRenderType extends RenderType {

    public GoboRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

    public static RenderType goboDecal(ResourceLocation textureLocation) {
        return create("gobo_decal",
                DefaultVertexFormat.POSITION_COLOR_LIGHTMAP,
                VertexFormat.Mode.QUADS,
                256,
                false,
                true,
                CompositeState.builder()
                        .setShaderState(RENDERTYPE_TRANSLUCENT_SHADER)
                        // EXTREMADAMENTE IMPORTANTE: El 3er parámetro (true) fuerza el clamping nativo en Vanilla/Sodium de forma segura
                        .setTextureState(new RenderStateShard.TextureStateShard(textureLocation, false, true))
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setDepthTestState(LEQUAL_DEPTH_TEST)
                        .setCullState(NO_CULL)
                        .setWriteMaskState(COLOR_WRITE)
                        .createCompositeState(false)
        );
    }
}