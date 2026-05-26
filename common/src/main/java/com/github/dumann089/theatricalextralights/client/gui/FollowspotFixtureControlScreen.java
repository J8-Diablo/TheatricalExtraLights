package com.github.dumann089.theatricalextralights.client.gui;

import com.github.dumann089.theatricalextralights.blockentities.FollowspotConsoleBlockEntity;
import com.github.dumann089.theatricalextralights.net.FollowspotConsoleControlPacket;
import com.github.dumann089.theatricalextralights.net.FollowspotExitControlPacket;
import com.github.dumann089.theatricalextralights.net.ModNetworkHandler;
import com.github.dumann089.theatricalextralights.util.FollowspotBeamHelper;
import com.mojang.blaze3d.platform.InputConstants;
import dev.imabad.theatrical.blockentities.light.BaseLightBlockEntity;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;

public class FollowspotFixtureControlScreen extends Screen {

    private static final int COLOR_HUD = 0xE0FFFFFF;
    private static final int COLOR_HUD_DIM = 0xB0B0B0BA;

    private final BlockPos consolePos;
    private final BlockPos fixturePos;
    private final FollowspotConsoleBlockEntity console;

    private int intensity;
    private int red;
    private int green;
    private int blue;
    private int focus;
    private int pan;
    private int tilt;

    private double lastMouseX;
    private double lastMouseY;
    private boolean mouseInitialized;
    private int controlSendCooldown;

    public FollowspotFixtureControlScreen(
            FollowspotConsoleBlockEntity console,
            BlockPos consolePos,
            BlockPos fixturePos,
            int intensity,
            int red,
            int green,
            int blue,
            int focus,
            int pan,
            int tilt
    ) {
        super(Component.translatable("screen.followspot_console.control_title"));
        this.console = console;
        this.consolePos = consolePos;
        this.fixturePos = fixturePos;
        this.intensity = intensity;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.focus = focus;
        this.pan = pan;
        this.tilt = tilt;
    }

    @Override
    protected void init() {
        super.init();
        if (minecraft != null) {
            minecraft.mouseHandler.grabMouse();
        }
        mouseInitialized = false;
    }

    @Override
    public void tick() {
        super.tick();
        if (controlSendCooldown > 0) {
            controlSendCooldown--;
        }
        handleMouseLook();
        handleMovementKeys();
        syncCamera();
    }

    private BaseLightBlockEntity getFixture() {
        if (minecraft == null || minecraft.level == null) {
            return null;
        }
        BlockEntity be = minecraft.level.getBlockEntity(fixturePos);
        return be instanceof BaseLightBlockEntity light ? light : null;
    }

    private void handleMouseLook() {
        if (minecraft == null || !minecraft.mouseHandler.isMouseGrabbed()) {
            return;
        }
        double mouseX = minecraft.mouseHandler.xpos();
        double mouseY = minecraft.mouseHandler.ypos();
        if (!mouseInitialized) {
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            mouseInitialized = true;
            return;
        }

        double dx = mouseX - lastMouseX;
        double dy = mouseY - lastMouseY;
        lastMouseX = mouseX;
        lastMouseY = mouseY;

        if (Math.abs(dx) < 0.001 && Math.abs(dy) < 0.001) {
            return;
        }

        float sensitivity = (float) (minecraft.options.sensitivity().get() * 0.8 + 0.15);
        pan = Mth.clamp(pan + (float) (dx * sensitivity * 0.35), -90, 90);
        tilt = Mth.clamp(tilt - (float) (dy * sensitivity * 0.35), -45, 45);
        sendControlIfReady();
    }

    private void handleMovementKeys() {
        if (minecraft == null) {
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
        if (changed) {
            sendControlIfReady();
        }
    }

    private void syncCamera() {
        BaseLightBlockEntity fixture = getFixture();
        if (fixture == null || minecraft == null || minecraft.player == null) {
            return;
        }
        FollowspotBeamHelper.applyCameraToPlayer(minecraft.player, fixture, pan, tilt);
    }

    private void sendControlIfReady() {
        if (controlSendCooldown > 0) {
            return;
        }
        ModNetworkHandler.CHANNEL.sendToServer(new FollowspotConsoleControlPacket(
                consolePos, intensity, red, green, blue, focus, pan, tilt
        ));
        controlSendCooldown = 2;
    }

    private static boolean isKeyDown(KeyMapping mapping) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) {
            return false;
        }
        return InputConstants.isKeyDown(minecraft.getWindow().getWindow(), mapping.getKey().getValue());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int cx = width / 2;
        int cy = height / 2;
        int cross = 8;
        guiGraphics.hLine(cx - cross, cx + cross, cy, 0xCCFFFFFF);
        guiGraphics.vLine(cx, cy - cross, cy + cross, 0xCCFFFFFF);
        guiGraphics.fill(cx - 1, cy - 1, cx + 2, cy + 2, 0xFFFF4040);

        guiGraphics.drawCenteredString(font, title, width / 2, 12, COLOR_HUD);
        guiGraphics.drawCenteredString(font,
                Component.translatable("screen.followspot_console.pan_tilt",
                        Integer.toString(pan), Integer.toString(tilt)),
                width / 2, 24, COLOR_HUD_DIM);
        guiGraphics.drawCenteredString(font,
                Component.translatable("screen.followspot_console.control_hint"),
                width / 2, height - 24, COLOR_HUD_DIM);
    }

    @Override
    public void onClose() {
        ModNetworkHandler.CHANNEL.sendToServer(new FollowspotExitControlPacket());
        if (minecraft != null && minecraft.mouseHandler.isMouseGrabbed()) {
            minecraft.mouseHandler.releaseMouse();
        }
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (minecraft != null && minecraft.options.keyInventory.matches(keyCode, scanCode)) {
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }
}
