package com.github.dumann089.theatricalextralights.net;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

/** Legacy packet — camera control is now client-side only. */
public class FollowspotEnterControlPacket {

    private final BlockPos consolePos;

    public FollowspotEnterControlPacket(BlockPos consolePos) {
        this.consolePos = consolePos;
    }

    public static FollowspotEnterControlPacket decode(FriendlyByteBuf buf) {
        return new FollowspotEnterControlPacket(buf.readBlockPos());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(consolePos);
    }

    public void handle(Supplier<NetworkManager.PacketContext> contextSupplier) {
        // No-op: first-person view uses client camera override without teleporting the player.
    }
}
