package com.github.dumann089.theatricalextralights.client.gui;

import com.github.dumann089.theatricalextralights.net.ModNetworkHandler;
import com.github.dumann089.theatricalextralights.net.SetJetHeightPacket;
import dev.imabad.theatrical.blockentities.light.BaseDMXConsumerLightBlockEntity;
import dev.imabad.theatrical.client.gui.screen.GenericManualPanTiltScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Method;


public class WaterJetPanTiltScreen extends GenericManualPanTiltScreen {

    private final BaseDMXConsumerLightBlockEntity blockEntity;
    private EditBox heightField;

    public WaterJetPanTiltScreen(BaseDMXConsumerLightBlockEntity be, String title) {
        super(be, title);
        this.blockEntity = be;
    }


    private int heightFieldX;
    private int heightFieldY;

    @Override
    protected void init() {
        super.init();

        int screenWidth = this.width;
        int screenHeight = this.height;

        int fieldWidth = 80;
        int fieldHeight = 20;

        heightFieldX = (int) (screenWidth * 0.75);
        heightFieldY = (int) (screenHeight * 0.5);

        heightField = new EditBox(
                this.font,
                heightFieldX,
                heightFieldY,
                fieldWidth,
                fieldHeight,
                Component.literal("Height")
        );

        try {
            Method getJetHeight = blockEntity.getClass().getMethod("getJetHeight");
            float currentHeight = (float) getJetHeight.invoke(blockEntity);
            heightField.setValue(String.valueOf(currentHeight));
        } catch (Exception e) {
            heightField.setValue("10.0");
        }

        heightField.setFilter(s -> s.matches("\\d*(\\.\\d*)?"));

        addRenderableWidget(heightField);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        String title = "Height";
        int titleX = heightFieldX + (heightField.getWidth() / 2) - (this.font.width(title) / 2);
        int titleY = heightFieldY - 12;
        guiGraphics.drawString(font, title, titleX, titleY, 0xFFFFFF, false);
    }

    @Override
    public void removed() {
        super.removed();
        try {
            float h = Float.parseFloat(heightField.getValue());
            ModNetworkHandler.CHANNEL.sendToServer(
                    new SetJetHeightPacket(blockEntity.getBlockPos(), h)
            );
        } catch (NumberFormatException ignored) {}
    }
}