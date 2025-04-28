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

    public static final RegistrySupplier<Fixture> SEARCHLIGHT =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "searchlight"), SearchlightFixture::new);

    public static final RegistrySupplier<Fixture> BEAM_7R =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "beam_7r"), Beam7RFixture::new);

    public static final RegistrySupplier<Fixture> LASER =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "laser"), LaserFixture::new);

    public static final RegistrySupplier<Fixture> RGB_BAR =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "rgb_bar"), RGBbarFixture::new);
      
    public static final RegistrySupplier<Fixture> VERTICAL_BAR =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "vertical_bar"), VerticalbarFixture::new);

    public static final RegistrySupplier<Fixture> PAR1000_BLUE =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "par1000_blue"), Par1000BlueFixture::new);

    public static final RegistrySupplier<Fixture> PAR1000_MAGENTA =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "par1000_magenta"), Par1000MagentaFixture::new);

    public static final RegistrySupplier<Fixture> PAR1000_AMBER =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "par1000_amber"), Par1000AmberFixture::new);
 
    public static final RegistrySupplier<Fixture> PAR1000_PURPLE =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "par1000_purple"), Par1000PurpleFixture::new);

    public static final RegistrySupplier<Fixture> PAR1000_LIGHTBLUE =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "par1000_lightblue"), Par1000LightblueFixture::new);

    public static final RegistrySupplier<Fixture> PAR1000 =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "par1000"), Par1000Fixture::new);

    public static final RegistrySupplier<Fixture> PAR1000_GREEN =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "par1000_green"), Par1000GreenFixture::new);

    public static final RegistrySupplier<Fixture> PAR1000_RED =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "par1000_red"), Par1000RedFixture::new);

    public static final RegistrySupplier<Fixture> PAR1000_WHITE =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "par1000_white"), Par1000WhiteFixture::new);

    public static final RegistrySupplier<Fixture> SOURCE_FOUR =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "source_four"), Source4Fixture::new);

    public static final RegistrySupplier<Fixture> MAC_VIP =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "mac_vip"), MacVipFixture::new);

    public static final RegistrySupplier<Fixture> MOVING500 =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "moving500"), Moving500Fixture::new);

    public static final RegistrySupplier<Fixture> ROBITSPOT =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "robitspot"), RobitspotFixture::new); 
            
    public static final RegistrySupplier<Fixture> VERVESPOT =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "vervespot"), VervespotFixture::new);        

    public static final RegistrySupplier<Fixture> SHARPLUS =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "sharplus"), SharplusFixture::new);

    public static final RegistrySupplier<Fixture> BIG_PANEL =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "big_panel"), BigPanelFixture::new);

    public static final RegistrySupplier<Fixture> BIG_PANEL2 =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "big_panel2"), BigPanel2Fixture::new);

    public static final RegistrySupplier<Fixture> LED_FOUNTAIN =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "led_fountain"), LEDfountainFixture::new);

    public static final RegistrySupplier<Fixture> BLINDER =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "blinder"), BlinderFixture::new);

    public static final RegistrySupplier<Fixture> STROBE =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "strobe"), StrobeFixture::new);
            
    public static final RegistrySupplier<Fixture> TRUSS_3LIGHTS =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "truss_3lights"), truss3lightsFixture::new);

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
