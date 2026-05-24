package com.github.dumann089.theatricalextralights.client.gui;

import com.github.dumann089.theatricalextralights.blockentities.interfaces.HasPersonality;
import com.github.dumann089.theatricalextralights.net.ModNetworkHandler;
import com.github.dumann089.theatricalextralights.net.SetPersonalityPacket;
import dev.imabad.theatrical.api.dmx.DMXPersonality;
import dev.imabad.theatrical.blockentities.light.BaseDMXConsumerLightBlockEntity;
import dev.imabad.theatrical.client.gui.screen.GenericManualPanTiltScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import java.util.List;

public class GenericPanTiltExtraLightsConfigScreen extends GenericManualPanTiltScreen {

    private final BaseDMXConsumerLightBlockEntity blockEntity;
    private final BlockPos pos;

    private List<DMXPersonality> personalities;
    private int currentIndex;
    private Button personalityButton;

    public GenericPanTiltExtraLightsConfigScreen(BaseDMXConsumerLightBlockEntity be, BlockPos pos, String title) {
        super(be, title);
        this.blockEntity = be; // tu propia referencia, sin depender de be del padre
        this.pos = pos;
    }

    @Override
    public void addExtraWidgetsToUI() {
        super.addExtraWidgetsToUI();

        if (!(blockEntity instanceof HasPersonality hp)) return;

        personalities = blockEntity.getFixture().getDMXPersonalities();
        if (personalities == null || personalities.size() <= 1) return;

        currentIndex = hp.getActivePersonality();

        personalityButton = Button.builder(
                Component.literal(getModeLabel()),
                btn -> {
                    currentIndex = (currentIndex + 1) % personalities.size();
                    btn.setMessage(Component.literal(getModeLabel()));
                }
        ).size(90, 10).build(); // más pequeño

        // Sin StringWidget, solo padding arriba para separarlo de los sliders
        LayoutSettings layoutSettings = layout.newChildLayoutSettings().paddingTop(40).paddingVertical(4);
        layout.addChild(personalityButton, layoutSettings);
    }

    @Override
    public void removed() {
        super.removed(); // esto ya envía el UpdateFixturePosition del pan/tilt
        if (personalityButton == null) return;
        ModNetworkHandler.CHANNEL.sendToServer(new SetPersonalityPacket(pos, currentIndex));
    }

    private String getModeLabel() {
        return personalities.get(currentIndex).getDescription();
    }
}