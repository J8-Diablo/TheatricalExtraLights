package com.github.dumann089.theatricalextralights.blockentities;

import com.github.dumann089.theatricalextralights.blocks.Par1000OrangeBlock;
import com.github.dumann089.theatricalextralights.blocks.Par1000WhiteBlock;
import com.github.dumann089.theatricalextralights.fixtures.Fixtures;
import dev.imabad.theatrical.api.Fixture;
import dev.imabad.theatrical.blockentities.light.BaseDMXConsumerLightBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;

public class Par1000WhiteBlockEntity extends BaseDMXConsumerLightBlockEntity {

    public Par1000WhiteBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.PAR1000_WHITE.get(), pos, state);
        setChannelCount(1);
    }
    @Override
    public Fixture getFixture() {
        return Fixtures.PAR1000_WHITE.get();
    }

    @Override
    public int getFocus() {
        return 200;
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
    public boolean isUpsideDown() {
        return getBlockState().getValue(Par1000WhiteBlock.HANGING) && getBlockState().getValue(Par1000WhiteBlock.HANG_DIRECTION) == Direction.UP;
    }

    @Override
    public String getModelName() {
        return "Par 1000 White";
    }

    @Override
    public ResourceLocation getFixtureId() {
        return Fixtures.PAR1000_WHITE.getId();
    }

    @Override
    public int getActivePersonality() {
        return 0;
    }

    public int convertByteToInt(byte val) {
        return Byte.toUnsignedInt(val);
    }

}