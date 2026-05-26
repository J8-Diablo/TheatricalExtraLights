package com.github.dumann089.theatricalextralights.client.followspot;

import com.github.dumann089.theatricalextralights.TheatricalExtraLights;
import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class FollowspotCameraAccess {

    private static final Logger LOGGER = LoggerFactory.getLogger(TheatricalExtraLights.MOD_ID);

    private static Field positionField;
    private static Method positionSetter;
    private static Method rotationSetter;

    private FollowspotCameraAccess() {
    }

    public static void tryApplyCameraState(Camera camera, Vec3 position, float yaw, float pitch) {
        trySetPosition(camera, position);
        trySetRotation(camera, yaw, pitch);
    }

    public static boolean trySetRotation(Camera camera, float yaw, float pitch) {
        try {
            Method method = resolveRotationSetter();
            method.invoke(camera, yaw, pitch);
            return true;
        } catch (ReflectiveOperationException | IllegalStateException e) {
            LOGGER.warn("Followspot camera rotation update failed", e);
            return false;
        }
    }

    public static boolean trySetPosition(Camera camera, Vec3 position) {
        if (trySetPositionField(camera, position)) {
            return true;
        }
        try {
            Method method = resolvePositionSetter();
            if (method.getParameterCount() == 1) {
                method.invoke(camera, position);
            } else {
                method.invoke(camera, position.x, position.y, position.z);
            }
            return true;
        } catch (ReflectiveOperationException | IllegalStateException e) {
            LOGGER.warn("Followspot camera position update failed", e);
            return false;
        }
    }

    private static boolean trySetPositionField(Camera camera, Vec3 position) {
        try {
            Field field = resolvePositionField();
            field.set(camera, position);
            return true;
        } catch (ReflectiveOperationException e) {
            return false;
        }
    }

    private static Field resolvePositionField() throws NoSuchFieldException {
        if (positionField != null) {
            return positionField;
        }
        for (Field field : Camera.class.getDeclaredFields()) {
            if (field.getType() == Vec3.class) {
                positionField = field;
                break;
            }
        }
        if (positionField == null) {
            throw new NoSuchFieldException("Camera position field not found");
        }
        positionField.setAccessible(true);
        return positionField;
    }

    private static Method resolvePositionSetter() {
        if (positionSetter != null) {
            return positionSetter;
        }
        Method vec3Candidate = null;
        Method dddCandidate = null;

        for (Method method : Camera.class.getDeclaredMethods()) {
            if (method.getReturnType() != void.class) {
                continue;
            }
            String name = method.getName().toLowerCase();
            if (name.contains("move")) {
                continue;
            }
            Class<?>[] params = method.getParameterTypes();
            if (params.length == 1 && params[0] == Vec3.class) {
                vec3Candidate = method;
            } else if (params.length == 3
                    && params[0] == double.class
                    && params[1] == double.class
                    && params[2] == double.class) {
                dddCandidate = method;
            }
        }

        if (vec3Candidate != null) {
            positionSetter = vec3Candidate;
        } else if (dddCandidate != null) {
            positionSetter = dddCandidate;
        } else {
            throw new IllegalStateException("Camera position setter not found");
        }

        positionSetter.setAccessible(true);
        return positionSetter;
    }

    private static Method resolveRotationSetter() throws NoSuchMethodException {
        if (rotationSetter != null) {
            return rotationSetter;
        }
        rotationSetter = Camera.class.getDeclaredMethod("setRotation", float.class, float.class);
        rotationSetter.setAccessible(true);
        return rotationSetter;
    }
}
