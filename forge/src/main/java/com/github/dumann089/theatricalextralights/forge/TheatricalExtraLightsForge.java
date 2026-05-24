package com.github.dumann089.theatricalextralights.forge;

import com.github.dumann089.theatricalextralights.TheatricalExtraLights;
import com.github.dumann089.theatricalextralights.TheatricalExtraLightsClient;
import com.github.dumann089.theatricalextralights.client.ModShaders; // <-- Importa la clase ModShaders
import com.github.dumann089.theatricalextralights.client.entities.FireworkRocketRenderer;
import com.github.dumann089.theatricalextralights.entities.ModEntities;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import dev.architectury.platform.forge.EventBuses;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.IOException;

@Mod(TheatricalExtraLights.MOD_ID)
public class TheatricalExtraLightsForge {

    public TheatricalExtraLightsForge() {
        EventBuses.registerModEventBus(TheatricalExtraLights.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // Inicialización común del mod
        TheatricalExtraLights.init();

        // Registramos eventos de Forge
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        // NUEVO: Registramos el evento para cargar nuestro Shader de GPU
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerShaders);

        // Registro nativo Forge del renderer del firework (Architectury falla a veces en el timing)
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerEntityRenderers);
    }

    private void registerEntityRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.FIREWORK_ROCKET.get(), FireworkRocketRenderer::new);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        // Inicializamos los renders generales a través de Architectury/común
        TheatricalExtraLightsClient.init();
    }

    // NUEVO: Método que Forge llama automáticamente para registrar Shaders
    private void registerShaders(final RegisterShadersEvent event) {
        try {
            event.registerShader(
                    new ShaderInstance(
                            event.getResourceProvider(),
                            new ResourceLocation("theatricalextralights", "gobo_projector"),
                            DefaultVertexFormat.POSITION_COLOR
                    ),
                    shader -> ModShaders.goboProjectorShader = shader
            );
        } catch (IOException e) {
            throw new RuntimeException("Error cargando el shader gobo_projector para Theatrical Extra Lights", e);
        }
    }
}