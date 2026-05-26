package com.github.dumann089.theatricalextralights.client.gui;

import com.github.dumann089.theatricalextralights.blockentities.FollowspotConsoleBlockEntity;
import com.github.dumann089.theatricalextralights.client.followspot.FollowspotFixtureCameraSession;
import com.github.dumann089.theatricalextralights.net.FollowspotConsoleControlPacket;
import com.github.dumann089.theatricalextralights.net.FollowspotConsolePatchPacket;
import com.github.dumann089.theatricalextralights.net.ModNetworkHandler;
import com.github.dumann089.theatricalextralights.util.FollowspotTargetHelper;
import com.mojang.blaze3d.platform.InputConstants;
import dev.imabad.theatrical.TheatricalClient;
import dev.imabad.theatrical.blockentities.light.BaseLightBlockEntity;
import dev.imabad.theatrical.util.UUIDUtil;
import net.minecraft.client.KeyMapping;
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
import java.util.Optional;
import java.util.UUID;

public class FollowspotConsoleScreen extends Screen {

    private static final int PANEL_W = 400;
    private static final int PANEL_H = 318;
    private static final int PAD = 14;
    private static final int ROW_H = 18;
    private static final int GAP = 6;
    private static final int LABEL_COL = 72;
    private static final int HEADER_H = 38;

    private static final int BG = 0xFF222228;
    private static final int BORDER = 0xFF08080C;
    private static final int HEADER = 0xFF3A3A44;
    private static final int TITLE = 0xFFF4F4F8;
    private static final int SUB = 0xFF9A9AA8;
    private static final int LABEL = 0xFF80808C;
    private static final int TEXT = 0xFFE4E4EA;
    private static final int ACCENT = 0xFF6AAEF0;
    private static final int WARN = 0xFFEA9468;

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

    private int panelX;
    private int panelY;
    private int contentX;
    private int contentW;
    private int patchFieldsY;
    private int slidersY;
    private int buttonsY;

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

        panelX = (width - PANEL_W) / 2;
        panelY = (height - PANEL_H) / 2;
        contentX = panelX + PAD;
        contentW = PANEL_W - PAD * 2;

        patchFieldsY = panelY + HEADER_H + 34;
        slidersY = panelY + HEADER_H + 108;
        buttonsY = panelY + PANEL_H - PAD - ROW_H;

        int netW = 98;
        int fieldW = 50;
        int uniX = contentX + netW + 10;
        int addrX = uniX + fieldW + 12;

        networkButton = addRenderableWidget(Button.builder(getNetworkLabel(), b -> {
            currentNetworkIndex = (currentNetworkIndex + 1) % networkIds.size();
            b.setMessage(getNetworkLabel());
        }).bounds(contentX, patchFieldsY, netW, ROW_H).build());

        universeField = new EditBox(font, uniX, patchFieldsY, fieldW, ROW_H, Component.literal("U"));
        universeField.setFilter(v -> v.isEmpty() || v.matches("\\d+"));
        universeField.setValue(Integer.toString(console.getUniverse()));
        addRenderableWidget(universeField);

        addressField = new EditBox(font, addrX, patchFieldsY, fieldW, ROW_H, Component.literal("A"));
        addressField.setFilter(v -> v.isEmpty() || v.matches("\\d+"));
        addressField.setValue(Integer.toString(console.getDmxAddress()));
        addRenderableWidget(addressField);

        int sliderW = contentW - LABEL_COL - 30;
        int sx = contentX + LABEL_COL;
        int y = slidersY;
        focusSlider = addSlider(sx, y, sliderW, focus, v -> focus = v);
        y += ROW_H + GAP;
        redSlider = addSlider(sx, y, sliderW, red, v -> red = v);
        y += ROW_H + GAP;
        greenSlider = addSlider(sx, y, sliderW, green, v -> green = v);
        y += ROW_H + GAP;
        blueSlider = addSlider(sx, y, sliderW, blue, v -> blue = v);
        y += ROW_H + GAP;
        intensitySlider = addSlider(sx, y, sliderW, intensity, v -> intensity = v);

        int btnW = (contentW - 12) / 3;
        addRenderableWidget(Button.builder(Component.translatable("screen.followspot_console.link"), b -> sendPatch())
                .bounds(contentX, buttonsY, btnW, ROW_H).build());
        addRenderableWidget(Button.builder(Component.translatable("screen.followspot_console.control"), b -> enterFixtureControl())
                .bounds(contentX + btnW + 6, buttonsY, btnW, ROW_H).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), b -> onClose())
                .bounds(contentX + (btnW + 6) * 2, buttonsY, btnW, ROW_H).build());

        refreshLinkedFixture();
    }

    private ValueSlider addSlider(int x, int y, int w, int initial, java.util.function.IntConsumer onChange) {
        return addRenderableWidget(new ValueSlider(x, y, w, initial, onChange));
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
            for (UUID id : TheatricalClient.getArtNetManager().getKnownNetworks().keySet()) {
                if (!available.contains(id)) {
                    available.add(id);
                }
            }
        }
        networkIds = available;
        currentNetworkIndex = Math.max(networkIds.indexOf(console.getNetworkId()), 0);
    }

    private Component getNetworkLabel() {
        UUID id = networkIds.get(currentNetworkIndex);
        if (id.equals(UUIDUtil.NULL)) {
            return Component.literal("—");
        }
        if (TheatricalClient.getArtNetManager() == null) {
            return Component.translatable("screen.artnetconfig.network.unknown");
        }
        String name = TheatricalClient.getArtNetManager().getKnownNetworks().get(id);
        if (name == null) {
            return Component.literal("?");
        }
        return Component.literal(name.length() > 11 ? name.substring(0, 10) + "…" : name);
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

    private void enterFixtureControl() {
        if (linkedFixturePos == null) {
            return;
        }
        FollowspotFixtureCameraSession.start(
                console, consolePos, linkedFixturePos,
                intensity, red, green, blue, focus, pan, tilt
        );
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
            updateSliders();
        }
    }

    private void updateSliders() {
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
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (controlSendCooldown > 0) {
            controlSendCooldown--;
        }
        if (!isFieldFocused()) {
            handleMovementKeys();
        }
    }

    private boolean isFieldFocused() {
        return (universeField != null && universeField.isFocused())
                || (addressField != null && addressField.isFocused());
    }

    private void handleMovementKeys() {
        if (linkedFixturePos == null || minecraft == null) {
            return;
        }
        boolean changed = false;
        if (isKeyDown(minecraft.options.keyUp)) {
            tilt = Mth.clamp(tilt + 2, -45, 45);
            changed = true;
        }
        if (isKeyDown(minecraft.options.keyDown)) {
            tilt = Mth.clamp(tilt - 2, -45, 45);
            changed = true;
        }
        if (isKeyDown(minecraft.options.keyLeft)) {
            pan = Mth.clamp(pan - 2, -90, 90);
            changed = true;
        }
        if (isKeyDown(minecraft.options.keyRight)) {
            pan = Mth.clamp(pan + 2, -90, 90);
            changed = true;
        }
        if (changed && controlSendCooldown <= 0) {
            sendControl();
        }
    }

    private static boolean isKeyDown(KeyMapping mapping) {
        Minecraft mc = Minecraft.getInstance();
        return mc != null && InputConstants.isKeyDown(mc.getWindow().getWindow(), mapping.getDefaultKey().getValue());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (minecraft != null && minecraft.options.keyInventory.matches(keyCode, scanCode)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float pt) {
        renderBackground(g);

        g.fill(panelX - 1, panelY - 1, panelX + PANEL_W + 1, panelY + PANEL_H + 1, BORDER);
        g.fill(panelX, panelY, panelX + PANEL_W, panelY + PANEL_H, BG);
        g.fill(panelX, panelY, panelX + PANEL_W, panelY + HEADER_H, HEADER);

        g.drawCenteredString(font, title, panelX + PANEL_W / 2, panelY + 9, TITLE);
        g.drawCenteredString(font, Component.translatable("screen.followspot_console.subtitle"),
                panelX + PANEL_W / 2, panelY + 22, SUB);

        int netW = 98;
        int fieldW = 50;
        int uniX = contentX + netW + 10;
        int addrX = uniX + fieldW + 12;

        g.drawString(font, Component.translatable("screen.followspot_console.section_patch"),
                contentX, panelY + HEADER_H + 8, LABEL, false);
        g.drawString(font, Component.translatable("screen.artnetconfig.network"), contentX, patchFieldsY - 11, SUB, false);
        g.drawString(font, Component.translatable("artneti.dmxUniverse"), uniX, patchFieldsY - 11, SUB, false);
        g.drawString(font, Component.translatable("fixture.dmxStart"), addrX, patchFieldsY - 11, SUB, false);

        int statusColor = linkedFixturePos != null ? ACCENT : WARN;
        g.drawString(font, getLinkStatus(), contentX, patchFieldsY + ROW_H + 8, statusColor, false);

        if (minecraft != null && linkedFixturePos != null) {
            g.drawString(font, Component.translatable("screen.followspot_console.pan_tilt",
                    Integer.toString(pan), Integer.toString(tilt)),
                    contentX, panelY + HEADER_H + 72, TEXT, false);
            g.drawString(font, Component.translatable("screen.followspot_console.movement_hint",
                            keyLabel(minecraft.options.keyUp), keyLabel(minecraft.options.keyLeft),
                            keyLabel(minecraft.options.keyDown), keyLabel(minecraft.options.keyRight)),
                    contentX, panelY + HEADER_H + 84, SUB, false);
        }

        g.drawString(font, Component.translatable("screen.followspot_console.section_control"),
                contentX, slidersY - 11, LABEL, false);

        drawSliderRow(g, "screen.followspot_console.focus", slidersY, focusSlider);
        drawSliderRow(g, "screen.followspot_console.red", slidersY + ROW_H + GAP, redSlider);
        drawSliderRow(g, "screen.followspot_console.green", slidersY + (ROW_H + GAP) * 2, greenSlider);
        drawSliderRow(g, "screen.followspot_console.blue", slidersY + (ROW_H + GAP) * 3, blueSlider);
        drawSliderRow(g, "screen.followspot_console.intensity", slidersY + (ROW_H + GAP) * 4, intensitySlider);

        super.render(g, mouseX, mouseY, pt);
    }

    private void drawSliderRow(GuiGraphics g, String labelKey, int y, ValueSlider slider) {
        Component label = Component.translatable(labelKey);
        int maxLabelW = LABEL_COL - 4;
        String labelText = label.getString();
        if (font.width(labelText) > maxLabelW) {
            while (labelText.length() > 3 && font.width(labelText + "…") > maxLabelW) {
                labelText = labelText.substring(0, labelText.length() - 1);
            }
            labelText = labelText + "…";
        }
        g.drawString(font, labelText, contentX, y + 5, TEXT, false);
        if (slider != null) {
            String value = Integer.toString(slider.getIntValue());
            g.drawString(font, value, contentX + contentW - font.width(value), y + 5, SUB, false);
        }
    }

    private Component getLinkStatus() {
        if (!FollowspotTargetHelper.isValidNetwork(networkIds.get(currentNetworkIndex))) {
            return Component.translatable("screen.followspot_console.no_network");
        }
        if (linkedFixturePos == null) {
            return Component.translatable("screen.followspot_console.not_found",
                    parseOrDefault(universeField, 0), parseOrDefault(addressField, 0));
        }
        return Component.translatable("screen.followspot_console.linked",
                parseOrDefault(universeField, 0), parseOrDefault(addressField, 0));
    }

    private static String keyLabel(KeyMapping mapping) {
        return mapping.getTranslatedKeyMessage().getString();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private class ValueSlider extends AbstractSliderButton {
        private final java.util.function.IntConsumer onChange;

        ValueSlider(int x, int y, int w, int initial, java.util.function.IntConsumer onChange) {
            super(x, y, w, ROW_H, Component.empty(), initial / 255.0);
            this.onChange = onChange;
        }

        void setValue(int value) {
            this.value = Mth.clamp(value / 255.0, 0.0, 1.0);
        }

        int getIntValue() {
            return Mth.clamp((int) Math.round(value * 255.0), 0, 255);
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.empty());
        }

        @Override
        protected void applyValue() {
            onChange.accept(getIntValue());
            sendControl();
        }
    }
}
