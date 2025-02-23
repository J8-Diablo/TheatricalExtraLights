package com.github.dumann089.theatricalextralights.forge;

import com.github.dumann089.theatricalextralights.TheatricalExtraLights;
import com.github.dumann089.theatricalextralights.blocks.Blocks;
import dev.imabad.theatrical.Theatrical;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.data.event.GatherDataEvent;

public class DataEvent {
    public static void onData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();

        gen.addProvider(event.includeClient(), new BlockState(output, event.getExistingFileHelper()));
        gen.addProvider(event.includeClient(), new Item(output, event.getExistingFileHelper()));
        gen.addProvider(event.includeClient(), new Lang(output, "en_us"));
    }

    public static class BlockState extends BlockStateProvider {
        public BlockState(PackOutput output, ExistingFileHelper exFileHelper) {
            super(output, Theatrical.MOD_ID, exFileHelper);
        }

        @Override
        protected void registerStatesAndModels() {

        }

    }

    public static class Item extends ItemModelProvider {

        public Item(PackOutput output, ExistingFileHelper existingFileHelper) {
            super(output, TheatricalExtraLights.MOD_ID, existingFileHelper);
        }

        @Override
        protected void registerModels() {
            withExistingParent(Blocks.MOVING_VL2C_BLOCK.getId().getPath(), new ResourceLocation(TheatricalExtraLights.MOD_ID, "block/moving_vl2c/moving_vl2c_whole"));
            withExistingParent(Blocks.MOVING_BEAM_BLOCK.getId().getPath(), new ResourceLocation(TheatricalExtraLights.MOD_ID, "block/moving_beam/moving_beam_whole"));
            withExistingParent(Blocks.MOVING_SCAN_BLOCK.getId().getPath(), new ResourceLocation(TheatricalExtraLights.MOD_ID, "block/moving_scan/moving_scan_whole"));
            withExistingParent(Blocks.RGB_BAR.getId().getPath(), new ResourceLocation(TheatricalExtraLights.MOD_ID, "block/ledbar/ledbar_whole"));
            withExistingParent(Blocks.MOVING_VL6_BLOCK.getId().getPath(), new ResourceLocation(TheatricalExtraLights.MOD_ID, "block/moving_vl6/moving_vl6_whole"));
            withExistingParent(Blocks.LED_FOUNTAIN.getId().getPath(), new ResourceLocation(TheatricalExtraLights.MOD_ID, "block/fountain_lamp/fountain_lamp_whole"));
            withExistingParent(Blocks.LED_PANEL_2.getId().getPath(), new ResourceLocation(TheatricalExtraLights.MOD_ID, "block/ledpanel/led_panel_body"));
            withExistingParent(Blocks.BIG_PANEL.getId().getPath(), new ResourceLocation(TheatricalExtraLights.MOD_ID, "block/bigpanel3x3/bigpanel_body"));
            withExistingParent(Blocks.BIG_PANEL2.getId().getPath(), new ResourceLocation(TheatricalExtraLights.MOD_ID, "block/bigpanel2x3/bigpanel2x3_body"));
            withExistingParent(Blocks.PAR_LED.getId().getPath(), new ResourceLocation(TheatricalExtraLights.MOD_ID, "block/parled/parled_body_whole"));
        }
    }

    public static class Lang extends LanguageProvider {

        public Lang(PackOutput output, String locale) {
            super(output, TheatricalExtraLights.MOD_ID, locale);
        }

        @Override
        protected void addTranslations() {
            addBlock(Blocks.MOVING_VL2C_BLOCK, "Moving VLC2C");
            addBlock(Blocks.MOVING_BEAM_BLOCK, "Moving Bean");
            addBlock(Blocks.MOVING_SCAN_BLOCK, "Moving Scan");
            addBlock(Blocks.MOVING_VL6_BLOCK, "Moving VL6");
            addBlock(Blocks.LED_FOUNTAIN, "Led Fountain");
            addBlock(Blocks.LED_PANEL_2, "LED Panel 2");
            addBlock(Blocks.BIG_PANEL, "Big Panel");
            addBlock(Blocks.BIG_PANEL2, "Big Panel");
            addBlock(Blocks.PAR_LED, "LED Par");
            addBlock(Blocks.RGB_BAR, "RGB Bar");
            add("itemGroup.theatricalextralights", "Theatrical: Extra Lights");
        }
    }

}
