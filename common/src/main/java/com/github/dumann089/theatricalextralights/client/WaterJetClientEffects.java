package com.github.dumann089.theatricalextralights.client;

import com.github.dumann089.theatricalextralights.blockentities.*;
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
import org.joml.Vector3f;

/**
 * Client-side water jet particles — per-frame spawns from BER (with distance culling).
 */
public final class WaterJetClientEffects {
    private static final double MAX_SPAWN_DISTANCE_SQ = 128.0 * 128.0;
    private static final double[] ORGAN_PIPE_X = {-1.4375, -1.125, -0.75, -0.375, 0.0, 0.375, 0.75, 1.125, 1.4375};
    private static final double[] ORGAN_PIPE_HEIGHT_MULT = {0.6, 0.7, 0.8, 0.9, 1.0, 0.9, 0.8, 0.7, 0.6};
    private static final double[][] SPINNER_NOZZLES = {
            {0.96, 2.0, 0.501},
            {0.5, 2.0, 0.968},
            {0.5, 2.0, 0.034},
            {0.034, 2.0, 0.501}
    };
    private static final double[] FAN_OFFSETS_X = {
            -0.507, -0.415, -0.321, -0.227, -0.133, -0.040,
            0.040,  0.133,  0.227,  0.321,  0.415,  0.507
    };
    private static final float[] FAN_ANGLES = {
            -10.5F, -8.5F, -6.5F, -4.5F, -2.5F, -0.5F,
            0.5f,  2.5F,  4.5F,  6.5F,  8.5F,  10.5F
    };
    private static final double[] FAN_HEIGHT_MULT = {
            0.75, 0.8, 0.85, 0.9, 0.95, 1.0,
            1.0, 0.95, 0.9, 0.85, 0.8, 0.75
    };
    private static final int[] CAKE_COUNTS = {10, 8, 6};
    private static final double[] CAKE_RADII = {0.6, 0.35, 0.1};
    private static final float[] CAKE_ELEVATIONS = {80.0F, 85.0F, 88.0F};
    private static final double[] CAKE_SPEED_MULT = {0.6, 0.8, 1.0};

    private static final int[] VASE_COUNTS = {0, 0, 12};
    private static final double[] VASE_RADII = {0.0, 0.00, 0.2};
    private static final float[] VASE_ELEVATIONS = {0.0F, 0.0F, 85.0F};
    private static final double[] VASE_SPEED_MULT = {0.0, 0.0, 1.0};

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

    public static boolean isWaterJetFixture(BaseLightBlockEntity blockEntity) {
        return blockEntity instanceof WaterJetBlockEntity
                || blockEntity instanceof WaterJetBigBlockEntity
                || blockEntity instanceof WaterJetThinBlockEntity
                || blockEntity instanceof WaterJetSpreadBlockEntity
                || blockEntity instanceof WaterJetFogBlockEntity
                || blockEntity instanceof WaterJetConeBlockEntity
                || blockEntity instanceof WaterJetCentralBlockEntity
                || blockEntity instanceof WaterJetBloomBlockEntity
                || blockEntity instanceof MovingJetBlockEntity
                || blockEntity instanceof OrganPipesBlockEntity
                || blockEntity instanceof OrganPipesInvBlockEntity
                || blockEntity instanceof SpinnerBlockEntity
                || blockEntity instanceof FanWaterJetBlockEntity
                || blockEntity instanceof CakeWaterJetBlockEntity
                || blockEntity instanceof VaseWaterJetBlockEntity
                || blockEntity instanceof WaltzesWaterJetBlockEntity
                || blockEntity instanceof WaltzCurtainBlockEntity;
    }

    public static void updateWaltzesClient(WaltzesWaterJetBlockEntity blockEntity) {
        if (resolveClientLevel(blockEntity) == null) {
            return;
        }
        float globalSwayTime = (blockEntity.getLevel().getGameTime() * 0.45F);
        blockEntity.prevAngle = blockEntity.currentAngle;

        float baseTilt = -25.0F + (blockEntity.getTilt() / 255.0F) * 50.0F;
        float targetAngle = baseTilt;

        if (blockEntity.swingChannel > 0) {
            float speed = (blockEntity.swingChannel / 255.0F) * 0.02F;
            float amplitude = 25.0F;
            targetAngle = baseTilt + (float) Math.sin(globalSwayTime * speed * 20.0F) * amplitude;
        }

        float alpha = 0.15F;
        blockEntity.currentAngle += (targetAngle - blockEntity.currentAngle) * alpha;

        float intensityNorm = blockEntity.getIntensity() / 255.0F;
        double targetHeight = intensityNorm * blockEntity.getJetHeight();
        blockEntity.smoothedHeight += (targetHeight - blockEntity.smoothedHeight) * 0.15;
    }

    // Este método es exclusivo para el WaltzCurtain
    public static void updateWaltzCurtainClient(WaltzCurtainBlockEntity blockEntity) {
        if (resolveClientLevel(blockEntity) == null) {
            return;
        }
        float globalSwayTime = (blockEntity.getLevel().getGameTime() * 0.45F);
        blockEntity.prevAngle = blockEntity.currentAngle;

        float baseTilt = -25.0F + (blockEntity.getTilt() / 255.0F) * 50.0F;
        float targetAngle = baseTilt;

        if (blockEntity.swingChannel > 0) {
            float speed = (blockEntity.swingChannel / 255.0F) * 0.02F;
            float amplitude = 25.0F;
            targetAngle = baseTilt + (float) Math.sin(globalSwayTime * speed * 20.0F) * amplitude;
        }

        float alpha = 0.15F;
        blockEntity.currentAngle += (targetAngle - blockEntity.currentAngle) * alpha;

        float intensityNorm = blockEntity.getIntensity() / 255.0F;
        double targetHeight = intensityNorm * blockEntity.getJetHeight();
        blockEntity.smoothedHeight += (targetHeight - blockEntity.smoothedHeight) * 0.15;
    }

    /** Called each render frame when the fixture is visible (LazyRenderers). */
    public static void spawnFromBeam(BaseLightBlockEntity blockEntity, float partialTick) {
        ClientLevel level = resolveClientLevel(blockEntity);
        if (level == null || blockEntity.getIntensity() <= 0) {
            return;
        }

        float pan = blockEntity.getPrevPan() + (blockEntity.getPan() - blockEntity.getPrevPan()) * partialTick;
        float tilt = blockEntity.getPrevTilt() + (blockEntity.getTilt() - blockEntity.getPrevTilt()) * partialTick;

        if (blockEntity instanceof WaterJetBlockEntity) {
            spawnJet(blockEntity, level, WATER_JET, pan, tilt);
        } else if (blockEntity instanceof WaterJetBigBlockEntity) {
            spawnJet(blockEntity, level, BIG, pan, tilt);
        } else if (blockEntity instanceof WaterJetThinBlockEntity) {
            spawnJet(blockEntity, level, THIN, pan, tilt);
        } else if (blockEntity instanceof WaterJetSpreadBlockEntity) {
            spawnJet(blockEntity, level, SPREAD, pan, tilt);
        } else if (blockEntity instanceof WaterJetFogBlockEntity) {
            spawnJet(blockEntity, level, FOG, pan, tilt);
        } else if (blockEntity instanceof WaterJetConeBlockEntity) {
            spawnJet(blockEntity, level, CONE, pan, tilt);
        } else if (blockEntity instanceof WaterJetCentralBlockEntity) {
            spawnJet(blockEntity, level, CENTRAL, pan, tilt);
        } else if (blockEntity instanceof WaterJetBloomBlockEntity) {
            spawnJet(blockEntity, level, BLOOM, pan, tilt);
        } else if (blockEntity instanceof MovingJetBlockEntity) {
            spawnJet(blockEntity, level, MOVING, pan, tilt);
        } else if (blockEntity instanceof OrganPipesBlockEntity organ) {
            spawnOrganPipes(organ, level, JetVariant.JET3, pan, tilt);

        } else if (blockEntity instanceof FanWaterJetBlockEntity fan) {
            spawnFanJets(fan, level, JetVariant.JET3, pan, tilt);

        } else if (blockEntity instanceof CakeWaterJetBlockEntity cake) {
            spawnCakeJets(cake, level, JetVariant.JET3, pan, tilt);

        } else if (blockEntity instanceof VaseWaterJetBlockEntity vase) {
            spawnVaseJets(vase, level, JetVariant.JET3, pan, tilt);

        } else if (blockEntity instanceof WaltzesWaterJetBlockEntity waltzes) {
            updateWaltzesClient(waltzes);
            spawnWaltzesParticles(waltzes, level, JetVariant.JET3, partialTick);

        } else if (blockEntity instanceof WaltzCurtainBlockEntity waltzcurtain) {
            updateWaltzCurtainClient(waltzcurtain);
            spawnWaltzCurtainParticles(waltzcurtain, level, JetVariant.JET3, partialTick);

        } else if (blockEntity instanceof OrganPipesInvBlockEntity organInv) {
            spawnOrganPipes(organInv, level, JetVariant.JET3, pan, tilt);
        } else if (blockEntity instanceof SpinnerBlockEntity spinner) {
            spawnSpinner(spinner, level);

            updateSpinnerClient(spinner);
            spawnSpinner(spinner, level);
        }
    }

    public static void updateSpinnerClient(SpinnerBlockEntity blockEntity) {
        if (resolveClientLevel(blockEntity) == null) {
            return;
        }
        float intensityNorm = blockEntity.getIntensity() / 255.0F;
        double targetHeight = intensityNorm * blockEntity.getJetHeight();
        blockEntity.smoothedHeight += (targetHeight - blockEntity.smoothedHeight) * 0.15;
        blockEntity.updateSpin();
    }

    private static void spawnJet(BaseLightBlockEntity blockEntity, ClientLevel level, JetPreset preset, float pan, float tilt) {
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
                    pan,
                    tilt,
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

    private static void spawnOrganPipes(BaseLightBlockEntity blockEntity, ClientLevel level, JetVariant variant, float pan, float tilt) {
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
        Vector3f baseDir = computeDirection(pan, tilt, facing, 0.0F, false);

        double blockCenterX = blockEntity.getBlockPos().getX() + 0.5;
        double blockCenterZ = blockEntity.getBlockPos().getZ() + 0.5;
        double baseY = blockEntity.getBlockPos().getY() + 2.0;
        double baseZOffset = 0.51;

        double yaw = Math.toRadians(pan);
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

    private static void spawnCakeJets(BaseLightBlockEntity blockEntity, ClientLevel level, JetVariant variant, float pan, float tilt) {
        if (!(blockEntity instanceof HasJetHeight hasJetHeight) || !(blockEntity instanceof HasJetThickness hasJetThickness)) {
            return;
        }

        Direction facing = blockEntity.getBlockState().getValue(HangableBlock.FACING);
        double maxHeight = hasJetHeight.getJetHeight();
        double targetHeight = (blockEntity.getIntensity() / 255.0) * maxHeight;

        double smoothed = readSmoothedHeight(blockEntity);
        smoothed += (targetHeight - smoothed) * 0.1;
        writeSmoothedHeight(blockEntity, smoothed);

        double baseSpeed = smoothed * 0.09;
        float intensity = blockEntity.getIntensity() / 255.0F;
        float thickness = hasJetThickness.getJetThickness();

        double blockCenterX = blockEntity.getBlockPos().getX() + 0.5;
        double blockCenterZ = blockEntity.getBlockPos().getZ() + 0.5;
        double baseY = blockEntity.getBlockPos().getY() + 2.0;

        if (!isNearPlayer(level, blockCenterX, baseY, blockCenterZ)) {
            return;
        }

        float blockFacingYaw = facing.toYRot();

        for (int t = 0; t < 3; t++) {
            int count = CAKE_COUNTS[t];
            double radius = CAKE_RADII[t];
            float elevation = CAKE_ELEVATIONS[t];
            double speedMult = CAKE_SPEED_MULT[t];

            for (int i = 0; i < count; i++) {
                float angleDeg = (360.0F / count) * i;
                if (t == 1) angleDeg += (360.0F / count) / 2.0F;

                float totalYaw = blockFacingYaw + pan + angleDeg;
                double yawRad = Math.toRadians(totalYaw);

                double pitchRad = Math.toRadians(elevation);

                double verticalForce = Math.sin(pitchRad);
                double horizontalForce = Math.cos(pitchRad);

                double dirX = -Math.sin(yawRad) * horizontalForce;
                double dirY = verticalForce;
                double dirZ = Math.cos(yawRad) * horizontalForce;

                double spawnX = blockCenterX + (-Math.sin(yawRad) * radius);
                double spawnZ = blockCenterZ + (Math.cos(yawRad) * radius);

                double particleSpeed = baseSpeed * speedMult;
                WaterJetParticleOptions options = new WaterJetParticleOptions(
                        intensity * (float) speedMult,
                        thickness,
                        variant
                );
                emit(level, options, spawnX, baseY, spawnZ,
                        dirX * particleSpeed, dirY * particleSpeed, dirZ * particleSpeed);
            }
        }
    }

    private static void spawnVaseJets(BaseLightBlockEntity blockEntity, ClientLevel level, JetVariant variant, float pan, float tilt) {
        if (!(blockEntity instanceof HasJetHeight hasJetHeight) || !(blockEntity instanceof HasJetThickness hasJetThickness)) {
            return;
        }

        Direction facing = blockEntity.getBlockState().getValue(HangableBlock.FACING);
        double maxHeight = hasJetHeight.getJetHeight();
        double targetHeight = (blockEntity.getIntensity() / 255.0) * maxHeight;

        double smoothed = readSmoothedHeight(blockEntity);
        smoothed += (targetHeight - smoothed) * 0.1;
        writeSmoothedHeight(blockEntity, smoothed);

        double baseSpeed = smoothed * 0.09;
        float intensity = blockEntity.getIntensity() / 255.0F;
        float thickness = hasJetThickness.getJetThickness();

        double blockCenterX = blockEntity.getBlockPos().getX() + 0.5;
        double blockCenterZ = blockEntity.getBlockPos().getZ() + 0.5;
        double baseY = blockEntity.getBlockPos().getY() + 2.0;

        if (!isNearPlayer(level, blockCenterX, baseY, blockCenterZ)) {
            return;
        }

        float blockFacingYaw = facing.toYRot();

        for (int t = 0; t < 3; t++) {
            int count = VASE_COUNTS[t];
            double radius = VASE_RADII[t];
            float elevation = VASE_ELEVATIONS[t];
            double speedMult = VASE_SPEED_MULT[t];

            for (int i = 0; i < count; i++) {
                float angleDeg = (360.0F / count) * i;
                if (t == 1) angleDeg += (360.0F / count) / 2.0F;

                float totalYaw = blockFacingYaw + pan + angleDeg;
                double yawRad = Math.toRadians(totalYaw);

                double pitchRad = Math.toRadians(elevation);

                double verticalForce = Math.sin(pitchRad);
                double horizontalForce = Math.cos(pitchRad);

                double dirX = -Math.sin(yawRad) * horizontalForce;
                double dirY = verticalForce;
                double dirZ = Math.cos(yawRad) * horizontalForce;

                double spawnX = blockCenterX + (-Math.sin(yawRad) * radius);
                double spawnZ = blockCenterZ + (Math.cos(yawRad) * radius);

                double particleSpeed = baseSpeed * speedMult;
                WaterJetParticleOptions options = new WaterJetParticleOptions(
                        intensity * (float) speedMult,
                        thickness,
                        variant
                );
                emit(level, options, spawnX, baseY, spawnZ,
                        dirX * particleSpeed, dirY * particleSpeed, dirZ * particleSpeed);
            }
        }
    }

    private static void spawnFanJets(BaseLightBlockEntity blockEntity, ClientLevel level, JetVariant variant, float pan, float tilt) {
        if (!(blockEntity instanceof HasJetHeight hasJetHeight) || !(blockEntity instanceof HasJetThickness hasJetThickness)) {
            return;
        }

        Direction facing = blockEntity.getBlockState().getValue(HangableBlock.FACING);
        double maxHeight = hasJetHeight.getJetHeight();
        double targetHeight = (blockEntity.getIntensity() / 255.0) * maxHeight;

        double smoothed = readSmoothedHeight(blockEntity);
        smoothed += (targetHeight - smoothed) * 0.1;
        writeSmoothedHeight(blockEntity, smoothed);

        double baseSpeed = smoothed * 0.09;
        float intensity = blockEntity.getIntensity() / 255.0F;
        float thickness = hasJetThickness.getJetThickness();

        double blockCenterX = blockEntity.getBlockPos().getX() + 0.5;
        double blockCenterZ = blockEntity.getBlockPos().getZ() + 0.5;
        double baseY = blockEntity.getBlockPos().getY() + 2.0;

        if (!isNearPlayer(level, blockCenterX, baseY, blockCenterZ)) {
            return;
        }

        Vector3f baseDir = computeDirection(pan, tilt, facing, 0.0F, false);

        double baseRightX = facing.getClockWise().getStepX();
        double baseRightZ = facing.getClockWise().getStepZ();

        double panRad = Math.toRadians(pan);
        double rightX = baseRightX * Math.cos(panRad) - baseRightZ * Math.sin(panRad);
        double rightZ = baseRightX * Math.sin(panRad) + baseRightZ * Math.cos(panRad);

        for (int i = 0; i < FAN_OFFSETS_X.length; i++) {
            double spawnX = blockCenterX + (FAN_OFFSETS_X[i] * rightX);
            double spawnZ = blockCenterZ + (FAN_OFFSETS_X[i] * rightZ);

            double angleRad = Math.toRadians(FAN_ANGLES[i]);
            double cosAngle = Math.cos(angleRad);
            double sinAngle = Math.sin(angleRad);
            double dirX = (baseDir.x * cosAngle) + (rightX * sinAngle);
            double dirY = (baseDir.y * cosAngle);
            double dirZ = (baseDir.z * cosAngle) + (rightZ * sinAngle);

            double particleSpeed = baseSpeed * FAN_HEIGHT_MULT[i];

            WaterJetParticleOptions options = new WaterJetParticleOptions(
                    intensity * (float) FAN_HEIGHT_MULT[i],
                    thickness,
                    variant
            );

            emit(level, options, spawnX, baseY, spawnZ,
                    dirX * particleSpeed, dirY * particleSpeed, dirZ * particleSpeed);
        }
    }
    public static void spawnWaltzesParticles(WaltzesWaterJetBlockEntity blockEntity, ClientLevel level, JetVariant variant, float partialTicks) {
        if (!isNearPlayer(level, blockEntity.getBlockPos().getX() + 0.5, blockEntity.getBlockPos().getY() + 2.0, blockEntity.getBlockPos().getZ() + 0.5)) {
            return;
        }

        float intensityNorm = blockEntity.getIntensity() / 255.0f;

        net.minecraft.core.Direction facing = blockEntity.getBlockState().getValue(dev.imabad.theatrical.blocks.HangableBlock.FACING);
        float yawRad = (float) Math.toRadians(facing.getOpposite().toYRot());

        float smoothAngle = blockEntity.getRenderAngle(partialTicks);
        float tiltRad = (float) Math.toRadians(smoothAngle);

        double yOffsetFromBlock = 2.0;

        double xBase = blockEntity.getBlockPos().getX() + 0.5;
        double yBase = blockEntity.getBlockPos().getY() + yOffsetFromBlock;
        double zBase = blockEntity.getBlockPos().getZ() + 0.5;

        double[] xOffsets = {-1.4375, -1.125, -0.75, -0.375, 0.0, 0.375, 0.75, 1.125, 1.4375};

        double cosYaw = Math.cos(yawRad);
        double sinYaw = Math.sin(yawRad);

        double speed = blockEntity.smoothedHeight * 0.1;

        double vLocalY = speed * Math.cos(tiltRad);
        double vLocalZ = speed * Math.sin(tiltRad);

        double vGlobalX = vLocalZ * -sinYaw;
        double vGlobalY = vLocalY;
        double vGlobalZ = vLocalZ * cosYaw;

        for (double xOffset : xOffsets) {
            double finalX = xBase + (xOffset * cosYaw);
            double finalY = yBase;
            double finalZ = zBase - (xOffset * sinYaw);

            emit(level,
                    new WaterJetParticleOptions(intensityNorm, blockEntity.getJetThickness(), variant),
                    finalX,
                    finalY,
                    finalZ,
                    vGlobalX,
                    vGlobalY,
                    vGlobalZ
            );
        }
    }

    public static void spawnWaltzCurtainParticles(WaltzCurtainBlockEntity blockEntity, ClientLevel level, JetVariant variant, float partialTicks) {
        if (!isNearPlayer(level, blockEntity.getBlockPos().getX() + 0.5, blockEntity.getBlockPos().getY() + 2.0, blockEntity.getBlockPos().getZ() + 0.5)) {
            return;
        }

        float intensityNorm = blockEntity.getIntensity() / 255.0f;

        net.minecraft.core.Direction facing = blockEntity.getBlockState().getValue(dev.imabad.theatrical.blocks.HangableBlock.FACING);
        float yawRad = (float) Math.toRadians(facing.getOpposite().toYRot());

        float smoothAngle = blockEntity.getRenderAngle(partialTicks);
        float tiltRad = (float) Math.toRadians(smoothAngle);

        double yOffsetFromBlock = 2.0;

        double xBase = blockEntity.getBlockPos().getX() + 0.5;
        double yBase = blockEntity.getBlockPos().getY() + yOffsetFromBlock;
        double zBase = blockEntity.getBlockPos().getZ() + 0.5;

        double[] xOffsets = { -0.375, 0.0, 0.375,};

        double cosYaw = Math.cos(yawRad);
        double sinYaw = Math.sin(yawRad);

        double speed = blockEntity.smoothedHeight * 0.1;
        double vLocalX = speed * Math.sin(tiltRad); // Movimiento lateral
        double vLocalY = speed * Math.cos(tiltRad); // Elevación
        double vLocalZ = 0; // Sin profundidad

        double vGlobalX = (vLocalX * cosYaw) + (vLocalZ * sinYaw);
        double vGlobalY = vLocalY;
        double vGlobalZ = (-vLocalX * sinYaw) + (vLocalZ * cosYaw);

        for (double xOffset : xOffsets) {
            double finalX = xBase + (xOffset * cosYaw);
            double finalY = yBase;
            double finalZ = zBase - (xOffset * sinYaw);

            emit(level,
                    new WaterJetParticleOptions(intensityNorm, blockEntity.getJetThickness(), variant),
                    finalX,
                    finalY,
                    finalZ,
                    vGlobalX,
                    vGlobalY,
                    vGlobalZ
            );
        }
    }

    private static void spawnSpinner(SpinnerBlockEntity blockEntity, ClientLevel level) {
        if (!isNearPlayer(level, blockEntity.getBlockPos().getX() + 0.5, blockEntity.getBlockPos().getY() + 2.0, blockEntity.getBlockPos().getZ() + 0.5)) {
            return;
        }

        double speed = blockEntity.smoothedHeight * 0.10;
        float intensity = blockEntity.getIntensity() / 255.0F;
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
        if (blockEntity instanceof com.github.dumann089.theatricalextralights.blockentities.FanWaterJetBlockEntity e) {
            return e.smoothedHeight;
        }
        if (blockEntity instanceof com.github.dumann089.theatricalextralights.blockentities.CakeWaterJetBlockEntity e) {
            return e.smoothedHeight;
        }
        if (blockEntity instanceof com.github.dumann089.theatricalextralights.blockentities.VaseWaterJetBlockEntity e) {
            return e.smoothedHeight;
        }
        if (blockEntity instanceof com.github.dumann089.theatricalextralights.blockentities.WaltzesWaterJetBlockEntity e) {
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
        } else if (blockEntity instanceof com.github.dumann089.theatricalextralights.blockentities.FanWaterJetBlockEntity e) {
            e.smoothedHeight = value;
        } else if (blockEntity instanceof com.github.dumann089.theatricalextralights.blockentities.CakeWaterJetBlockEntity e) {
            e.smoothedHeight = value;
        } else if (blockEntity instanceof com.github.dumann089.theatricalextralights.blockentities.VaseWaterJetBlockEntity e) {
            e.smoothedHeight = value;
        } else if (blockEntity instanceof com.github.dumann089.theatricalextralights.blockentities.WaltzesWaterJetBlockEntity e) {
            e.smoothedHeight = value;
        }
    }



    private static ClientLevel resolveClientLevel(BaseLightBlockEntity blockEntity) {
        if (Minecraft.getInstance().isPaused()) {
            return null;
        }
        net.minecraft.world.level.Level level = blockEntity.getLevel();
        if (level == null || !level.isClientSide()) {
            return null;
        }
        return (ClientLevel) level;
    }

    private static void emit(ClientLevel level, ParticleOptions options, double x, double y, double z, double vx, double vy, double vz) {
        level.addAlwaysVisibleParticle(options, true, x, y, z, vx, vy, vz);
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
}