package com.github.dumann089.theatricalextralights.util;

import dev.imabad.theatrical.blockentities.light.BaseLightBlockEntity;
import dev.imabad.theatrical.blocks.HangableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class FollowspotBeamHelper {

    private FollowspotBeamHelper() {
    }

    public static Vec3 getBeamOrigin(BaseLightBlockEntity fixture) {
        BlockPos pos = fixture.getBlockPos();
        float[] beam = fixture.getFixture().getBeamStartPosition();
        return new Vec3(pos.getX() + beam[0], pos.getY() + beam[1], pos.getZ() + beam[2]);
    }

    public static Vec3 getBeamDirection(BaseLightBlockEntity fixture, int pan, int tilt) {
        float panRad = (float) Math.toRadians(pan);
        float tiltRad = (float) Math.toRadians(-tilt);
        Direction facing = fixture.getBlockState().getValue(HangableBlock.FACING);
        float baseYaw = facing.toYRot();

        float yaw = (float) Math.toRadians(-baseYaw + panRad);
        float pitch = tiltRad;

        double dx = -Math.sin(yaw) * Math.cos(pitch);
        double dy = -Math.sin(pitch);
        double dz = Math.cos(yaw) * Math.cos(pitch);
        return new Vec3(dx, dy, dz).normalize();
    }

    public static float[] getLookAngles(BaseLightBlockEntity fixture, int pan, int tilt) {
        Direction facing = fixture.getBlockState().getValue(HangableBlock.FACING);
        float yaw = -facing.toYRot() + pan;
        float pitch = -tilt;
        return new float[]{yaw, pitch};
    }

    public static void applyCameraToPlayer(Player player, BaseLightBlockEntity fixture, int pan, int tilt) {
        Vec3 origin = getBeamOrigin(fixture);
        float[] look = getLookAngles(fixture, pan, tilt);
        player.setPos(origin.x, origin.y, origin.z);
        player.setYRot(look[0]);
        player.setXRot(look[1]);
        player.yRotO = look[0];
        player.xRotO = look[1];
    }
}
