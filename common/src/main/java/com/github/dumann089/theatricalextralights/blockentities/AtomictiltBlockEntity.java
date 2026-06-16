package com.github.dumann089.theatricalextralights.blockentities;

import com.github.dumann089.theatricalextralights.blockentities.interfaces.HasPersonality;
import com.github.dumann089.theatricalextralights.blocks.AtomictiltBlock;
import com.github.dumann089.theatricalextralights.fixtures.Fixtures;
import com.github.dumann089.theatricalextralights.util.DmxShutterStrobeHelper;
import dev.imabad.theatrical.api.Fixture;
import dev.imabad.theatrical.api.dmx.DMXPersonality;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.List;

public class AtomictiltBlockEntity extends ExtraLightsLightBlockEntity implements HasPersonality {
    private static final int RGB_FOCUS_TILT_MODE = 0;
    private static final int RGB_FOCUS_STROBE_TILT_MODE = 1;

    private int activePersonalityIndex = RGB_FOCUS_TILT_MODE;
    private int strobe = 255;
    private int prevStrobe = 255;

    public AtomictiltBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        setChannelCount(getPersonalityChannelCount());
    }

    public AtomictiltBlockEntity(BlockPos pos, BlockState state) {
        this(BlockEntities.ATOMICTILT.get(), pos, state);
    }

    @Override
    public Fixture getFixture() {
        return Fixtures.ATOMICTILT.get();
    }

    @Override
    public int getActivePersonality() {
        return activePersonalityIndex;
    }

    @Override
    public void setActivePersonality(int index) {
        List<DMXPersonality> personalities = getFixture().getDMXPersonalities();
        if (index < 0 || index >= personalities.size()) {
            return;
        }
        activePersonalityIndex = index;
        setChannelCount(getPersonalityChannelCount());
        if (activePersonalityIndex == RGB_FOCUS_STROBE_TILT_MODE) {
            strobe = 255;
            prevStrobe = 255;
        }
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    private int getPersonalityChannelCount() {
        List<DMXPersonality> personalities = getFixture().getDMXPersonalities();
        if (personalities == null || personalities.isEmpty()) {
            return 6;
        }
        return personalities.get(activePersonalityIndex).getChannelCount();
    }

    private boolean usesStrobeChannel() {
        return activePersonalityIndex == RGB_FOCUS_STROBE_TILT_MODE;
    }

    private long getGameTimeForStrobe() {
        return level != null ? level.getGameTime() : 0L;
    }

    @Override
    public float getIntensity() {
        if (usesStrobeChannel()) {
            return DmxShutterStrobeHelper.computeEffectiveIntensity(intensity, strobe, getGameTimeForStrobe());
        }
        return intensity;
    }

    @Override
    public int getPrevIntensity() {
        if (usesStrobeChannel()) {
            return (int) DmxShutterStrobeHelper.computeEffectiveIntensity(
                    prevIntensity,
                    prevStrobe,
                    Math.max(0L, getGameTimeForStrobe() - 1)
            );
        }
        return prevIntensity;
    }

    @Override
    public int getFocus() {
        return 255;
    }

    @Override
    public void consume(byte[] dmxValues) {
        int channelCount = getPersonalityChannelCount();
        int start = this.getChannelStart() > 0 ? this.getChannelStart() - 1 : 0;
        byte[] ourValues = Arrays.copyOfRange(dmxValues, start, start + channelCount);
        if (ourValues.length < channelCount) {
            return;
        }
        boolean prevAdvanced = beginDmxUpdate();
        int _pi = intensity, _pr = red, _pg = green, _pb = blue, _pf = focus, _pp = pan, _pt = tilt, _ps = strobe;

        intensity = convertByteToInt(ourValues[0]);
        red = convertByteToInt(ourValues[1]);
        green = convertByteToInt(ourValues[2]);
        blue = convertByteToInt(ourValues[3]);
        focus = convertByteToInt(ourValues[4]);

        if (usesStrobeChannel()) {
            strobe = convertByteToInt(ourValues[5]);
            tilt = mapTiltDmx(convertByteToInt(ourValues[6]));
        } else {
            tilt = mapTiltDmx(convertByteToInt(ourValues[5]));
        }

        boolean changed = intensity != _pi || red != _pr || green != _pg || blue != _pb
                || focus != _pf || pan != _pp || tilt != _pt || strobe != _ps;
        finishDmxUpdate(changed, prevAdvanced);
    }

    private static int mapTiltDmx(int dmx) {
        return (int) ((dmx * 270) / 255F) - 225;
    }

    @Override
    public void lightTick() {
        super.lightTick();
        if (level != null && level.isClientSide && usesStrobeChannel()) {
            prevStrobe = strobe;
            if (DmxShutterStrobeHelper.isStrobing(strobe)) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            }
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AtomictiltBlockEntity be) {
        ExtraLightsLightBlockEntity.tick(level, pos, state, be);
    }

    @Override
    public int getDeviceTypeId() {
        return 0x01;
    }

    @Override
    public String getModelName() {
        return "Atomic Tilt";
    }

    @Override
    public ResourceLocation getFixtureId() {
        return Fixtures.ATOMICTILT.getId();
    }

    public int convertByteToInt(byte val) {
        return Byte.toUnsignedInt(val);
    }

    @Override
    public boolean isUpsideDown() {
        return getBlockState().getValue(AtomictiltBlock.HANGING) && getBlockState().getValue(AtomictiltBlock.HANG_DIRECTION) == Direction.UP;
    }

    @Override
    public int getBasePan() {
        return 0;
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("activePersonality", activePersonalityIndex);
        tag.putInt("strobe", strobe);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("activePersonality")) {
            setActivePersonality(tag.getInt("activePersonality"));
        }
        if (tag.contains("strobe")) {
            strobe = tag.getInt("strobe");
            prevStrobe = strobe;
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("activePersonality", activePersonalityIndex);
        tag.putInt("strobe", strobe);
        return tag;
    }

    @Override
    public String getTranslationKey() {
        return "block.theatricalextralights.atomictilt";
    }
}
