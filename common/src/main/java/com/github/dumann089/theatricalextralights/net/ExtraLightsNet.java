package com.github.dumann089.theatricalextralights.net;

import com.github.dumann089.theatricalextralights.net.OpenExtraLightsScreenPacket;
import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;
import net.minecraft.resources.ResourceLocation;

public interface ExtraLightsNet {
    SimpleNetworkManager MAIN = SimpleNetworkManager.create("theatricalextralights");

    // S2C
    MessageType OPEN_SCREEN = MAIN.registerS2C("open_extra_lights_screen", OpenExtraLightsScreenPacket::new);

    static void init(){}
}