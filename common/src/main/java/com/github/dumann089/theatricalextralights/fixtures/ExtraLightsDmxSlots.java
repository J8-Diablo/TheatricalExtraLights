package com.github.dumann089.theatricalextralights.fixtures;

import ch.bildspur.artnet.rdm.RDMSlotID;
import ch.bildspur.artnet.rdm.RDMSlotType;
import dev.imabad.theatrical.api.dmx.DMXSlot;

public final class ExtraLightsDmxSlots {
    public static final DMXSlot EFFECT = new DMXSlot("Effect", RDMSlotType.ST_PRIMARY, RDMSlotID.SD_PAN);
    public static final DMXSlot MOTOR = new DMXSlot("Effect", RDMSlotType.ST_PRIMARY, RDMSlotID.SD_PAN);

    private ExtraLightsDmxSlots() {
    }
}
