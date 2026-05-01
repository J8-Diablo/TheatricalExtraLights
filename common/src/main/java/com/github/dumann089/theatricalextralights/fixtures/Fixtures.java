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

    public static final RegistrySupplier<Fixture> PAR1000_ORANGE =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "par1000_orange"), Par1000OrangeFixture::new);

    public static final RegistrySupplier<Fixture> PAR1000_WHITE =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "par1000_white"), Par1000WhiteFixture::new);

    public static final RegistrySupplier<Fixture> SOURCE_FOUR =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "source_four"), Source4Fixture::new);

    public static final RegistrySupplier<Fixture> SOURCE_FOUR_WARM =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "source_four_warm"), Source4warmFixture::new);

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

    public static final RegistrySupplier<Fixture> BLINDER_WARM =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "blinder_warm"), BlinderwarmFixture::new);

    public static final RegistrySupplier<Fixture> STROBE =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "strobe"), StrobeFixture::new);
            
    public static final RegistrySupplier<Fixture> TRUSS_3LIGHTS =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "truss_3lights"), truss3lightsFixture::new);

    public static final RegistrySupplier<Fixture> MOVING_VL2C =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "moving_vl2c"), MovingVL2CFixture::new);

    public static final RegistrySupplier<Fixture> MOVING_VL2C_BEAMS =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "moving_vl2c_beams"), MovingVL2CBeamsFixture::new);
    public static final RegistrySupplier<Fixture> MOVING_SCAN_BEAMS =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "moving_scan_beams"), MovingScanBeamsFixture::new);



    public static final RegistrySupplier<Fixture> WASHLIGHT =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "washlight"), WashlightFixture::new);

 public static final RegistrySupplier<Fixture> MINIWASH =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "miniwash"), MiniwashFixture::new);

 public static final RegistrySupplier<Fixture> INVISIBLELIGHT =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "invisiblelight"), InvisiblelightFixture::new);

    public static final RegistrySupplier<Fixture> ATOMICTILT =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "atomictilt"), AtomictiltFixture::new);


    public static final RegistrySupplier<Fixture> MOVING_SCAN =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "moving_scan"), MovingScanFixture::new);

    public static final RegistrySupplier<Fixture> LED_PANEL_2 =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "led_panel_2"), LEDPanel2Fixture::new);
    public static final RegistrySupplier<Fixture> PAR_LED =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "par_led"), ParLedFixture::new);
    public static final RegistrySupplier<Fixture> MOVING_JET =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "moving_jet"), MovingJetFixture::new);
    public static final RegistrySupplier<Fixture> WATER_JET_THIN =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "water_jet_thin"), WaterJetThinFixture::new);
    public static final RegistrySupplier<Fixture> WATER_JET_SPREAD =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "water_jet_spread"), WaterJetSpreadFixture::new);
    public static final RegistrySupplier<Fixture> WATER_JET_BLOOM =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "water_jet_bloom"), WaterJetBloomFixture::new);
    public static final RegistrySupplier<Fixture> WATER_JET_FOG =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "water_jet_fog"), WaterJetFogFixture::new);

    public static final RegistrySupplier<Fixture> x8PAR_RED =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "x8par_red"), x8par_redFixture::new);
    public static final RegistrySupplier<Fixture> x8PAR_GREEN =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "x8par_green"), x8par_greenFixture::new);
    public static final RegistrySupplier<Fixture> x8PAR_BLUE =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "x8par_blue"), x8par_blueFixture::new);
    public static final RegistrySupplier<Fixture> x8PAR_MAGENTA =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "x8par_magenta"), x8par_magentaFixture::new);
    public static final RegistrySupplier<Fixture> x8PAR_LIGHTBLUE =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "x8par_lightblue"), x8par_lightblueFixture::new);
    public static final RegistrySupplier<Fixture> x8PAR_YELLOW =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "x8par_yellow"), x8par_yellowFixture::new);
    public static final RegistrySupplier<Fixture> x8PAR_WHITE =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "x8par_white"), x8par_whiteFixture::new);
    public static final RegistrySupplier<Fixture> x8PAR_WARM =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "x8par_warm"), x8par_warmFixture::new);
    public static final RegistrySupplier<Fixture> x8PAR_PURPLE =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "x8par_purple"), x8par_purpleFixture::new);
    public static final RegistrySupplier<Fixture> x8PAR_ORANGE =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "x8par_orange"), x8par_orangeFixture::new);

    public static final RegistrySupplier<Fixture> PAR56_RED =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "par56_red"), par56_redFixture::new);
    public static final RegistrySupplier<Fixture> PAR56_GREEN =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "par56_green"), par56_greenFixture::new);
    public static final RegistrySupplier<Fixture> PAR56_BLUE =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "par56_blue"), par56_blueFixture::new);
    public static final RegistrySupplier<Fixture> PAR56_ORANGE =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "par56_orange"), par56_orangeFixture::new);
    public static final RegistrySupplier<Fixture> PAR56_MAGENTA =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "par56_magenta"), par56_magentaFixture::new);
    public static final RegistrySupplier<Fixture> PAR56_LIGHTBLUE =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "par56_lightblue"), par56_lightblueFixture::new);
    public static final RegistrySupplier<Fixture> PAR56_PURPLE =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "par56_purple"), par56_purpleFixture::new);
    public static final RegistrySupplier<Fixture> PAR56_YELLOW =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "par56_yellow"), par56_yellowFixture::new);
    public static final RegistrySupplier<Fixture> PAR56_WHITE =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "par56_white"), par56_whiteFixture::new);
    public static final RegistrySupplier<Fixture> PAR56_WARM =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "par56_warm"), par56_warmFixture::new);

    public static final RegistrySupplier<Fixture> A2X2PAR64_RED =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "a2x2par64_red"), a2x2par64_redFixture::new);
    public static final RegistrySupplier<Fixture> A2X2PAR64_GREEN =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "a2x2par64_green"), a2x2par64_greenFixture::new);
    public static final RegistrySupplier<Fixture> A2X2PAR64_BLUE =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "a2x2par64_blue"), a2x2par64_blueFixture::new);
    public static final RegistrySupplier<Fixture> A2X2PAR64_MAGENTA =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "a2x2par64_magenta"), a2x2par64_magentaFixture::new);
    public static final RegistrySupplier<Fixture> A2X2PAR64_PURPLE =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "a2x2par64_purple"), a2x2par64_purpleFixture::new);
    public static final RegistrySupplier<Fixture> A2X2PAR64_LIGHTBLUE =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "a2x2par64_lightblue"), a2x2par64_lightblueFixture::new);
    public static final RegistrySupplier<Fixture> A2X2PAR64_ORANGE =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "a2x2par64_orange"), a2x2par64_orangeFixture::new);
    public static final RegistrySupplier<Fixture> A2X2PAR64_YELLOW =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "a2x2par64_yellow"), a2x2par64_yellowFixture::new);
    public static final RegistrySupplier<Fixture> A2X2PAR64_WARM =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "a2x2par64_warm"), a2x2par64_warmFixture::new);
    public static final RegistrySupplier<Fixture> A2X2PAR64_WHITE =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "a2x2par64_white"), a2x2par64_whiteFixture::new);


    public static final RegistrySupplier<Fixture> VL6000 =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "vl6000"), VL6000Fixture::new);
    public static final RegistrySupplier<Fixture> FOLLOWSPOT =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "followspot"), FollowspotFixture::new);
    public static final RegistrySupplier<Fixture> BIGSCROLLER =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "bigscroller"), bigscrollerFixture::new);
    public static final RegistrySupplier<Fixture> HORIZONTALSCROLLER =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "horizontalscroller"), horizontalscrollerFixture::new);
    public static final RegistrySupplier<Fixture> VERTICALSCROLLER =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "verticalscroller"), verticalscrollerFixture::new);
    public static final RegistrySupplier<Fixture> WASHLED =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "washled"), washledFixture::new);
    public static final RegistrySupplier<Fixture> MOVING_BAR =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "moving_bar"), MovingbarFixture::new);
    public static final RegistrySupplier<Fixture> MOVING_MINI_BAR =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "moving_mini_bar"), MovingMiniBarFixture::new);

    public static final RegistrySupplier<Fixture> WATER_JET =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "water_jet"), WaterJetFixture::new);
    public static final RegistrySupplier<Fixture> SPINNER =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "spinner"), SpinnerFixture::new);
    public static final RegistrySupplier<Fixture> ORGANPIPES =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "organpipes"), OrganPipesFixture::new);
    public static final RegistrySupplier<Fixture> ORGANPIPES_INV =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "organpipes_inv"), OrganPipesInvFixture::new);
    public static final RegistrySupplier<Fixture> WATER_JET_BIG =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "water_jet_inv"), WaterJetBigFixture::new);
    public static final RegistrySupplier<Fixture> WATER_JET_CENTRAL =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "water_jet_central"), WaterJetCentralFixture::new);
    public static final RegistrySupplier<Fixture> WATER_JET_CONE =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "water_jet_cone"), WaterJetConeFixture::new);

    public static final RegistrySupplier<Fixture> WHITE_STROBE =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "white_strobe"), WhiteStrobeFixture::new);
    public static final RegistrySupplier<Fixture> LASER_MIRROR =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "laser_mirror"), LaserMirrorFixture::new);
    public static final RegistrySupplier<Fixture> PARSCROLLER =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "parscroller"), ParScrollerFixture::new);
    public static final RegistrySupplier<Fixture> BLINDER2X2 =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "blinder2x2"), Blinder2x2Fixture::new);
    public static final RegistrySupplier<Fixture> BLINDER2X2WARM =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "blinder2x2warm"), Blinder2x2warmFixture::new);
    public static final RegistrySupplier<Fixture> MINI_BAR =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "mini_bar"), MiniBarFixture::new);

    public static final RegistrySupplier<Fixture> A1X1PAR64 =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "a1x1par64"), a1x1par64Fixture::new);
    public static final RegistrySupplier<Fixture> A2X8PAR64 =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "a2x8par64"), a2x8par64Fixture::new);
    public static final RegistrySupplier<Fixture> A6X3PAR64_VERTICAL =
            FIXTURES.register(new ResourceLocation(TheatricalExtraLights.MOD_ID, "a6x3par64_vertical"), a6x3par64_verticalFixture::new);

    public static void init(){
        FIXTURES.register();
    }
}
