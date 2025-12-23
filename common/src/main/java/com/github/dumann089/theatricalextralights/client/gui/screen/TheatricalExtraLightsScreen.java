package com.github.dumann089.theatricalextralights.client.gui.screen;

import com.github.dumann089.theatricalextralights.TheatricalExtraLightsScreens;
import com.github.dumann089.theatricalextralights.client.gui.WaterJetGenericScreen;
import com.github.dumann089.theatricalextralights.client.gui.WaterJetPanTiltScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import dev.imabad.theatrical.blockentities.light.BaseDMXConsumerLightBlockEntity;

public class TheatricalExtraLightsScreen {

    public static void handleOpenScreen(com.github.dumann089.theatricalextralights.net.OpenExtraLightsScreenPacket packet) {
        BlockPos pos = packet.getPos();
        TheatricalExtraLightsScreens screenType = packet.getScreen();

        BaseDMXConsumerLightBlockEntity be =
                (BaseDMXConsumerLightBlockEntity) Minecraft.getInstance().level.getBlockEntity(pos);

        if(be == null) return;

        switch(screenType) {
            case WATER_GENERIC -> Minecraft.getInstance().setScreen(new WaterJetGenericScreen(be, "Water Generic"));
            case WATER_MANUAL -> Minecraft.getInstance().setScreen(new WaterJetPanTiltScreen(be, "Water Manual"));
        }
    }
}