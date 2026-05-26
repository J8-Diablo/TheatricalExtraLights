package com.github.dumann089.theatricalextralights.client.followspot;

import com.github.dumann089.theatricalextralights.blockentities.FollowspotConsoleBlockEntity;
import com.github.dumann089.theatricalextralights.net.FollowspotConsoleControlPacket;
import com.github.dumann089.theatricalextralights.net.ModNetworkHandler;
import com.github.dumann089.theatricalextralights.util.FollowspotBeamHelper;
import com.mojang.blaze3d.platform.InputConstants;
import dev.imabad.theatrical.blockentities.light.BaseLightBlockEntity;
import net.minecraft.client.Camera;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.lwjgl.glfw.GLFW;

/**
 * Client-only first-person fixture view. Moves the render camera to the lens without teleporting the player.
 */
public final class FollowspotFixtureCameraSession {

    private static FollowspotFixtureCameraSession active;

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

    private int controlSendCooldown;
    private boolean mouseGrabbed;

    private FollowspotFixtureCameraSession(
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

    public static boolean isActive() {
        return active != null;
    }

    public static FollowspotFixtureCameraSession getActive() {
        return active;
    }

    public static void start(
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
        active = new FollowspotFixtureCameraSession(
                console, consolePos, fixturePos,
                intensity, red, green, blue, focus, pan, tilt
        );
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft != null) {
            minecraft.setScreen(null);
            minecraft.mouseHandler.grabMouse();
            active.mouseGrabbed = true;
        }
    }

    public static void stop() {
        if (active == null) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft != null && active.mouseGrabbed && minecraft.mouseHandler.isMouseGrabbed()) {
            minecraft.mouseHandler.releaseMouse();
        }
        active = null;
    }

    public void tick(Minecraft minecraft) {
        if (minecraft.player == null || minecraft.level == null) {
            stop();
            return;
        }

        if (!mouseGrabbed || !minecraft.mouseHandler.isMouseGrabbed()) {
            minecraft.mouseHandler.grabMouse();
            mouseGrabbed = true;
        }

        if (isKeyDown(minecraft, GLFW.GLFW_KEY_ESCAPE)) {
            stop();
            return;
        }

        handleMouseLook(minecraft);
        handleMovementKeys(minecraft);

        // Keep the player at the desk — only the render camera moves to the fixture.
        var player = minecraft.player;
        player.setDeltaMovement(0, 0, 0);
        player.setYRot(player.yRotO);
        player.setXRot(player.xRotO);

        if (controlSendCooldown > 0) {
            controlSendCooldown--;
        }
    }

    public void applyCamera(Camera camera) {
        BaseLightBlockEntity fixture = getFixture();
        if (fixture == null) {
            return;
        }
        var origin = FollowspotBeamHelper.getBeamOrigin(fixture);
        float[] look = FollowspotBeamHelper.getLookAngles(fixture, pan, tilt);
        FollowspotCameraAccess.configure(camera, origin, look[0], look[1]);
    }

    private BaseLightBlockEntity getFixture() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.level == null) {
            return null;
        }
        BlockEntity be = minecraft.level.getBlockEntity(fixturePos);
        return be instanceof BaseLightBlockEntity light ? light : null;
    }

    private void handleMouseLook(Minecraft minecraft) {
        long window = minecraft.getWindow().getWindow();
        double centerX = minecraft.getWindow().getScreenWidth() / 2.0;
        double centerY = minecraft.getWindow().getScreenHeight() / 2.0;

        double[] mx = new double[1];
        double[] my = new double[1];
        GLFW.glfwGetCursorPos(window, mx, my);

        double dx = mx[0] - centerX;
        double dy = my[0] - centerY;
        if (Math.abs(dx) < 0.5 && Math.abs(dy) < 0.5) {
            return;
        }

        float sensitivity = (float) (minecraft.options.sensitivity().get() * 0.6 + 0.2);
        pan = (int) Mth.clamp(pan + dx * sensitivity * 0.06, -90, 90);
        tilt = (int) Mth.clamp(tilt - dy * sensitivity * 0.06, -45, 45);
        GLFW.glfwSetCursorPos(window, centerX, centerY);
        sendControlIfReady();
    }

    private void handleMovementKeys(Minecraft minecraft) {
        boolean changed = false;
        if (isKeyDown(minecraft, minecraft.options.keyUp)) {
            tilt = Mth.clamp(tilt + 2, -45, 45);
            changed = true;
        }
        if (isKeyDown(minecraft, minecraft.options.keyDown)) {
            tilt = Mth.clamp(tilt - 2, -45, 45);
            changed = true;
        }
        if (isKeyDown(minecraft, minecraft.options.keyLeft)) {
            pan = Mth.clamp(pan - 2, -90, 90);
            changed = true;
        }
        if (isKeyDown(minecraft, minecraft.options.keyRight)) {
            pan = Mth.clamp(pan + 2, -90, 90);
            changed = true;
        }
        if (changed) {
            sendControlIfReady();
        }
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

    private static boolean isKeyDown(Minecraft minecraft, KeyMapping mapping) {
        return InputConstants.isKeyDown(minecraft.getWindow().getWindow(), mapping.getDefaultKey().getValue());
    }

    private static boolean isKeyDown(Minecraft minecraft, int glfwKey) {
        return InputConstants.isKeyDown(minecraft.getWindow().getWindow(), glfwKey);
    }
}
