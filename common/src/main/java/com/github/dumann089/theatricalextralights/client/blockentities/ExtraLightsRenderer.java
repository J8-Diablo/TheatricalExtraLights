package com.github.dumann089.theatricalextralights.client.blockentities;

import dev.imabad.theatrical.blockentities.light.BaseLightBlockEntity;
import dev.imabad.theatrical.client.blockentities.FixtureRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

/** Renderer Extra Lights sans faisceau parent Theatrical (évite clignotement à intensité max). */
public abstract class ExtraLightsRenderer<T extends BaseLightBlockEntity> extends FixtureRenderer<T> {

    public ExtraLightsRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRenderBeam(T blockEntity) {
        return false;
    }
}
