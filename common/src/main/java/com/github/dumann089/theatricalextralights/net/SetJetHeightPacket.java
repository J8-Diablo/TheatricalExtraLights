package com.github.dumann089.theatricalextralights.net;

import com.github.dumann089.theatricalextralights.blockentities.interfaces.HasJetHeight;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public class SetJetHeightPacket {

    private final BlockPos pos;
    private final float height;

    public SetJetHeightPacket(BlockPos pos, float height) {
        this.pos = pos;
        this.height = height;
    }

    public static SetJetHeightPacket decode(FriendlyByteBuf buf) {
        return new SetJetHeightPacket(buf.readBlockPos(), buf.readFloat());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeFloat(height);
    }

    public void handle(Supplier<NetworkManager.PacketContext> contextSupplier) {
        contextSupplier.get().queue(() -> {
            var be = contextSupplier.get().getPlayer().level().getBlockEntity(pos);
            if (be instanceof HasJetHeight jet) {
                jet.setJetHeight(height);
            }
        });
    }
}
