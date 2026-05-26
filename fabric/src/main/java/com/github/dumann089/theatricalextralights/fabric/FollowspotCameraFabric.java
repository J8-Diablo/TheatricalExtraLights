package com.github.dumann089.theatricalextralights.fabric;

import com.github.dumann089.theatricalextralights.client.followspot.FollowspotFixtureCameraSession;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public final class FollowspotCameraFabric {

    private FollowspotCameraFabric() {
    }

    public static void init() {
        WorldRenderEvents.BEFORE_ENTITIES.register(context -> {
            if (!FollowspotFixtureCameraSession.isActive()) {
                return;
            }
            FollowspotFixtureCameraSession.getActive().applyCamera(context.camera());
        });
    }
}
