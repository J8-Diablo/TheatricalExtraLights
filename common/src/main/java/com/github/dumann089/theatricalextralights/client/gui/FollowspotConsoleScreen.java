package com.github.dumann089.theatricalextralights.client.gui;

import com.github.dumann089.theatricalextralights.blockentities.FollowspotConsoleBlockEntity;
import com.github.dumann089.theatricalextralights.client.preview.FollowspotConsolePreview;
import com.github.dumann089.theatricalextralights.net.FollowspotConsoleControlPacket;
import com.github.dumann089.theatricalextralights.net.FollowspotConsolePatchPacket;
import com.github.dumann089.theatricalextralights.net.ModNetworkHandler;
import com.github.dumann089.theatricalextralights.util.FollowspotTargetHelper;
import dev.imabad.theatrical.TheatricalClient;
import dev.imabad.theatrical.blockentities.light.BaseLightBlockEntity;
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
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FollowspotConsoleScreen extends Screen {

    private static final int PANEL_WIDTH = 440;
    private static final int PANEL_PADDING = 14;
    private static final int WIDGET_HEIGHT = 18;
    private static final int ROW_GAP = 6;
    private static final int PREVIEW_WIDTH = 196;
    private static final int PREVIEW_HEIGHT = 112;

    private static final int COLOR_PANEL_BG = 0xFF2A2A2E;
    private static final int COLOR_PANEL_BORDER = 0xFF101014;
    private static final int COLOR_TITLE = 0xFFE8E8EC;
    private static final int COLOR_SUBTITLE = 0xFFB0B0BA;
    private static final int COLOR_TEXT = 0xFFD8D8DE;
    private static final int COLOR_ACCENT = 0xFF4A90D9;
    private static final int COLOR_WARNING = 0xFFE07040;
    private static final int COLOR_PREVIEW_BG = 0xFF0D0D12;

    private record ColorPreset(String label, int r, int g, int b) {
    }

    private static final ColorPreset[] COLOR_PRESETS = {
            new ColorPreset("W", 255, 255, 255),
            new ColorPreset("Warm", 255, 180, 100),
            new ColorPreset("R", 255, 0, 0),
            new ColorPreset("G", 0, 255, 0),
            new ColorPreset("B", 0, 0, 255),
            new ColorPreset("C", 0, 255, 255),
            new ColorPreset("M", 255, 0, 255),
            new ColorPreset("A", 255, 160, 0),
    };

    private final BlockPos consolePos;
    private final FollowspotConsoleBlockEntity console;

    private EditBox universeField;
    private EditBox addressField;
    private Button networkButton;
    private ValueSlider focusSlider;
    private ValueSlider redSlider;
    private ValueSlider greenSlider;
    private ValueSlider blueSlider;
    private ValueSlider intensitySlider;

    private List<UUID> networkIds = List.of(UUIDUtil.NULL);
    private int currentNetworkIndex;

    private int intensity;
    private int red;
    private int green;
    private int blue;
    private int focus;
    private int pan;
    private int tilt;

    private int panelLeft;
    private int panelTop;
    private int panelHeight;
    private int contentLeft;
    private int contentWidth;
    private int previewLeft;
    private int previewTop;

    private int controlSendCooldown;
    private BlockPos linkedFixturePos;

    public FollowspotConsoleScreen(FollowspotConsoleBlockEntity console, BlockPos consolePos) {
        super(Component.translatable("screen.followspot_console.title"));
        this.console = console;
        this.consolePos = consolePos;
    }

    @Override
    protected void init() {
        loadFromConsole();
        setupNetworks();

        panelHeight = 360;
        panelLeft = (width - PANEL_WIDTH) / 2;
        panelTop = (height - panelHeight) / 2;
        contentLeft = panelLeft + PANEL_PADDING;
        contentWidth = PANEL_WIDTH - PANEL_PADDING * 2;
        previewLeft = contentLeft;
        previewTop = panelTop + 118;

        int rightColumn = contentLeft + PREVIEW_WIDTH + 12;
        int rightWidth = contentWidth - PREVIEW_WIDTH - 12;
        int y = panelTop + 72;

        universeField = new EditBox(font, contentLeft, y, 70, WIDGET_HEIGHT, Component.literal("U"));
        universeField.setFilter(value -> value.isEmpty() || value.matches("\\d+"));
        universeField.setValue(Integer.toString(console.getUniverse()));
        addRenderableWidget(universeField);

        addressField = new EditBox(font, contentLeft + 78, y, 70, WIDGET_HEIGHT, Component.literal("A"));
        addressField.setFilter(value -> value.isEmpty() || value.matches("\\d+"));
        addressField.setValue(Integer.toString(console.getDmxAddress()));
        addRenderableWidget(addressField);

        networkButton = addRenderableWidget(Button.builder(getNetworkLabel(), button -> {
            currentNetworkIndex = (currentNetworkIndex + 1) % networkIds.size();
            button.setMessage(getNetworkLabel());
        }).bounds(contentLeft + 156, y, contentWidth - 156, WIDGET_HEIGHT).build());

        y = previewTop + PREVIEW_HEIGHT + 14;
        focusSlider = addValueSlider(rightColumn, y, rightWidth, "screen.followspot_console.focus", focus, value -> focus = value);
        y += WIDGET_HEIGHT + ROW_GAP;
        redSlider = addValueSlider(rightColumn, y, rightWidth, "screen.followspot_console.red", red, value -> red = value);
        y += WIDGET_HEIGHT + ROW_GAP;
        greenSlider = addValueSlider(rightColumn, y, rightWidth, "screen.followspot_console.green", green, value -> green = value);
        y += WIDGET_HEIGHT + ROW_GAP;
        blueSlider = addValueSlider(rightColumn, y, rightWidth, "screen.followspot_console.blue", blue, value -> blue = value);
        y += WIDGET_HEIGHT + ROW_GAP;
        intensitySlider = addValueSlider(rightColumn, y, rightWidth, "screen.followspot_console.intensity", intensity,
                value -> intensity = value);

        y = previewTop + PREVIEW_HEIGHT + 14 + (WIDGET_HEIGHT + ROW_GAP) * 5 + 4;
        int presetWidth = (rightWidth - (COLOR_PRESETS.length - 1) * 3) / COLOR_PRESETS.length;
        for (int i = 0; i < COLOR_PRESETS.length; i++) {
            ColorPreset preset = COLOR_PRESETS[i];
            int px = rightColumn + i * (presetWidth + 3);
            addRenderableWidget(Button.builder(Component.literal(preset.label()), button -> applyPreset(preset))
                    .bounds(px, y, presetWidth, 16)
                    .build());
        }

        int buttonWidth = 88;
        int buttonsY = panelTop + panelHeight - PANEL_PADDING - WIDGET_HEIGHT;
        addRenderableWidget(Button.builder(Component.translatable("screen.followspot_console.link"), button -> sendPatch())
                .bounds(contentLeft, buttonsY, buttonWidth, WIDGET_HEIGHT).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), button -> onClose())
                .bounds(contentLeft + buttonWidth + 8, buttonsY, buttonWidth, WIDGET_HEIGHT).build());

        refreshLinkedFixture();
    }

    private ValueSlider addValueSlider(int x, int y, int width, String labelKey, int initial,
                                       java.util.function.IntConsumer onChange) {
        return addRenderableWidget(new ValueSlider(x, y, width, initial, Component.translatable(labelKey), onChange));
    }

    private void loadFromConsole() {
        intensity = console.getIntensity();
        red = console.getRed();
        green = console.getGreen();
        blue = console.getBlue();
        focus = console.getFocus();
        pan = console.getPan();
        tilt = console.getTilt();
    }

    private void setupNetworks() {
        ArrayList<UUID> available = new ArrayList<>();
        available.add(UUIDUtil.NULL);
        if (TheatricalClient.getArtNetManager() != null) {
            for (UUID networkId : TheatricalClient.getArtNetManager().getKnownNetworks().keySet()) {
                if (!available.contains(networkId)) {
                    available.add(networkId);
                }
            }
        }
        networkIds = available;
        currentNetworkIndex = Math.max(networkIds.indexOf(console.getNetworkId()), 0);
    }

    private Component getNetworkLabel() {
        UUID networkId = networkIds.get(currentNetworkIndex);
        if (networkId.equals(UUIDUtil.NULL)) {
            return Component.literal("—");
        }
        if (TheatricalClient.getArtNetManager() == null) {
            return Component.translatable("screen.artnetconfig.network.unknown");
        }
        String name = TheatricalClient.getArtNetManager().getKnownNetworks().get(networkId);
        return Component.literal(name != null ? name : "?");
    }

    private void applyPreset(ColorPreset preset) {
        red = preset.r();
        green = preset.g();
        blue = preset.b();
        if (redSlider != null) {
            redSlider.setValue(red);
            greenSlider.setValue(green);
            blueSlider.setValue(blue);
        }
        sendControl();
    }

    private void sendPatch() {
        UUID networkId = networkIds.get(currentNetworkIndex);
        int universe = parseOrDefault(universeField, console.getUniverse());
        int address = parseOrDefault(addressField, console.getDmxAddress());
        ModNetworkHandler.CHANNEL.sendToServer(new FollowspotConsolePatchPacket(consolePos, networkId, universe, address));
        console.setNetworkId(networkId);
        console.setUniverse(universe);
        console.setDmxAddress(address);
        refreshLinkedFixture();
    }

    private void sendControl() {
        ModNetworkHandler.CHANNEL.sendToServer(new FollowspotConsoleControlPacket(
                consolePos, intensity, red, green, blue, focus, pan, tilt
        ));
        controlSendCooldown = 2;
    }

    private void refreshLinkedFixture() {
        if (minecraft == null || minecraft.level == null) {
            linkedFixturePos = null;
            return;
        }
        Optional<FollowspotTargetHelper.TargetMatch> target = FollowspotTargetHelper.findTarget(
                minecraft.level,
                networkIds.get(currentNetworkIndex),
                parseOrDefault(universeField, console.getUniverse()),
                parseOrDefault(addressField, console.getDmxAddress()),
                consolePos
        );
        linkedFixturePos = target.map(FollowspotTargetHelper.TargetMatch::pos).orElse(null);
        if (target.isPresent()) {
            BaseLightBlockEntity light = target.get().fixture();
            pan = light.getPan();
            tilt = light.getTilt();
            focus = light.getFocus();
            intensity = (int) light.getIntensity();
            red = light.getRed();
            green = light.getGreen();
            blue = light.getBlue();
            updateSlidersFromState();
        }
    }

    private void updateSlidersFromState() {
        if (focusSlider == null) {
            return;
        }
        focusSlider.setValue(focus);
        redSlider.setValue(red);
        greenSlider.setValue(green);
        blueSlider.setValue(blue);
        intensitySlider.setValue(intensity);
    }

    private int parseOrDefault(EditBox field, int fallback) {
        try {
            return Integer.parseInt(field.getValue());
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (controlSendCooldown > 0) {
            controlSendCooldown--;
        }
        handleMovementKeys();
    }

    private void handleMovementKeys() {
        if (linkedFixturePos == null || minecraft == null) {
            return;
        }
        var options = minecraft.options;
        boolean changed = false;
        if (options.keyUp.isDown()) {
            tilt = Mth.clamp(tilt + 2, -45, 45);
            changed = true;
        }
        if (options.keyDown.isDown()) {
            tilt = Mth.clamp(tilt - 2, -45, 45);
            changed = true;
        }
        if (options.keyLeft.isDown()) {
            pan = Mth.clamp(pan - 2, -90, 90);
            changed = true;
        }
        if (options.keyRight.isDown()) {
            pan = Mth.clamp(pan + 2, -90, 90);
            changed = true;
        }
        if (changed && controlSendCooldown <= 0) {
            sendControl();
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (minecraft != null && minecraft.options.keyInventory.matches(keyCode, scanCode)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);

        guiGraphics.fill(panelLeft - 1, panelTop - 1, panelLeft + PANEL_WIDTH + 1, panelTop + panelHeight + 1,
                COLOR_PANEL_BORDER);
        guiGraphics.fill(panelLeft, panelTop, panelLeft + PANEL_WIDTH, panelTop + panelHeight, COLOR_PANEL_BG);

        guiGraphics.drawCenteredString(font, title, panelLeft + PANEL_WIDTH / 2, panelTop + PANEL_PADDING, COLOR_TITLE);
        guiGraphics.drawCenteredString(font, Component.translatable("screen.followspot_console.subtitle"),
                panelLeft + PANEL_WIDTH / 2, panelTop + PANEL_PADDING + 12, COLOR_SUBTITLE);

        guiGraphics.drawString(font, Component.translatable("screen.artnetconfig.network"), contentLeft,
                panelTop + 58, COLOR_TEXT, false);
        guiGraphics.drawString(font, Component.translatable("artneti.dmxUniverse"), contentLeft, panelTop + 72 - 10,
                COLOR_SUBTITLE, false);
        guiGraphics.drawString(font, Component.translatable("fixture.dmxStart"), contentLeft + 78, panelTop + 72 - 10,
                COLOR_SUBTITLE, false);

        Component linkStatus = getLinkStatus();
        int linkColor = linkedFixturePos != null ? COLOR_ACCENT : COLOR_WARNING;
        guiGraphics.drawString(font, linkStatus, contentLeft, panelTop + 96, linkColor, false);

        guiGraphics.fill(previewLeft, previewTop, previewLeft + PREVIEW_WIDTH, previewTop + PREVIEW_HEIGHT, COLOR_PREVIEW_BG);
        guiGraphics.renderOutline(previewLeft, previewTop, PREVIEW_WIDTH, PREVIEW_HEIGHT, COLOR_ACCENT);

        if (minecraft != null && minecraft.level != null && linkedFixturePos != null) {
            BlockEntity be = minecraft.level.getBlockEntity(linkedFixturePos);
            if (be instanceof BaseLightBlockEntity light) {
                FollowspotConsolePreview.render(guiGraphics, previewLeft, previewTop, PREVIEW_WIDTH, PREVIEW_HEIGHT, light);
            }
        } else {
            guiGraphics.drawCenteredString(font, Component.translatable("screen.followspot_console.no_fixture"),
                    previewLeft + PREVIEW_WIDTH / 2, previewTop + PREVIEW_HEIGHT / 2 - 4, COLOR_SUBTITLE);
        }

        guiGraphics.drawString(font, Component.translatable("screen.followspot_console.movement_hint",
                        getKeyLabel(minecraft.options.keyUp),
                        getKeyLabel(minecraft.options.keyLeft),
                        getKeyLabel(minecraft.options.keyDown),
                        getKeyLabel(minecraft.options.keyRight)),
                previewLeft + PREVIEW_WIDTH + 12, previewTop, COLOR_TEXT, false);
        guiGraphics.drawString(font, Component.translatable("screen.followspot_console.pan_tilt",
                        Integer.toString(pan), Integer.toString(tilt)),
                previewLeft + PREVIEW_WIDTH + 12, previewTop + 14, COLOR_SUBTITLE, false);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private Component getLinkStatus() {
        if (!FollowspotTargetHelper.isValidNetwork(networkIds.get(currentNetworkIndex))) {
            return Component.translatable("screen.followspot_console.no_network");
        }
        if (linkedFixturePos == null) {
            return Component.translatable("screen.followspot_console.not_found",
                    Integer.toString(parseOrDefault(universeField, 0)),
                    Integer.toString(parseOrDefault(addressField, 0)));
        }
        return Component.translatable("screen.followspot_console.linked",
                Integer.toString(parseOrDefault(universeField, 0)),
                Integer.toString(parseOrDefault(addressField, 0)));
    }

    private static String getKeyLabel(net.minecraft.client.KeyMapping mapping) {
        return mapping.getTranslatedKeyMessage().getString();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private class ValueSlider extends AbstractSliderButton {
        private final Component label;
        private final java.util.function.IntConsumer onChange;

        private ValueSlider(int x, int y, int width, int initial, Component label,
                            java.util.function.IntConsumer onChange) {
            super(x, y, width, WIDGET_HEIGHT, Component.empty(), initial / 255.0);
            this.label = label;
            this.onChange = onChange;
            updateMessage();
        }

        void setValue(int value) {
            this.value = Mth.clamp(value / 255.0, 0.0, 1.0);
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(label.copy().append(": " + getIntValue()));
        }

        @Override
        protected void applyValue() {
            onChange.accept(getIntValue());
            sendControl();
        }

        private int getIntValue() {
            return Mth.clamp((int) Math.round(value * 255.0), 0, 255);
        }
    }
}
