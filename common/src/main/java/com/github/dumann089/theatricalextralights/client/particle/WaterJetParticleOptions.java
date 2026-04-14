package com.github.dumann089.theatricalextralights.client.particle;

import com.github.dumann089.theatricalextralights.particle.ModParticle;
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
    public final float coneAngle;
    public final JetVariant variant;

    public final boolean hasYaw;
    public final float particleYaw;

    public WaterJetParticleOptions(
            float intensity,
            float thickness,
            float coneAngle,
            JetVariant variant
    ) {
        this.intensity = intensity;
        this.thickness = thickness;
        this.coneAngle = coneAngle;
        this.variant = variant;
        this.hasYaw = false;
        this.particleYaw = 0.0f;
    }

    public WaterJetParticleOptions(
            float intensity,
            float thickness,
            float coneAngle,
            JetVariant variant,
            float particleYaw
    ) {
        this.intensity = intensity;
        this.thickness = thickness;
        this.coneAngle = coneAngle;
        this.variant = variant;
        this.hasYaw = true;
        this.particleYaw = particleYaw;
    }

    public WaterJetParticleOptions(float intensity, float thickness, JetVariant variant) {
        this(intensity, thickness, 45.0f, variant);
    }

    @Override
    public ParticleType<?> getType() {
        return ModParticle.WATERJET_OPTIONS.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeFloat(intensity);
        buf.writeFloat(thickness);
        buf.writeFloat(coneAngle);
        buf.writeEnum(variant);
        buf.writeBoolean(hasYaw);
        if (hasYaw) {
            buf.writeFloat(particleYaw);
        }
    }

    @Override
    public String writeToString() {
        if (hasYaw) {
            return intensity + " " + thickness + " " + coneAngle + " " + variant.name() + " " + particleYaw;
        }
        return intensity + " " + thickness + " " + coneAngle + " " + variant.name();
    }

    public static final Codec<WaterJetParticleOptions> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.FLOAT.fieldOf("intensity").forGetter(o -> o.intensity),
                    Codec.FLOAT.fieldOf("thickness").forGetter(o -> o.thickness),
                    Codec.FLOAT.optionalFieldOf("coneAngle", 45.0f).forGetter(o -> o.coneAngle),
                    JetVariant.CODEC.fieldOf("variant").forGetter(o -> o.variant),
                    Codec.BOOL.optionalFieldOf("hasYaw", false).forGetter(o -> o.hasYaw),
                    Codec.FLOAT.optionalFieldOf("particleYaw", 0.0f).forGetter(o -> o.particleYaw)
            ).apply(instance, (i, t, c, v, h, y) ->
                    h
                            ? new WaterJetParticleOptions(i, t, c, v, y)
                            : new WaterJetParticleOptions(i, t, c, v)
            ));

    public static final Deserializer<WaterJetParticleOptions> DESERIALIZER =
            new Deserializer<>() {

                @Override
                public WaterJetParticleOptions fromCommand(
                        ParticleType<WaterJetParticleOptions> type,
                        StringReader reader
                ) throws CommandSyntaxException {

                    float intensity = 1.0f;
                    float thickness = 0.12f;
                    float coneAngle = 45.0f;
                    JetVariant variant = JetVariant.JET2;
                    float yaw = 0.0f;
                    boolean hasYaw = false;

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
                        coneAngle = reader.readFloat();
                    }
                    if (reader.canRead()) {
                        reader.expect(' ');
                        variant = JetVariant.valueOf(reader.readString().toUpperCase());
                    }
                    if (reader.canRead()) {
                        reader.expect(' ');
                        yaw = reader.readFloat();
                        hasYaw = true;
                    }

                    return hasYaw
                            ? new WaterJetParticleOptions(intensity, thickness, coneAngle, variant, yaw)
                            : new WaterJetParticleOptions(intensity, thickness, coneAngle, variant);
                }

                @Override
                public WaterJetParticleOptions fromNetwork(
                        ParticleType<WaterJetParticleOptions> type,
                        FriendlyByteBuf buf
                ) {
                    float intensity = buf.readFloat();
                    float thickness = buf.readFloat();
                    float coneAngle = buf.readFloat();
                    JetVariant variant = buf.readEnum(JetVariant.class);

                    boolean hasYaw = buf.readBoolean();
                    if (hasYaw) {
                        float yaw = buf.readFloat();
                        return new WaterJetParticleOptions(intensity, thickness, coneAngle, variant, yaw);
                    }

                    return new WaterJetParticleOptions(intensity, thickness, coneAngle, variant);
                }
            };
}
