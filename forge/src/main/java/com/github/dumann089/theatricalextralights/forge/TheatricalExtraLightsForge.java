package com.github.dumann089.theatricalextralights.forge;

import com.github.dumann089.theatricalextralights.TheatricalExtraLights;
import com.github.dumann089.theatricalextralights.TheatricalExtraLightsClient;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TheatricalExtraLights.MOD_ID)
public class TheatricalExtraLightsForge {
    public TheatricalExtraLightsForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(TheatricalExtraLights.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClient);
        TheatricalExtraLights.init();
        ForgeConfigInit.register();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(DataEvent::onData);
    }
    public void onClient(FMLClientSetupEvent event){
        TheatricalExtraLightsClient.init();
    }
}
