package com.github.dumann089.theatricalextralights.client.gui;

import dev.imabad.theatrical.blockentities.light.BaseDMXConsumerLightBlockEntity;
import net.minecraft.core.BlockPos;

/**
 * @deprecated Utilise {@link ExtraLightsConfigScreen} — l'ancien écran Theatrical (LabeledEditBox 10px) provoquait
 * des chevauchements de texte.
 */
@Deprecated
public class GenericPanTiltExtraLightsConfigScreen extends ExtraLightsConfigScreen {

    public GenericPanTiltExtraLightsConfigScreen(BaseDMXConsumerLightBlockEntity be, BlockPos pos, String title) {
        super(be, pos, title, true);
    }
}
