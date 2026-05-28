package com.github.dumann089.theatricalextralights.net;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

/** Legacy packet — camera control is now client-side only. */
public class FollowspotExitControlPacket {

    public FollowspotExitControlPacket() {
    }

    public static FollowspotExitControlPacket decode(FriendlyByteBuf buf) {
        return new FollowspotExitControlPacket();
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkManager.PacketContext> contextSupplier) {
        // No-op
    }
}
