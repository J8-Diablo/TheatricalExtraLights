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
    public static final RegistrySupplier<BlockEntityType<LaserBlockEntity>> LASER = BLOCK_ENTITIES.register("laser", () -> BlockEntityType.Builder.of(LaserBlockEntity::new, Blocks.LASER_BLOCK.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<BlinderBlockEntity>> BLINDER = BLOCK_ENTITIES.register("blinder", () -> BlockEntityType.Builder.of(BlinderBlockEntity::new, Blocks.BLINDER.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<StrobeBlockEntity>> STROBE = BLOCK_ENTITIES.register("strobe", () -> BlockEntityType.Builder.of(StrobeBlockEntity::new, Blocks.STROBE.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<truss3lightsBlockEntity>> TRUSS_3LIGHTS = BLOCK_ENTITIES.register("truss_3lights", () -> BlockEntityType.Builder.of(truss3lightsBlockEntity::new, Blocks.TRUSS_3LIGHTS.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<Beam7RBlockEntity>> BEAM_7R = BLOCK_ENTITIES.register("beam_7r", () -> BlockEntityType.Builder.of(Beam7RBlockEntity::new, Blocks.BEAM_7R_BLOCK.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<Source4BlockEntity>> SOURCE_FOUR = BLOCK_ENTITIES.register("source_four", () -> BlockEntityType.Builder.of(Source4BlockEntity::new, Blocks.SOURCE_FOUR_BLOCK.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<MacVipBlockEntity>> MAC_VIP = BLOCK_ENTITIES.register("mac_vip", () -> BlockEntityType.Builder.of(MacVipBlockEntity::new, Blocks.MAC_VIP_BLOCK.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<SharplusBlockEntity>> SHARPLUS = BLOCK_ENTITIES.register("sharplus", () -> BlockEntityType.Builder.of(SharplusBlockEntity::new, Blocks.SHARPLUS_BLOCK.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<Par1000RedBlockEntity>> PAR1000_RED = BLOCK_ENTITIES.register("par1000_red", () -> BlockEntityType.Builder.of(Par1000RedBlockEntity::new, Blocks.PAR1000_RED_BLOCK.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<Par1000BlueBlockEntity>> PAR1000_BLUE = BLOCK_ENTITIES.register("par1000_blue", () -> BlockEntityType.Builder.of(Par1000BlueBlockEntity::new, Blocks.PAR1000_BLUE_BLOCK.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<Par1000GreenBlockEntity>> PAR1000_GREEN = BLOCK_ENTITIES.register("par1000_green", () -> BlockEntityType.Builder.of(Par1000GreenBlockEntity::new, Blocks.PAR1000_GREEN_BLOCK.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<Par1000MagentaBlockEntity>> PAR1000_MAGENTA = BLOCK_ENTITIES.register("par1000_magenta", () -> BlockEntityType.Builder.of(Par1000MagentaBlockEntity::new, Blocks.PAR1000_MAGENTA_BLOCK.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<Par1000AmberBlockEntity>> PAR1000_AMBER = BLOCK_ENTITIES.register("par1000_amber", () -> BlockEntityType.Builder.of(Par1000AmberBlockEntity::new, Blocks.PAR1000_AMBER_BLOCK.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<Par1000BlockEntity>> PAR1000 = BLOCK_ENTITIES.register("par1000", () -> BlockEntityType.Builder.of(Par1000BlockEntity::new, Blocks.PAR1000_BLOCK.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<Par1000PurpleBlockEntity>> PAR1000_PURPLE = BLOCK_ENTITIES.register("par1000_purple", () -> BlockEntityType.Builder.of(Par1000PurpleBlockEntity::new, Blocks.PAR1000_PURPLE_BLOCK.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<Par1000LightblueBlockEntity>> PAR1000_LIGHTBLUE = BLOCK_ENTITIES.register("par1000_lightblue", () -> BlockEntityType.Builder.of(Par1000LightblueBlockEntity::new, Blocks.PAR1000_LIGHTBLUE_BLOCK.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<Par1000WhiteBlockEntity>> PAR1000_WHITE = BLOCK_ENTITIES.register("par1000_white", () -> BlockEntityType.Builder.of(Par1000WhiteBlockEntity::new, Blocks.PAR1000_WHITE_BLOCK.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<Moving500BlockEntity>> MOVING500 = BLOCK_ENTITIES.register("moving500", () -> BlockEntityType.Builder.of(Moving500BlockEntity::new, Blocks.MOVING500_BLOCK.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<RobitspotBlockEntity>> ROBITSPOT = BLOCK_ENTITIES.register("robitspot", () -> BlockEntityType.Builder.of(RobitspotBlockEntity::new, Blocks.ROBITSPOT_BLOCK.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<VervespotBlockEntity>> VERVESPOT = BLOCK_ENTITIES.register("vervespot", () -> BlockEntityType.Builder.of(VervespotBlockEntity::new, Blocks.VERVESPOT_BLOCK.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<VerticalbarBlockEntity>> VERTICAL_BAR = BLOCK_ENTITIES.register("vertical_bar", () -> BlockEntityType.Builder.of(VerticalbarBlockEntity::new, Blocks.VERTICALBAR_BLOCK.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<SearchlightBlockEntity>> SEARCHLIGHT = BLOCK_ENTITIES.register("searchlight", () -> BlockEntityType.Builder.of(SearchlightBlockEntity::new, Blocks.SEARCHLIGHT_BLOCK.get()).build(null));



    public static void init(){
        BLOCK_ENTITIES.register();
    }
}
