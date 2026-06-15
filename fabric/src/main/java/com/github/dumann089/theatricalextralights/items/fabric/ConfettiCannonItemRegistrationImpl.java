package com.github.dumann089.theatricalextralights.items.fabric;

import com.github.dumann089.theatricalextralights.items.ConfettiCannonItem;
import net.minecraft.world.item.Item;

@SuppressWarnings("unused")
public class ConfettiCannonItemRegistrationImpl {
    private ConfettiCannonItemRegistrationImpl() {
    }

    public static Item create() {
        return ConfettiCannonItem.createDefault();
    }
}
