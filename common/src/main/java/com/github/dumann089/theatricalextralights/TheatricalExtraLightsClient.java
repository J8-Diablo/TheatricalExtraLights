package com.github.dumann089.theatricalextralights;

import com.github.dumann089.theatricalextralights.blockentities.BlockEntities;
import com.github.dumann089.theatricalextralights.client.blockentities.*;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;

public class TheatricalExtraLightsClient {

    public static void init() {
        BlockEntityRendererRegistry.register(BlockEntities.MOVING_SCAN.get(), MovingScanRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.MOVING_VL2C.get(), MovingVL2CRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.MOVING_VL6.get(), MovingVL6Renderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.MOVING_BEAM.get(), MovingBeamRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.LED_FOUNTAIN.get(), LEDfountainRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.PAR_LED.get(), ParLedRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.BIG_PANEL.get(), BigPanelRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.BIG_PANEL2.get(), BigPanel2Renderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.RGB_BAR.get(), RGBbarRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.LASER.get(), LaserRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.BLINDER.get(), BlinderRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.STROBE.get(), StrobeRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.LED_PANEL_2.get(), LEDPanel2Renderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.TRUSS_3LIGHTS.get(), truss3lightsRenderer::new);
        


    }
}
