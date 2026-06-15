package com.github.dumann089.theatricalextralights.client;

import com.github.dumann089.theatricalextralights.client.model.ConfettiCannonModel;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.ModelLayerLocation;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class ConfettiCannonClientSetup {
    private ConfettiCannonClientSetup() {
    }

    public static void registerModelLayer(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> registrar) {
        registrar.accept(ConfettiCannonModel.LAYER_LOCATION, ConfettiCannonModel::createBodyLayer);
    }
}
