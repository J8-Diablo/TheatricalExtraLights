package com.github.dumann089.theatricalextralights.net;

import com.github.dumann089.theatricalextralights.blockentities.interfaces.HasJetConeAngle;
import com.github.dumann089.theatricalextralights.blockentities.interfaces.HasJetThickness;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public class SetJetConeAnglePacket {

    private final BlockPos pos;
    private final float coneangle;

    public SetJetConeAnglePacket(BlockPos pos, float coneangle) {
        this.pos = pos;
        this.coneangle = coneangle;
    }

    public static SetJetConeAnglePacket decode(FriendlyByteBuf buf) {
        return new SetJetConeAnglePacket(buf.readBlockPos(), buf.readFloat());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeFloat(coneangle);
    }

    public void handle(Supplier<NetworkManager.PacketContext> ctx) {
        ctx.get().queue(() -> {
            var be = ctx.get().getPlayer().level().getBlockEntity(pos);
            if (be instanceof HasJetConeAngle jet) {
                jet.setJetConeAngle(coneangle);
            }
        });
    }
}