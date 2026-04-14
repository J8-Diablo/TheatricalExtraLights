package com.github.dumann089.theatricalextralights.blockentities;

import com.github.dumann089.theatricalextralights.blockentities.interfaces.HasPersonality;
import dev.imabad.theatrical.api.dmx.DMXPersonality;
import dev.imabad.theatrical.blockentities.light.BaseDMXConsumerLightBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.List;

public abstract class BaseExtraLightsBlockEntity extends BaseDMXConsumerLightBlockEntity implements HasPersonality {

    private int activePersonalityIndex = 0;

    protected BaseExtraLightsBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        setChannelCount(getPersonalityChannelCount());
    }

    // ─── HasPersonality ───────────────────────────────────────────────────────

    @Override
    public int getActivePersonality() {
        return activePersonalityIndex;
    }

    @Override
    public void setActivePersonality(int index) {
        List<DMXPersonality> personalities = getFixture().getDMXPersonalities();
        if (index < 0 || index >= personalities.size()) return;
        activePersonalityIndex = index;
        setChannelCount(getPersonalityChannelCount());
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    protected int getPersonalityChannelCount() {
        List<DMXPersonality> p = getFixture().getDMXPersonalities();
        if (p == null || p.isEmpty()) return 7;
        return p.get(activePersonalityIndex).getChannelCount();
    }

    // ─── NBT ─────────────────────────────────────────────────────────────────

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("activePersonality", activePersonalityIndex);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("activePersonality")) {
            activePersonalityIndex = tag.getInt("activePersonality");
            setChannelCount(getPersonalityChannelCount());
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("activePersonality", activePersonalityIndex);
        return tag;
    }

    // ─── Consume DMX ─────────────────────────────────────────────────────────

    @Override
    public void consume(byte[] dmxValues) {
        int channelCount = getPersonalityChannelCount();
        int start = this.getChannelStart() > 0 ? this.getChannelStart() - 1 : 0;
        byte[] ourValues = Arrays.copyOfRange(dmxValues, start, start + channelCount);

        if (ourValues.length < 7) return;

        if (this.storePrev()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }

        // 7 channels  BASE
        intensity = convertByteToInt(ourValues[0]);
        red       = convertByteToInt(ourValues[1]);
        green     = convertByteToInt(ourValues[2]);
        blue      = convertByteToInt(ourValues[3]);
        focus     = convertByteToInt(ourValues[4]);
        pan       = (int) ((convertByteToInt(ourValues[5]) * 360) / 255f) - 180;
        tilt      = (int) ((convertByteToInt(ourValues[6]) * 270) / 255F) - 225;

        if (ourValues.length > 7) {
            consumeExtendedChannels(ourValues, channelCount);
        }

        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        setChanged();
    }

    /**
     *
     *
     *
     *
     *
     *
     * @param values
     * @param totalCount
     */
    protected void consumeExtendedChannels(byte[] values, int totalCount) {
    }

    public int convertByteToInt(byte val) {
        return Byte.toUnsignedInt(val);
    }
}