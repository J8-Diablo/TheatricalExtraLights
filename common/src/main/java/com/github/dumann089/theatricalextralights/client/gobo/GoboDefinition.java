package com.github.dumann089.theatricalextralights.client.gobo;

import net.minecraft.resources.ResourceLocation;

public class GoboDefinition {

    private final ResourceLocation texture;
    private final boolean rotating;

    public GoboDefinition(ResourceLocation texture, boolean rotating) {
        this.texture = texture;
        this.rotating = rotating;
    }

    public ResourceLocation texture() {
        return texture;
    }

    public boolean rotating() {
        return rotating;
    }
}