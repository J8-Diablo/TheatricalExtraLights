package com.github.dumann089.theatricalextralights.fabric;

import com.github.dumann089.theatricalextralights.TheatricalExtraLights;
import com.github.dumann089.theatricalextralights.TheatricalExtraLightsClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

public class TheatricalExtraLightsClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TheatricalExtraLightsClient.init();
    }
}