package com.github.dumann089.theatricalextralights.client.followspot;

import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.Minecraft;

public final class FollowspotCameraClient {

    private FollowspotCameraClient() {
    }

    public static void init() {
        ClientTickEvent.CLIENT_PRE.register(FollowspotCameraClient::onClientPreTick);
        ClientTickEvent.CLIENT_POST.register(FollowspotCameraClient::onClientPostTick);
    }

    private static void onClientPreTick(Minecraft minecraft) {
        FollowspotFixtureCameraSession.tickExitGrace();
        if (!FollowspotFixtureCameraSession.isActive()) {
            return;
        }
        FollowspotFixtureCameraSession.getActive().tick(minecraft);
    }

    private static void onClientPostTick(Minecraft minecraft) {
        if (!FollowspotFixtureCameraSession.isActive()
                || FollowspotFixtureCameraSession.usesForgeCameraHook()
                || minecraft.gameRenderer == null) {
            return;
        }
        FollowspotFixtureCameraSession.getActive().applyCamera(minecraft.gameRenderer.getMainCamera());
    }
}
