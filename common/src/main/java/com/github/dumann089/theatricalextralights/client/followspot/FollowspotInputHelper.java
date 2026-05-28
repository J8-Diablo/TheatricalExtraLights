package com.github.dumann089.theatricalextralights.client.followspot;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public final class FollowspotInputHelper {

    private FollowspotInputHelper() {
    }

    public static boolean isKeyDown(KeyMapping mapping) {
        return mapping != null && mapping.isDown();
    }

    public static boolean isEscapeDown(Minecraft minecraft) {
        return minecraft != null
                && InputConstants.isKeyDown(minecraft.getWindow().getWindow(), GLFW.GLFW_KEY_ESCAPE);
    }
}
