package com.github.dumann089.theatricalextralights.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

public class WaterJetParticleOptions implements ParticleOptions {

    public final float intensity;
    public final float thickness;
    public final JetVariant variant;

    public WaterJetParticleOptions(float intensity, float thickness, JetVariant variant) {
        this.intensity = intensity;
        this.thickness = thickness;
        this.variant = variant;
    }

    @Override
    public ParticleType<?> getType() {
        return com.github.dumann089.theatricalextralights.particle.ModParticle.WATERJET_OPTIONS.get();
    }

    // ========================
    // NETWORK
    // ========================

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeFloat(intensity);
        buf.writeFloat(thickness);
        buf.writeEnum(variant);
    }

    @Override
    public String writeToString() {
        return intensity + " " + thickness + " " + variant.name();
    }

    // ========================
    // CODEC
    // ========================

    public static final Codec<WaterJetParticleOptions> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.FLOAT.fieldOf("intensity").forGetter(o -> o.intensity),
                    Codec.FLOAT.fieldOf("thickness").forGetter(o -> o.thickness),
                    JetVariant.CODEC.fieldOf("variant").forGetter(o -> o.variant)
            ).apply(instance, WaterJetParticleOptions::new));

    // ========================
    // DESERIALIZER
    // ========================

    public static final Deserializer<WaterJetParticleOptions> DESERIALIZER =
            new Deserializer<>() {

                @Override
                public WaterJetParticleOptions fromCommand(
                        ParticleType<WaterJetParticleOptions> type,
                        StringReader reader
                ) throws CommandSyntaxException {

                    float intensity = 1.0f;
                    float thickness = 1.0f;
                    JetVariant variant = JetVariant.JET2; // default

                    if (reader.canRead()) {
                        reader.expect(' ');
                        intensity = reader.readFloat();
                    }

                    if (reader.canRead()) {
                        reader.expect(' ');
                        thickness = reader.readFloat();
                    }

                    if (reader.canRead()) {
                        reader.expect(' ');
                        variant = JetVariant.valueOf(reader.readString().toUpperCase());
                    }

                    return new WaterJetParticleOptions(intensity, thickness, variant);
                }

                @Override
                public WaterJetParticleOptions fromNetwork(
                        ParticleType<WaterJetParticleOptions> type,
                        FriendlyByteBuf buf
                ) {
                    return new WaterJetParticleOptions(
                            buf.readFloat(),
                            buf.readFloat(),
                            buf.readEnum(JetVariant.class)
                    );
                }
            };
}
