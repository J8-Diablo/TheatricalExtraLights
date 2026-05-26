package com.github.dumann089.theatricalextralights.forge;

import com.github.dumann089.theatricalextralights.client.followspot.FollowspotCameraAccess;
import com.github.dumann089.theatricalextralights.client.followspot.FollowspotFixtureCameraSession;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

/**
 * Forge camera hook — registered lazily when the player enters fixture control.
 */
public final class FollowspotCameraForge {

    private static boolean listenerRegistered;

    private FollowspotCameraForge() {
    }

    public static void ensureRegistered() {
        if (listenerRegistered) {
            return;
        }
        MinecraftForge.EVENT_BUS.addListener(
                EventPriority.LOWEST,
                false,
                FollowspotCameraForge::onComputeCameraAngles
        );
        listenerRegistered = true;
    }

    private static void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        if (!FollowspotFixtureCameraSession.isActive()) {
            return;
        }
        FollowspotFixtureCameraSession.CameraState state = FollowspotFixtureCameraSession.getActive().getCameraState();
        if (state == null) {
            return;
        }

        FollowspotCameraAccess.trySetPosition(event.getCamera(), state.position());
        event.setYaw(state.yaw());
        event.setPitch(state.pitch());
        event.setRoll(0);
    }
}
