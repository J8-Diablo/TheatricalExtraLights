package com.github.dumann089.theatricalextralights.client.gui;

import com.github.dumann089.theatricalextralights.net.ModNetworkHandler;
import com.github.dumann089.theatricalextralights.net.SetJetHeightPacket;
import dev.imabad.theatrical.blockentities.light.BaseDMXConsumerLightBlockEntity;
import dev.imabad.theatrical.client.gui.screen.GenericDMXConfigurationScreen;
import dev.imabad.theatrical.client.gui.screen.GenericManualPanTiltScreen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Method;


public class WaterJetGenericScreen extends GenericDMXConfigurationScreen<BaseDMXConsumerLightBlockEntity> {

    private final BaseDMXConsumerLightBlockEntity blockEntity;
    private EditBox heightField;

    public WaterJetGenericScreen(BaseDMXConsumerLightBlockEntity be, String title) {
        super(be, be.getBlockPos(), title);
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