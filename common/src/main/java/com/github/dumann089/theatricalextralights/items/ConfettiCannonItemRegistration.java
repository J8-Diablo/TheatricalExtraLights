package com.github.dumann089.theatricalextralights.items;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.Item;

public final class ConfettiCannonItemRegistration {
    private ConfettiCannonItemRegistration() {
    }

    @ExpectPlatform
    public static Item create() {
        throw new AssertionError();
    }
}
