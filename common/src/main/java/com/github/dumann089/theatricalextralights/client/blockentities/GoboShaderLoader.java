package com.github.dumann089.theatricalextralights.client;

// ============================================================
//  GoboShaderLoader.java  —  módulo COMMON
//
//  ESTE ARCHIVO NO IMPORTA NADA DE FORGE NI DE FABRIC.
//  Solo contiene lógica neutral que puede vivir en common.
//
//  El registro real del shader ocurre en cada plataforma:
//    Forge  → TheatricalExtraLightsForge   (onRegisterShaders)
//    Fabric → TheatricalExtraLightsClientFabric (CoreShaderRegistrationCallback)
//
//  Ambas plataformas llaman a GoboShaderLoader.onShaderRegistered()
//  como callback, que a su vez llama a GoboGPUProjector.setShaderInstance().
// ============================================================

import com.github.dumann089.theatricalextralights.client.blockentities.GoboGPUProjector;
import net.minecraft.client.renderer.ShaderInstance;

/**
 * Punto de entrada neutral (sin dependencias de plataforma) para
 * recibir el shader compilado y reenviarlo al GoboGPUProjector.
 */
public class GoboShaderLoader {

    /**
     * Llamado desde Forge y Fabric como referencia de método (method reference)
     * en el callback de registro de shaders.
     *
     * <p>Forge (TheatricalExtraLightsForge.java):
     * <pre>
     *   event.registerShader(
     *       new ShaderInstance(..., "gobo_worldspace", POSITION_TEX),
     *       GoboShaderLoader::onShaderRegistered   // ← aquí
     *   );
     * </pre>
     *
     * <p>Fabric (TheatricalExtraLightsClientFabric.java):
     * <pre>
     *   context.register(
     *       new ResourceLocation("theatricalextralights", "gobo_worldspace"),
     *       POSITION_TEX,
     *       GoboShaderLoader::onShaderRegistered   // ← aquí
     *   );
     * </pre>
     *
     * @param shader instancia compilada de gobo_worldspace
     */
}