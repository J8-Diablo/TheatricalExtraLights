package com.github.dumann089.theatricalextralights.items;

import com.github.dumann089.theatricalextralights.TheatricalExtraLights;
import com.github.dumann089.theatricalextralights.TheatricalExtraLightsRegistry;
import com.github.dumann089.theatricalextralights.blocks.Blocks;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public class Items {
    public static final DeferredRegister<Item> ITEMS = TheatricalExtraLightsRegistry.get(Registries.ITEM);
    public static final RegistrySupplier<Item> MOVING_VL2C = ITEMS.register(
            "moving_vl2c",
            () -> new BlockItem(Blocks.MOVING_VL2C_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> MOVING_SCAN = ITEMS.register(
            "moving_scan",
            () -> new BlockItem(Blocks.MOVING_SCAN_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> RGB_BAR = ITEMS.register(
            "rgb_bar",
            () -> new BlockItem(Blocks.RGB_BAR.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> MOVING_BEAM = ITEMS.register(
            "moving_beam",
            () -> new BlockItem(Blocks.MOVING_BEAM_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> LED_FOUNTAIN = ITEMS.register(
            "led_fountain",
            () -> new BlockItem(Blocks.LED_FOUNTAIN.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> LED_PANEL_2 = ITEMS.register(
            "led_panel_2",
            () -> new BlockItem(Blocks.LED_PANEL_2.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> BIG_PANEL = ITEMS.register(
            "big_panel",
            () -> new BlockItem(Blocks.BIG_PANEL.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> BIG_PANEL2 = ITEMS.register(
            "big_panel2",
            () -> new BlockItem(Blocks.BIG_PANEL2.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> MOVING_VL6 = ITEMS.register(
            "moving_vl6",
            () -> new BlockItem(Blocks.MOVING_VL6_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> PAR_LED = ITEMS.register(
            "par_led",
            () -> new BlockItem(Blocks.PAR_LED.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> LASER = ITEMS.register(
           "laser",
           () -> new BlockItem(Blocks.LASER_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> BLINDER = ITEMS.register(
            "blinder",
            () -> new BlockItem(Blocks.BLINDER.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    ); 
    public static final RegistrySupplier<Item> STROBE = ITEMS.register(
            "strobe",
            () -> new BlockItem(Blocks.STROBE.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    ); 
    public static final RegistrySupplier<Item> TRUSS_3LIGHTS = ITEMS.register(
        "truss_3lights",
        () -> new BlockItem(Blocks.TRUSS_3LIGHTS.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    ); 


    public static void init(){
        ITEMS.register();
    }
}
