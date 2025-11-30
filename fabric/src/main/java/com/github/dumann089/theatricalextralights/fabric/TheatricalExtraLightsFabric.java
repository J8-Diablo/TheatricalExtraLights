package com.github.dumann089.theatricalextralights.fabric;

import com.github.dumann089.theatricalextralights.TheatricalExtraLights;
import net.fabricmc.api.ModInitializer;

public class TheatricalExtraLightsFabric implements ModInitializer {
    @Override
    public void onInitialize() {

        TheatricalExtraLights.init();
        com.github.dumann089.theatricalextralights.config.TheatricalExtraLightsConfig.load();
    }

}