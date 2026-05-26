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

/**
 * Fixture DMX configuration — layout label au-dessus des champs, sans texte superposé aux boutons.
 */
public class ExtraLightsConfigScreen extends Screen {

    private static final int PANEL_WIDTH = 300;
    private static final int PANEL_PADDING = 16;
    private static final int WIDGET_HEIGHT = 20;
    private static final int LABEL_GAP = 10;
    private static final int ROW_GAP = 8;

    private static final int COLOR_PANEL_BG = 0xFFC6C6C6;
    private static final int COLOR_PANEL_BORDER = 0xFF1F1F1F;
    private static final int COLOR_TEXT = 0x404040;
    private static final int COLOR_SECTION = 0x606060;

    private final BaseDMXConsumerLightBlockEntity blockEntity;
    private final BlockPos pos;
    private final boolean showPositionControls;

    private EditBox dmxAddressField;
    private EditBox dmxUniverseField;
    private PanTiltSlider tiltSlider;
    private PanTiltSlider panSlider;
    private Button personalityButton;
    private Button networkButton;

    private List<DMXPersonality> personalities = List.of();
    private int currentPersonalityIndex;

    private List<UUID> networkIds = List.of(UUIDUtil.NULL);
    private int currentNetworkIndex;

    private int panelLeft;
    private int panelTop;
    private int panelHeight;
    private int contentLeft;
    private int contentWidth;

    private int dmxAddressLabelY;
    private int dmxUniverseLabelY;
    private int positionSectionY;
    private int tiltLabelY;
    private int panLabelY;
    private int personalityLabelY;
    private int networkLabelY;

    public ExtraLightsConfigScreen(BaseDMXConsumerLightBlockEntity blockEntity, BlockPos pos, String title) {
        this(blockEntity, pos, title, true);
    }

    public ExtraLightsConfigScreen(BaseDMXConsumerLightBlockEntity blockEntity, BlockPos pos, String title,
                                   boolean showPositionControls) {
        super(Component.translatable(title));
        this.blockEntity = blockEntity;
        this.pos = pos;
        this.showPositionControls = showPositionControls;
    }

    @Override
    protected void init() {
        super.init();

        if (blockEntity == null) {
            Minecraft.getInstance().setScreen(null);
            return;
        }

        setupState();
        layoutPanel();
        buildWidgets();
    }

    private void setupState() {
        if (blockEntity instanceof HasPersonality) {
            personalities = blockEntity.getFixture().getDMXPersonalities();
            if (personalities == null) {
                personalities = List.of();
            }
            currentPersonalityIndex = Mth.clamp(
                    ((HasPersonality) blockEntity).getActivePersonality(),
                    0,
                    Math.max(personalities.size() - 1, 0)
            );
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

    private void layoutPanel() {
        int rows = 2; // dmx + universe
        if (showPositionControls) {
            rows += 3; // section + tilt + pan
        }
        if (hasPersonalityOptions()) {
            rows += 1;
        }
        rows += 1; // network
        rows += 1; // buttons row

        panelHeight = PANEL_PADDING * 2 + 18 + (rows * (LABEL_GAP + WIDGET_HEIGHT + ROW_GAP)) + 4;
        panelLeft = (width - PANEL_WIDTH) / 2;
        panelTop = (height - panelHeight) / 2;
        contentLeft = panelLeft + PANEL_PADDING;
        contentWidth = PANEL_WIDTH - PANEL_PADDING * 2;
    }

    private void buildWidgets() {
        clearWidgets();

        int y = panelTop + PANEL_PADDING + 22;

        dmxAddressLabelY = y;
        y += LABEL_GAP;
        dmxAddressField = new EditBox(font, contentLeft, y, contentWidth, WIDGET_HEIGHT,
                Component.translatable("fixture.dmxStart"));
        dmxAddressField.setFilter(this::isIntegerInput);
        dmxAddressField.setValue(Integer.toString(blockEntity.getChannelStart()));
        addRenderableWidget(dmxAddressField);
        y += WIDGET_HEIGHT + ROW_GAP;

        dmxUniverseLabelY = y;
        y += LABEL_GAP;
        dmxUniverseField = new EditBox(font, contentLeft, y, contentWidth, WIDGET_HEIGHT,
                Component.translatable("artneti.dmxUniverse"));
        dmxUniverseField.setFilter(this::isIntegerInput);
        dmxUniverseField.setValue(Integer.toString(blockEntity.getUniverse()));
        addRenderableWidget(dmxUniverseField);
        y += WIDGET_HEIGHT + ROW_GAP;

        if (showPositionControls) {
            positionSectionY = y;
            y += LABEL_GAP;

            tiltLabelY = y;
            y += LABEL_GAP;
            tiltSlider = addRenderableWidget(new PanTiltSlider(
                    contentLeft, y, contentWidth,
                    blockEntity.getTilt(), -90, 90,
                    Component.translatable("fixture.tilt"),
                    this::applyTilt
            ));
            y += WIDGET_HEIGHT + ROW_GAP;

            panLabelY = y;
            y += LABEL_GAP;
            panSlider = addRenderableWidget(new PanTiltSlider(
                    contentLeft, y, contentWidth,
                    blockEntity.getPan(), -180, 180,
                    Component.translatable("fixture.pan"),
                    this::applyPan
            ));
            y += WIDGET_HEIGHT + ROW_GAP;
        } else {
            tiltSlider = null;
            panSlider = null;
        }

        if (hasPersonalityOptions()) {
            personalityLabelY = y;
            y += LABEL_GAP;
            personalityButton = addRenderableWidget(Button.builder(
                    getPersonalityValue(),
                    button -> {
                        currentPersonalityIndex = (currentPersonalityIndex + 1) % personalities.size();
                        button.setMessage(getPersonalityValue());
                    }
            ).bounds(contentLeft, y, contentWidth, WIDGET_HEIGHT).build());
            y += WIDGET_HEIGHT + ROW_GAP;
        } else {
            personalityButton = null;
        }

        networkLabelY = y;
        y += LABEL_GAP;
        networkButton = addRenderableWidget(Button.builder(
                getNetworkValue(),
                button -> {
                    currentNetworkIndex = (currentNetworkIndex + 1) % networkIds.size();
                    button.setMessage(getNetworkValue());
                }
        ).bounds(contentLeft, y, contentWidth, WIDGET_HEIGHT).build());
        y += WIDGET_HEIGHT + ROW_GAP + 4;

        int buttonWidth = 90;
        int gap = 10;
        int buttonsWidth = buttonWidth * 2 + gap;
        int buttonsX = panelLeft + (PANEL_WIDTH - buttonsWidth) / 2;

        addRenderableWidget(Button.builder(
                Component.translatable("artneti.save"),
                button -> {
                    commitChanges();
                    onClose();
                }
        ).bounds(buttonsX, y, buttonWidth, WIDGET_HEIGHT).build());

        addRenderableWidget(Button.builder(
                Component.translatable("gui.cancel"),
                button -> onClose()
        ).bounds(buttonsX + buttonWidth + gap, y, buttonWidth, WIDGET_HEIGHT).build());
    }

    private boolean hasPersonalityOptions() {
        return personalities.size() > 1;
    }

    private boolean isIntegerInput(String value) {
        return value.isEmpty() || value.matches("\\d+");
    }

    private Component getPersonalityValue() {
        return Component.literal(personalities.get(currentPersonalityIndex).getDescription());
    }

    private Component getNetworkValue() {
        UUID networkId = networkIds.get(currentNetworkIndex);
        if (networkId.equals(UUIDUtil.NULL)) {
            return Component.literal("—");
        }
        if (TheatricalClient.getArtNetManager() == null) {
            return Component.translatable("screen.artnetconfig.network.unknown");
        }
        String name = TheatricalClient.getArtNetManager().getKnownNetworks().get(networkId);
        return Component.literal(name != null ? name : "Unknown");
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

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);

        guiGraphics.fill(panelLeft - 2, panelTop - 2, panelLeft + PANEL_WIDTH + 2, panelTop + panelHeight + 2,
                COLOR_PANEL_BORDER);
        guiGraphics.fill(panelLeft, panelTop, panelLeft + PANEL_WIDTH, panelTop + panelHeight, COLOR_PANEL_BG);

        guiGraphics.drawCenteredString(font, title, panelLeft + PANEL_WIDTH / 2, panelTop + PANEL_PADDING, COLOR_TEXT);

        drawFieldLabel(guiGraphics, Component.translatable("fixture.dmxStart"), dmxAddressLabelY);
        drawFieldLabel(guiGraphics, Component.translatable("artneti.dmxUniverse"), dmxUniverseLabelY);

        if (showPositionControls) {
            guiGraphics.drawCenteredString(font, Component.translatable("fixture.position"),
                    panelLeft + PANEL_WIDTH / 2, positionSectionY, COLOR_SECTION);
            drawFieldLabel(guiGraphics, Component.translatable("fixture.tilt"), tiltLabelY);
            drawFieldLabel(guiGraphics, Component.translatable("fixture.pan"), panLabelY);
        }

        if (hasPersonalityOptions()) {
            drawFieldLabel(guiGraphics, Component.translatable("fixture.personality"), personalityLabelY);
        }

        drawFieldLabel(guiGraphics, Component.translatable("screen.artnetconfig.network"), networkLabelY);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void drawFieldLabel(GuiGraphics guiGraphics, Component label, int y) {
        guiGraphics.drawString(font, label, contentLeft, y, COLOR_TEXT, false);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static class PanTiltSlider extends AbstractSliderButton {

        private final int minValue;
        private final int maxValue;
        private final java.util.function.IntConsumer onChange;

        private PanTiltSlider(int x, int y, int width, int value, int minValue, int maxValue,
                              Component label, java.util.function.IntConsumer onChange) {
            super(x, y, width, WIDGET_HEIGHT, Component.empty(), 0.0D);
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.onChange = onChange;
            this.value = Mth.clamp((value - minValue) / (double) (maxValue - minValue), 0.0D, 1.0D);
            updateMessage();
        }

        private int getIntValue() {
            return Mth.clamp((int) Math.round(minValue + (value * (maxValue - minValue))), minValue, maxValue);
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.literal(Integer.toString(getIntValue())));
        }

        @Override
        protected void applyValue() {
            onChange.accept(getIntValue());
        }
    }
}
