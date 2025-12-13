package com.github.dumann089.theatricalextralights.forge;

import com.github.dumann089.theatricalextralights.config.TheatricalExtraLightsConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeConfigInit {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue ENABLE_OVERLAY =
            BUILDER.comment("Extra Lights Settings")
                    .define("enableOverlay", true);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static void register() {
        ModLoadingContext.get().registerConfig(Type.CLIENT, SPEC);
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event) {
    }
}
