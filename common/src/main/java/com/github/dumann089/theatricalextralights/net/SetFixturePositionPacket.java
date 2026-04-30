package com.github.dumann089.theatricalextralights.net;

import dev.architectury.networking.NetworkManager;
import dev.imabad.theatrical.blockentities.light.BaseLightBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class SetFixturePositionPacket {

    private final BlockPos pos;
    private final int tilt;
    private final int pan;

    public SetFixturePositionPacket(BlockPos pos, int tilt, int pan) {
        this.pos = pos;
        this.tilt = tilt;
        this.pan = pan;
    }

    public static SetFixturePositionPacket decode(FriendlyByteBuf buf) {
        return new SetFixturePositionPacket(buf.readBlockPos(), buf.readInt(), buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(tilt);
        buf.writeInt(pan);
    }

    public void handle(Supplier<NetworkManager.PacketContext> contextSupplier) {
        contextSupplier.get().queue(() -> {
            BlockEntity blockEntity = contextSupplier.get().getPlayer().level().getBlockEntity(pos);
            if (!(blockEntity instanceof BaseLightBlockEntity lightBlockEntity)) {
                return;
            }

            lightBlockEntity.setTilt(tilt);
            lightBlockEntity.setPan(pan);
            lightBlockEntity.setChanged();

            if (lightBlockEntity.getLevel() == null) {
                return;
            }

            BlockState state = lightBlockEntity.getBlockState();
            lightBlockEntity.getLevel().sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
        });
    }
}
