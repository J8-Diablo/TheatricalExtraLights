package com.github.dumann089.theatricalextralights.fabric;

import com.github.dumann089.theatricalextralights.TheatricalExtraLights;
import dev.imabad.theatrical.Theatrical;
import net.fabricmc.api.ModInitializer;

public class TheatricalExtraLightsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        TheatricalExtraLights.init();
    }
}