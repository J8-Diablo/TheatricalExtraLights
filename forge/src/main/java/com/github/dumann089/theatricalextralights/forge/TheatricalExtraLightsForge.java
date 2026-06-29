package com.github.dumann089.theatricalextralights.forge;

import com.github.dumann089.theatricalextralights.TheatricalExtraLights;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TheatricalExtraLights.MOD_ID)
public class TheatricalExtraLightsForge {

    public TheatricalExtraLightsForge() {
        EventBuses.registerModEventBus(TheatricalExtraLights.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        TheatricalExtraLights.init();
    }
}
