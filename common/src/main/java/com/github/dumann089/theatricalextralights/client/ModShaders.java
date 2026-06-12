package com.github.dumann089.theatricalextralights.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ModShaders {

    public static ShaderInstance goboProjectorShader;
    public static ShaderInstance volumetricBeamShader;

    public static float configDensity = 0.15f;
    public static float configMaxAlpha = 0.25f;
    public static float currentFixtureIntensity = 1.0f;

    public static final RenderStateShard.ShaderStateShard GOBO_SHADER_STATE =
            new RenderStateShard.ShaderStateShard(() -> goboProjectorShader);

    public static final RenderStateShard.ShaderStateShard VOLUMETRIC_SHADER_STATE =
            new RenderStateShard.ShaderStateShard(() -> volumetricBeamShader);

    private static final Map<ResourceLocation, RenderType> RENDER_TYPE_CACHE = new HashMap<>();
    private static final Map<ResourceLocation, RenderType> VOLUMETRIC_TYPE_CACHE = new HashMap<>();

    public static RenderType getGoboRenderType(ResourceLocation texture) {
        return RENDER_TYPE_CACHE.computeIfAbsent(texture, tex -> {
            RenderType.CompositeState state = RenderType.CompositeState.builder()
                    .setShaderState(GOBO_SHADER_STATE)
                    .setTextureState(new RenderStateShard.TextureStateShard(tex, false, false))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setLightmapState(RenderStateShard.NO_LIGHTMAP)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .createCompositeState(false);

            return RenderType.create("gobo_decal", DefaultVertexFormat.POSITION_COLOR_TEX,
                    VertexFormat.Mode.QUADS, 256, false, true, state);
        });
    }

    public static RenderType getVolumetricRenderType(ResourceLocation texture) {
        return VOLUMETRIC_TYPE_CACHE.computeIfAbsent(texture, tex -> {
            RenderType.CompositeState state = RenderType.CompositeState.builder()
                    .setShaderState(VOLUMETRIC_SHADER_STATE)
                    .setTextureState(new RenderStateShard.TextureStateShard(tex, false, false))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(new RenderStateShard.DepthTestStateShard("lequal_depth", 515))
                    .setCullState(new RenderStateShard.CullStateShard(false)) // Sin culling
                    .setWriteMaskState(new RenderStateShard.WriteMaskStateShard(true, false)) // Evita ocultamiento de capas Z
                    .createCompositeState(false);

            return RenderType.create("volumetric_beam", DefaultVertexFormat.POSITION_COLOR_TEX,
                    VertexFormat.Mode.QUADS, 256, false, true, state);
        });
    }
}