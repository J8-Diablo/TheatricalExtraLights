package com.github.dumann089.theatricalextralights.fixtures;

import com.github.dumann089.theatricalextralights.TheatricalExtraLights;
import com.github.dumann089.theatricalextralights.TheatricalExtraLightsRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.imabad.theatrical.Theatrical;
import dev.imabad.theatrical.api.Fixture;
import net.minecraft.resources.ResourceLocation;

public class Fixtures {
    public static final DeferredRegister<Fixture> FIXTURES = TheatricalExtraLightsRegistry.get(dev.imabad.theatrical.fixtures.Fixtures.FIXTURE_REGISTRY);

    public static final RegistrySupplier<Fixture> MOVING_VL6 =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "moving_vl6"), MovingVL6Fixture::new);

    public static final RegistrySupplier<Fixture> MOVING_BEAM =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "moving_beam"), MovingBeamFixture::new);
    public static final RegistrySupplier<Fixture> RGB_BAR =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "rgb_bar"), RGBbarFixture::new);

    public static final RegistrySupplier<Fixture> BIG_PANEL =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "big_panel"), BigPanelFixture::new);

    public static final RegistrySupplier<Fixture> BIG_PANEL2 =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "big_panel2"), BigPanel2Fixture::new);

    public static final RegistrySupplier<Fixture> LED_FOUNTAIN =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "led_fountain"), LEDfountainFixture::new);

    public static final RegistrySupplier<Fixture> MOVING_VL2C =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "moving_vl2c"), MovingVL2CFixture::new);

    public static final RegistrySupplier<Fixture> MOVING_SCAN =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "moving_scan"), MovingScanFixture::new);

    public static final RegistrySupplier<Fixture> LED_PANEL_2 =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "led_panel_2"), LEDPanel2Fixture::new);
    public static final RegistrySupplier<Fixture> PAR_LED =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "par_led"), ParLedFixture::new);

    public static void init(){
        FIXTURES.register();
    }
}
