package com.github.dumann089.theatricalextralights.blockentities;

import com.github.dumann089.theatricalextralights.blockentities.interfaces.HasPersonality;
import com.github.dumann089.theatricalextralights.blocks.StrobeBlock;
import com.github.dumann089.theatricalextralights.fixtures.Fixtures;
import com.github.dumann089.theatricalextralights.util.DmxShutterStrobeHelper;
import dev.imabad.theatrical.api.Fixture;
import dev.imabad.theatrical.api.dmx.DMXPersonality;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.List;

public class StrobeBlockEntity extends ExtraLightsLightBlockEntity implements HasPersonality {
    private static final int LEGACY_4CH_MODE = 0;
    private static final int FOCUS_5CH_MODE = 1;
    private static final int FOCUS_STROBE_6CH_MODE = 2;
    private static final float MIN_LIGHT_SPREAD = 1.0f;
    private static final float FOCUS_SPREAD_MULTIPLIER = 30.0f;
    private static final float CLOSE_EMISSION_DISTANCE = 3.0f;
    private static final float FAR_EMISSION_DISTANCE = 10.0f;

    private int activePersonalityIndex = FOCUS_5CH_MODE;
    /** Canal strobe DMX (personnalité 6 canaux). */
    private int strobe = 255;
    private int prevStrobe = 255;

    public StrobeBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.STROBE.get(), pos, state);
        setChannelCount(getPersonalityChannelCount());
        focus = 1;
    }

    @Override
    public Fixture getFixture() {
        return Fixtures.STROBE.get();
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
        if (activePersonalityIndex == LEGACY_4CH_MODE) {
            focus = 255;
        } else {
            focus = Math.max(1, focus);
        }
        if (activePersonalityIndex == FOCUS_STROBE_6CH_MODE) {
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
            return 4;
        }
        return personalities.get(activePersonalityIndex).getChannelCount();
    }

    private boolean usesStrobeChannel() {
        return activePersonalityIndex == FOCUS_STROBE_6CH_MODE;
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
        if (activePersonalityIndex == LEGACY_4CH_MODE) {
            return 255;
        }
        return Math.max(1, focus);
    }

    @Override
    public float getLightSpread() {
        if (activePersonalityIndex == LEGACY_4CH_MODE) {
            return 50.0f;
        }
        float normalizedFocus = getNormalizedFocus();
        float maxLightSpread = (float) (getFixture().getLightRadius() * FOCUS_SPREAD_MULTIPLIER);
        return Mth.lerp(
                normalizedFocus,
                MIN_LIGHT_SPREAD,
                maxLightSpread
        );
    }

    @Override
    public float getMaxLightDistance() {
        if (activePersonalityIndex == LEGACY_4CH_MODE) {
            return 50.0f;
        }

        return Mth.lerp(
                getNormalizedFocus(),
                CLOSE_EMISSION_DISTANCE,
                FAR_EMISSION_DISTANCE
        );
    }

    private float getNormalizedFocus() {
        return (Math.max(1, getFocus()) - 1) / 254.0f;
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
        int _pi = intensity, _pr = red, _pg = green, _pb = blue, _pf = focus, _ps = strobe;

        intensity = convertByteToInt(ourValues[0]);
        red = convertByteToInt(ourValues[1]);
        green = convertByteToInt(ourValues[2]);
        blue = convertByteToInt(ourValues[3]);
        if (channelCount >= 5) {
            focus = Math.max(1, convertByteToInt(ourValues[4]));
        } else {
            focus = 128;
        }
        if (channelCount >= 6) {
            strobe = convertByteToInt(ourValues[5]);
        } else if (usesStrobeChannel()) {
            strobe = 255;
        }

        boolean changed = intensity != _pi || red != _pr || green != _pg || blue != _pb
                || focus != _pf || strobe != _ps;
        finishDmxUpdate(changed, prevAdvanced);
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

    public static void tick(Level level, BlockPos pos, BlockState state, StrobeBlockEntity be) {
        ExtraLightsLightBlockEntity.tick(level, pos, state, be);
    }

    @Override
    public int getDeviceTypeId() {
        return 0x02;
    }

    @Override
    public String getModelName() {
        return "Strobe";
    }

    @Override
    public ResourceLocation getFixtureId() {
        return Fixtures.STROBE.getId();
    }

    @Override
    public boolean isUpsideDown() {
        return getBlockState().getValue(StrobeBlock.HANGING) && getBlockState().getValue(StrobeBlock.HANG_DIRECTION) == Direction.UP;
    }

    public int convertByteToInt(byte val) {
        return Byte.toUnsignedInt(val);
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
        } else {
            setActivePersonality(FOCUS_5CH_MODE);
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
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public String getTranslationKey() {
        return "block.theatricalextralights.strobe";
    }
}
