package com.github.dumann089.theatricalextralights.entities;

import com.github.dumann089.theatricalextralights.TheatricalExtraLightsRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            TheatricalExtraLightsRegistry.get(Registries.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<FireworkRocketEntity>> FIREWORK_ROCKET =
            ENTITY_TYPES.register("firework_rocket", () ->
                    EntityType.Builder.<FireworkRocketEntity>of(FireworkRocketEntity::new, MobCategory.MISC)
                            .sized(0.25f, 0.25f)
                            .clientTrackingRange(512)
                            .updateInterval(1)
                            .build("firework_rocket"));

    private ModEntities() {
    }

    public static void init() {
        ENTITY_TYPES.register();
    }
}
