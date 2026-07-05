package com.github.dumann089.theatricalextralights.compat.dmx;

import net.minecraft.network.FriendlyByteBuf;

/**
 * Local mirror of Theatrical's extended DMX frame API.
 * Kept in our package to avoid JPMS export conflicts with the theatrical module.
 */
public interface DmxFrameExtendedFixture {

    byte EXTRA_TYPE_LASER = 1;
    /** strobe + prevStrobe (unsigned bytes). */
    byte EXTRA_TYPE_STROBE = 2;

    byte dmxFrameExtraType();

    void writeDmxFrameExtras(FriendlyByteBuf buf);

    void applyDmxFrameExtras(FriendlyByteBuf buf);
}
