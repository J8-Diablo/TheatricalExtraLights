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
    public static final RegistrySupplier<Block> MOVING500_BLOCK = BLOCKS.register("moving500", Moving500Block::new);
    public static final RegistrySupplier<Block> ROBITSPOT_BLOCK = BLOCKS.register("robitspot", RobitspotBlock::new);
    public static final RegistrySupplier<Block> VERVESPOT_BLOCK = BLOCKS.register("vervespot", VervespotBlock::new);
    public static final RegistrySupplier<Block> VERTICALBAR_BLOCK = BLOCKS.register("vertical_bar", VerticalbarBlock::new);
    public static final RegistrySupplier<Block> SEARCHLIGHT_BLOCK = BLOCKS.register("searchlight", SearchlightBlock::new);



    public static void init(){
        BLOCKS.register();
    }
}
