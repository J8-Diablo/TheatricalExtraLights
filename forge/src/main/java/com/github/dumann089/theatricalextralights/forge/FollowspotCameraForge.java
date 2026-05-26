package com.github.dumann089.theatricalextralights.forge;

import com.github.dumann089.theatricalextralights.client.followspot.FollowspotFixtureCameraSession;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = com.github.dumann089.theatricalextralights.TheatricalExtraLights.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class FollowspotCameraForge {

    private FollowspotCameraForge() {
    }

    @SubscribeEvent
    public static void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        if (!FollowspotFixtureCameraSession.isActive()) {
            return;
        }
        FollowspotFixtureCameraSession.getActive().applyCamera(event.getCamera());
        var camera = event.getCamera();
        event.setYaw(camera.getYRot());
        event.setPitch(camera.getXRot());
        event.setRoll(0);
    }
}
