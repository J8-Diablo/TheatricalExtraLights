package com.github.dumann089.theatricalextralights.client.followspot;

import com.github.dumann089.theatricalextralights.blockentities.ExtraLightsLightBlockEntity;
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
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.lwjgl.glfw.GLFW;

/**
 * Client-only operator view beside the fixture. Camera and beam use the same float angles.
 */
public final class FollowspotFixtureCameraSession {

    private static final float MOVE_SPEED = 1.15f;

    private static FollowspotFixtureCameraSession active;

    private final BlockPos consolePos;
    private final BlockPos fixturePos;

    private int intensity;
    private int red;
    private int green;
    private int blue;
    private int focus;
    private float panAngle;
    private float tiltAngle;

    private int controlSendCooldown;
    private int actionBarCooldown;
    private boolean wasMoving;

    private FollowspotFixtureCameraSession(
            BlockPos consolePos,
            BlockPos fixturePos,
            int intensity,
            int red,
            int green,
            int blue,
            int focus,
            float pan,
            float tilt
    ) {
        this.consolePos = consolePos;
        this.fixturePos = fixturePos;
        this.intensity = intensity;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.focus = focus;
        this.panAngle = pan;
        this.tiltAngle = tilt;
    }

    public static boolean isActive() {
        return active != null;
    }

    public static FollowspotFixtureCameraSession getActive() {
        return active;
    }

    public static boolean isControlling(BlockPos fixturePos) {
        return active != null && active.fixturePos.equals(fixturePos);
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
                consolePos, fixturePos,
                intensity, red, green, blue, focus, pan, tilt
        );
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft != null) {
            minecraft.setScreen(null);
            if (minecraft.mouseHandler.isMouseGrabbed()) {
                minecraft.mouseHandler.releaseMouse();
            }
            active.showExitHint(minecraft);
            active.applyLocalFixtureState();
            active.sendControlNow();
        }
        registerPlatformCameraHook();
    }

    private static void registerPlatformCameraHook() {
        try {
            Class<?> forgeHook = Class.forName(
                    "com.github.dumann089.theatricalextralights.forge.FollowspotCameraForge"
            );
            forgeHook.getMethod("ensureRegistered").invoke(null);
        } catch (ReflectiveOperationException ignored) {
            // Fabric client hook
        }
    }

    public static void stop() {
        if (active != null) {
            active.sendControlNow();
        }
        active = null;
        com.github.dumann089.theatricalextralights.client.blockentities.FollowspotRenderer.resetBeamLengthSmoothing();
    }

    public void tick(Minecraft minecraft) {
        if (minecraft.player == null || minecraft.level == null) {
            stop();
            return;
        }

        if (isKeyDown(minecraft, GLFW.GLFW_KEY_ESCAPE)) {
            stop();
            return;
        }

        if (actionBarCooldown > 0) {
            actionBarCooldown--;
        } else {
            showExitHint(minecraft);
        }

        handleMovementKeys(minecraft);
        applyLocalFixtureState();

        var player = minecraft.player;
        player.setDeltaMovement(0, 0, 0);
        player.setYRot(player.yRotO);
        player.setXRot(player.xRotO);

        if (controlSendCooldown > 0) {
            controlSendCooldown--;
        }
    }

    public record CameraState(net.minecraft.world.phys.Vec3 position, float yaw, float pitch) {
    }

    public CameraState getCameraState() {
        BaseLightBlockEntity fixture = getFixture();
        if (fixture == null) {
            return null;
        }
        float[] look = FollowspotBeamHelper.getLookAngles(fixture, panAngle, tiltAngle);
        return new CameraState(
                FollowspotBeamHelper.getCameraPosition(fixture, panAngle, tiltAngle),
                look[0],
                look[1]
        );
    }

    public void applyCamera(Camera camera) {
        CameraState state = getCameraState();
        if (state == null) {
            return;
        }
        FollowspotCameraAccess.trySetPosition(camera, state.position());
    }

    public BlockPos getFixturePos() {
        return fixturePos;
    }

    public float getPanAngle() {
        return panAngle;
    }

    public float getTiltAngle() {
        return tiltAngle;
    }

    public int getPan() {
        return Math.round(panAngle);
    }

    public int getTilt() {
        return Math.round(tiltAngle);
    }

    private void showExitHint(Minecraft minecraft) {
        if (minecraft.player != null) {
            minecraft.player.displayClientMessage(
                    Component.translatable("screen.followspot_console.actionbar_exit"),
                    true
            );
            actionBarCooldown = 80;
        }
    }

    private BaseLightBlockEntity getFixture() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.level == null) {
            return null;
        }
        BlockEntity be = minecraft.level.getBlockEntity(fixturePos);
        return be instanceof BaseLightBlockEntity light ? light : null;
    }

    private void handleMovementKeys(Minecraft minecraft) {
        boolean moving = false;
        if (isKeyDown(minecraft, minecraft.options.keyUp)) {
            tiltAngle = Mth.clamp(tiltAngle + MOVE_SPEED, -45f, 45f);
            moving = true;
        }
        if (isKeyDown(minecraft, minecraft.options.keyDown)) {
            tiltAngle = Mth.clamp(tiltAngle - MOVE_SPEED, -45f, 45f);
            moving = true;
        }
        if (isKeyDown(minecraft, minecraft.options.keyLeft)) {
            panAngle = Mth.clamp(panAngle - MOVE_SPEED, -90f, 90f);
            moving = true;
        }
        if (isKeyDown(minecraft, minecraft.options.keyRight)) {
            panAngle = Mth.clamp(panAngle + MOVE_SPEED, -90f, 90f);
            moving = true;
        }

        if (moving) {
            wasMoving = true;
            sendControlIfReady();
        } else if (wasMoving) {
            wasMoving = false;
            sendControlNow();
        }
    }

    private void applyLocalFixtureState() {
        BaseLightBlockEntity fixture = getFixture();
        if (fixture == null) {
            return;
        }
        if (fixture instanceof ExtraLightsLightBlockEntity extra) {
            extra.syncOperatorAngles(panAngle, tiltAngle);
        } else {
            int pi = Math.round(panAngle);
            int ti = Math.round(tiltAngle);
            fixture.setPan(pi);
            fixture.setTilt(ti);
        }
    }

    private void sendControlIfReady() {
        if (controlSendCooldown > 0) {
            return;
        }
        sendControlNow();
        controlSendCooldown = 2;
    }

    private void sendControlNow() {
        ModNetworkHandler.CHANNEL.sendToServer(new FollowspotConsoleControlPacket(
                consolePos, intensity, red, green, blue, focus, getPan(), getTilt()
        ));
    }

    private static boolean isKeyDown(Minecraft minecraft, KeyMapping mapping) {
        return InputConstants.isKeyDown(minecraft.getWindow().getWindow(), mapping.getDefaultKey().getValue());
    }

    private static boolean isKeyDown(Minecraft minecraft, int glfwKey) {
        return InputConstants.isKeyDown(minecraft.getWindow().getWindow(), glfwKey);
    }
}
