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
    public static final RegistrySupplier<Item> MOVING_BAR = ITEMS.register(
            "moving_bar",
            () -> new BlockItem(Blocks.MOVING_BAR_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
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
    public static final RegistrySupplier<Item> BLINDER_WARM = ITEMS.register(
            "blinder_warm",
            () -> new BlockItem(Blocks.BLINDER_WARM_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> STROBE = ITEMS.register(
            "strobe",
            () -> new BlockItem(Blocks.STROBE.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> WHITE_STROBE = ITEMS.register(
            "white_strobe",
            () -> new BlockItem(Blocks.WHITE_STROBE_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
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
        public static final RegistrySupplier<Item> WASHLIGHT = ITEMS.register(
        "washlight",
        () -> new BlockItem(Blocks.WASHLIGHT_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
            public static final RegistrySupplier<Item> MINIWASH = ITEMS.register(
        "miniwash",
        () -> new BlockItem(Blocks.MINIWASH_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
            public static final RegistrySupplier<Item> ATOMICTILT = ITEMS.register(
        "atomictilt",
        () -> new BlockItem(Blocks.ATOMICTILT_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
                public static final RegistrySupplier<Item> VL6000 = ITEMS.register(
        "vl6000",
        () -> new BlockItem(Blocks.VL6000_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> WASHLED = ITEMS.register(
            "washled",
            () -> new BlockItem(Blocks.WASHLED_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
                public static final RegistrySupplier<Item> INVISIBLELIGHT = ITEMS.register(
        "invisiblelight",
        () -> new BlockItem(Blocks.INVISIBLE_LIGHT_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
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
        public static final RegistrySupplier<Item> PAR1000_ORANGE = ITEMS.register(
        "par1000_orange",
        () -> new BlockItem(Blocks.PAR1000_ORANGE_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
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
    public static final RegistrySupplier<Item> x8PAR_RED = ITEMS.register(
        "x8par_red",
        () -> new BlockItem(Blocks.X8PAR_RED_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
        public static final RegistrySupplier<Item> x8PAR_GREEN = ITEMS.register(
        "x8par_green",
        () -> new BlockItem(Blocks.X8PAR_GREEN_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
            public static final RegistrySupplier<Item> x8PAR_BLUE = ITEMS.register(
        "x8par_blue",
        () -> new BlockItem(Blocks.X8PAR_BLUE_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
                public static final RegistrySupplier<Item> x8PAR_MAGENTA = ITEMS.register(
        "x8par_magenta",
        () -> new BlockItem(Blocks.X8PAR_MAGENTA_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
            public static final RegistrySupplier<Item> x8PAR_LIGHTBLUE = ITEMS.register(
        "x8par_lightblue",
        () -> new BlockItem(Blocks.X8PAR_LIGHTBLUE_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
            public static final RegistrySupplier<Item> x8PAR_YELLOW = ITEMS.register(
        "x8par_yellow",
        () -> new BlockItem(Blocks.X8PAR_YELLOW_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
                    public static final RegistrySupplier<Item> x8PAR_PURPLE = ITEMS.register(
        "x8par_purple",
        () -> new BlockItem(Blocks.X8PAR_PURPLE_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
                public static final RegistrySupplier<Item> x8PAR_WARM = ITEMS.register(
        "x8par_warm",
        () -> new BlockItem(Blocks.X8PAR_WARM_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
                public static final RegistrySupplier<Item> x8PAR_ORANGE = ITEMS.register(
        "x8par_orange",
        () -> new BlockItem(Blocks.X8PAR_ORANGE_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
                    public static final RegistrySupplier<Item> x8PAR_WHITE = ITEMS.register(
        "x8par_white",
        () -> new BlockItem(Blocks.X8PAR_WHITE_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
                        public static final RegistrySupplier<Item> PAR56_RED = ITEMS.register(
        "par56_red",
        () -> new BlockItem(Blocks.PAR56_RED_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
                            public static final RegistrySupplier<Item> PAR56_GREEN = ITEMS.register(
        "par56_green",
        () -> new BlockItem(Blocks.PAR56_GREEN_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
                        public static final RegistrySupplier<Item> PAR56_BLUE = ITEMS.register(
        "par56_blue",
        () -> new BlockItem(Blocks.PAR56_BLUE_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
                        public static final RegistrySupplier<Item> PAR56_ORANGE = ITEMS.register(
        "par56_orange",
        () -> new BlockItem(Blocks.PAR56_ORANGE_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
                            public static final RegistrySupplier<Item> PAR56_MAGENTA = ITEMS.register(
        "par56_magenta",
        () -> new BlockItem(Blocks.PAR56_MAGENTA_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
                                public static final RegistrySupplier<Item> PAR56_LIGHTBLUE = ITEMS.register(
        "par56_lightblue",
        () -> new BlockItem(Blocks.PAR56_LIGHTBLUE_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
                                    public static final RegistrySupplier<Item> PAR56_PURPLE = ITEMS.register(
        "par56_purple",
        () -> new BlockItem(Blocks.PAR56_PURPLE_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
                                        public static final RegistrySupplier<Item> PAR56_YELLOW = ITEMS.register(
        "par56_yellow",
        () -> new BlockItem(Blocks.PAR56_YELLOW_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
                                        public static final RegistrySupplier<Item> PAR56_WHITE = ITEMS.register(
        "par56_white",
        () -> new BlockItem(Blocks.PAR56_WHITE_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
                                            public static final RegistrySupplier<Item> PAR56_WARM = ITEMS.register(
        "par56_warm",
        () -> new BlockItem(Blocks.PAR56_WARM_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    
        public static final RegistrySupplier<Item> A2X2PAR64_RED = ITEMS.register(
        "a2x2par64_red",
        () -> new BlockItem(Blocks.A2X2PAR64_RED_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> A2X2PAR64_GREEN = ITEMS.register(
            "a2x2par64_green",
            () -> new BlockItem(Blocks.A2X2PAR64_GREEN_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> A2X2PAR64_BLUE = ITEMS.register(
            "a2x2par64_blue",
            () -> new BlockItem(Blocks.A2X2PAR64_BLUE_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> A2X2PAR64_MAGENTA = ITEMS.register(
            "a2x2par64_magenta",
            () -> new BlockItem(Blocks.A2X2PAR64_MAGENTA_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> A2X2PAR64_LIGHTBLUE = ITEMS.register(
            "a2x2par64_lightblue",
            () -> new BlockItem(Blocks.A2X2PAR64_LIGHTBLUE_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> A2X2PAR64_PURPLE = ITEMS.register(
            "a2x2par64_purple",
            () -> new BlockItem(Blocks.A2X2PAR64_PURPLE_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> A2X2PAR64_ORANGE = ITEMS.register(
            "a2x2par64_orange",
            () -> new BlockItem(Blocks.A2X2PAR64_ORANGE_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> A2X2PAR64_YELLOW = ITEMS.register(
            "a2x2par64_yellow",
            () -> new BlockItem(Blocks.A2X2PAR64_YELLOW_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> A2X2PAR64_WARM = ITEMS.register(
            "a2x2par64_warm",
            () -> new BlockItem(Blocks.A2X2PAR64_WARM_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> A2X2PAR64_WHITE = ITEMS.register(
            "a2x2par64_white",
            () -> new BlockItem(Blocks.A2X2PAR64_WHITE_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> FOLLOWSPOT = ITEMS.register(
        "followspot",
        () -> new BlockItem(Blocks.FOLLOWSPOT_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    ); 
    public static final RegistrySupplier<Item> BIGSCROLLER = ITEMS.register(
        "bigscroller",
        () -> new BlockItem(Blocks.BIGSCROLLER_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
        public static final RegistrySupplier<Item> HORIZONTALSCROLLER = ITEMS.register(
        "horizontalscroller",
        () -> new BlockItem(Blocks.HORIZONTALSCROLLER_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
            public static final RegistrySupplier<Item> VERTICALSCROLLER = ITEMS.register(
        "verticalscroller",
        () -> new BlockItem(Blocks.VERTICALSCROLLER_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );
    public static final RegistrySupplier<Item> WATER_JET = ITEMS.register(
            "water_jet",
            () -> new BlockItem(Blocks.WATER_JET_BLOCK.get(), new Item.Properties().arch$tab(TheatricalExtraLights.TAB))
    );


    public static void init(){
        ITEMS.register();
    }
}
