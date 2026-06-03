package com.github.dumann089.theatricalextralights.fabric;

import com.github.dumann089.theatricalextralights.TheatricalExtraLightsClient;
import com.github.dumann089.theatricalextralights.client.ModShaders;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.resources.ResourceLocation;

public class TheatricalExtraLightsClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Inicialización original
        TheatricalExtraLightsClient.init();

        com.github.dumann089.theatricalextralights.fabric.FollowspotCameraFabric.init();

        // Registro de los Core Shaders para la GPU
        CoreShaderRegistrationCallback.EVENT.register(context -> {

            // 1. Shader Original del Gobo Projector
            context.register(
                    new ResourceLocation("theatricalextralights", "gobo_projector"),
                    DefaultVertexFormat.POSITION_COLOR,
                    shader -> ModShaders.goboProjectorShader = shader
            );

            // 2. NUEVO: Shader del Volumetric Beam
            // IMPORTANTE: Utiliza POSITION_COLOR_TEX porque enviamos coordenadas UV
            context.register(
                    new ResourceLocation("theatricalextralights", "volumetric_beam"),
                    DefaultVertexFormat.POSITION_COLOR_TEX,
                    shader -> ModShaders.volumetricBeamShader = shader
            );

        });
    }
}