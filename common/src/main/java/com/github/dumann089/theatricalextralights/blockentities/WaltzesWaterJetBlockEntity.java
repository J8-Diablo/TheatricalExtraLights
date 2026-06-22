package com.github.dumann089.theatricalextralights.blockentities;

import com.github.dumann089.theatricalextralights.blockentities.interfaces.HasJetHeight;
import com.github.dumann089.theatricalextralights.blockentities.interfaces.HasJetThickness;
import com.github.dumann089.theatricalextralights.client.particle.JetVariant;
import com.github.dumann089.theatricalextralights.client.particle.WaterJetParticleOptions;
import com.github.dumann089.theatricalextralights.fixtures.Fixtures;
import dev.imabad.theatrical.api.Fixture;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;

public class WaltzesWaterJetBlockEntity extends ExtraLightsLightBlockEntity
        implements HasJetHeight, HasJetThickness {

    public double smoothedHeight = 0.0;

    private float jetHeight = 9.0f;
    private float jetThickness = 0.1f;

    private int tickCounter = 0;

    public static final float MIN_THICKNESS = 0.05f;
    public static final float MAX_THICKNESS = 9.5f;
    public static final float MIN_JET_HEIGHT = 0.1f;
    public static final float MAX_JET_HEIGHT = 99.0f;

    public float currentAngle = 0; // Ángulo para render y chorros
    private float swayTime = 0;

    public WaltzesWaterJetBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.WALTZES_WATER_JET.get(), pos, state);
        setChannelCount(1);
    }
    // -------------------
    // GETTERS / SETTERS
    // -------------------

    @Override
    public float getJetThickness() {
        return jetThickness;
    }

    @Override
    public void setJetThickness(float thickness) {
        this.jetThickness = Mth.clamp(thickness, MIN_THICKNESS, MAX_THICKNESS);
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public float getJetHeight() {
        return jetHeight;
    }

    @Override
    public void setJetHeight(float h) {
        this.jetHeight = Mth.clamp(h, MIN_JET_HEIGHT, MAX_JET_HEIGHT);
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    // -------------------
    // DMX
    // -------------------

    @Override
    public void consume(byte[] dmxValues) {
        int start = getChannelStart() > 0 ? getChannelStart() - 1 : 0;
        byte[] ourValues = Arrays.copyOfRange(dmxValues, start, start + getChannelCount());
        if (ourValues.length < 1) return;

        intensity = convertByteToInt(ourValues[0]);

        if (storePrev()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
        setChanged();
    }

    public int convertByteToInt(byte val) {
        return Byte.toUnsignedInt(val);
    }

    // -------------------
    // PARTICLE TICK
    // -------------------

    public void tick() {
        if (!level.isClientSide || Minecraft.getInstance().isPaused()) return;

        tickCounter++;

        // 1. Lógica de Movimiento (Vaivén)
        int panValue = getPan();
        int tiltValue = getTilt();
        float targetAngle;

        if (tiltValue > 0) { // Si hay valor en Tilt, modo Automático
            swayTime += (tiltValue / 255.0f) * 0.1f;
            targetAngle = (float) Math.sin(swayTime) * 45.0f; // Oscilación -45 a 45 grados
        } else { // Modo Manual
            targetAngle = (panValue / 255.0f) * 90.0f - 45.0f;
        }

        // 2. Smoothing
        currentAngle += (targetAngle - currentAngle) * 0.1f;

        // 3. Lógica de Altura
        double targetHeight = (intensity / 255.0) * getJetHeight();
        smoothedHeight += (targetHeight - smoothedHeight) * 0.1;

        // 4. Emisión de 9 chorros sincronizados
        if (tickCounter % 18 == 0) {
            float intensityNorm = intensity / 255.0f;
            double rad = Math.toRadians(currentAngle);

            // Offsets definidos en tu Renderer
            double[] offsets = {-0.9375, -0.625, -0.25, 0.125, 0.5, 0.875, 1.25, 1.625, 1.9375};

            for (double offset : offsets) {
                // Rotación aplicada a la posición y vector de velocidad
                double zOffset = offset * Math.sin(rad);
                double yOffset = offset * (1 - Math.cos(rad));

                level.addAlwaysVisibleParticle(
                        new WaterJetParticleOptions(intensityNorm, getJetThickness(), JetVariant.JET3),
                        true,
                        worldPosition.getX() + 0.5,
                        worldPosition.getY() + smoothedHeight + yOffset,
                        worldPosition.getZ() + 0.5 + zOffset,
                        0, 0.1, Math.sin(rad) * 0.05 // Velocidad Z para seguir el ángulo
                );
            }
        }
    }

    // -------------------
    // FIXTURE OVERRIDES
    // -------------------

    @Override
    public Fixture getFixture() {
        return Fixtures.WALTZES_WATER_JET.get();
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
        return "Vase Water Jet";
    }

    @Override
    public ResourceLocation getFixtureId() {
        return Fixtures.WALTZES_WATER_JET.getId();
    }

    @Override
    public int getActivePersonality() {
        return 0;
    }

    // -------------------
    // NBT
    // -------------------

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putFloat("JetHeight", jetHeight);
        tag.putFloat("JetThickness", jetThickness);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("JetHeight")) jetHeight = tag.getFloat("JetHeight");
        if (tag.contains("JetThickness")) jetThickness = tag.getFloat("JetThickness");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putFloat("JetHeight", jetHeight);
        tag.putFloat("JetThickness", jetThickness);
        return tag;
    }

    @Override
    public String getTranslationKey() {
        return "block.theatricalextralights.WaltzesWaterJet";
    }
}