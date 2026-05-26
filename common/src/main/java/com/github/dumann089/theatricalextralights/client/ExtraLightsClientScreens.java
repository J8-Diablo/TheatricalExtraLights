package com.github.dumann089.theatricalextralights.client;

import com.github.dumann089.theatricalextralights.TheatricalExtraLightsScreens;
import com.github.dumann089.theatricalextralights.client.gui.*;
import dev.imabad.theatrical.blockentities.light.BaseDMXConsumerLightBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

@Environment(EnvType.CLIENT)
public class ExtraLightsClientScreens {

    public static void open(TheatricalExtraLightsScreens screenType, BlockPos pos) {

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        BlockEntity be = mc.level.getBlockEntity(pos);
        if (!(be instanceof BaseDMXConsumerLightBlockEntity lightBE)) return;

        Screen gui = switch (screenType) {
            case WATER_GENERIC ->
                    new WaterJetConfigScreen(lightBE, pos, lightBE.getTranslationKey(), WaterJetConfigScreen.Mode.GENERIC);
            case WATER_MANUAL ->
                    new WaterJetConfigScreen(lightBE, pos, lightBE.getTranslationKey(), WaterJetConfigScreen.Mode.MANUAL);
            case WATER_CONE ->
                    new WaterJetConfigScreen(lightBE, pos, lightBE.getTranslationKey(), WaterJetConfigScreen.Mode.CONE);

            case CHANNEL_MENU ->
                    new ExtraLightsConfigScreen(lightBE, pos, lightBE.getTranslationKey(), false);
            case CHANNEL_PANTILT ->
                    new ExtraLightsConfigScreen(lightBE, pos, lightBE.getTranslationKey(), true);
        };

        mc.setScreen(gui);
    }
}