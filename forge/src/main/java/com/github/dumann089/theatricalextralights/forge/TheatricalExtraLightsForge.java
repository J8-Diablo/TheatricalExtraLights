package com.github.dumann089.theatricalextralights.forge;

import com.github.dumann089.theatricalextralights.TheatricalExtraLights;
import com.github.dumann089.theatricalextralights.TheatricalExtraLightsClient;
import com.github.dumann089.theatricalextralights.client.ModShaders;
import com.github.dumann089.theatricalextralights.client.ConfettiCannonClientSetup;
import com.github.dumann089.theatricalextralights.client.forge.ModParticleClientImpl;
import com.github.dumann089.theatricalextralights.client.entities.FireworkRocketRenderer;
import com.github.dumann089.theatricalextralights.entities.ModEntities;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import dev.architectury.platform.forge.EventBuses;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.IOException;

@Mod(TheatricalExtraLights.MOD_ID)
public class TheatricalExtraLightsForge {

    public TheatricalExtraLightsForge() {
        EventBuses.registerModEventBus(TheatricalExtraLights.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        TheatricalExtraLights.init();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        // Registramos el evento para cargar nuestros Shaders de GPU
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerShaders);

        // Registro nativo Forge del renderer del firework
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerEntityRenderers);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerLayerDefinitions);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerParticleProviders);
    }

    private void registerParticleProviders(final RegisterParticleProvidersEvent event) {
        ModParticleClientImpl.registerForgeProviders(event);
    }

    private void registerLayerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions event) {
        ConfettiCannonClientSetup.registerModelLayer(event::registerLayerDefinition);
    }

    private void registerEntityRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.FIREWORK_ROCKET.get(), FireworkRocketRenderer::new);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        // Inicializamos los renders generales a través de Architectury/común
        TheatricalExtraLightsClient.init();
    }

    private void registerShaders(final RegisterShadersEvent event) {
        try {
            // 1. Shader Original del Gobo Projector
            event.registerShader(
                    new ShaderInstance(
                            event.getResourceProvider(),
                            new ResourceLocation("theatricalextralights", "gobo_projector"),
                            DefaultVertexFormat.POSITION_COLOR
                    ),
                    shader -> ModShaders.goboProjectorShader = shader
            );

            // 2. NUEVO: Shader del Volumetric Beam
            // IMPORTANTE: Utiliza POSITION_COLOR_TEX porque enviamos coordenadas UV
            event.registerShader(
                    new ShaderInstance(
                            event.getResourceProvider(),
                            new ResourceLocation("theatricalextralights", "volumetric_beam"),
                            DefaultVertexFormat.POSITION_COLOR_TEX
                    ),
                    shader -> ModShaders.volumetricBeamShader = shader
            );
        } catch (IOException e) {
            throw new RuntimeException("Error cargando los shaders para Theatrical Extra Lights", e);
        }
    }
}