package com.github.dumann089.theatricalextralights.net;

import com.github.dumann089.theatricalextralights.TheatricalExtraLightsClient;
import com.github.dumann089.theatricalextralights.TheatricalExtraLightsScreens;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.fabricmc.api.EnvType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class OpenExtraLightsScreenPacket extends BaseS2CMessage {

    private final BlockPos pos;
    private final TheatricalExtraLightsScreens screen;

    public OpenExtraLightsScreenPacket(BlockPos pos, TheatricalExtraLightsScreens screen) {
        this.pos = pos;
        this.screen = screen;
    }

    public OpenExtraLightsScreenPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.screen = buf.readEnum(TheatricalExtraLightsScreens.class);
    }

    @Override
    public MessageType getType() {
        return ExtraLightsNet.OPEN_SCREEN;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeEnum(screen);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> TheatricalExtraLightsClient.handleOpenScreen(this));
    }

    public BlockPos getPos() {
        return pos;
    }

    public TheatricalExtraLightsScreens getScreen() {
        return screen;
    }
}