package com.github.dumann089.theatricalextralights.blockentities;

import com.github.dumann089.theatricalextralights.blockentities.interfaces.HasJetConeAngle;
import com.github.dumann089.theatricalextralights.blockentities.interfaces.HasJetHeight;
import com.github.dumann089.theatricalextralights.blockentities.interfaces.HasJetThickness;
import com.github.dumann089.theatricalextralights.client.particle.JetVariant;
import com.github.dumann089.theatricalextralights.client.particle.WaterJetParticleOptions;
import com.github.dumann089.theatricalextralights.fixtures.Fixtures;
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

public class WaterJetConeBlockEntity extends ExtraLightsLightBlockEntity
        implements HasJetHeight, HasJetThickness, HasJetConeAngle {

    public static final float MIN_THICKNESS = 0.02f;
    public static final float MAX_THICKNESS = 1.0f;

    public static final float MIN_JET_HEIGHT = 0.1f;
    public static final float MAX_JET_HEIGHT = 99.0f;

    public static final float MIN_CONE_ANGLE = 1.0f;
    public static final float MAX_CONE_ANGLE = 180.0f;

    public static final float DEFAULT_CONE_ANGLE = 45.0f; // mushroom
    public static final float DEFAULT_THICKNESS = 0.4f;
    public static final float DEFAULT_HEIGHT = 5.0f;

    /* ================= ESTADO ================= */

    public double smoothedHeight = 0.0;
    private float jetHeight = DEFAULT_HEIGHT;
    private float jetThickness = DEFAULT_THICKNESS;
    private float jetConeAngle = DEFAULT_CONE_ANGLE;

    private int tickCounter = 0;

    /* ================= CONSTRUCTOR ================= */

    public WaterJetConeBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.WATER_JET_CONE.get(), pos, state);
        setChannelCount(1);
    }

    /* ================= GETTERS / SETTERS ================= */

    @Override
    public float getJetHeight() {
        return jetHeight;
    }

    @Override
    public void setJetHeight(float h) {
        this.jetHeight = Mth.clamp(h, MIN_JET_HEIGHT, MAX_JET_HEIGHT);
        markUpdated();
    }

    @Override
    public float getJetThickness() {
        return jetThickness;
    }

    @Override
    public void setJetThickness(float thickness) {
        this.jetThickness = Mth.clamp(thickness, MIN_THICKNESS, MAX_THICKNESS);
        markUpdated();
    }

    @Override
    public float getJetConeAngle() {
        return jetConeAngle;
    }

    @Override
    public void setJetConeAngle(float angle) {
        this.jetConeAngle = Mth.clamp(angle, MIN_CONE_ANGLE, MAX_CONE_ANGLE);
        setChanged();

        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(
                    worldPosition,
                    getBlockState(),
                    getBlockState(),
                    Block.UPDATE_CLIENTS
            );
        }
    }
    /* ================= HELPER ================= */

    private void markUpdated() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(
                    worldPosition,
                    getBlockState(),
                    getBlockState(),
                    Block.UPDATE_CLIENTS
            );
        }
    }

    /* ================= DMX ================= */

    @Override
    public void consume(byte[] dmxValues) {
        int start = Math.max(getChannelStart() - 1, 0);
        byte[] ourValues = Arrays.copyOfRange(dmxValues, start, start + getChannelCount());
        if (ourValues.length < 1) return;

        intensity = Byte.toUnsignedInt(ourValues[0]);
        markUpdated();
    }

    /* ================= PARTICLES ================= */


    /* ================= FIXTURE ================= */

    @Override
    public Fixture getFixture() {
        return Fixtures.WATER_JET_CONE.get();
    }

    @Override
    public int getFocus() {
        return 255;
    }

    @Override
    public int getDeviceTypeId() {
        return 0x02;
    }

    @Override
    public String getModelName() {
        return "Water Jet Cone";
    }

    @Override
    public ResourceLocation getFixtureId() {
        return Fixtures.WATER_JET_CONE.getId();
    }

    @Override
    public int getActivePersonality() {
        return 0;
    }

    /* ================= NBT ================= */

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putFloat("JetHeight", jetHeight);
        tag.putFloat("JetThickness", jetThickness);
        tag.putFloat("JetConeAngle", jetConeAngle);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        jetHeight = tag.contains("JetHeight")
                ? Mth.clamp(tag.getFloat("JetHeight"), MIN_JET_HEIGHT, MAX_JET_HEIGHT)
                : DEFAULT_HEIGHT;

        jetThickness = tag.contains("JetThickness")
                ? Mth.clamp(tag.getFloat("JetThickness"), MIN_THICKNESS, MAX_THICKNESS)
                : DEFAULT_THICKNESS;

        jetConeAngle = tag.contains("JetConeAngle")
                ? Mth.clamp(tag.getFloat("JetConeAngle"), MIN_CONE_ANGLE, MAX_CONE_ANGLE)
                : DEFAULT_CONE_ANGLE;
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putFloat("JetHeight", jetHeight);
        tag.putFloat("JetThickness", jetThickness);
        tag.putFloat("JetConeAngle", jetConeAngle);
        return tag;
    }

    @Override
    public String getTranslationKey() {
        return "block.theatricalextralights.water_jet_cone";
    }
}