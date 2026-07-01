package com.github.dumann089.theatricalextralights.client;

import com.github.dumann089.theatricalextralights.util.DmxStrobeFixture;
import dev.imabad.theatrical.blockentities.light.BaseLightBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

/**
 * Intensité rendue — carré pour le strobe, interpolation lisse pour le reste.
 */
public final class StrobeRenderHelper {

    private StrobeRenderHelper() {
    }

    public static float renderedIntensity(BaseLightBlockEntity blockEntity, float partialTick) {
        if (blockEntity instanceof DmxStrobeFixture strobeFixture) {
            return strobeFixture.getRenderedIntensity(partialTick);
        }
        return blockEntity.getPrevIntensity()
                + (blockEntity.getIntensity() - blockEntity.getPrevIntensity()) * partialTick;
    }

    /** Force Sodium / vanilla à rafraîchir le chunk (strobe, faisceaux, lentilles). */
    public static void markSectionDirty(BlockPos pos) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.levelRenderer != null) {
            mc.levelRenderer.setSectionDirtyWithNeighbors(pos.getX(), pos.getY(), pos.getZ());
        }
    }
}
