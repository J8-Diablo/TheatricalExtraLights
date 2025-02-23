package com.github.dumann089.theatricalextralights.blockentities;

import com.github.dumann089.theatricalextralights.TheatricalExtraLightsRegistry;
import com.github.dumann089.theatricalextralights.blocks.Blocks;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = TheatricalExtraLightsRegistry.get(Registries.BLOCK_ENTITY_TYPE);
    public static final RegistrySupplier<BlockEntityType<MovingVL2CBlockEntity>> MOVING_VL2C = BLOCK_ENTITIES.register("moving_vl2c", () -> BlockEntityType.Builder.of(MovingVL2CBlockEntity::new, Blocks.MOVING_VL2C_BLOCK.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<MovingVL6BlockEntity>> MOVING_VL6 = BLOCK_ENTITIES.register("moving_vl6", () -> BlockEntityType.Builder.of(MovingVL6BlockEntity::new, Blocks.MOVING_VL6_BLOCK.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<MovingBeamBlockEntity>> MOVING_BEAM = BLOCK_ENTITIES.register("moving_beam", () -> BlockEntityType.Builder.of(MovingBeamBlockEntity::new, Blocks.MOVING_BEAM_BLOCK.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<MovingScanBlockEntity>> MOVING_SCAN = BLOCK_ENTITIES.register("moving_scan", () -> BlockEntityType.Builder.of(MovingScanBlockEntity::new, Blocks.MOVING_SCAN_BLOCK.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<RGBBarBlockEntity>> RGB_BAR = BLOCK_ENTITIES.register("rgb_bar", () -> BlockEntityType.Builder.of(RGBBarBlockEntity::new, Blocks.RGB_BAR.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<LEDFountainBlockEntity>> LED_FOUNTAIN = BLOCK_ENTITIES.register("led_fountain", () -> BlockEntityType.Builder.of(LEDFountainBlockEntity::new, Blocks.LED_FOUNTAIN.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<LEDPanel2BlockEntity>> LED_PANEL_2 = BLOCK_ENTITIES.register("led_panel_2", () -> BlockEntityType.Builder.of(LEDPanel2BlockEntity::new, Blocks.LED_PANEL_2.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<BigPanelBlockEntity>> BIG_PANEL = BLOCK_ENTITIES.register("big_panel", () -> BlockEntityType.Builder.of(BigPanelBlockEntity::new, Blocks.BIG_PANEL.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<BigPanel2BlockEntity>> BIG_PANEL2 = BLOCK_ENTITIES.register("big_panel2", () -> BlockEntityType.Builder.of(BigPanel2BlockEntity::new, Blocks.BIG_PANEL2.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<ParLedBlockEntity>> PAR_LED = BLOCK_ENTITIES.register("par_led", () -> BlockEntityType.Builder.of(ParLedBlockEntity::new, Blocks.PAR_LED.get()).build(null));
    public static void init(){
        BLOCK_ENTITIES.register();
    }
}
