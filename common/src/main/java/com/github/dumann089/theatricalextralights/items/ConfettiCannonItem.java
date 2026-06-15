package com.github.dumann089.theatricalextralights.items;

import com.github.dumann089.theatricalextralights.TheatricalExtraLights;
import com.github.dumann089.theatricalextralights.blocks.Blocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ConfettiCannonItem extends BlockItem {
    public ConfettiCannonItem(Block block) {
        super(block, createProperties());
    }

    private static Item.Properties createProperties() {
        return new Item.Properties().arch$tab(TheatricalExtraLights.PYRO_TAB);
    }

    public static Item createDefault() {
        return new ConfettiCannonItem(Blocks.CONFETTI_CANNON_BLOCK.get());
    }
}
