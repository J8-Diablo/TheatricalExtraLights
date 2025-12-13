package com.github.dumann089.theatricalextralights.blockentities;

import com.github.dumann089.theatricalextralights.fixtures.Fixtures;
import com.github.dumann089.theatricalextralights.particle.ModParticle;
import dev.imabad.theatrical.api.Fixture;
import dev.imabad.theatrical.blockentities.light.BaseDMXConsumerLightBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;

public class WaterJet35mBlockEntity extends BaseDMXConsumerLightBlockEntity {

    public double smoothedHeight = 0.0;
    private int tickCounter = 0;

    public WaterJet35mBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.WATER_JET35M.get(), pos, state);
        setChannelCount(1);
    }

    @Override
    public Fixture getFixture() {
        return Fixtures.WATER_JET35M.get();
    }

    @Override
    public int getFocus() {
        return 255;
    }

    @Override
    public void consume(byte[] dmxValues) {
        int start = this.getChannelStart() > 0 ? this.getChannelStart() - 1 : 0;
        byte[] ourValues = Arrays.copyOfRange(dmxValues, start, start + this.getChannelCount());
        if (ourValues.length < 1) {
            return;
        }
        if (this.storePrev()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
        intensity = convertByteToInt(ourValues[0]);
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        setChanged();
    }

    @Override
    public int getDeviceTypeId() {
        return 0x02;
    }

    @Override
    public String getModelName() {
        return "Water Jet 35m";
    }

    @Override
    public ResourceLocation getFixtureId() {
        return Fixtures.WATER_JET35M.getId();
    }

    @Override
    public int getActivePersonality() {
        return 0;
    }

    public int convertByteToInt(byte val) {
        return Byte.toUnsignedInt(val);
    }

    public void tick() {
        if (!level.isClientSide || Minecraft.getInstance().isPaused()) return;

        double x = worldPosition.getX() + 0.5;
        double y = worldPosition.getY();
        double z = worldPosition.getZ() + 0.5;

        double maxHeight = 3.0;
        double targetHeight = (intensity / 255.0) * maxHeight;

        double normalizedIntensity = intensity / 255.0;

        smoothedHeight += (targetHeight - smoothedHeight) * 0.1;

        if (tickCounter % 8 == 0) {
            level.addAlwaysVisibleParticle(
                    ModParticle.WATERJETPARTICLE.get(),
                    true,
                    x, y + smoothedHeight, z,
                    0,
                    intensity / 255.0,
                    0                   
            );
        }
    }
    @Override
    public String getTranslationKey() {
        return "block.theatricalextralights.water_jet35m";
    }
}
