package com.github.dumann089.theatricalextralights.net;

import com.github.dumann089.theatricalextralights.client.ConfettiBurstClient;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class ConfettiBurstPacket {
    private final Vec3 origin;
    private final Vec3 direction;
    private final float intensity;

    public ConfettiBurstPacket(Vec3 origin, Vec3 direction, float intensity) {
        this.origin = origin;
        this.direction = direction;
        this.intensity = intensity;
    }

    public static ConfettiBurstPacket decode(FriendlyByteBuf buf) {
        return new ConfettiBurstPacket(
                new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()),
                new Vec3(buf.readFloat(), buf.readFloat(), buf.readFloat()),
                buf.readFloat()
        );
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(origin.x);
        buf.writeDouble(origin.y);
        buf.writeDouble(origin.z);
        buf.writeFloat((float) direction.x);
        buf.writeFloat((float) direction.y);
        buf.writeFloat((float) direction.z);
        buf.writeFloat(intensity);
    }

    public void handle(Supplier<NetworkManager.PacketContext> contextSupplier) {
        contextSupplier.get().queue(() -> ConfettiBurstClient.queue(origin, direction, intensity));
    }
}
