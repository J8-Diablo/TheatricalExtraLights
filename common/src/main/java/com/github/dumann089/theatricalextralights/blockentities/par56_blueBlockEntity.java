package com.github.dumann089.theatricalextralights.blockentities;

import com.github.dumann089.theatricalextralights.fixtures.Fixtures;
import dev.imabad.theatrical.api.Fixture;
import dev.imabad.theatrical.blockentities.light.BaseDMXConsumerLightBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;

public class par56_blueBlockEntity extends BaseDMXConsumerLightBlockEntity {

    public par56_blueBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.PAR56_BLUE.get(), pos, state);
        setChannelCount(1);
    }
    @Override
    public Fixture getFixture() {
        return Fixtures.PAR56_BLUE.get();
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
        return "Par56 Blue";
    }

    @Override
    public ResourceLocation getFixtureId() {
        return Fixtures.PAR56_BLUE.getId();
    }

    @Override
    public int getActivePersonality() {
        return 0;
    }


    public int convertByteToInt(byte val) {
        return Byte.toUnsignedInt(val);
    }


}