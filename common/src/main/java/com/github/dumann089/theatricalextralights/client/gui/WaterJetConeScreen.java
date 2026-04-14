package com.github.dumann089.theatricalextralights.client.gui;

import com.github.dumann089.theatricalextralights.blockentities.interfaces.HasJetConeAngle;
import com.github.dumann089.theatricalextralights.blockentities.interfaces.HasJetHeight;
import com.github.dumann089.theatricalextralights.blockentities.interfaces.HasJetThickness;
import com.github.dumann089.theatricalextralights.net.*;
import dev.imabad.theatrical.blockentities.light.BaseDMXConsumerLightBlockEntity;
import dev.imabad.theatrical.client.gui.screen.GenericManualPanTiltScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class WaterJetConeScreen extends GenericManualPanTiltScreen {

    private final BlockPos pos;
    private final BaseDMXConsumerLightBlockEntity blockEntity;

    private EditBox heightField;
    private ValueSlider thicknessSlider;
    private ValueSlider coneAngleSlider;

    private int baseX, baseY;

    public WaterJetConeScreen(BlockPos pos, String title) {
        super(
                (BaseDMXConsumerLightBlockEntity)
                        Minecraft.getInstance().level.getBlockEntity(pos),
                title
        );
        this.pos = pos;
        this.blockEntity =
                (BaseDMXConsumerLightBlockEntity)
                        Minecraft.getInstance().level.getBlockEntity(pos);
    }

    @Override
    protected void init() {
        if (blockEntity == null) {
            Minecraft.getInstance().setScreen(null);
            return;
        }

        super.init();

        int w = 90;
        int h = 20;

        baseX = (int) (this.width * 0.75);
        baseY = (int) (this.height * 0.40);

        /* HEIGHT */

        heightField = new EditBox(font, baseX, baseY, w, h, Component.literal("Height"));
        if (blockEntity instanceof HasJetHeight jet)
            heightField.setValue(String.valueOf(jet.getJetHeight()));
        heightField.setFilter(s -> s.matches("\\d*(\\.\\d*)?"));
        addRenderableWidget(heightField);

        /* THICKNESS */

        float thickness = blockEntity instanceof HasJetThickness jt
                ? jt.getJetThickness() : 0.3f;

        thicknessSlider = new ValueSlider(
                baseX, baseY + 40, w, h,
                0.02f, 1.0f, thickness,
                "Thickness"
        );
        addRenderableWidget(thicknessSlider);

        /* CONE ANGLE */

        float angle = blockEntity instanceof HasJetConeAngle jc
                ? jc.getJetConeAngle() : 45f;

        coneAngleSlider = new ValueSlider(
                baseX, baseY + 80, w, h,
                5f, 90f, angle,
                "Cone"
        );
        addRenderableWidget(coneAngleSlider);
    }

    @Override
    public void render(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        super.render(gg, mouseX, mouseY, partialTick);
        drawLabel(gg, "Height", baseX, baseY);
        drawLabel(gg, "Thickness", baseX, baseY + 40);
        drawLabel(gg, "Cone Angle", baseX, baseY + 80);
    }

    private void drawLabel(GuiGraphics gg, String text, int x, int y) {
        gg.drawString(font,
                text,
                x + 45 - font.width(text) / 2,
                y - 12,
                0xFFFFFF,
                false
        );
    }

    @Override
    public void removed() {
        super.removed();

        if (blockEntity instanceof HasJetHeight) {
            ModNetworkHandler.CHANNEL.sendToServer(
                    new SetJetHeightPacket(pos, Float.parseFloat(heightField.getValue()))
            );
        }

        if (blockEntity instanceof HasJetThickness) {
            ModNetworkHandler.CHANNEL.sendToServer(
                    new SetJetThicknessPacket(pos, thicknessSlider.getValue())
            );
        }

        if (blockEntity instanceof HasJetConeAngle) {
            ModNetworkHandler.CHANNEL.sendToServer(
                    new SetJetConeAnglePacket(pos, coneAngleSlider.getValue())
            );
        }
    }

    /* ================================================= */

    private static class ValueSlider extends AbstractSliderButton {

        private final float min, max;
        private final String label;

        public ValueSlider(int x, int y, int w, int h,
                           float min, float max, float current,
                           String label) {
            super(x, y, w, h, Component.empty(), 0);
            this.min = min;
            this.max = max;
            this.label = label;
            this.value = (current - min) / (max - min);
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.literal(
                    label + ": " + String.format("%.2f", getValue())
            ));
        }

        @Override
        protected void applyValue() {
        }

        public float getValue() {
            return min + (max - min) * (float) value;
        }
    }
}