package com.github.dumann089.theatricalextralights.blockentities;

import com.github.dumann089.theatricalextralights.client.Flow2JetClientEffects;
import com.github.dumann089.theatricalextralights.fixtures.Fixtures;
import dev.imabad.theatrical.api.Fixture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import com.github.dumann089.theatricalextralights.util.FixtureJetDirection;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;

public class Flow2JetBlockEntity extends ExtraLightsLightBlockEntity {

    public Flow2JetBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.FLOW2JET.get(), pos, state);
        setChannelCount(1);
        intensity = 0;
        pan = 0;
        tilt = 90;
        prevPan = pan;
        prevTilt = tilt;
    }

    public static <T extends net.minecraft.world.level.block.entity.BlockEntity> void tick(
            Level level,
            BlockPos pos,
            BlockState state,
            T blockEntity
    ) {
        if (blockEntity instanceof Flow2JetBlockEntity flow2Jet && level.isClientSide) {
            flow2Jet.tickClient();
        }
    }

    public float getInterpolatedPan(float partialTick) {
        return FixtureJetDirection.interpolateAngle(prevPan, pan, partialTick);
    }

    public float getInterpolatedTilt(float partialTick) {
        return FixtureJetDirection.interpolateAngle(prevTilt, tilt, partialTick);
    }

    private void tickClient() {
        lightTick();
        Flow2JetClientEffects.tick(this);
    }

    @Override
    public void consume(byte[] dmxValues) {
        int start = getChannelStart() > 0 ? getChannelStart() - 1 : 0;
        byte[] ourValues = Arrays.copyOfRange(dmxValues, start, start + getChannelCount());
        if (ourValues.length < 1) {
            return;
        }

        boolean prevAdvanced = beginDmxUpdate();
        int newIntensity = Byte.toUnsignedInt(ourValues[0]);
        boolean valuesChanged = intensity != newIntensity;

        intensity = newIntensity;
        focus = 255;

        finishDmxUpdate(valuesChanged, prevAdvanced);
    }

    @Override
    public Fixture getFixture() {
        return Fixtures.FLOW2JET.get();
    }

    @Override
    public int getFocus() {
        return 255;
    }

    @Override
    public int getDeviceTypeId() {
        return 0x06;
    }

    @Override
    public String getModelName() {
        return "Flow2Jet";
    }

    @Override
    public ResourceLocation getFixtureId() {
        return Fixtures.FLOW2JET.getId();
    }

    @Override
    public int getActivePersonality() {
        return 0;
    }

    @Override
    public String getTranslationKey() {
        return getBlockState().getBlock().getDescriptionId();
    }

    @Override
    protected boolean needsContinuousClientRender() {
        return intensity > 0;
    }

    @Override
    public void setRemoved() {
        if (level != null && level.isClientSide) {
            Flow2JetClientEffects.stop(worldPosition);
        }
        super.setRemoved();
    }
}
