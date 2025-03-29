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

    public static void init(){
        BLOCKS.register();
    }
}
