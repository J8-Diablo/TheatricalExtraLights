package com.github.dumann089.theatricalextralights.client.gui;

import com.github.dumann089.theatricalextralights.blockentities.interfaces.HasPersonality;
import com.github.dumann089.theatricalextralights.net.ModNetworkHandler;
import com.github.dumann089.theatricalextralights.net.SetFixturePositionPacket;
import com.github.dumann089.theatricalextralights.net.SetPersonalityPacket;
import dev.imabad.theatrical.TheatricalClient;
import dev.imabad.theatrical.api.dmx.DMXPersonality;
import dev.imabad.theatrical.blockentities.light.BaseDMXConsumerLightBlockEntity;
import dev.imabad.theatrical.net.UpdateDMXFixture;
import dev.imabad.theatrical.net.UpdateNetworkId;
import dev.imabad.theatrical.util.UUIDUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExtraLightsConfigScreen extends Screen {

    private static final int PANEL_WIDTH = 260;
    private static final int PANEL_PADDING = 14;
    private static final int FIELD_WIDTH = 80;
    private static final int BUTTON_WIDTH = 220;
    private static final int SLIDER_WIDTH = 220;
    private static final int WIDGET_HEIGHT = 20;
    private static final int ROW_SPACING = 24;

    private final BaseDMXConsumerLightBlockEntity blockEntity;
    private final BlockPos pos;

    private EditBox dmxAddressField;
    private EditBox dmxUniverseField;
    private PanTiltSlider tiltSlider;
    private PanTiltSlider panSlider;
    private Button networkButton;
    private Button personalityButton;
    private Button saveButton;

    private List<DMXPersonality> personalities = List.of();
    private int currentPersonalityIndex;

    private List<UUID> networkIds = List.of(UUIDUtil.NULL);
    private int currentNetworkIndex;

    private int panelLeft;
    private int panelTop;
    private int panelHeight;
    private boolean closingFromSave;
    private int dmxAddressLabelY;
    private int dmxUniverseLabelY;
    private int positionLabelY;
    private int networkLabelY;

    public ExtraLightsConfigScreen(BaseDMXConsumerLightBlockEntity blockEntity, BlockPos pos, String title) {
        super(Component.translatable(title));
        this.blockEntity = blockEntity;
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();

        if (blockEntity == null) {
            Minecraft.getInstance().setScreen(null);
            return;
        }

        setupState();
        setupLayout();
        buildWidgets();
    }

    private void setupState() {
        if (blockEntity instanceof HasPersonality hasPersonality) {
            personalities = blockEntity.getFixture().getDMXPersonalities();
            if (personalities == null) {
                personalities = List.of();
            }
            currentPersonalityIndex = Mth.clamp(hasPersonality.getActivePersonality(), 0, Math.max(personalities.size() - 1, 0));
        } else {
            personalities = List.of();
            currentPersonalityIndex = 0;
        }

        ArrayList<UUID> availableNetworks = new ArrayList<>();
        availableNetworks.add(UUIDUtil.NULL);
        if (TheatricalClient.getArtNetManager() != null) {
            for (UUID networkId : TheatricalClient.getArtNetManager().getKnownNetworks().keySet()) {
                if (!availableNetworks.contains(networkId)) {
                    availableNetworks.add(networkId);
                }
            }
        }
        networkIds = availableNetworks;
        currentNetworkIndex = Math.max(networkIds.indexOf(blockEntity.getNetworkId()), 0);
    }

    private void setupLayout() {
        int rowCount = 7;
        if (hasPersonalityOptions()) {
            rowCount++;
        }
        panelHeight = PANEL_PADDING * 2 + 18 + (rowCount * ROW_SPACING) + 28;
        panelLeft = (width - PANEL_WIDTH) / 2;
        panelTop = (height - panelHeight) / 2;
    }

    private void buildWidgets() {
        clearWidgets();

        int labelX = panelLeft + PANEL_PADDING;
        int inputX = panelLeft + PANEL_WIDTH - PANEL_PADDING - FIELD_WIDTH;
        int widgetX = panelLeft + (PANEL_WIDTH - BUTTON_WIDTH) / 2;
        int sliderX = panelLeft + (PANEL_WIDTH - SLIDER_WIDTH) / 2;
        int centerX = panelLeft + (PANEL_WIDTH / 2);
        int y = panelTop + PANEL_PADDING + 26;

        dmxAddressLabelY = y - 2;

        dmxAddressField = new EditBox(font, inputX, y - 4, FIELD_WIDTH, WIDGET_HEIGHT, Component.translatable("fixture.dmxStart"));
        dmxAddressField.setFilter(this::isIntegerInput);
        dmxAddressField.setValue(Integer.toString(blockEntity.getChannelStart()));
        addRenderableWidget(dmxAddressField);
        y += ROW_SPACING;

        dmxUniverseLabelY = y - 2;

        dmxUniverseField = new EditBox(font, inputX, y - 4, FIELD_WIDTH, WIDGET_HEIGHT, Component.translatable("artneti.dmxUniverse"));
        dmxUniverseField.setFilter(this::isIntegerInput);
        dmxUniverseField.setValue(Integer.toString(blockEntity.getUniverse()));
        addRenderableWidget(dmxUniverseField);
        y += ROW_SPACING;

        positionLabelY = y - 12;

        tiltSlider = addRenderableWidget(new PanTiltSlider(
                sliderX,
                y,
                SLIDER_WIDTH,
                blockEntity.getTilt(),
                -90,
                90,
                "Tilt",
                this::applyTilt
        ));
        y += ROW_SPACING;

        panSlider = addRenderableWidget(new PanTiltSlider(
                sliderX,
                y,
                SLIDER_WIDTH,
                blockEntity.getPan(),
                -180,
                180,
                "Pan",
                this::applyPan
        ));
        y += ROW_SPACING;

        if (hasPersonalityOptions()) {
            personalityButton = addRenderableWidget(Button.builder(
                    Component.literal(getModeLabel()),
                    button -> {
                        currentPersonalityIndex = (currentPersonalityIndex + 1) % personalities.size();
                        button.setMessage(Component.literal(getModeLabel()));
                    }
            ).bounds(widgetX, y, BUTTON_WIDTH, WIDGET_HEIGHT).build());
            y += ROW_SPACING;
        } else {
            personalityButton = null;
        }

        networkButton = addRenderableWidget(Button.builder(
                Component.literal(getNetworkLabel()),
                button -> {
                    currentNetworkIndex = (currentNetworkIndex + 1) % networkIds.size();
                    button.setMessage(Component.literal(getNetworkLabel()));
                }
        ).bounds(widgetX, y, BUTTON_WIDTH, WIDGET_HEIGHT).build());
        networkLabelY = y - 12;
        y += ROW_SPACING + 6;

        saveButton = addRenderableWidget(Button.builder(
                Component.translatable("artneti.save"),
                button -> {
                    commitChanges();
                    closingFromSave = true;
                    onClose();
                }
        ).bounds(centerX - 50, y, 100, WIDGET_HEIGHT).build());
    }

    private boolean hasPersonalityOptions() {
        return personalities.size() > 1;
    }

    private boolean isIntegerInput(String value) {
        return value.isEmpty() || value.matches("\\d+");
    }

    private void applyTilt(int value) {
        blockEntity.setTilt(value);
        sendPositionUpdate();
    }

    private void applyPan(int value) {
        blockEntity.setPan(value);
        sendPositionUpdate();
    }

    private void sendPositionUpdate() {
        ModNetworkHandler.CHANNEL.sendToServer(
                new SetFixturePositionPacket(pos, blockEntity.getTilt(), blockEntity.getPan())
        );
    }

    private void commitChanges() {
        int dmxAddress = parseOrDefault(dmxAddressField, blockEntity.getChannelStart());
        int dmxUniverse = parseOrDefault(dmxUniverseField, blockEntity.getUniverse());

        new UpdateDMXFixture(pos, Mth.clamp(dmxAddress, 0, 512), Math.max(0, dmxUniverse)).sendToServer();
        new UpdateNetworkId(pos, networkIds.get(currentNetworkIndex)).sendToServer();
        sendPositionUpdate();

        if (hasPersonalityOptions() && blockEntity instanceof HasPersonality) {
            ModNetworkHandler.CHANNEL.sendToServer(new SetPersonalityPacket(pos, currentPersonalityIndex));
        }
    }

    private int parseOrDefault(EditBox field, int fallbackValue) {
        try {
            return Integer.parseInt(field.getValue());
        } catch (NumberFormatException ignored) {
            return fallbackValue;
        }
    }

    private String getModeLabel() {
        return "CH: " + personalities.get(currentPersonalityIndex).getDescription();
    }

    private String getNetworkLabel() {
        UUID networkId = networkIds.get(currentNetworkIndex);
        if (networkId.equals(UUIDUtil.NULL)) {
            return "No network";
        }
        if (TheatricalClient.getArtNetManager() == null) {
            return "Unknown network";
        }
        return TheatricalClient.getArtNetManager().getKnownNetworks().getOrDefault(networkId, "Unknown network");
    }

    @Override
    public void removed() {
        super.removed();
        if (!closingFromSave && blockEntity != null) {
            commitChanges();
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);

        guiGraphics.fill(panelLeft - 2, panelTop - 2, panelLeft + PANEL_WIDTH + 2, panelTop + panelHeight + 2, 0xFF1F1F1F);
        guiGraphics.fill(panelLeft, panelTop, panelLeft + PANEL_WIDTH, panelTop + panelHeight, 0xFFC6C6C6);
        guiGraphics.drawCenteredString(font, title, panelLeft + (PANEL_WIDTH / 2), panelTop + PANEL_PADDING, 0x404040);

        int labelX = panelLeft + PANEL_PADDING;
        guiGraphics.drawString(font, Component.translatable("fixture.dmxStart"), labelX, dmxAddressLabelY, 0x404040, false);
        guiGraphics.drawString(font, Component.translatable("artneti.dmxUniverse"), labelX, dmxUniverseLabelY, 0x404040, false);
        guiGraphics.drawCenteredString(font, Component.literal("Position"), panelLeft + (PANEL_WIDTH / 2), positionLabelY, 0x404040);
        guiGraphics.drawCenteredString(font, Component.literal("Network"), panelLeft + (PANEL_WIDTH / 2), networkLabelY, 0x404040);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static class PanTiltSlider extends AbstractSliderButton {

        private final int minValue;
        private final int maxValue;
        private final String label;
        private final java.util.function.IntConsumer onChange;

        private PanTiltSlider(int x, int y, int width, int value, int minValue, int maxValue, String label,
                              java.util.function.IntConsumer onChange) {
            super(x, y, width, WIDGET_HEIGHT, Component.empty(), 0.0D);
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.label = label;
            this.onChange = onChange;
            this.value = Mth.clamp((value - minValue) / (double) (maxValue - minValue), 0.0D, 1.0D);
            updateMessage();
        }

        private int getIntValue() {
            return Mth.clamp((int) Math.round(minValue + (value * (maxValue - minValue))), minValue, maxValue);
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.literal(label + ": " + getIntValue()));
        }

        @Override
        protected void applyValue() {
            onChange.accept(getIntValue());
        }
    }
}
