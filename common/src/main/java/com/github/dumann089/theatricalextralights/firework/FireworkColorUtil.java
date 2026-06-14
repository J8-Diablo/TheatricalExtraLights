package com.github.dumann089.theatricalextralights.firework;

import net.minecraft.util.Mth;

public final class FireworkColorUtil {
    private FireworkColorUtil() {
    }

    public static int[] paletteFromRgb(int red, int green, int blue) {
        int base = (Mth.clamp(red, 0, 255) << 16)
                | (Mth.clamp(green, 0, 255) << 8)
                | Mth.clamp(blue, 0, 255);
        return new int[]{base, lighten(base, 0.45f), lighten(base, 0.72f)};
    }

    private static int lighten(int rgb, float amount) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        r = Mth.clamp((int) (r + (255 - r) * amount), 0, 255);
        g = Mth.clamp((int) (g + (255 - g) * amount), 0, 255);
        b = Mth.clamp((int) (b + (255 - b) * amount), 0, 255);
        return (r << 16) | (g << 8) | b;
    }
}
