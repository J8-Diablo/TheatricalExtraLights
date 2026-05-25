package com.github.dumann089.theatricalextralights.blockentities;

import com.github.dumann089.theatricalextralights.blocks.Source4Block;
import com.github.dumann089.theatricalextralights.fixtures.Fixtures;
import dev.imabad.theatrical.api.Fixture;
import dev.imabad.theatrical.blockentities.light.BaseDMXConsumerLightBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;

public class Source4warmBlockEntity extends ExtraLightsLightBlockEntity {

    public Source4warmBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.SOURCE_FOUR_WARM.get(), pos, state);
        setChannelCount(1);
    }
    @Override
    public Fixture getFixture() {
        return Fixtures.SOURCE_FOUR_WARM.get();
    }

    @Override
    public int getFocus() {
        return 255;
    }

    @Override
    public void consume(byte[] dmxValues) {
        int start = this.getChannelStart() > 0 ? this.getChannelStart() - 1 : 0;
        byte[] ourValues = Arrays.copyOfRange(dmxValues, start,
                start+ this.getChannelCount());
        if(ourValues.length < 1){
            return;
        }
                boolean prevAdvanced = beginDmxUpdate();
        int _pi = intensity, _pr = red, _pg = green, _pb = blue, _pf = focus, _pp = pan, _pt = tilt;
        intensity = convertByteToInt(ourValues[0]);

        float scale = intensity / 255f;

        red = (int)(255 * scale);
        green = (int)(200 * scale);
        blue = (int)(100 * scale);

        finishDmxUpdate(intensity != _pi || red != _pr || green != _pg || blue != _pb || focus != _pf || pan != _pp || tilt != _pt, prevAdvanced);
    }

    @Override
    public int getDeviceTypeId() {
        return 0x02;
    }

    @Override
    public String getModelName() {
        return "Source Four Warm";
    }

    @Override
    public boolean isUpsideDown() {
        return getBlockState().getValue(Source4Block.HANGING) && getBlockState().getValue(Source4Block.HANG_DIRECTION) == Direction.UP;
    }

    @Override
    public ResourceLocation getFixtureId() {
        return Fixtures.SOURCE_FOUR_WARM.getId();
    }

    @Override
    public int getActivePersonality() {
        return 0;
    }

    public int convertByteToInt(byte val) {
        return Byte.toUnsignedInt(val);
    }

    @Override
    public String getTranslationKey() {
        return "block.theatricalextralights.source_four_warm";
    }
}