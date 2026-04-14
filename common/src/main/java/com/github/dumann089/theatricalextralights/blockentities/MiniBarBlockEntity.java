package com.github.dumann089.theatricalextralights.blockentities;

import com.github.dumann089.theatricalextralights.blocks.RGBbarBlock;
import com.github.dumann089.theatricalextralights.fixtures.Fixtures;
import dev.imabad.theatrical.api.Fixture;
import dev.imabad.theatrical.blockentities.light.BaseDMXConsumerLightBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;

public class MiniBarBlockEntity extends BaseDMXConsumerLightBlockEntity {

    public MiniBarBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.MINI_BAR.get(), pos, state);
        setChannelCount(4);
    }
    @Override
    public Fixture getFixture() {
        return Fixtures.MINI_BAR.get();
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
        if(ourValues.length < 4){
            return;
        }
        if(this.storePrev()){
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
        intensity = convertByteToInt(ourValues[0]);
        red = convertByteToInt(ourValues[1]);
        green = convertByteToInt(ourValues[2]);
        blue = convertByteToInt(ourValues[3]);
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        setChanged();
    }


    @Override
    public int getDeviceTypeId() {
        return 0x02;
    }

    @Override
    public String getModelName() {
        return "Mini Bar";
    }

    @Override
    public boolean isUpsideDown() {
        return getBlockState().getValue(RGBbarBlock.HANGING) && getBlockState().getValue(RGBbarBlock.HANG_DIRECTION) == Direction.UP;
    }

    @Override
    public ResourceLocation getFixtureId() {
        return Fixtures.MINI_BAR.getId();
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
        return "block.theatricalextralights.mini_bar";
    }
}