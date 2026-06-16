package com.github.dumann089.theatricalextralights.client;

import com.github.dumann089.theatricalextralights.blockentities.OrganPipesBlockEntity;
import com.github.dumann089.theatricalextralights.blockentities.OrganPipesInvBlockEntity;
import com.github.dumann089.theatricalextralights.blockentities.SpinnerBlockEntity;
import com.github.dumann089.theatricalextralights.blockentities.interfaces.HasJetConeAngle;
import com.github.dumann089.theatricalextralights.blockentities.interfaces.HasJetHeight;
import com.github.dumann089.theatricalextralights.blockentities.interfaces.HasJetThickness;
import com.github.dumann089.theatricalextralights.client.particle.JetVariant;
import com.github.dumann089.theatricalextralights.client.particle.WaterJetParticleOptions;
import com.github.dumann089.theatricalextralights.particle.ModParticle;
import dev.imabad.theatrical.blockentities.light.BaseLightBlockEntity;
import dev.imabad.theatrical.blocks.HangableBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

/**
 * Client-side water jet particles — throttled block-entity ticks instead of per-frame BER lazy renders.
 */
public final class WaterJetClientEffects {
    private static final double MAX_SPAWN_DISTANCE_SQ = 56.0 * 56.0;
    private static final double[] ORGAN_PIPE_X = {-0.9375, -0.625, -0.25, 0.125, 0.5, 0.875, 1.25, 1.625, 1.9375};
    private static final double[] ORGAN_PIPE_HEIGHT_MULT = {0.6, 0.7, 0.8, 0.9, 1.0, 0.9, 0.8, 0.7, 0.6};
    private static final double[][] SPINNER_NOZZLES = {
            {0.96, 2.0, 0.501},
            {0.5, 2.0, 0.968},
            {0.5, 2.0, 0.034},
            {0.034, 2.0, 0.501}
    };

    public record JetPreset(
            int interval,
            float yOffset,
            double speedMult,
            double fixedMaxHeight,
            JetVariant variant,
            boolean legacyParticle,
            float pitchOffsetDeg,
            boolean fixedUp,
            boolean movingJetPitch,
            double smoothFactor
    ) {
    }

    public static final JetPreset WATER_JET = new JetPreset(8, 1.81F, 0.05, 37.0, null, true, 0.0F, false, false, 0.1);
    public static final JetPreset BIG = new JetPreset(6, 2.03F, 0.09, 0.0, JetVariant.JET1, false, 0.0F, false, false, 0.1);
    public static final JetPreset THIN = new JetPreset(8, 2.03F, 0.09, 0.0, JetVariant.JET3, false, 0.0F, false, false, 0.1);
    public static final JetPreset SPREAD = new JetPreset(8, 2.03F, 0.09, 0.0, JetVariant.JET2, false, 0.0F, false, false, 0.1);
    public static final JetPreset FOG = new JetPreset(4, 2.03F, 0.09, 0.0, JetVariant.JETFog, false, 0.0F, false, false, 0.1);
    public static final JetPreset CONE = new JetPreset(4, 2.03F, 0.09, 0.0, JetVariant.JETCone, false, 0.0F, false, false, 0.1);
    public static final JetPreset CENTRAL = new JetPreset(7, 2.03F, 0.09, 0.0, JetVariant.JET4, false, 0.0F, false, false, 0.1);
    public static final JetPreset BLOOM = new JetPreset(4, 2.03F, 0.09, 0.0, JetVariant.JETBloom, false, 0.0F, true, false, 0.1);
    public static final JetPreset MOVING = new JetPreset(8, 1.81F, 0.09, 0.0, JetVariant.JET3, false, 0.0F, false, true, 0.1);

    private WaterJetClientEffects() {
    }

    public static void tickJet(BaseLightBlockEntity blockEntity, JetPreset preset, TickCounter counter) {
        if (!(blockEntity.getLevel() instanceof ClientLevel level) || Minecraft.getInstance().isPaused()) {
            return;
        }
        if (blockEntity.getIntensity() <= 0) {
            return;
        }
        if (!counter.advanceAndShouldSpawn(preset.interval())) {
            return;
        }
        if (!isNearPlayer(level, blockEntity.getBlockPos().getX() + 0.5, blockEntity.getBlockPos().getY() + preset.yOffset(), blockEntity.getBlockPos().getZ() + 0.5)) {
            return;
        }

        double maxHeight = preset.fixedMaxHeight() > 0.0
                ? preset.fixedMaxHeight()
                : ((HasJetHeight) blockEntity).getJetHeight();
        double targetHeight = (blockEntity.getIntensity() / 255.0) * maxHeight;
        double smoothed = readSmoothedHeight(blockEntity);
        smoothed += (targetHeight - smoothed) * preset.smoothFactor();
        writeSmoothedHeight(blockEntity, smoothed);
        double speed = smoothed * preset.speedMult();

        double x = blockEntity.getBlockPos().getX() + 0.5;
        double y = blockEntity.getBlockPos().getY() + preset.yOffset();
        double z = blockEntity.getBlockPos().getZ() + 0.5;

        Vector3f direction;
        if (preset.fixedUp()) {
            direction = new Vector3f(0.0F, 1.0F, 0.0F);
        } else {
            direction = computeDirection(
                    blockEntity.getPan(),
                    blockEntity.getTilt(),
                    blockEntity.getBlockState().getValue(HangableBlock.FACING),
                    preset.pitchOffsetDeg(),
                    preset.movingJetPitch()
            );
        }

        if (preset.legacyParticle()) {
            emit(level, ModParticle.WATERJETPARTICLE.get(), x, y, z,
                    direction.x * (float) speed, direction.y * (float) speed, direction.z * (float) speed);
            return;
        }

        float intensity = blockEntity.getIntensity() / 255.0F;
        float thickness = blockEntity instanceof HasJetThickness jetThickness ? jetThickness.getJetThickness() : 0.12F;
        float coneAngle = blockEntity instanceof HasJetConeAngle jetCone ? jetCone.getJetConeAngle() : 45.0F;
        WaterJetParticleOptions options = new WaterJetParticleOptions(intensity, thickness, coneAngle, preset.variant());
        emit(level, options, x, y, z,
                direction.x * speed, direction.y * speed, direction.z * speed);
    }

    public static void tickOrganPipes(OrganPipesBlockEntity blockEntity, JetVariant variant, TickCounter counter) {
        tickOrganPipesInternal(blockEntity, variant, counter, 12);
    }

    public static void tickOrganPipesInv(OrganPipesInvBlockEntity blockEntity, JetVariant variant, TickCounter counter) {
        tickOrganPipesInternal(blockEntity, variant, counter, 12);
    }

    private static void tickOrganPipesInternal(BaseLightBlockEntity blockEntity, JetVariant variant, TickCounter counter, int interval) {
        if (!(blockEntity.getLevel() instanceof ClientLevel level) || Minecraft.getInstance().isPaused()) {
            return;
        }
        if (blockEntity.getIntensity() <= 0 || !counter.advanceAndShouldSpawn(interval)) {
            return;
        }
        if (!(blockEntity instanceof HasJetHeight hasJetHeight) || !(blockEntity instanceof HasJetThickness hasJetThickness)) {
            return;
        }

        Direction facing = blockEntity.getBlockState().getValue(HangableBlock.FACING);
        double maxHeight = hasJetHeight.getJetHeight();
        double targetHeight = (blockEntity.getIntensity() / 255.0) * maxHeight;
        double smoothed = getOrganSmoothed(blockEntity);
        smoothed += (targetHeight - smoothed) * 0.1;
        setOrganSmoothed(blockEntity, smoothed);

        double baseSpeed = smoothed * 0.09;
        float intensity = blockEntity.getIntensity() / 255.0F;
        float thickness = hasJetThickness.getJetThickness();
        Vector3f baseDir = computeDirection(blockEntity.getPan(), blockEntity.getTilt(), facing, 0.0F, false);

        double blockCenterX = blockEntity.getBlockPos().getX() + 0.5;
        double blockCenterZ = blockEntity.getBlockPos().getZ() + 0.5;
        double baseY = blockEntity.getBlockPos().getY() + 2.0;
        double baseZOffset = 0.51;

        double yaw = Math.toRadians(blockEntity.getPan());
        double cosYaw = Math.cos(yaw);
        double sinYaw = Math.sin(yaw);

        for (int i = 0; i < ORGAN_PIPE_X.length; i++) {
            double relativeX = ORGAN_PIPE_X[i];
            double relativeZ = baseZOffset - 0.5;
            double rotatedX = relativeX * cosYaw - relativeZ * sinYaw;
            double rotatedZ = relativeX * sinYaw + relativeZ * cosYaw;
            org.joml.Vector2f facingRot = rotateXZ(rotatedX, rotatedZ, facing);

            double particleX = blockCenterX + facingRot.x;
            double particleZ = blockCenterZ + facingRot.y;
            if (!isNearPlayer(level, particleX, baseY, particleZ)) {
                continue;
            }

            double particleSpeed = baseSpeed * ORGAN_PIPE_HEIGHT_MULT[i];
            WaterJetParticleOptions options = new WaterJetParticleOptions(
                    intensity * (float) ORGAN_PIPE_HEIGHT_MULT[i],
                    thickness,
                    variant
            );
            emit(level, options, particleX, baseY, particleZ,
                    baseDir.x * particleSpeed, baseDir.y * particleSpeed, baseDir.z * particleSpeed);
        }
    }

    public static void tickSpinner(SpinnerBlockEntity blockEntity, TickCounter counter) {
        if (!(blockEntity.getLevel() instanceof ClientLevel level) || Minecraft.getInstance().isPaused()) {
            return;
        }

        float intensityNorm = blockEntity.getIntensity() / 255.0F;
        double targetHeight = intensityNorm * blockEntity.getJetHeight();
        blockEntity.smoothedHeight += (targetHeight - blockEntity.smoothedHeight) * 0.15;
        blockEntity.updateSpin();

        if (blockEntity.getIntensity() <= 0 || !counter.advanceAndShouldSpawn(4)) {
            return;
        }
        if (!isNearPlayer(level, blockEntity.getBlockPos().getX() + 0.5, blockEntity.getBlockPos().getY() + 2.0, blockEntity.getBlockPos().getZ() + 0.5)) {
            return;
        }

        double speed = blockEntity.smoothedHeight * 0.10;
        float intensity = intensityNorm;
        float thickness = blockEntity.getJetThickness();
        WaterJetParticleOptions options = new WaterJetParticleOptions(intensity, thickness, JetVariant.JET3);

        double baseX = blockEntity.getBlockPos().getX();
        double baseY = blockEntity.getBlockPos().getY();
        double baseZ = blockEntity.getBlockPos().getZ();
        float angle = blockEntity.getSpinAngle() % 360.0F;
        double rad = Math.toRadians(angle);
        double nozzleRad = Math.toRadians(blockEntity.getNozzleAngle());

        for (double[] nozzle : SPINNER_NOZZLES) {
            double offsetX = nozzle[0] - 0.5;
            double offsetZ = nozzle[2] - 0.5;
            double rotatedX = offsetX * Math.cos(rad) - offsetZ * Math.sin(rad);
            double rotatedZ = offsetX * Math.sin(rad) + offsetZ * Math.cos(rad);

            double worldX = baseX + 0.5 + rotatedX;
            double worldY = baseY + nozzle[1];
            double worldZ = baseZ + 0.5 + rotatedZ;

            double dirX = rotatedX;
            double dirZ = rotatedZ;
            double dirLength = Math.sqrt(dirX * dirX + dirZ * dirZ);
            if (dirLength > 0.001) {
                dirX /= dirLength;
                dirZ /= dirLength;
            } else {
                dirX = 0.0;
                dirZ = 0.0;
            }

            double velY = speed * Math.cos(nozzleRad);
            double horizontalSpeed = speed * Math.sin(nozzleRad);
            emit(level, options, worldX, worldY, worldZ, dirX * horizontalSpeed, velY, dirZ * horizontalSpeed);
        }
    }

    private static Vector3f computeDirection(float pan, float tilt, Direction facing, float pitchOffsetDeg, boolean movingJetPitch) {
        double yaw = Math.toRadians(pan);
        double pitch = Math.toRadians(tilt + pitchOffsetDeg + (movingJetPitch ? 90.0 : 0.0));

        double dirX = -Math.sin(yaw) * Math.cos(pitch);
        double dirY = Math.sin(pitch);
        double dirZ = Math.cos(yaw) * Math.cos(pitch);

        switch (facing) {
            case NORTH -> {
                dirX = -dirX;
                dirZ = -dirZ;
            }
            case EAST -> {
                double tmp = dirX;
                dirX = dirZ;
                dirZ = -tmp;
            }
            case WEST -> {
                double tmp = dirX;
                dirX = -dirZ;
                dirZ = tmp;
            }
            default -> {
            }
        }
        return new Vector3f((float) dirX, (float) dirY, (float) dirZ);
    }

    private static org.joml.Vector2f rotateXZ(double x, double z, Direction facing) {
        double dirX = x;
        double dirZ = z;
        switch (facing) {
            case NORTH -> {
                dirX = -x;
                dirZ = -z;
            }
            case EAST -> {
                dirX = z;
                dirZ = -x;
            }
            case WEST -> {
                dirX = -z;
                dirZ = x;
            }
            default -> {
            }
        }
        return new org.joml.Vector2f((float) dirX, (float) dirZ);
    }

    private static double readSmoothedHeight(BaseLightBlockEntity blockEntity) {
        if (blockEntity instanceof com.github.dumann089.theatricalextralights.blockentities.WaterJetBlockEntity e) {
            return e.smoothedHeight;
        }
        if (blockEntity instanceof com.github.dumann089.theatricalextralights.blockentities.WaterJetBigBlockEntity e) {
            return e.smoothedHeight;
        }
        if (blockEntity instanceof com.github.dumann089.theatricalextralights.blockentities.WaterJetThinBlockEntity e) {
            return e.smoothedHeight;
        }
        if (blockEntity instanceof com.github.dumann089.theatricalextralights.blockentities.WaterJetSpreadBlockEntity e) {
            return e.smoothedHeight;
        }
        if (blockEntity instanceof com.github.dumann089.theatricalextralights.blockentities.WaterJetFogBlockEntity e) {
            return e.smoothedHeight;
        }
        if (blockEntity instanceof com.github.dumann089.theatricalextralights.blockentities.WaterJetConeBlockEntity e) {
            return e.smoothedHeight;
        }
        if (blockEntity instanceof com.github.dumann089.theatricalextralights.blockentities.WaterJetCentralBlockEntity e) {
            return e.smoothedHeight;
        }
        if (blockEntity instanceof com.github.dumann089.theatricalextralights.blockentities.WaterJetBloomBlockEntity e) {
            return e.smoothedHeight;
        }
        if (blockEntity instanceof com.github.dumann089.theatricalextralights.blockentities.MovingJetBlockEntity e) {
            return e.smoothedHeight;
        }
        return 0.0;
    }

    private static void writeSmoothedHeight(BaseLightBlockEntity blockEntity, double value) {
        if (blockEntity instanceof com.github.dumann089.theatricalextralights.blockentities.WaterJetBlockEntity e) {
            e.smoothedHeight = value;
        } else if (blockEntity instanceof com.github.dumann089.theatricalextralights.blockentities.WaterJetBigBlockEntity e) {
            e.smoothedHeight = value;
        } else if (blockEntity instanceof com.github.dumann089.theatricalextralights.blockentities.WaterJetThinBlockEntity e) {
            e.smoothedHeight = value;
        } else if (blockEntity instanceof com.github.dumann089.theatricalextralights.blockentities.WaterJetSpreadBlockEntity e) {
            e.smoothedHeight = value;
        } else if (blockEntity instanceof com.github.dumann089.theatricalextralights.blockentities.WaterJetFogBlockEntity e) {
            e.smoothedHeight = value;
        } else if (blockEntity instanceof com.github.dumann089.theatricalextralights.blockentities.WaterJetConeBlockEntity e) {
            e.smoothedHeight = value;
        } else if (blockEntity instanceof com.github.dumann089.theatricalextralights.blockentities.WaterJetCentralBlockEntity e) {
            e.smoothedHeight = value;
        } else if (blockEntity instanceof com.github.dumann089.theatricalextralights.blockentities.WaterJetBloomBlockEntity e) {
            e.smoothedHeight = value;
        } else if (blockEntity instanceof com.github.dumann089.theatricalextralights.blockentities.MovingJetBlockEntity e) {
            e.smoothedHeight = value;
        }
    }

    private static void emit(ClientLevel level, ParticleOptions options, double x, double y, double z, double vx, double vy, double vz) {
        level.addParticle(options, x, y, z, vx, vy, vz);
    }

    private static boolean isNearPlayer(ClientLevel level, double x, double y, double z) {
        if (Minecraft.getInstance().player == null) {
            return false;
        }
        double dx = x - Minecraft.getInstance().player.getX();
        double dy = y - Minecraft.getInstance().player.getY();
        double dz = z - Minecraft.getInstance().player.getZ();
        return dx * dx + dy * dy + dz * dz <= MAX_SPAWN_DISTANCE_SQ;
    }

    private static double getOrganSmoothed(BaseLightBlockEntity blockEntity) {
        if (blockEntity instanceof OrganPipesBlockEntity organ) {
            return organ.smoothedHeight;
        }
        return ((OrganPipesInvBlockEntity) blockEntity).smoothedHeight;
    }

    private static void setOrganSmoothed(BaseLightBlockEntity blockEntity, double value) {
        if (blockEntity instanceof OrganPipesBlockEntity organ) {
            organ.smoothedHeight = value;
        } else {
            ((OrganPipesInvBlockEntity) blockEntity).smoothedHeight = value;
        }
    }

    public static final class TickCounter {
        private int ticks;

        public boolean advanceAndShouldSpawn(int interval) {
            ticks++;
            return ticks % interval == 0;
        }
    }
}
