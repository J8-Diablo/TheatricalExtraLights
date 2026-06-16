package com.github.dumann089.theatricalextralights.blockentities;

import com.github.dumann089.theatricalextralights.fixtures.Fixtures;
import dev.imabad.theatrical.api.Fixture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;

public class WaterJetBlockEntity extends ExtraLightsLightBlockEntity {

    public double smoothedHeight = 0.0;

    public WaterJetBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.WATER_JET.get(), pos, state);
        setChannelCount(1);
    }

    @Override
    public Fixture getFixture() {
        return Fixtures.WATER_JET.get();
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
                boolean prevAdvanced = beginDmxUpdate();
        int _pi = intensity, _pr = red, _pg = green, _pb = blue, _pf = focus, _pp = pan, _pt = tilt;
        intensity = convertByteToInt(ourValues[0]);
        finishDmxUpdate(intensity != _pi || red != _pr || green != _pg || blue != _pb || focus != _pf || pan != _pp || tilt != _pt, prevAdvanced);
    }

    @Override
    public int getDeviceTypeId() {
        return 0x02;
    }

    @Override
    public String getModelName() {
        return "Water Jet";
    }

    @Override
    public ResourceLocation getFixtureId() {
        return Fixtures.WATER_JET.getId();
    }

    @Override
    public int getActivePersonality() {
        return 0;
    }


    private final com.github.dumann089.theatricalextralights.client.WaterJetClientEffects.TickCounter particleTick =
            new com.github.dumann089.theatricalextralights.client.WaterJetClientEffects.TickCounter();

    public static void tick(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos,
                            net.minecraft.world.level.block.state.BlockState state, WaterJetBlockEntity blockEntity) {
        if (level.isClientSide()) {
            com.github.dumann089.theatricalextralights.client.WaterJetClientEffects.tickJet(
                    blockEntity,
                    com.github.dumann089.theatricalextralights.client.WaterJetClientEffects.WATER_JET,
                    blockEntity.particleTick
            );
        }
    }

    public int convertByteToInt(byte val) {
        return Byte.toUnsignedInt(val);
    }

    @Override
    public String getTranslationKey() {
        return "block.theatricalextralights.water_jet";
    }
}
