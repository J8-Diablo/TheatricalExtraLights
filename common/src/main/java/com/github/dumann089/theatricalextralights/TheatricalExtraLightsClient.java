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
        BlockEntityRendererRegistry.register(BlockEntities.BEAM_7R.get(), Beam7RRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.SOURCE_FOUR.get(), Source4Renderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.MAC_VIP.get(), MacVipRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.SHARPLUS.get(), SharplusRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.MOVING500.get(), Moving500Renderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.ROBITSPOT.get(), RobitspotRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.PAR1000_RED.get(), Par1000RedRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.PAR1000_BLUE.get(), Par1000BlueRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.PAR1000_GREEN.get(), Par1000GreenRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.PAR1000_MAGENTA.get(), Par1000MagentaRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.PAR1000_AMBER.get(), Par1000AmberRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.PAR1000.get(), Par1000Renderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.PAR1000_PURPLE.get(), Par1000PurpleRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.PAR1000_LIGHTBLUE.get(), Par1000LightblueRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.PAR1000_WHITE.get(), Par1000WhiteRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.VERVESPOT.get(), VervespotRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.VERTICAL_BAR.get(), VerticalbarRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.SEARCHLIGHT.get(), SearchlightRenderer::new);







    }
}
