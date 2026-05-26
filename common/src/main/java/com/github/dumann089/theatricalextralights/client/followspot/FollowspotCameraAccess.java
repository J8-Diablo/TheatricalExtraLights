package com.github.dumann089.theatricalextralights.client.followspot;

import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Method;

final class FollowspotCameraAccess {

    private static Method setPosition;
    private static Method setRotation;

    private FollowspotCameraAccess() {
    }

    static void configure(Camera camera, Vec3 position, float yaw, float pitch) {
        try {
            if (setPosition == null) {
                setPosition = Camera.class.getDeclaredMethod("setPosition", double.class, double.class, double.class);
                setPosition.setAccessible(true);
                setRotation = Camera.class.getDeclaredMethod("setRotation", float.class, float.class);
                setRotation.setAccessible(true);
            }
            setPosition.invoke(camera, position.x, position.y, position.z);
            setRotation.invoke(camera, yaw, pitch);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to configure followspot camera", e);
        }
    }
}
