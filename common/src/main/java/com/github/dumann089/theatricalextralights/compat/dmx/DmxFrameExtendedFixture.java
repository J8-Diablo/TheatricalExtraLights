package com.github.dumann089.theatricalextralights.compat.dmx;

import net.minecraft.network.FriendlyByteBuf;

/**
 * Bridge vers l'API Theatrical pour que {@code instanceof DmxFrameExtendedFixture}
 * fonctionne dans {@link dev.imabad.theatrical.networks.dmxframe.DmxFrameCodec}.
 */
public interface DmxFrameExtendedFixture extends dev.imabad.theatrical.api.dmx.DmxFrameExtendedFixture {

    @Override
    byte dmxFrameExtraType();

    @Override
    void writeDmxFrameExtras(FriendlyByteBuf buf);

    @Override
    void applyDmxFrameExtras(FriendlyByteBuf buf);
}
