package com.github.dumann089.theatricalextralights.fabric;

import com.github.dumann089.theatricalextralights.TheatricalExtraLightsClient;
import net.fabricmc.api.ClientModInitializer;

public class TheatricalExtraLightsClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        TheatricalExtraLightsClient.init();
        com.github.dumann089.theatricalextralights.TheatricalExtraLightsClient.init();
    }
}