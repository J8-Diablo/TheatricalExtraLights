package com.github.dumann089.theatrical.neoforge;

import com.github.dumann089.theatricalextralights.TheatricalExtraLights;
import com.github.dumann089.theatricalextralights.TheatricalExtraLightsClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(TheatricalExtraLights.MOD_ID)
public class TheatricalExtraLightsNeoForge {

    public TheatricalExtraLightsNeoForge() {
        TheatricalExtraLights.init();
    }
    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = TheatricalExtraLights.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void setupClient(FMLClientSetupEvent event) {
            TheatricalExtraLightsClient.init();
        }
    }
}
