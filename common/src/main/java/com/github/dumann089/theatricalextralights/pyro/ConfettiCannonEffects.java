
package com.github.dumann089.theatricalextralights.pyro;

import com.github.dumann089.theatricalextralights.net.ConfettiBurstPacket;
import com.github.dumann089.theatricalextralights.net.ModNetworkHandler;
import com.github.dumann089.theatricalextralights.sounds.ModSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public final class ConfettiCannonEffects {
    private static final double VIEW_RANGE = 64.0D;

    private ConfettiCannonEffects() {
    }

    public static void fireBurst(ServerLevel level, Vec3 origin, Vec3 direction, float intensityNorm) {
        if (intensityNorm <= 0.0F) {
            return;
        }

        ConfettiBurstPacket packet = new ConfettiBurstPacket(origin, direction, intensityNorm);
        double rangeSq = VIEW_RANGE * VIEW_RANGE;
        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(origin) <= rangeSq) {
                ModNetworkHandler.CHANNEL.sendToPlayer(player, packet);
            }
        }

        RandomSource random = level.getRandom();
        level.playSound(
                null,
                origin.x, origin.y, origin.z,
                ModSounds.CONFETTI_CANNON.get(),
                SoundSource.BLOCKS,
                0.95F + intensityNorm * 0.45F,
                0.82F + random.nextFloat() * 0.28F
        );
    }
}
