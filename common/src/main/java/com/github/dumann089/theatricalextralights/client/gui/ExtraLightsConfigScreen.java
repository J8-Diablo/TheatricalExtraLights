package com.github.dumann089.theatricalextralights.client.gui;

import com.github.dumann089.theatricalextralights.blockentities.interfaces.HasPersonality;
import com.github.dumann089.theatricalextralights.net.ModNetworkHandler;
import com.github.dumann089.theatricalextralights.net.SetPersonalityPacket;
import dev.imabad.theatrical.api.dmx.DMXPersonality;
import dev.imabad.theatrical.blockentities.light.BaseDMXConsumerLightBlockEntity;
import dev.imabad.theatrical.client.gui.screen.GenericDMXConfigurationScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ExtraLightsConfigScreen extends GenericDMXConfigurationScreen {

    private final BaseDMXConsumerLightBlockEntity blockEntity;
    private final BlockPos pos;

    private List<DMXPersonality> personalities;
    private int currentIndex;
    private Button personalityButton;

    public ExtraLightsConfigScreen(BaseDMXConsumerLightBlockEntity be, BlockPos pos, String title) {
        super(be, pos, title);
        this.blockEntity = be;
        this.pos = pos;
    }

    @Override
    public void addExtraWidgetsToUI() {
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
        ).size(150, 20).build();

        layout.addChild(personalityButton);
    }

    @Override
    public void render(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        super.render(gg, mouseX, mouseY, partialTick);
        if (personalityButton != null) {
            int x = personalityButton.getX() + 75 - (this.font.width("") / 2);
            int y = personalityButton.getY() - 12;
            gg.drawString(this.font, "", x, y, 0xFFFFFF, false);
        }
    }

    @Override
    public void removed() {
        super.removed();
        if (personalityButton == null) return;
        ModNetworkHandler.CHANNEL.sendToServer(
                new SetPersonalityPacket(pos, currentIndex)
        );
    }

    private String getModeLabel() {
        return personalities.get(currentIndex).getDescription();
    }
}