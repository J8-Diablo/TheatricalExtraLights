package com.github.dumann089.theatricalextralights.net;

import com.github.dumann089.theatricalextralights.blockentities.FollowspotConsoleBlockEntity;
import com.github.dumann089.theatricalextralights.util.FollowspotBeamHelper;
import com.github.dumann089.theatricalextralights.util.FollowspotControlSessions;
import com.github.dumann089.theatricalextralights.util.FollowspotTargetHelper;
import dev.architectury.networking.NetworkManager;
import dev.imabad.theatrical.blockentities.light.BaseLightBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

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
        contextSupplier.get().queue(() -> {
            if (!(contextSupplier.get().getPlayer() instanceof ServerPlayer player)) {
                return;
            }
            BlockEntity blockEntity = player.level().getBlockEntity(consolePos);
            if (!(blockEntity instanceof FollowspotConsoleBlockEntity console)) {
                return;
            }
            var target = FollowspotTargetHelper.findTarget(
                    player.level(),
                    console.getNetworkId(),
                    console.getUniverse(),
                    console.getDmxAddress(),
                    consolePos
            );
            if (target.isEmpty()) {
                return;
            }

            BaseLightBlockEntity fixture = target.get().fixture();
            FollowspotControlSessions.begin(player);
            Vec3 origin = FollowspotBeamHelper.getBeamOrigin(fixture);
            float[] look = FollowspotBeamHelper.getLookAngles(fixture, console.getPan(), console.getTilt());
            player.teleportTo(origin.x, origin.y, origin.z);
            player.setYRot(look[0]);
            player.setXRot(look[1]);
            player.yRotO = look[0];
            player.xRotO = look[1];
        });
    }
}
