package com.github.dumann089.theatricalextralights.client.particle;
import com.mojang.serialization.Codec;

public enum JetVariant {
    JET1,
    JET2,
    JET3,
    JET4;

    public static final Codec<JetVariant> CODEC =
            Codec.STRING.xmap(
                    s -> JetVariant.valueOf(s.toUpperCase()),
                    JetVariant::name
            );
}
