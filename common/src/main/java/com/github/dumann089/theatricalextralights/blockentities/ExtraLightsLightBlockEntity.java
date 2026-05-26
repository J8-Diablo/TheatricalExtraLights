package com.github.dumann089.theatricalextralights.blockentities;

import com.github.dumann089.theatricalextralights.util.FollowspotDmxHelper;
import dev.imabad.theatrical.blockentities.light.BaseDMXConsumerLightBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Base DMX Extra Lights : préserve tous les prev* au sync client et les avance côté client
 * à chaque tick (évite clignotement pan/tilt/intensité et désync jusqu'au clic).
 */
public abstract class ExtraLightsLightBlockEntity extends BaseDMXConsumerLightBlockEntity {

    protected ExtraLightsLightBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /** Capture les prev* serveur avant lecture DMX. Retourne true si prev* étaient en retard. */
    protected boolean beginDmxUpdate() {
        return storePrev();
    }

    /**
     * Sync client si valeurs changées OU si prev* serveur ont rattrapé (pattern Theatrical).
     * {@code setChanged()} seulement quand les valeurs DMX ont changé.
     */
    protected void finishDmxUpdate(boolean valuesChanged, boolean prevAdvanced) {
        if (level == null || level.isClientSide) {
            return;
        }
        if (valuesChanged || prevAdvanced) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
        if (valuesChanged) {
            setChanged();
        }
    }

    /** Sync pan/tilt for operator mode — keeps prev* aligned to avoid interpolation flicker. */
    public void syncOperatorAngles(float pan, float tilt) {
        int pi = FollowspotDmxHelper.quantizePan(pan);
        int ti = FollowspotDmxHelper.quantizeTilt(tilt);
        setPan(pi);
        setTilt(ti);
        prevPan = pi;
        prevTilt = ti;
    }

    @Override
    public void lightTick() {
        super.lightTick();
        if (level != null && level.isClientSide) {
            if (com.github.dumann089.theatricalextralights.client.followspot.FollowspotFixtureCameraSession.isControlling(getBlockPos())) {
                return;
            }
            prevPan = pan;
            prevTilt = tilt;
            prevFocus = focus;
            prevIntensity = intensity;
            prevRed = red;
            prevGreen = green;
            prevBlue = blue;
        }
    }

    @Override
    public void write(CompoundTag tag) {
        super.write(tag);
        tag.putInt("prevPan", prevPan);
        tag.putInt("prevTilt", prevTilt);
        tag.putInt("prevFocus", prevFocus);
    }

    @Override
    public void read(CompoundTag tag) {
        boolean preserveAngles = level != null && level.isClientSide
                && com.github.dumann089.theatricalextralights.client.followspot.FollowspotFixtureCameraSession.isControlling(getBlockPos());
        int savedPan = pan;
        int savedTilt = tilt;
        int savedPrevPan = tag.contains("prevPan") ? tag.getInt("prevPan") : prevPan;
        int savedPrevTilt = tag.contains("prevTilt") ? tag.getInt("prevTilt") : prevTilt;
        int savedPrevFocus = tag.contains("prevFocus") ? tag.getInt("prevFocus") : prevFocus;
        int savedPrevIntensity = tag.contains("prevIntensity") ? tag.getInt("prevIntensity") : prevIntensity;
        int savedPrevRed = tag.contains("prevRed") ? tag.getInt("prevRed") : prevRed;
        int savedPrevGreen = tag.contains("prevGreen") ? tag.getInt("prevGreen") : prevGreen;
        int savedPrevBlue = tag.contains("prevBlue") ? tag.getInt("prevBlue") : prevBlue;

        super.read(tag);

        if (preserveAngles) {
            pan = savedPan;
            tilt = savedTilt;
            prevPan = savedPan;
            prevTilt = savedTilt;
        } else {
            prevPan = savedPrevPan;
            prevTilt = savedPrevTilt;
        }
        prevFocus = savedPrevFocus;
        prevIntensity = savedPrevIntensity;
        prevRed = savedPrevRed;
        prevGreen = savedPrevGreen;
        prevBlue = savedPrevBlue;
    }
}
