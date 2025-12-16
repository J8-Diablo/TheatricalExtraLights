package com.github.dumann089.theatricalextralights.net;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.lang.reflect.Method;

public class SetJetHeightPacket {
    public static final ResourceLocation ID = new ResourceLocation("theatricalextralights", "set_jet_height");

    private final BlockPos pos;
    private final float height;

    public SetJetHeightPacket(BlockPos pos, float height) {
        this.pos = pos;
        this.height = height;
    }

    public static SetJetHeightPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        float height = buf.readFloat();
        return new SetJetHeightPacket(pos, height);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeFloat(height);
    }

    public void handle(java.util.function.Supplier<NetworkManager.PacketContext> contextSupplier) {
        NetworkManager.PacketContext context = contextSupplier.get();
        context.queue(() -> {
            if (context.getPlayer() != null) {
                BlockEntity be = context.getPlayer().level().getBlockEntity(pos);
                if (be != null) {
                    try {
                        Method setJetHeight = be.getClass().getMethod("setJetHeight", float.class);
                        setJetHeight.invoke(be, height);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }
}