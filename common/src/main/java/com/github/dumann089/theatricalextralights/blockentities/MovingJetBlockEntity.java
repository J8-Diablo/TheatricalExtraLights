package com.github.dumann089.theatricalextralights.blockentities;

import com.github.dumann089.theatricalextralights.fixtures.Fixtures;
import com.github.dumann089.theatricalextralights.particle.ModParticle;
import dev.imabad.theatrical.api.Fixture;
import dev.imabad.theatrical.blockentities.light.BaseDMXConsumerLightBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;

public class MovingJetBlockEntity extends BaseDMXConsumerLightBlockEntity  {

    public double smoothedHeight = 0.0;
    private float jetHeight = 10.0f;
    private int tickCounter = 0;

    // Constantes configurables
    private static final float MIN_JET_HEIGHT = 0.1f; // ming height
    private static final float MAX_JET_HEIGHT = 95.0f; // max height
    private static final float DEFAULT_JET_HEIGHT = 20.0f; //default value


    public MovingJetBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.MOVING_JET.get(), pos, state);
        setChannelCount(3);
        this.jetHeight = DEFAULT_JET_HEIGHT;
    }

    @Override
    public Fixture getFixture() {
        return Fixtures.MOVING_JET.get();
    }

    @Override
    public int getFocus() {
        return 255;
    }

    @Override
    public void consume(byte[] dmxValues) {
        int start = this.getChannelStart() > 0 ? this.getChannelStart() - 1 : 0;
        byte[] ourValues = Arrays.copyOfRange(dmxValues, start, start + this.getChannelCount());
        if (ourValues.length < 3) {
            return;
        }
        if (this.storePrev()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
        intensity = convertByteToInt(ourValues[0]);
        pan = (int) ((convertByteToInt(ourValues[1]) * 360) / 255f) - 180;
        tilt = (int) ((convertByteToInt(ourValues[2]) / 255F) * 180F - 90F);
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        setChanged();
    }

    @Override
    public int getDeviceTypeId() {
        return 0x02;
    }

    @Override
    public String getModelName() {
        return "Moving Jet";
    }

    @Override
    public ResourceLocation getFixtureId() {
        return Fixtures.MOVING_JET.getId();
    }

    @Override
    public int getActivePersonality() {
        return 0;
    }

    public float getJetHeight() {
        return jetHeight;
    }

    public void setJetHeight(float h) {
        jetHeight = Mth.clamp(h, MIN_JET_HEIGHT, MAX_JET_HEIGHT);
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putFloat("JetHeight", jetHeight);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("JetHeight")) {
            jetHeight = tag.getFloat("JetHeight");
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putFloat("JetHeight", jetHeight);
        return tag;
    }

    public int convertByteToInt(byte val) {
        return Byte.toUnsignedInt(val);
    }

    public void tick() {
        if (!level.isClientSide || Minecraft.getInstance().isPaused()) return;

        double x = worldPosition.getX() + 0.5;
        double y = worldPosition.getY();
        double z = worldPosition.getZ() + 0.5;

        double maxHeight = jetHeight; // AHORA USA EL VALOR CONFIGURADO
        double targetHeight = (intensity / 255.0) * maxHeight;

        double normalizedIntensity = intensity / 255.0;

        smoothedHeight += (targetHeight - smoothedHeight) * 0.1;

        if (tickCounter % 8 == 0) {
            level.addAlwaysVisibleParticle(
                    ModParticle.WATERMOVINGJETPARTICLE.get(),
                    true,
                    x, y + smoothedHeight, z,
                    0,
                    intensity / 255.0,
                    0
            );
        }

        tickCounter++;
    }

    @Override
    public String getTranslationKey() {
        return "block.theatricalextralights.water_jet35m";
    }
}