package com.github.dumann089.theatricalextralights.net;

import com.github.dumann089.theatricalextralights.util.FollowspotControlSessions;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Supplier;

public class FollowspotExitControlPacket {

    public FollowspotExitControlPacket() {
    }

    public static FollowspotExitControlPacket decode(FriendlyByteBuf buf) {
        return new FollowspotExitControlPacket();
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkManager.PacketContext> contextSupplier) {
        contextSupplier.get().queue(() -> {
            if (contextSupplier.get().getPlayer() instanceof ServerPlayer player) {
                FollowspotControlSessions.end(player);
            }
        });
    }
}
