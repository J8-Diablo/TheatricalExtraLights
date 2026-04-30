package com.github.dumann089.theatricalextralights.client.particle;

import com.github.dumann089.theatricalextralights.particle.ModParticle;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

public class FireworkSparkParticleOptions implements ParticleOptions {
    public static final Codec<FireworkSparkParticleOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("red").forGetter(o -> o.red),
            Codec.FLOAT.fieldOf("green").forGetter(o -> o.green),
            Codec.FLOAT.fieldOf("blue").forGetter(o -> o.blue),
            Codec.FLOAT.fieldOf("scale").forGetter(o -> o.scale),
            Codec.FLOAT.optionalFieldOf("alpha", 0.95f).forGetter(o -> o.alpha),
            Codec.INT.fieldOf("lifetime").forGetter(o -> o.lifetime),
            Codec.FLOAT.fieldOf("gravity").forGetter(o -> o.gravity),
            Codec.BOOL.optionalFieldOf("trail", false).forGetter(o -> o.trail),
            Codec.BOOL.optionalFieldOf("strobe", false).forGetter(o -> o.strobe)
    ).apply(instance, FireworkSparkParticleOptions::new));

    public static final Deserializer<FireworkSparkParticleOptions> DESERIALIZER = new Deserializer<>() {
        @Override
        public FireworkSparkParticleOptions fromCommand(ParticleType<FireworkSparkParticleOptions> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float red = reader.readFloat();
            reader.expect(' ');
            float green = reader.readFloat();
            reader.expect(' ');
            float blue = reader.readFloat();
            reader.expect(' ');
            float scale = reader.readFloat();
            reader.expect(' ');
            float alpha = reader.readFloat();
            reader.expect(' ');
            int lifetime = reader.readInt();
            reader.expect(' ');
            float gravity = reader.readFloat();
            boolean trail = false;
            boolean strobe = false;
            if (reader.canRead()) {
                reader.expect(' ');
                trail = reader.readBoolean();
            }
            if (reader.canRead()) {
                reader.expect(' ');
                strobe = reader.readBoolean();
            }
            return new FireworkSparkParticleOptions(red, green, blue, scale, alpha, lifetime, gravity, trail, strobe);
        }

        @Override
        public FireworkSparkParticleOptions fromNetwork(ParticleType<FireworkSparkParticleOptions> type, FriendlyByteBuf buf) {
            return new FireworkSparkParticleOptions(
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readInt(),
                    buf.readFloat(),
                    buf.readBoolean(),
                    buf.readBoolean()
            );
        }
    };

    public final float red;
    public final float green;
    public final float blue;
    public final float scale;
    public final float alpha;
    public final int lifetime;
    public final float gravity;
    public final boolean trail;
    public final boolean strobe;

    public FireworkSparkParticleOptions(float red, float green, float blue, float scale, float alpha, int lifetime, float gravity, boolean trail, boolean strobe) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.scale = scale;
        this.alpha = alpha;
        this.lifetime = lifetime;
        this.gravity = gravity;
        this.trail = trail;
        this.strobe = strobe;
    }

    @Override
    public ParticleType<?> getType() {
        return ModParticle.FIREWORK_SPARK.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeFloat(red);
        buf.writeFloat(green);
        buf.writeFloat(blue);
        buf.writeFloat(scale);
        buf.writeFloat(alpha);
        buf.writeInt(lifetime);
        buf.writeFloat(gravity);
        buf.writeBoolean(trail);
        buf.writeBoolean(strobe);
    }

    @Override
    public String writeToString() {
        return red + " " + green + " " + blue + " " + scale + " " + alpha + " " + lifetime + " " + gravity + " " + trail + " " + strobe;
    }
}
