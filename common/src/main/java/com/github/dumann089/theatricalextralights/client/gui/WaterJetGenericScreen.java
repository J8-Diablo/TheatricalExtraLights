package com.github.dumann089.theatricalextralights.client.gui;

import com.github.dumann089.theatricalextralights.blockentities.interfaces.HasJetHeight;
import com.github.dumann089.theatricalextralights.blockentities.interfaces.HasJetThickness;
import com.github.dumann089.theatricalextralights.net.ModNetworkHandler;
import com.github.dumann089.theatricalextralights.net.SetJetHeightPacket;
import com.github.dumann089.theatricalextralights.net.SetJetThicknessPacket;
import dev.imabad.theatrical.blockentities.light.BaseDMXConsumerLightBlockEntity;
import dev.imabad.theatrical.client.gui.screen.GenericDMXConfigurationScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class WaterJetGenericScreen extends GenericDMXConfigurationScreen {

    private final BaseDMXConsumerLightBlockEntity blockEntity;
    private final BlockPos pos;

    private EditBox heightField;
    private ThicknessSlider thicknessSlider;

    private int heightFieldX;
    private int heightFieldY;
    private int thicknessSliderX;
    private int thicknessSliderY;

    public WaterJetGenericScreen(BaseDMXConsumerLightBlockEntity be, BlockPos pos, String title) {
        super(be, pos, title);
        this.blockEntity = be;
        this.pos = pos;
    }

    @Override
    protected void init() {
        if (blockEntity == null) {

            Minecraft.getInstance().setScreen(null);
            return;
        }

        super.init();

        int fieldWidth = 80;
        int fieldHeight = 20;

        /* ================= HEIGHT ================= */

        heightFieldX = (int) (this.width * 0.75);
        heightFieldY = (int) (this.height * 0.45);

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

        /* ================= THICKNESS ================= */

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
    public void render(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        super.render(gg, mouseX, mouseY, partialTick);
        drawCenteredLabel(gg, "Height", heightFieldX, heightFieldY);
        drawCenteredLabel(gg, "Thickness", thicknessSliderX, thicknessSliderY);
    }

    private void drawCenteredLabel(GuiGraphics gg, String text, int fieldX, int fieldY) {
        int x = fieldX + 40 - (this.font.width(text) / 2);
        int y = fieldY - 12;
        gg.drawString(this.font, text, x, y, 0xFFFFFF, false);
    }

    @Override
    public void removed() {
        super.removed();

        if (blockEntity == null) return;

        if (blockEntity instanceof HasJetHeight jet) {
            try {
                float h = Float.parseFloat(heightField.getValue());
                ModNetworkHandler.CHANNEL.sendToServer(
                        new SetJetHeightPacket(blockEntity.getBlockPos(), h)
                );
            } catch (NumberFormatException ignored) {}
        }

        if (blockEntity instanceof HasJetThickness jt) {
            float t = thicknessSlider.getValue();
            ModNetworkHandler.CHANNEL.sendToServer(
                    new SetJetThicknessPacket(blockEntity.getBlockPos(), t)
            );
        }
    }

    /* ================= SLIDER ================= */

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
            this.value = (current - min) / (max - min);
            updateMessage();
            applyValue();
        }

        @Override
        protected void updateMessage() {
            this.setMessage(
                    Component.literal("Thickness: " + String.format("%.2f", getValue()))
            );
        }

        @Override
        protected void applyValue() {
            if (blockEntity instanceof HasJetThickness jt) {
                jt.setJetThickness(getValue());
            }
        }

        public float getValue() {
            return min + (max - min) * (float) this.value;
        }
    }
}