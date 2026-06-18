package com.github.dumann089.theatricalextralights.fixtures;

import com.github.dumann089.theatricalextralights.TheatricalExtraLights;
import dev.imabad.theatrical.api.Fixture;
import dev.imabad.theatrical.api.HangType;
import dev.imabad.theatrical.api.dmx.DMXPersonality;
import dev.imabad.theatrical.api.dmx.DMXSlot;
import dev.imabad.theatrical.blocks.light.BaseLightBlock;
import dev.imabad.theatrical.fixtures.SharedSlots;
import ch.bildspur.artnet.rdm.RDMSlotID;
import ch.bildspur.artnet.rdm.RDMSlotType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class PyroFanFixture extends Fixture {
    public static final int PERSONALITY_3CH = 0;
    public static final int PERSONALITY_10CH = 1;

    private static final ResourceLocation STATIC_MODEL =
            new ResourceLocation(TheatricalExtraLights.MOD_ID, "block/firework/firework_gold_comet_static");
    private static final ResourceLocation PAN_MODEL =
            new ResourceLocation(TheatricalExtraLights.MOD_ID, "block/firework/firework_gold_comet_pan");
    private static final List<DMXPersonality> PERSONALITIES = buildPersonalities();
    private final float[] tiltPivot = new float[]{0.5f, 0.25f, 0.5f};

    private static List<DMXPersonality> buildPersonalities() {
        DMXPersonality threeChannel = new DMXPersonality(3, "3-Channel Pyro Fan (All Tubes)")
                .addSlot(SharedSlots.INTENSITY)
                .addSlot(SharedSlots.TILT)
                .addSlot(SharedSlots.FOCUS);
        DMXPersonality tenChannel = new DMXPersonality(10, "10-Channel Pyro Fan (Per Tube)");
        for (int i = 1; i <= 10; i++) {
            tenChannel.addSlot(new DMXSlot("Tube " + i, RDMSlotType.ST_PRIMARY, RDMSlotID.SD_INTENSITY));
        }
        return List.of(threeChannel, tenChannel);
    }

    @Override
    public ResourceLocation getTiltModel() {
        return PAN_MODEL;
    }

    @Override
    public ResourceLocation getPanModel() {
        return PAN_MODEL;
    }

    @Override
    public ResourceLocation getStaticModel() {
        return STATIC_MODEL;
    }

    @Override
    public float[] getTiltRotationPosition() {
        return tiltPivot;
    }

    @Override
    public float[] getPanRotationPosition() {
        return tiltPivot;
    }

    @Override
    public float[] getBeamStartPosition() {
        return new float[]{0.5f, 0.85f, 0.5f};
    }

    @Override
    public float getDefaultRotation() {
        return 180.0f;
    }

    @Override
    public float getBeamWidth() {
        return 0.0f;
    }

    @Override
    public float getRayTraceRotation() {
        return 0.0f;
    }

    @Override
    public HangType getHangType() {
        return HangType.BRACE_BAR;
    }

    @Override
    public float[] getTransforms(BlockState fixtureBlockState, BlockState supportBlockState) {
        if (fixtureBlockState.getValue(BaseLightBlock.HANG_DIRECTION) == Direction.UP) {
            return new float[]{0, 0.5f, 0};
        }
        return new float[]{0, -0.35f, 0};
    }

    @Override
    public List<DMXPersonality> getDMXPersonalities() {
        return PERSONALITIES;
    }

    @Override
    public double getLightRadius() {
        return 0.5d;
    }
}
