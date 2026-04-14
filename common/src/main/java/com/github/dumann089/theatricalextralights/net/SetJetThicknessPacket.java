package com.github.dumann089.theatricalextralights.net;

import com.github.dumann089.theatricalextralights.blockentities.interfaces.HasJetThickness;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public class SetJetThicknessPacket {

    private final BlockPos pos;
    private final float thickness;

    public SetJetThicknessPacket(BlockPos pos, float thickness) {
        this.pos = pos;
        this.thickness = thickness;
    }

    public static SetJetThicknessPacket decode(FriendlyByteBuf buf) {
        return new SetJetThicknessPacket(buf.readBlockPos(), buf.readFloat());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeFloat(thickness);
    }

    public void handle(Supplier<NetworkManager.PacketContext> ctx) {
        ctx.get().queue(() -> {
            var be = ctx.get().getPlayer().level().getBlockEntity(pos);
            if (be instanceof HasJetThickness jet) {
                jet.setJetThickness(thickness);
            }
        });
    }
}