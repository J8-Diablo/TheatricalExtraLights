package com.github.dumann089.theatricalextralights.client.gui;

import com.github.dumann089.theatricalextralights.blockentities.interfaces.HasJetHeight;
import com.github.dumann089.theatricalextralights.blockentities.interfaces.HasJetThickness;
import com.github.dumann089.theatricalextralights.net.ModNetworkHandler;
import com.github.dumann089.theatricalextralights.net.SetJetHeightPacket;
import com.github.dumann089.theatricalextralights.net.SetJetThicknessPacket;
import dev.imabad.theatrical.blockentities.light.BaseDMXConsumerLightBlockEntity;
import dev.imabad.theatrical.client.gui.screen.GenericDMXConfigurationScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Method;

public class WaterJetGenericScreen extends GenericDMXConfigurationScreen {

    private final BaseDMXConsumerLightBlockEntity blockEntity;
    private EditBox heightField;
    private ThicknessSlider thicknessSlider;

    private int heightFieldX;
    private int heightFieldY;
    private int thicknessSliderX;
    private int thicknessSliderY;

    public WaterJetGenericScreen(BaseDMXConsumerLightBlockEntity be, String title) {
        super(be, be.getBlockPos(), title);
        this.blockEntity = be;
    }

    @Override
    protected void init() {
        super.init();

        int fieldWidth = 80;
        int fieldHeight = 20;

        heightFieldX = (int) (this.width * 0.75);
        heightFieldY = (int) (this.height * 0.45);

        // =========================
        // HEIGHT FIELD
        // =========================
        heightField = new EditBox(
                this.font,
                heightFieldX,
                heightFieldY,
                fieldWidth,
                fieldHeight,
                Component.literal("Height")
        );

        if (blockEntity instanceof HasJetHeight jet) {
            heightField.setValue(String.valueOf(jet.getJetHeight()));
        } else {
            heightField.setValue("20.0");
        }

        heightField.setFilter(s -> s.matches("\\d*(\\.\\d*)?"));
        addRenderableWidget(heightField);

        // =========================
        // THICKNESS SLIDER
        // =========================
        thicknessSliderX = heightFieldX;
        thicknessSliderY = heightFieldY + 40;

        float thicknessValue = 0.12f;
        if (blockEntity instanceof HasJetThickness jt) {
            thicknessValue = jt.getJetThickness();
        }

        thicknessSlider = new ThicknessSlider(
                thicknessSliderX,
                thicknessSliderY,
                fieldWidth,
                20,
                Component.literal("Thickness"),
                0.02f,
                1.0f,
                thicknessValue,
                blockEntity
        );
        addRenderableWidget(thicknessSlider);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Labels
        drawCenteredLabel(guiGraphics, "Height", heightFieldX, heightFieldY);
        drawCenteredLabel(guiGraphics, "Thickness", thicknessSliderX, thicknessSliderY);
    }

    private void drawCenteredLabel(GuiGraphics gg, String text, int fieldX, int fieldY) {
        int x = fieldX + 40 - (this.font.width(text) / 2);
        int y = fieldY - 12;
        gg.drawString(this.font, text, x, y, 0xFFFFFF, false);
    }

    @Override
    public void removed() {
        super.removed();

        // Enviar Height
        if (blockEntity instanceof HasJetHeight jet) {
            float h = Float.parseFloat(heightField.getValue());
            ModNetworkHandler.CHANNEL.sendToServer(
                    new SetJetHeightPacket(blockEntity.getBlockPos(), h)
            );
        }

        // Enviar Thickness
        if (blockEntity instanceof HasJetThickness jt) {
            float t = thicknessSlider.getValue();
            ModNetworkHandler.CHANNEL.sendToServer(
                    new SetJetThicknessPacket(blockEntity.getBlockPos(), t)
            );
        }
    }

    // =========================
    // THICKNESS SLIDER INNER CLASS
    // =========================
    private static class ThicknessSlider extends AbstractSliderButton {

        private final float min;
        private final float max;
        private final BaseDMXConsumerLightBlockEntity blockEntity;

        public ThicknessSlider(int x, int y, int width, int height, Component text,
                               float min, float max, float current,
                               BaseDMXConsumerLightBlockEntity blockEntity) {
            super(x, y, width, height, text, 0);
            this.min = min;
            this.max = max;
            this.blockEntity = blockEntity;
            this.value = (current - min) / (max - min); // normalizamos
            updateMessage();
            applyValue(); // aplica valor inicial al BlockEntity
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Component.literal("Thickness: " + String.format("%.2f", getValue())));
        }

        @Override
        protected void applyValue() {
            // Aplica el valor al BlockEntity en tiempo real
            if (blockEntity instanceof HasJetThickness jt) {
                jt.setJetThickness(getValue());
            }
        }

        public float getValue() {
            return min + (max - min) * (float) this.value;
        }
    }
}