package com.github.dumann089.theatricalextralights.fixtures;

import com.github.dumann089.theatricalextralights.TheatricalExtraLights;
import dev.imabad.theatrical.Theatrical;
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

public class bigscrollerFixture extends Fixture {

    private static final List<DMXPersonality> PERSONALITIES = Collections.singletonList(
            new DMXPersonality(4, "4-Channel Mode")
                    .addSlot(SharedSlots.INTENSITY)
                    .addSlot(SharedSlots.RED)
                    .addSlot(SharedSlots.GREEN)
                    .addSlot(SharedSlots.BLUE)

    );

    private static final ResourceLocation TILT_MODEL = new ResourceLocation(TheatricalExtraLights.MOD_ID, "block/scrollers/bigscrollers/bigscroller_whole");
    private static final ResourceLocation PAN_MODEL = new ResourceLocation(TheatricalExtraLights.MOD_ID, "block/scrollers/bigscrollers/bigscroller_static");
    private static final ResourceLocation STATIC_MODEL = new ResourceLocation(TheatricalExtraLights.MOD_ID, "block/scrollers/bigscrollers/bigscroller_static");

    private final float[] tiltRotation = new float[]{0.5F, 0.5F, .5F};
    private final float[] panRotation = new float[]{0.5F, 0.5F, 0.5F};
//  private final float[] beamStartPosition = new float[]{ 0.56F, 1.43F, 0.226F };
// private final float[] beamStartPosition2 = new float[]{ -0.56F, 1.43F, 0.226F };
// private final float[] beamStartPosition3 = new float[]{ 0.56F, 0.43F, 0.226F };
// private final float[] beamStartPosition4 = new float[]{ -0.56F, 0.43F, 0.226F };


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
    return new float[]{
        0.625F, -0.625F, 0.812F,   // Beam 1
        0.625F, -0.1875F, 0.812F,   // Beam 2
        0.625F, 0.25F, 0.812F,   // Beam 3
       0.625F, 0.6875F, 0.812F,   // Beam 4
       0.625F, 1.125F, 0.812F,   // Beam 5
       0.625F, 1.5625F, 0.812F,   // Beam 6
       0.0F, -0.625F, 0.812F,   // Beam 7
       0.0F, -0.1875F, 0.812F,   // Beam 8
       0.0F, 0.25F, 0.812F,   // Beam 9
       0.0F, 0.6875F, 0.812F,   // Beam 10
       0.0F, 1.125F, 0.812F,   // Beam 11
       0.0F, 1.5625F, 0.812F,   // Beam 12
        -0.618F, -0.625F, 0.812F,   // Beam 13
       -0.618F, -0.1875F, 0.812F,   // Beam 14
       -0.618F, 0.25F, 0.812F,   // Beam 15
       -0.618F, 0.6875F, 0.812F,   // Beam 16
       -0.618F, 1.125F, 0.812F,   // Beam 17
       -0.618F, 1.5625F, 0.812F,   // Beam 18
        };
    }
    

    @Override
    public float getDefaultRotation() {
        return 0;
    }

    @Override
    public float getBeamWidth() {
        return 0.0f;
    }

    @Override
    public float getRayTraceRotation() {
        return 180f;
    }

    @Override
    public HangType getHangType() {
        return HangType.HOOK_BAR;
    }

    @Override
    public float[] getTransforms(BlockState fixtureBlockState, BlockState supportBlockState) {
        if(fixtureBlockState.getValue(BaseLightBlock.HANG_DIRECTION) == Direction.UP){
            return new float[]{0, .5f, 0};
        }
        return new float[]{0, 0.5F, 0};
    }

    @Override
    public List<DMXPersonality> getDMXPersonalities() {
        return PERSONALITIES;
    }

    @Override
    public boolean invertTilt() {
        return true;
    }

    @Override
    public boolean invertPan() {
        return true;
    }

    @Override
    public double getLightRadius() {
        return 8.5;
    }
}
