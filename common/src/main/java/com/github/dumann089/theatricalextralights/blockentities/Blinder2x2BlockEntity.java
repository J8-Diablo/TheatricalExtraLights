package com.github.dumann089.theatricalextralights.blockentities;

import com.github.dumann089.theatricalextralights.fixtures.Fixtures;
import dev.imabad.theatrical.api.Fixture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class Blinder2x2BlockEntity extends BlinderBaseBlockEntity {

    public Blinder2x2BlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.BLINDER2X2.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, Blinder2x2BlockEntity be) {
        BlinderBaseBlockEntity.tick(level, pos, state, be);
    }

    @Override
    public Fixture getFixture() {
        return Fixtures.BLINDER2X2.get();
    }

    @Override
    public int getDeviceTypeId() {
        return 0x02;
    }

    @Override
    public String getModelName() {
        return "Blinder 2x2";
    }

    @Override
    public ResourceLocation getFixtureId() {
        return Fixtures.BLINDER2X2.getId();
    }

    @Override
    public String getTranslationKey() {
        return "block.theatricalextralights.blinder2x2";
    }
}
