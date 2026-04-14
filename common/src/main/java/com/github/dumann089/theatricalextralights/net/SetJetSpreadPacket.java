package com.github.dumann089.theatricalextralights.net;

import com.github.dumann089.theatricalextralights.blockentities.interfaces.HasJetSpread;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public class SetJetSpreadPacket {

    private final BlockPos pos;
    private final float spreadX;
    private final float spreadY;
    private final float spreadZ;

    public SetJetSpreadPacket(BlockPos pos, float spreadX, float spreadY, float spreadZ) {
        this.pos = pos;
        this.spreadX = spreadX;
        this.spreadY = spreadY;
        this.spreadZ = spreadZ;
    }

    public static SetJetSpreadPacket decode(FriendlyByteBuf buf) {
        return new SetJetSpreadPacket(
                buf.readBlockPos(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat()
        );
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeFloat(spreadX);
        buf.writeFloat(spreadY);
        buf.writeFloat(spreadZ);
    }

    public void handle(Supplier<NetworkManager.PacketContext> contextSupplier) {
        contextSupplier.get().queue(() -> {
            var be = contextSupplier.get().getPlayer().level().getBlockEntity(pos);
            if (be instanceof HasJetSpread jet) {
                jet.setJetSpread(spreadX, spreadY, spreadZ);
            }
        });
    }
}
