package com.github.dumann089.theatricalextralights.client.followspot;

import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.Minecraft;

public final class FollowspotCameraClient {

    private FollowspotCameraClient() {
    }

    public static void init() {
        ClientTickEvent.CLIENT_PRE.register(FollowspotCameraClient::onClientTick);
    }

    private static void onClientTick(Minecraft minecraft) {
        if (!FollowspotFixtureCameraSession.isActive()) {
            return;
        }
        FollowspotFixtureCameraSession.getActive().tick(minecraft);
    }
}
