package com.github.dumann089.theatricalextralights.net;

import com.github.dumann089.theatricalextralights.blockentities.LedFacadeBlockEntity;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * C2S : applique la configuration d'une façade LED (résolution, univers/adresse de base, réseau).
 * Changer la résolution efface le dessin côté serveur (indices invalidés) — l'éditeur renvoie
 * ensuite les pixels via {@link SetLedFacadePixelsPacket}.
 */
public class SetLedFacadeConfigPacket {

    private final BlockPos pos;
    private final int resolution;
    private final int universe;
    private final int address;
    private final UUID networkId;

    public SetLedFacadeConfigPacket(BlockPos pos, int resolution, int universe, int address, UUID networkId) {
        this.pos = pos;
        this.resolution = resolution;
        this.universe = universe;
        this.address = address;
        this.networkId = networkId;
    }

    public static SetLedFacadeConfigPacket decode(FriendlyByteBuf buf) {
        return new SetLedFacadeConfigPacket(buf.readBlockPos(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readUUID());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(resolution);
        buf.writeInt(universe);
        buf.writeInt(address);
        buf.writeUUID(networkId);
    }

    public void handle(Supplier<NetworkManager.PacketContext> contextSupplier) {
        contextSupplier.get().queue(() -> {
            BlockEntity be = contextSupplier.get().getPlayer().level().getBlockEntity(pos);
            if (!(be instanceof LedFacadeBlockEntity facade)) {
                return;
            }
            facade.setNetworkId(networkId);
            facade.setUniverse(Math.max(0, universe));
            facade.setChannelStartPoint(Math.max(1, address));
            facade.setResolution(resolution);
        });
    }
}
