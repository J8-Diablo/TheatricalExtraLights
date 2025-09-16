package com.github.dumann089.theatricalextralights;

import com.github.dumann089.theatricalextralights.blockentities.BlockEntities;
import com.github.dumann089.theatricalextralights.blocks.Blocks;
import com.github.dumann089.theatricalextralights.fixtures.Fixtures;
import com.github.dumann089.theatricalextralights.items.Items;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TheatricalExtraLights {
    public static final String MOD_ID = "theatricalextralights";
    public static final DeferredRegister<CreativeModeTab> TABS = TheatricalExtraLightsRegistry.get(Registries.CREATIVE_MODE_TAB);
    public static final RegistrySupplier<CreativeModeTab> TAB = TABS.register(
            TheatricalExtraLights.MOD_ID,
            () -> CreativeTabRegistry.create(
                    Component.translatable("itemGroup." + TheatricalExtraLights.MOD_ID),
                    () -> new ItemStack(Items.BIG_PANEL.get())
            )
    );

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        TABS.register();
        Blocks.init();
        Fixtures.init();
        Items.init();
        BlockEntities.init();    }
}
