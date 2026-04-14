package com.github.dumann089.theatricalextralights.blockentities;

import com.github.dumann089.theatricalextralights.blocks.WhiteStrobeBlock;
import com.github.dumann089.theatricalextralights.fixtures.Fixtures;
import dev.imabad.theatrical.api.Fixture;
import dev.imabad.theatrical.blockentities.light.BaseDMXConsumerLightBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;

public class WhiteStrobeBlockEntity extends BaseDMXConsumerLightBlockEntity {

    public WhiteStrobeBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.WHITE_STROBE.get(), pos, state);
        setChannelCount(1);
    }

    private int strobeTick = 0;
    private boolean strobeOn = false;

    @Override
    public Fixture getFixture() {
        return Fixtures.WHITE_STROBE.get();
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
        if(this.storePrev()){
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
        intensity = convertByteToInt(ourValues[0]);
        red = convertByteToInt(ourValues[0]);
        green = convertByteToInt(ourValues[0]);
        blue = convertByteToInt(ourValues[0]);
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        setChanged();
    }

    @Override
    public int getDeviceTypeId() {
        return 0x02;
    }

    @Override
    public String getModelName() {
        return "White Strobe";
    }

    @Override
    public ResourceLocation getFixtureId() {
        return Fixtures.WHITE_STROBE.getId();
    }

    @Override
    public boolean isUpsideDown() {
        return getBlockState().getValue(WhiteStrobeBlock.HANGING) && getBlockState().getValue(WhiteStrobeBlock.HANG_DIRECTION) == Direction.UP;
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
        return "block.theatricalextralights.white_strobe";
    }
}