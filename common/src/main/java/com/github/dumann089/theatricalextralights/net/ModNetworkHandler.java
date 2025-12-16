package com.github.dumann089.theatricalextralights.net;

import com.github.dumann089.theatricalextralights.net.SetJetHeightPacket;
import dev.architectury.networking.NetworkChannel;
import net.minecraft.resources.ResourceLocation;

public class ModNetworkHandler {
    public static final NetworkChannel CHANNEL = NetworkChannel.create(
            new ResourceLocation("theatricalextralights", "main")
    );

    public static void register() {
        CHANNEL.register(
                SetJetHeightPacket.class,
                SetJetHeightPacket::encode,
                SetJetHeightPacket::decode,
                SetJetHeightPacket::handle
        );
    }
}