package com.github.dumann089.theatricalextralights.fabric;

import com.github.dumann089.theatricalextralights.TheatricalExtraLightsClient;
import com.github.dumann089.theatricalextralights.client.ModShaders; // <-- Importa la clase ModShaders
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.resources.ResourceLocation;

public class TheatricalExtraLightsClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Tu inicialización original
        TheatricalExtraLightsClient.init();

        // NUEVO: Registro del Core Shader para la GPU
        CoreShaderRegistrationCallback.EVENT.register(context -> {
            context.register(
                    new ResourceLocation("theatricalextralights", "gobo_projector"),
                    DefaultVertexFormat.POSITION_COLOR,
                    shader -> ModShaders.goboProjectorShader = shader
            );
        });
    }
}