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

public class VoleroFixture extends Fixture {

    private static final List<DMXPersonality> PERSONALITIES = Collections.singletonList(
            new DMXPersonality(10, "10-Channel Mode")
                    .addSlot(SharedSlots.INTENSITY)
                    .addSlot(SharedSlots.RED)
                    .addSlot(SharedSlots.GREEN)
                    .addSlot(SharedSlots.BLUE)
                    .addSlot(SharedSlots.TILT)
                    .addSlot(SharedSlots.TILT)
                    .addSlot(SharedSlots.TILT)
                    .addSlot(SharedSlots.TILT)
                    .addSlot(SharedSlots.TILT)
                    .addSlot(SharedSlots.TILT)
    );

    private static final ResourceLocation TILT_MODEL1 = new ResourceLocation(TheatricalExtraLights.MOD_ID, "block/volero_wave/volero_tilt_1");
    private static final ResourceLocation TILT_MODEL2 = new ResourceLocation(TheatricalExtraLights.MOD_ID, "block/volero_wave/volero_tilt_2");
    private static final ResourceLocation TILT_MODEL3 = new ResourceLocation(TheatricalExtraLights.MOD_ID, "block/volero_wave/volero_tilt_3");
    private static final ResourceLocation TILT_MODEL4 = new ResourceLocation(TheatricalExtraLights.MOD_ID, "block/volero_wave/volero_tilt_4");
    private static final ResourceLocation TILT_MODEL5 = new ResourceLocation(TheatricalExtraLights.MOD_ID, "block/volero_wave/volero_tilt_5");
    private static final ResourceLocation TILT_MODEL6 = new ResourceLocation(TheatricalExtraLights.MOD_ID, "block/volero_wave/volero_tilt_6");

    private static final ResourceLocation STATIC_MODEL = new ResourceLocation(TheatricalExtraLights.MOD_ID, "block/volero_wave/volero_static");

    private final float[] tiltRotation1 = new float[]{0.89F, 0.26F, .54F};
    private final float[] tiltRotation2 = new float[]{0.73F, 0.26F, .54F};
    private final float[] tiltRotation3 = new float[]{0.57F, 0.26F, .54F};
    private final float[] tiltRotation4 = new float[]{0.42F, 0.26F, .54F};
    private final float[] tiltRotation5 = new float[]{0.26F, 0.26F, .54F};
    private final float[] tiltRotation6 = new float[]{0.11F, 0.26F, .54F};

//    private final float[] beamStartPosition = new float[]{0.5F, 1.875F, 0.4375F};


    @Override
    public ResourceLocation getTiltModel() {
        return TILT_MODEL1;
    }


    public ResourceLocation getTiltModel2() {
        return TILT_MODEL2;
    }


    public ResourceLocation getTiltModel3() {
        return TILT_MODEL3;
    }


    public ResourceLocation getTiltModel4() {
        return TILT_MODEL4;
    }


    public ResourceLocation getTiltModel5() {
        return TILT_MODEL5;
    }

    public ResourceLocation getTiltModel6() {
        return TILT_MODEL6;
    }
    

    
    @Override
    public ResourceLocation getStaticModel() {
        return STATIC_MODEL;
    }

    @Override
    public float[] getTiltRotationPosition() {
        return tiltRotation1;
    }

    public float[] getTiltRotationPosition2() {
        return tiltRotation2;
    }

    public float[] getTiltRotationPosition3() {
        return tiltRotation3;
    }

    public float[] getTiltRotationPosition4() {
        return tiltRotation4;
    }

    public float[] getTiltRotationPosition5() {
        return tiltRotation5;
    }

    public float[] getTiltRotationPosition6() {
        return tiltRotation6;
    }

    @Override
    public float[] getBeamStartPosition() {
        return new float[]{0.89F, 0.26F, .54F};
    }

    public float[] getBeamStartPosition2() {
        return new float[]{0.73F, 0.26F, .54F};
    }

    public float[] getBeamStartPosition3() {
        return new float[]{0.57F, 0.26F, .54F};
    }

    public float[] getBeamStartPosition4() {
        return new float[]{0.42F, 0.26F, .54F};
    }

    public float[] getBeamStartPosition5() {
        return new float[]{0.26F, 0.26F, .54F};
    }

    public float[] getBeamStartPosition6() {
        return new float[]{0.11F, 0.26F, .54F};
    }

    @Override
    public float getDefaultRotation() {
        return 90;
    }

    @Override
    public float getBeamWidth() {
        return 0.08f;
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
        if(fixtureBlockState.getValue(BaseLightBlock.HANG_DIRECTION) == Direction.UP){
            return new float[]{0, .5f, 0};
        }
        return new float[]{0, -0.35F, 0};
    }

    @Override
    public List<DMXPersonality> getDMXPersonalities() {
        return PERSONALITIES;
    }

    @Override
    public ResourceLocation getPanModel() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public float[] getPanRotationPosition() {
        // TODO Auto-generated method stub
        return null;
    }
}