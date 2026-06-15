package com.github.dumann089.theatricalextralights.blockentities;

import com.github.dumann089.theatricalextralights.fixtures.Fixtures;
import dev.imabad.theatrical.api.Fixture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BlinderBlockEntity extends BlinderBaseBlockEntity {

    public BlinderBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.BLINDER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlinderBlockEntity be) {
        BlinderBaseBlockEntity.tick(level, pos, state, be);
    }

    @Override
    public Fixture getFixture() {
        return Fixtures.BLINDER.get();
    }

    @Override
    public int getDeviceTypeId() {
        return 0x02;
    }

    @Override
    public String getModelName() {
        return "Blinder";
    }

    @Override
    public ResourceLocation getFixtureId() {
        return Fixtures.BLINDER.getId();
    }

    @Override
    public String getTranslationKey() {
        return "block.theatricalextralights.blinder";
    }
}
