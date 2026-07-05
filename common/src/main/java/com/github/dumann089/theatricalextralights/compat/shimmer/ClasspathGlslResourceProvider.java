package com.github.dumann089.theatricalextralights.compat.shimmer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;

import java.io.InputStream;
import java.util.Optional;

/**
 * During Forge's first shader reload, mod resource packs are not mounted yet.
 * Shimmer injects {@code #moj_import minecraft:shaders/include/shimmer.glsl} into vanilla shaders;
 * when that import fails, compilation aborts before fog.glsl and {@code fog_distance} is undefined.
 */
public final class ClasspathGlslResourceProvider implements ResourceProvider {
    private static final ResourceLocation SHIMMER_GLSL =
            new ResourceLocation("minecraft", "shaders/include/shimmer.glsl");
    private static final ResourceLocation FOG_GLSL =
            new ResourceLocation("minecraft", "shaders/include/fog.glsl");

    private final ResourceProvider parent;

    public ClasspathGlslResourceProvider(ResourceProvider parent) {
        this.parent = parent;
    }

    @Override
    public Optional<Resource> getResource(ResourceLocation location) {
        Optional<Resource> found = parent.getResource(location);
        if (found.isPresent()) {
            return found;
        }
        if (SHIMMER_GLSL.equals(location) || FOG_GLSL.equals(location)) {
            return classpathResource(location);
        }
        return Optional.empty();
    }

    private static Optional<Resource> classpathResource(ResourceLocation location) {
        String path = "/assets/" + location.getNamespace() + "/" + location.getPath();
        if (ClasspathGlslResourceProvider.class.getResource(path) == null) {
            return Optional.empty();
        }
        IoSupplier<InputStream> supplier = () -> {
            InputStream stream = ClasspathGlslResourceProvider.class.getResourceAsStream(path);
            if (stream == null) {
                throw new java.io.FileNotFoundException(path);
            }
            return stream;
        };
        return Optional.of(new Resource(FallbackGlslPackResources.INSTANCE, supplier));
    }
}
