package com.github.dumann089.theatricalextralights.client;

public final class IrisCompat {

    private IrisCompat() {}

    private static final boolean IRIS_PRESENT;

    static {
        boolean found = false;
        String[] classesToProbe = {
                "net.irisshaders.iris.Iris",
                "net.coderbot.iris.Iris"
        };
        for (String cls : classesToProbe) {
            try {
                Class.forName(cls);
                found = true;
                break;
            } catch (ClassNotFoundException ignored) {}
        }
        IRIS_PRESENT = found;
    }

    public static boolean isIrisPresent() {
        return IRIS_PRESENT;
    }

    public static boolean isShadersActive() {
        if (!IRIS_PRESENT) return false;
        try {
            Class<?> pipelineManager = Class.forName("net.irisshaders.iris.pipeline.IrisRenderingPipeline");
            return (boolean) pipelineManager
                    .getMethod("isShadersEnabled")
                    .invoke(null);
        } catch (Exception ignored) {}
        try {
            Class<?> pipelineManager = Class.forName("net.coderbot.iris.pipeline.IrisRenderingPipeline");
            return (boolean) pipelineManager
                    .getMethod("isShadersEnabled")
                    .invoke(null);
        } catch (Exception ignored) {}
        try {
            Class<?> irisApi = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            Object instance = irisApi.getMethod("getInstance").invoke(null);
            return (boolean) instance.getClass()
                    .getMethod("isShaderPackInUse")
                    .invoke(instance);
        } catch (Exception ignored) {}
        try {
            Class<?> irisApi = Class.forName("net.coderbot.iris.api.v0.IrisApi");
            Object instance = irisApi.getMethod("getInstance").invoke(null);
            return (boolean) instance.getClass()
                    .getMethod("isShaderPackInUse")
                    .invoke(instance);
        } catch (Exception ignored) {}
        return false;
    }
}