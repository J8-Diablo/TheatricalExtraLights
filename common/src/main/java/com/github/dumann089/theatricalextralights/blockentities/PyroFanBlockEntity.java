package com.github.dumann089.theatricalextralights.blockentities;

import com.github.dumann089.theatricalextralights.entities.FireworkRocketEntity;
import com.github.dumann089.theatricalextralights.fixtures.Fixtures;
import com.github.dumann089.theatricalextralights.firework.FireworkLaunchMath;
import com.github.dumann089.theatricalextralights.firework.FireworkPreset;
import com.github.dumann089.theatricalextralights.firework.FireworkRocketTracker;
import dev.imabad.theatrical.api.Fixture;
import dev.imabad.theatrical.blocks.light.BaseLightBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;

public class PyroFanBlockEntity extends ExtraLightsLightBlockEntity {
    public static final int TUBE_COUNT = 10;
    private static final float FAN_SPREAD_DEGREES = 165.0f;
    private static final float LAUNCH_PITCH_DEGREES = 52.0f;
    private static final float MIN_SHOTS_PER_SECOND = 0.5f;
    private static final float MAX_SHOTS_PER_SECOND = 6.0f;
    private static final double TUBE_LENGTH = 10.0 / 16.0;
    private static final double TUBE_BASE_HEIGHT = 4.0 / 16.0;

    private final int[] tubeIntensity = new int[TUBE_COUNT];
    private final int[] prevTubeIntensity = new int[TUBE_COUNT];
    private final float[] fireAccumulator = new float[TUBE_COUNT];
    private final boolean[] pendingOneShot = new boolean[TUBE_COUNT];

    public PyroFanBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.PYRO_FAN.get(), pos, state);
        setChannelCount(TUBE_COUNT);
    }

    public static void tick(net.minecraft.world.level.Level level, BlockPos pos, BlockState state, PyroFanBlockEntity blockEntity) {
        blockEntity.tickServer();
    }

    private void tickServer() {
        if (!(level instanceof ServerLevel serverLevel) || level.isClientSide) {
            return;
        }

        boolean anyActive = false;
        for (int i = 0; i < TUBE_COUNT; i++) {
            if (tubeIntensity[i] > 0) {
                anyActive = true;
                break;
            }
        }
        if (!anyActive) {
            Arrays.fill(fireAccumulator, 0.0f);
            Arrays.fill(pendingOneShot, false);
            return;
        }

        for (int tube = 0; tube < TUBE_COUNT; tube++) {
            int intensity = tubeIntensity[tube];
            if (intensity <= 0) {
                fireAccumulator[tube] = 0.0f;
                pendingOneShot[tube] = false;
                continue;
            }

            if (pendingOneShot[tube]) {
                launchTube(serverLevel, tube);
                pendingOneShot[tube] = false;
            }

            if (intensity < 2) {
                fireAccumulator[tube] = 0.0f;
                continue;
            }

            fireAccumulator[tube] += shotsPerSecond(intensity) / 20.0f;
            while (fireAccumulator[tube] >= 1.0f) {
                fireAccumulator[tube] -= 1.0f;
                launchTube(serverLevel, tube);
            }
        }
    }

    private void launchTube(ServerLevel serverLevel, int tubeIndex) {
        if (!FireworkRocketTracker.tryRegisterLaunch(serverLevel)) {
            return;
        }

        Vec3 spawn = getTubeLaunchPosition(tubeIndex);
        Vec3 velocity = getTubeLaunchVelocity(tubeIndex, serverLevel.random);
        FireworkRocketEntity rocket = new FireworkRocketEntity(serverLevel, FireworkPreset.GOLD_COMET, worldPosition);
        rocket.moveTo(spawn.x, spawn.y, spawn.z, 0.0f, 0.0f);
        rocket.setDeltaMovement(velocity);
        serverLevel.addFreshEntity(rocket);
        serverLevel.playSound(
                null,
                spawn.x,
                spawn.y,
                spawn.z,
                SoundEvents.FIREWORK_ROCKET_LAUNCH,
                SoundSource.BLOCKS,
                0.75f,
                0.85f + serverLevel.random.nextFloat() * 0.25f
        );
    }

    private float tubeYawOffset(int tubeIndex) {
        if (TUBE_COUNT <= 1) {
            return 0.0f;
        }
        float step = FAN_SPREAD_DEGREES / (TUBE_COUNT - 1);
        return (-FAN_SPREAD_DEGREES * 0.5f) + tubeIndex * step;
    }

    private Direction getLaunchFacing() {
        return getBlockState().getValue(BaseLightBlock.FACING).getClockWise();
    }

    private Vec3 getTubeLaunchPosition(int tubeIndex) {
        Direction facing = getLaunchFacing();
        float yawOffset = tubeYawOffset(tubeIndex);
        float yawRad = (facing.toYRot() + yawOffset) * Mth.DEG_TO_RAD;
        float pitchRad = LAUNCH_PITCH_DEGREES * Mth.DEG_TO_RAD;

        double forward = Math.cos(pitchRad) * TUBE_LENGTH;
        double up = Math.sin(pitchRad) * TUBE_LENGTH;
        double offsetX = -Mth.sin(yawRad) * forward;
        double offsetZ = Mth.cos(yawRad) * forward;

        return new Vec3(
                worldPosition.getX() + 0.5 + offsetX,
                worldPosition.getY() + TUBE_BASE_HEIGHT + up,
                worldPosition.getZ() + 0.5 + offsetZ
        );
    }

    private Vec3 getTubeLaunchVelocity(int tubeIndex, net.minecraft.util.RandomSource random) {
        return FireworkLaunchMath.computeVelocity(
                getLaunchFacing(),
                LAUNCH_PITCH_DEGREES,
                tubeYawOffset(tubeIndex),
                1.0f,
                FireworkPreset.GOLD_COMET.getPattern().getLaunchSpeedMultiplier(),
                random
        );
    }

    private static float shotsPerSecond(int intensity) {
        int clamped = Mth.clamp(intensity, 2, 255);
        return Mth.lerp((clamped - 2) / 253.0f, MIN_SHOTS_PER_SECOND, MAX_SHOTS_PER_SECOND);
    }

    @Override
    public Fixture getFixture() {
        return Fixtures.PYRO_FAN.get();
    }

    @Override
    public int getFocus() {
        return 255;
    }

    @Override
    public void consume(byte[] dmxValues) {
        int start = getChannelStart() > 0 ? getChannelStart() - 1 : 0;
        byte[] ourValues = Arrays.copyOfRange(dmxValues, start, start + getChannelCount());
        if (ourValues.length < TUBE_COUNT) {
            return;
        }

        if (storePrev() && level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }

        for (int i = 0; i < TUBE_COUNT; i++) {
            int newIntensity = Byte.toUnsignedInt(ourValues[i]);
            if (prevTubeIntensity[i] == 0 && newIntensity > 0) {
                pendingOneShot[i] = true;
            }
            prevTubeIntensity[i] = newIntensity;
            tubeIntensity[i] = newIntensity;
        }

        intensity = 0;
        for (int value : tubeIntensity) {
            intensity = Math.max(intensity, value);
        }
        pan = 0;
        tilt = 0;
        focus = 255;
        red = (FireworkPreset.GOLD_COMET.getLaunchColor() >> 16) & 0xFF;
        green = (FireworkPreset.GOLD_COMET.getLaunchColor() >> 8) & 0xFF;
        blue = FireworkPreset.GOLD_COMET.getLaunchColor() & 0xFF;

        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
        setChanged();
    }

    @Override
    public int getDeviceTypeId() {
        return 0x03;
    }

    @Override
    public String getModelName() {
        return "Pyro Fan";
    }

    @Override
    public ResourceLocation getFixtureId() {
        return Fixtures.PYRO_FAN.getId();
    }

    @Override
    public int getActivePersonality() {
        return 0;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putIntArray("TubeIntensity", tubeIntensity);
        tag.putIntArray("PrevTubeIntensity", prevTubeIntensity);
        tag.putIntArray("FireAccumulator", encodeFloatArray(fireAccumulator));
        tag.putByteArray("PendingOneShot", toPendingBytes());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        setChannelCount(TUBE_COUNT);
        if (tag.contains("TubeIntensity")) {
            int[] loaded = tag.getIntArray("TubeIntensity");
            System.arraycopy(loaded, 0, tubeIntensity, 0, Math.min(loaded.length, TUBE_COUNT));
        }
        if (tag.contains("PrevTubeIntensity")) {
            int[] loaded = tag.getIntArray("PrevTubeIntensity");
            System.arraycopy(loaded, 0, prevTubeIntensity, 0, Math.min(loaded.length, TUBE_COUNT));
        }
        if (tag.contains("FireAccumulator")) {
            float[] loaded = decodeFloatArray(tag.getIntArray("FireAccumulator"));
            for (int i = 0; i < Math.min(loaded.length, TUBE_COUNT); i++) {
                fireAccumulator[i] = loaded[i];
            }
        }
        if (tag.contains("PendingOneShot")) {
            byte[] loaded = tag.getByteArray("PendingOneShot");
            for (int i = 0; i < Math.min(loaded.length, TUBE_COUNT); i++) {
                pendingOneShot[i] = loaded[i] != 0;
            }
        }
    }

    private byte[] toPendingBytes() {
        byte[] pendingBytes = new byte[TUBE_COUNT];
        for (int i = 0; i < TUBE_COUNT; i++) {
            pendingBytes[i] = (byte) (pendingOneShot[i] ? 1 : 0);
        }
        return pendingBytes;
    }

    private static int[] encodeFloatArray(float[] values) {
        int[] encoded = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            encoded[i] = Float.floatToIntBits(values[i]);
        }
        return encoded;
    }

    private static float[] decodeFloatArray(int[] encoded) {
        float[] values = new float[encoded.length];
        for (int i = 0; i < encoded.length; i++) {
            values[i] = Float.intBitsToFloat(encoded[i]);
        }
        return values;
    }

    @Override
    public String getTranslationKey() {
        return "block.theatricalextralights.pyro_fan";
    }
}
