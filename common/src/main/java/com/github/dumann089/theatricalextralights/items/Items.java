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
    public static final RegistrySupplier<Item> VERTICAL_BAR = ITEMS.register(
        "vertical_bar",
        () -> new BlockItem(Blocks.VERTICALBAR_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
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
    public static final RegistrySupplier<Item> BEAM_7R = ITEMS.register(
        "beam_7r",
        () -> new BlockItem(Blocks.BEAM_7R_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> MAC_VIP = ITEMS.register(
        "mac_vip",
        () -> new BlockItem(Blocks.MAC_VIP_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> SHARPLUS = ITEMS.register(
        "sharplus",
        () -> new BlockItem(Blocks.SHARPLUS_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> MOVING500 = ITEMS.register(
        "moving500",
        () -> new BlockItem(Blocks.MOVING500_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> ROBITSPOT = ITEMS.register(
        "robitspot",
        () -> new BlockItem(Blocks.ROBITSPOT_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> VERVESPOT = ITEMS.register(
        "vervespot",
        () -> new BlockItem(Blocks.VERVESPOT_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> SEARCHLIGHT = ITEMS.register(
        "searchlight",
        () -> new BlockItem(Blocks.SEARCHLIGHT_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> SOURCE_FOUR = ITEMS.register(
        "source_four",
        () -> new BlockItem(Blocks.SOURCE_FOUR_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> PAR1000_RED = ITEMS.register(
        "par1000_red",
        () -> new BlockItem(Blocks.PAR1000_RED_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> PAR1000_BLUE = ITEMS.register(
        "par1000_blue",
        () -> new BlockItem(Blocks.PAR1000_BLUE_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> PAR1000_GREEN = ITEMS.register(
        "par1000_green",
        () -> new BlockItem(Blocks.PAR1000_GREEN_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> PAR1000_MAGENTA = ITEMS.register(
        "par1000_magenta",
        () -> new BlockItem(Blocks.PAR1000_MAGENTA_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> PAR1000_AMBER = ITEMS.register(
        "par1000_amber",
        () -> new BlockItem(Blocks.PAR1000_AMBER_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> PAR1000 = ITEMS.register(
        "par1000",
        () -> new BlockItem(Blocks.PAR1000_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> PAR1000_PURPLE = ITEMS.register(
        "par1000_purple",
        () -> new BlockItem(Blocks.PAR1000_PURPLE_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> PAR1000_LIGHTBLUE = ITEMS.register(
        "par1000_lightblue",
        () -> new BlockItem(Blocks.PAR1000_LIGHTBLUE_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> PAR1000_WHITE = ITEMS.register(
        "par1000_white",
        () -> new BlockItem(Blocks.PAR1000_WHITE_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );


    
    public static void init(){
        ITEMS.register();
    }
}
