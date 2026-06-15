package com.github.dumann089.theatricalextralights.compat;

import dev.imabad.theatrical.api.DynamicLightProvider;
import net.minecraft.core.BlockPos;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * File d'attente des mises à jour Shimmer pour les fusées.
 * Évite les ConcurrentModificationException quand beaucoup de pyro modifient
 * LightManager pendant le rendu.
 */
public final class FireworkLightUpdateQueue {
    public record Update(BlockPos ownerPos, Vector3f pos, int color, float spread, boolean remove) {
    }

    private static final ConcurrentHashMap<BlockPos, Update> PENDING = new ConcurrentHashMap<>();

    private FireworkLightUpdateQueue() {
    }

    public static void queueSync(DynamicLightProvider provider) {
        BlockPos owner = provider.getOwnerPos();
        if (provider.getLightLuminance() <= 0) {
            PENDING.put(owner, new Update(owner, null, 0, 0, true));
            return;
        }
        PENDING.put(owner, new Update(
                owner,
                provider.getLightPos(),
                provider.getLightColour(),
                provider.getLightSpread(),
                false
        ));
    }

    public static void queueRemove(BlockPos ownerPos) {
        PENDING.put(ownerPos, new Update(ownerPos, null, 0, 0, true));
    }

    public static void drain(Consumer<Update> consumer) {
        if (PENDING.isEmpty()) {
            return;
        }
        List<Update> batch = new ArrayList<>(PENDING.size());
        PENDING.forEach((key, update) -> {
            if (PENDING.remove(key, update)) {
                batch.add(update);
            }
        });
        for (Update update : batch) {
            consumer.accept(update);
        }
    }
}
