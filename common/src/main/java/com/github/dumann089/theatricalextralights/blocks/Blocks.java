package com.github.dumann089.theatricalextralights.blocks;

import com.github.dumann089.theatricalextralights.TheatricalExtraLightsRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.imabad.theatrical.blocks.interfaces.ArtNetInterfaceBlock;
import dev.imabad.theatrical.blocks.light.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;

public class Blocks {
    public static final DeferredRegister<Block> BLOCKS = TheatricalExtraLightsRegistry.get(Registries.BLOCK);
    public static final RegistrySupplier<Block> MOVING_VL2C_BLOCK = BLOCKS.register("moving_vl2c", MovingVL2CBlock::new);
    public static final RegistrySupplier<Block> MOVING_VL6_BLOCK = BLOCKS.register("moving_vl6", MovingVL6Block::new);
    public static final RegistrySupplier<Block> MOVING_BEAM_BLOCK = BLOCKS.register("moving_beam", MovingBeamBlock::new);
    public static final RegistrySupplier<Block> MOVING_SCAN_BLOCK = BLOCKS.register("moving_scan", MovingScanBlock::new);
    public static final RegistrySupplier<Block> LED_PANEL_2 = BLOCKS.register("led_panel_2", LEDPanel2Block::new);
    public static final RegistrySupplier<Block> PAR_LED = BLOCKS.register("par_led", ParLedBlock::new);
    public static final RegistrySupplier<Block> LED_FOUNTAIN = BLOCKS.register("led_fountain", LEDfountainBlock::new);
    public static final RegistrySupplier<Block> RGB_BAR = BLOCKS.register("rgb_bar", RGBbarBlock::new);
    public static final RegistrySupplier<Block> BIG_PANEL = BLOCKS.register("big_panel", BigPanelBlock::new);
    public static final RegistrySupplier<Block> BIG_PANEL2 = BLOCKS.register("big_panel2", BigPanel2Block::new);
    public static final RegistrySupplier<Block> LASER_BLOCK = BLOCKS.register("laser", LaserBlock::new);
    public static final RegistrySupplier<Block> BLINDER = BLOCKS.register("blinder", BlinderBlock::new);
    public static final RegistrySupplier<Block> STROBE = BLOCKS.register("strobe", StrobeBlock::new);
    public static final RegistrySupplier<Block> TRUSS_3LIGHTS = BLOCKS.register("truss_3lights", truss3lightsBlock::new);
    public static final RegistrySupplier<Block> BEAM_7R_BLOCK = BLOCKS.register("beam_7r", Beam7RBlock::new);
    public static final RegistrySupplier<Block> SOURCE_FOUR_BLOCK = BLOCKS.register("source_four", Source4Block::new);
    public static final RegistrySupplier<Block> MAC_VIP_BLOCK = BLOCKS.register("mac_vip", MacVipBlock::new);
    public static final RegistrySupplier<Block> SHARPLUS_BLOCK = BLOCKS.register("sharplus", SharplusBlock::new);
    public static final RegistrySupplier<Block> PAR1000_RED_BLOCK = BLOCKS.register("par1000_red", Par1000RedBlock::new);
    public static final RegistrySupplier<Block> PAR1000_BLUE_BLOCK = BLOCKS.register("par1000_blue", Par1000BlueBlock::new);
    public static final RegistrySupplier<Block> PAR1000_GREEN_BLOCK = BLOCKS.register("par1000_green", Par1000GreenBlock::new);
    public static final RegistrySupplier<Block> PAR1000_MAGENTA_BLOCK = BLOCKS.register("par1000_magenta", Par1000MagentaBlock::new);
    public static final RegistrySupplier<Block> PAR1000_AMBER_BLOCK = BLOCKS.register("par1000_amber", Par1000AmberBlock::new);
    public static final RegistrySupplier<Block> PAR1000_BLOCK = BLOCKS.register("par1000", Par1000Block::new);
    public static final RegistrySupplier<Block> PAR1000_PURPLE_BLOCK = BLOCKS.register("par1000_purple", Par1000PurpleBlock::new);
    public static final RegistrySupplier<Block> PAR1000_LIGHTBLUE_BLOCK = BLOCKS.register("par1000_lightblue", Par1000LightblueBlock::new);
    public static final RegistrySupplier<Block> PAR1000_WHITE_BLOCK = BLOCKS.register("par1000_white", Par1000WhiteBlock::new);
    public static final RegistrySupplier<Block> PAR1000_ORANGE_BLOCK = BLOCKS.register("par1000_orange", Par1000OrangeBlock::new);
    public static final RegistrySupplier<Block> MOVING500_BLOCK = BLOCKS.register("moving500", Moving500Block::new);
    public static final RegistrySupplier<Block> ROBITSPOT_BLOCK = BLOCKS.register("robitspot", RobitspotBlock::new);
    public static final RegistrySupplier<Block> VERVESPOT_BLOCK = BLOCKS.register("vervespot", VervespotBlock::new);
    public static final RegistrySupplier<Block> VERTICALBAR_BLOCK = BLOCKS.register("vertical_bar", VerticalbarBlock::new);
    public static final RegistrySupplier<Block> SEARCHLIGHT_BLOCK = BLOCKS.register("searchlight", SearchlightBlock::new);
    public static final RegistrySupplier<Block> BLINDER_WARM_BLOCK = BLOCKS.register("blinder_warm", BlinderwarmBlock::new);
    public static final RegistrySupplier<Block> WASHLIGHT_BLOCK = BLOCKS.register("washlight", WashlightBlock::new);
    public static final RegistrySupplier<Block> ATOMICTILT_BLOCK = BLOCKS.register("atomictilt", AtomictiltBlock::new);
    public static final RegistrySupplier<Block> MINIWASH_BLOCK = BLOCKS.register("miniwash", MiniwashBlock::new);
    public static final RegistrySupplier<Block> INVISIBLE_LIGHT_BLOCK = BLOCKS.register("invisiblelight", InvisiblelightBlock::new);
    public static final RegistrySupplier<Block> X8PAR_RED_BLOCK = BLOCKS.register("x8par_red", x8par_redBlock::new);
    public static final RegistrySupplier<Block> X8PAR_GREEN_BLOCK = BLOCKS.register("x8par_green", x8par_greenBlock::new);
    public static final RegistrySupplier<Block> X8PAR_BLUE_BLOCK = BLOCKS.register("x8par_blue", x8par_blueBlock::new);
    public static final RegistrySupplier<Block> X8PAR_MAGENTA_BLOCK = BLOCKS.register("x8par_magenta", x8par_magentaBlock::new);
    public static final RegistrySupplier<Block> X8PAR_LIGHTBLUE_BLOCK = BLOCKS.register("x8par_lightblue", x8par_lightblueBlock::new);
    public static final RegistrySupplier<Block> X8PAR_YELLOW_BLOCK = BLOCKS.register("x8par_yellow", x8par_yellowBlock::new);
    public static final RegistrySupplier<Block> X8PAR_WHITE_BLOCK = BLOCKS.register("x8par_white", x8par_whiteBlock::new);
    public static final RegistrySupplier<Block> X8PAR_PURPLE_BLOCK = BLOCKS.register("x8par_purple", x8par_purpleBlock::new);
    public static final RegistrySupplier<Block> X8PAR_WARM_BLOCK = BLOCKS.register("x8par_warm", x8par_warmBlock::new);
    public static final RegistrySupplier<Block> X8PAR_ORANGE_BLOCK = BLOCKS.register("x8par_orange", x8par_orangeBlock::new);
    public static final RegistrySupplier<Block> PAR56_RED_BLOCK = BLOCKS.register("par56_red", par56_redBlock::new);
    public static final RegistrySupplier<Block> PAR56_GREEN_BLOCK = BLOCKS.register("par56_green", par56_greenBlock::new);
    public static final RegistrySupplier<Block> PAR56_BLUE_BLOCK = BLOCKS.register("par56_blue", par56_blueBlock::new);
    public static final RegistrySupplier<Block> PAR56_ORANGE_BLOCK = BLOCKS.register("par56_orange", par56_orangeBlock::new);
    public static final RegistrySupplier<Block> PAR56_MAGENTA_BLOCK = BLOCKS.register("par56_magenta", par56_magentaBlock::new);
    public static final RegistrySupplier<Block> PAR56_LIGHTBLUE_BLOCK = BLOCKS.register("par56_lightblue", par56_lightblueBlock::new);
    public static final RegistrySupplier<Block> PAR56_PURPLE_BLOCK = BLOCKS.register("par56_purple", par56_purpleBlock::new);
    public static final RegistrySupplier<Block> PAR56_WHITE_BLOCK = BLOCKS.register("par56_white", par56_whiteBlock::new);
    public static final RegistrySupplier<Block> PAR56_WARM_BLOCK = BLOCKS.register("par56_warm", par56_warmBlock::new);
    public static final RegistrySupplier<Block> PAR56_YELLOW_BLOCK = BLOCKS.register("par56_yellow", par56_yellowBlock::new);
    public static final RegistrySupplier<Block> VL6000_BLOCK = BLOCKS.register("vl6000", VL6000Block::new);
    public static final RegistrySupplier<Block> A2X2PAR64_RED_BLOCK = BLOCKS.register("a2x2par64_red", a2x2par64_redBlock::new);
    public static final RegistrySupplier<Block> A2X2PAR64_GREEN_BLOCK = BLOCKS.register("a2x2par64_green", a2x2par64_greenBlock::new);
    public static final RegistrySupplier<Block> A2X2PAR64_BLUE_BLOCK = BLOCKS.register("a2x2par64_blue", a2x2par64_blueBlock::new);
    public static final RegistrySupplier<Block> A2X2PAR64_MAGENTA_BLOCK = BLOCKS.register("a2x2par64_magenta", a2x2par64_magentaBlock::new);
    public static final RegistrySupplier<Block> A2X2PAR64_LIGHTBLUE_BLOCK = BLOCKS.register("a2x2par64_lightblue", a2x2par64_lightblueBlock::new);
    public static final RegistrySupplier<Block> A2X2PAR64_PURPLE_BLOCK = BLOCKS.register("a2x2par64_purple", a2x2par64_purpleBlock::new);
    public static final RegistrySupplier<Block> A2X2PAR64_ORANGE_BLOCK = BLOCKS.register("a2x2par64_orange", a2x2par64_orangeBlock::new);
    public static final RegistrySupplier<Block> A2X2PAR64_YELLOW_BLOCK = BLOCKS.register("a2x2par64_yellow", a2x2par64_yellowBlock::new);
    public static final RegistrySupplier<Block> A2X2PAR64_WARM_BLOCK = BLOCKS.register("a2x2par64_warm", a2x2par64_warmBlock::new);
    public static final RegistrySupplier<Block> A2X2PAR64_WHITE_BLOCK = BLOCKS.register("a2x2par64_white", a2x2par64_whiteBlock::new);

    public static final RegistrySupplier<Block> FOLLOWSPOT_BLOCK = BLOCKS.register("followspot", FollowspotBlock::new);
    public static final RegistrySupplier<Block> BIGSCROLLER_BLOCK = BLOCKS.register("bigscroller", bigscrollerBlock::new);
    public static final RegistrySupplier<Block> HORIZONTALSCROLLER_BLOCK = BLOCKS.register("horizontalscroller", horizontalscrollerBlock::new);
    public static final RegistrySupplier<Block> VERTICALSCROLLER_BLOCK = BLOCKS.register("verticalscroller", verticalscrollerBlock::new);
    public static final RegistrySupplier<Block> WASHLED_BLOCK = BLOCKS.register("washled", washledBlock::new);
    public static final RegistrySupplier<Block> MOVING_BAR_BLOCK = BLOCKS.register("moving_bar", MovingbarBlock::new);

























    public static void init(){
        BLOCKS.register();
    }
}
