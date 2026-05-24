package com.github.dumann089.theatricalextralights.fixtures;

import com.github.dumann089.theatricalextralights.TheatricalExtraLights;
import dev.imabad.theatrical.api.Fixture;
import dev.imabad.theatrical.api.HangType;
import dev.imabad.theatrical.api.dmx.DMXPersonality;
import dev.imabad.theatrical.blocks.light.BaseLightBlock;
import dev.imabad.theatrical.fixtures.SharedSlots;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;
import java.util.List;

public class LaserFixture extends Fixture {

    // Slot labels in DMX consoles will reuse the closest semantic from SharedSlots since
    // the RDM API isn't on the common module classpath. Channel order is what matters
    // for the controller; see LaserBlockEntity.consume() for the canonical mapping.
    private static final List<DMXPersonality> PERSONALITIES = Collections.singletonList(
            new DMXPersonality(19, "19-Channel Mode")
                    .addSlot(SharedSlots.INTENSITY)   // 1: Intensity
                    .addSlot(SharedSlots.RED)         // 2: R1
                    .addSlot(SharedSlots.GREEN)       // 3: G1
                    .addSlot(SharedSlots.BLUE)        // 4: B1
                    .addSlot(SharedSlots.RED)         // 5: R2
                    .addSlot(SharedSlots.GREEN)       // 6: G2
                    .addSlot(SharedSlots.BLUE)        // 7: B2
                    .addSlot(SharedSlots.RED)         // 8: R3
                    .addSlot(SharedSlots.GREEN)       // 9: G3
                    .addSlot(SharedSlots.BLUE)        // 10: B3
                    .addSlot(SharedSlots.FOCUS)       // 11: Pattern
                    .addSlot(SharedSlots.FOCUS)       // 12: Size
                    .addSlot(SharedSlots.FOCUS)       // 13: Amplitude
                    .addSlot(SharedSlots.FOCUS)       // 14: Speed
                    .addSlot(SharedSlots.FOCUS)       // 15: Rotation
                    .addSlot(SharedSlots.PAN)         // 16: Pan
                    .addSlot(SharedSlots.TILT)        // 17: Tilt
                    .addSlot(SharedSlots.FOCUS)       // 18: Focus
                    .addSlot(SharedSlots.FOCUS)       // 19: Persistence
    );

    private static final ResourceLocation TILT_MODEL = new ResourceLocation(TheatricalExtraLights.MOD_ID, "block/laser/laser_tilt");
    private static final ResourceLocation PAN_MODEL = new ResourceLocation(TheatricalExtraLights.MOD_ID, "block/laser/laser_pan");
    private static final ResourceLocation STATIC_MODEL = new ResourceLocation(TheatricalExtraLights.MOD_ID, "block/laser/laser_static");

    private final float[] tiltRotation = new float[]{0.5F, 0.53F, 0.0F};
    private final float[] panRotation = new float[]{0.5F, 0.53F, 0.0F};

    @Override
    public ResourceLocation getTiltModel() {
        return TILT_MODEL;
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
        return tiltRotation;
    }

    @Override
    public float[] getPanRotationPosition() {
        return panRotation;
    }

    @Override
    public float[] getBeamStartPosition() {
        return new float[]{0.5F, 0.53F, 0.093f};
    }

    @Override
    public float getDefaultRotation() {
        return 90;
    }

    @Override
    public float getBeamWidth() {
        return 0.00f;
    }

    @Override
    public float getRayTraceRotation() {
        return 0;
    }

    @Override
    public HangType getHangType() {
        return HangType.BRACE_BAR;
    }

    @Override
    public float[] getTransforms(BlockState fixtureBlockState, BlockState supportBlockState) {
        if (fixtureBlockState.getValue(BaseLightBlock.HANG_DIRECTION) == Direction.UP) {
            return new float[]{0, .5f, 0};
        }
        return new float[]{0, -0.35F, 0};
    }

    @Override
    public List<DMXPersonality> getDMXPersonalities() {
        return PERSONALITIES;
    }

    @Override
    public double getLightRadius() {
        return 0.0;
    }
}
