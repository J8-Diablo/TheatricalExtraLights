package com.github.dumann089.theatricalextralights.net;

import dev.architectury.networking.NetworkChannel;
import net.minecraft.resources.ResourceLocation;

public class ModNetworkHandler {

    public static final NetworkChannel CHANNEL = NetworkChannel.create(
            new ResourceLocation("theatricalextralights", "main")
    );

    public static void register() {
        CHANNEL.register(SetJetHeightPacket.class, SetJetHeightPacket::encode, SetJetHeightPacket::decode, SetJetHeightPacket::handle);
        CHANNEL.register(SetJetThicknessPacket.class, SetJetThicknessPacket::encode, SetJetThicknessPacket::decode, SetJetThicknessPacket::handle);
    }
}
