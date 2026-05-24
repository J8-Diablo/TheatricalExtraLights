package com.github.dumann089.theatricalextralights.net;

import com.github.dumann089.theatricalextralights.blockentities.interfaces.HasPersonality;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.Supplier;

public class SetPersonalityPacket {

    private final BlockPos pos;
    private final int personalityIndex;

    public SetPersonalityPacket(BlockPos pos, int personalityIndex) {
        this.pos = pos;
        this.personalityIndex = personalityIndex;
    }

    public static SetPersonalityPacket decode(FriendlyByteBuf buf) {
        return new SetPersonalityPacket(buf.readBlockPos(), buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(personalityIndex);
    }

    public void handle(Supplier<NetworkManager.PacketContext> contextSupplier) {
        contextSupplier.get().queue(() -> {
            BlockEntity be = contextSupplier.get().getPlayer().level().getBlockEntity(pos);
            if (be instanceof HasPersonality hp) {
                hp.setActivePersonality(personalityIndex);
            }
        });
    }
}
