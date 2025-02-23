package com.github.dumann089.theatricalextralights;

import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class TheatricalExtraLightsRegistry {
	public static final Supplier<RegistrarManager> MANAGER =
		Suppliers.memoize(() -> RegistrarManager.get(TheatricalExtraLights.MOD_ID));

	public static <T> DeferredRegister<T> get(ResourceKey<Registry<T>> registry) {
		return DeferredRegister.create(TheatricalExtraLights.MOD_ID, registry);
	}

	@SuppressWarnings("unchecked")
	public static <T> Registrar<T> create(ResourceLocation registryId) {
		return (Registrar<T>) MANAGER.get().builder(registryId).build();
	}
}
