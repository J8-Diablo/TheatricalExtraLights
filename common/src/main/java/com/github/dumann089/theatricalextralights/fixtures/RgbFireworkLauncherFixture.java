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

import java.util.List;

public class RgbFireworkLauncherFixture extends Fixture {
    private static final List<DMXPersonality> PERSONALITIES = List.of(
            new DMXPersonality(7, "7-Channel RGB Firework")
                    .addSlot(SharedSlots.INTENSITY)
                    .addSlot(SharedSlots.TILT)
                    .addSlot(SharedSlots.FOCUS)
                    .addSlot(SharedSlots.RED)
                    .addSlot(SharedSlots.GREEN)
                    .addSlot(SharedSlots.BLUE)
                    .addSlot(SharedSlots.PAN)
    );

    private static final ResourceLocation STATIC_MODEL = new ResourceLocation(TheatricalExtraLights.MOD_ID, "block/firework/firework_rgb_launcher_static");
    private static final ResourceLocation PAN_MODEL = new ResourceLocation(TheatricalExtraLights.MOD_ID, "block/firework/firework_rgb_launcher_pan");
    private final float[] tiltPivot = new float[]{0.5f, 0.25f, 0.5f};

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
