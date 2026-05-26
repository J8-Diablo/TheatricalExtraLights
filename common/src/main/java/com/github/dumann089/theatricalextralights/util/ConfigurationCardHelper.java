package com.github.dumann089.theatricalextralights.util;

import dev.imabad.theatrical.blockentities.light.BaseDMXConsumerLightBlockEntity;
import net.minecraft.nbt.CompoundTag;

/**
 * Adressage DMX avec bascule automatique vers l'univers suivant quand les canaux ne tiennent pas dans 512.
 */
public final class ConfigurationCardHelper {

    public static final int DMX_CHANNELS_PER_UNIVERSE = 512;

    private ConfigurationCardHelper() {
    }

    public record DmxPatch(int universe, int address) {
    }

    public static boolean fitsInUniverse(int address, int channelCount) {
        if (channelCount <= 0) {
            return true;
        }
        return address > 0 && address + channelCount - 1 <= DMX_CHANNELS_PER_UNIVERSE;
    }

    /** Adresse de départ en tenant compte du débordement d'univers (ex. U1@500 + 34ch → U2@1). */
    public static DmxPatch resolvePatch(int universe, int address, int channelCount) {
        int safeUniverse = Math.max(0, universe);
        int safeAddress = Math.max(1, address);
        if (fitsInUniverse(safeAddress, channelCount)) {
            return new DmxPatch(safeUniverse, safeAddress);
        }
        return new DmxPatch(safeUniverse + 1, 1);
    }

    /** Prochaine adresse après patch, avec wrap univers si nécessaire. */
    public static DmxPatch advancePatch(int universe, int address, int channelCount) {
        int nextAddress = address + channelCount;
        int nextUniverse = Math.max(0, universe);
        if (nextAddress > DMX_CHANNELS_PER_UNIVERSE) {
            nextUniverse++;
            nextAddress = 1;
        }
        return new DmxPatch(nextUniverse, nextAddress);
    }

    public static void applyToFixture(CompoundTag tagData, BaseDMXConsumerLightBlockEntity consumer) {
        boolean universeFromCard = tagData.getBoolean("universeEnabled");
        boolean addressFromCard = tagData.getBoolean("addressEnabled");
        int channelCount = consumer.getChannelCount();

        int universe = universeFromCard ? tagData.getInt("dmxUniverse") : consumer.getUniverse();
        int address = addressFromCard ? tagData.getInt("dmxAddress") : consumer.getChannelStart();

        if (addressFromCard) {
            DmxPatch patch = resolvePatch(universe, address, channelCount);
            consumer.setUniverse(patch.universe());
            consumer.setChannelStartPoint(patch.address());
            tagData.putInt("dmxUniverse", patch.universe());
            tagData.putInt("dmxAddress", patch.address());
        } else if (universeFromCard) {
            consumer.setUniverse(universe);
        }

        if (tagData.getBoolean("autoIncrement")) {
            DmxPatch next = advancePatch(tagData.getInt("dmxUniverse"), tagData.getInt("dmxAddress"), channelCount);
            tagData.putInt("dmxUniverse", next.universe());
            tagData.putInt("dmxAddress", next.address());
        }
    }
}
