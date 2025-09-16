package com.github.dumann089.theatricalextralights.blockentities;

import com.github.dumann089.theatricalextralights.blocks.Par1000BlueBlock;
import com.github.dumann089.theatricalextralights.blocks.Par1000OrangeBlock;
import com.github.dumann089.theatricalextralights.fixtures.Fixtures;
import dev.imabad.theatrical.api.Fixture;
import dev.imabad.theatrical.blockentities.light.BaseDMXConsumerLightBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;

public class Par1000OrangeBlockEntity extends BaseDMXConsumerLightBlockEntity {

    public Par1000OrangeBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.PAR1000_ORANGE.get(), pos, state);
        setChannelCount(1);
    }
    @Override
    public Fixture getFixture() {
        return Fixtures.PAR1000_ORANGE.get();
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
        red = intensity; 
        green = (int)(intensity * 0.45);

        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        setChanged();
    }

    @Override
    public int getDeviceTypeId() {
        return 0x02;
    }

    @Override
    public String getModelName() {
        return "Par 1000 Orange";
    }

    @Override
    public boolean isUpsideDown() {
        return getBlockState().getValue(Par1000OrangeBlock.HANGING) && getBlockState().getValue(Par1000OrangeBlock.HANG_DIRECTION) == Direction.UP;
    }

    @Override
    public ResourceLocation getFixtureId() {
        return Fixtures.PAR1000_ORANGE.getId();
    }

    @Override
    public int getActivePersonality() {
        return 0;
    }

    public int convertByteToInt(byte val) {
        return Byte.toUnsignedInt(val);
    }

}