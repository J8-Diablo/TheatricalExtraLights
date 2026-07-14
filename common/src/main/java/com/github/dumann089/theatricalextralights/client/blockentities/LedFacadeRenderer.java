package com.github.dumann089.theatricalextralights.client.blockentities;

import com.github.dumann089.theatricalextralights.TheatricalExtraLights;
import com.github.dumann089.theatricalextralights.blockentities.LedFacadeBlockEntity;
import com.github.dumann089.theatricalextralights.client.render.LedFacadeRenderTypes;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Rendu de la façade LED, calqué sur le pipeline des panneaux du mod :
 * <ul>
 *   <li><b>LED éteints</b> : {@link RenderType#entityCutout} avec la lumière du monde ({@code
 *       packedLight}) → se comportent comme n'importe quelle surface de bloc (sombres/invisibles
 *       dans le noir, visibles au jour), jamais auto-éclairés.</li>
 *   <li><b>LED allumés</b> : {@link RenderType#entityTranslucentEmissive} (plein-bright, glow) +
 *       émission de lumière Shimmer (via le BlockEntity).</li>
 * </ul>
 * Sans pixel dessiné, une roue crantée s'affiche. Les 4 coins des quads sont placés explicitement
 * en coordonnées de bloc selon {@code FACING} (orientation déterministe, jamais en miroir, face
 * côté joueur). Deux textures dynamiques (éteinte / allumée) régénérées à la demande.
 */
public class LedFacadeRenderer extends ExtraLightsRenderer<LedFacadeBlockEntity> {

    private static final int STALE_FRAMES = 600;
    private static final float OUT = 0.02f;
    private static final float LIT_OUT = 0.026f;
    private static final int OFF_GREY = 0xFF2A2A2A; // gris LED éteint (natif ABGR, symétrique)
    private static final ResourceLocation GEAR_TEXTURE =
            new ResourceLocation(TheatricalExtraLights.MOD_ID, "textures/gui/led_facade_gear.png");

    private final Map<BlockPos, Panel> panels = new HashMap<>();
    private long frame;

    public LedFacadeRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(LedFacadeBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        Direction front = be.getBlockState().getValue(HorizontalDirectionalBlock.FACING).getOpposite();
        Matrix4f matrix = poseStack.last().pose();
        int worldLight = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos());

        if (be.hasActivePixels()) {
            Panel panel = acquirePanel(be);
            // Éteints : rendu bloc standard (cutout + lumière du monde + ombrage). U inversé = miroir éditeur.
            VertexConsumer off = multiBufferSource.getBuffer(RenderType.entityCutout(panel.offLocation));
            emitFace(off, matrix, worldLight, true, front, OUT, 0f, 0f, 1f, 1f, 1f, 0f, 0f, 1f);
            // Allumés : plein-bright SANS ombrage diffus (couleurs vives, blanc = blanc pur), devant.
            VertexConsumer lit = multiBufferSource.getBuffer(LedFacadeRenderTypes.surface(panel.litLocation));
            emitFace(lit, matrix, LightTexture.FULL_BRIGHT, false, front, LIT_OUT, 0f, 0f, 1f, 1f, 1f, 0f, 0f, 1f);
        } else {
            VertexConsumer vc = multiBufferSource.getBuffer(RenderType.entityCutout(GEAR_TEXTURE));
            emitFace(vc, matrix, worldLight, true, front, OUT, 0.34f, 0.34f, 0.66f, 0.66f, 0f, 0f, 1f, 1f);
        }

        sweep();
    }

    /**
     * Émet le quad sur la face avant ({@code facing}), avancé de {@code depth}, au format entité
     * (POSITION_COLOR_TEX_OVERLAY_LIGHT_NORMAL). ({@code pu},{@code pv}) = position en fraction de
     * face (pv=0 en haut) ; ({@code tu},{@code tv}) = UV de texture.
     */
    private void emitFace(VertexConsumer vc, Matrix4f m, int light, boolean entityFormat, Direction facing, float depth,
                          float pu0, float pv0, float pu1, float pv1,
                          float tu0, float tv0, float tu1, float tv1) {
        float ox, oy, oz, rx, rz, nx, nz;
        switch (facing) {
            case SOUTH -> { ox = 0;          oy = 1; oz = 1 - depth; rx = 1;  rz = 0;  nx = 0;  nz = 1; }
            case WEST  -> { ox = depth;      oy = 1; oz = 0;         rx = 0;  rz = 1;  nx = -1; nz = 0; }
            case EAST  -> { ox = 1 - depth;  oy = 1; oz = 1;         rx = 0;  rz = -1; nx = 1;  nz = 0; }
            default    -> { ox = 1;          oy = 1; oz = depth;     rx = -1; rz = 0;  nx = 0;  nz = -1; } // NORTH
        }
        // point(pu,pv) = O + R*pu + (0,-1,0)*pv
        vertex(vc, m, light, entityFormat, nx, nz, ox + rx * pu0, oy - pv0, oz + rz * pu0, tu0, tv0); // haut-gauche
        vertex(vc, m, light, entityFormat, nx, nz, ox + rx * pu1, oy - pv0, oz + rz * pu1, tu1, tv0); // haut-droite
        vertex(vc, m, light, entityFormat, nx, nz, ox + rx * pu1, oy - pv1, oz + rz * pu1, tu1, tv1); // bas-droite
        vertex(vc, m, light, entityFormat, nx, nz, ox + rx * pu0, oy - pv1, oz + rz * pu0, tu0, tv1); // bas-gauche
    }

    /**
     * {@code entityFormat=true} → format entité (POSITION_COLOR_TEX_OVERLAY_LIGHT_NORMAL, ombrage
     * diffus) pour les éteints ; {@code false} → POSITION_COLOR_TEX_LIGHTMAP (sans ombrage) pour
     * les allumés, afin que les couleurs vives restent pleines.
     */
    private void vertex(VertexConsumer vc, Matrix4f m, int light, boolean entityFormat, float nx, float nz,
                        float x, float y, float z, float u, float v) {
        var vb = vc.vertex(m, x, y, z).color(255, 255, 255, 255).uv(u, v);
        if (entityFormat) {
            vb.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(nx, 0f, nz).endVertex();
        } else {
            vb.uv2(light).endVertex();
        }
    }

    // Requis par la classe abstraite mais inutilisés (le rendu passe entièrement par render()).
    @Override
    public void renderModel(LedFacadeBlockEntity be, PoseStack poseStack, VertexConsumer vertexConsumer,
                            Direction facing, float partialTicks, boolean isFlipped, BlockState blockState,
                            boolean isHanging, int packedLight, int packedOverlay) {
    }

    @Override
    public void preparePoseStack(LedFacadeBlockEntity be, PoseStack poseStack, Direction facing,
                                 float partialTicks, boolean isFlipped, BlockState blockState, boolean isHanging) {
    }

    // ─── Textures dynamiques par façade (éteinte + allumée) ────────────────────

    private Panel acquirePanel(LedFacadeBlockEntity be) {
        BlockPos pos = be.getBlockPos();
        int res = be.getResolution();
        Panel panel = panels.get(pos);
        if (panel == null || panel.resolution != res) {
            if (panel != null) {
                release(panel);
            }
            panel = new Panel();
            panel.resolution = res;
            String key = "led_facade/" + Long.toHexString(pos.asLong());
            panel.offLocation = new ResourceLocation(TheatricalExtraLights.MOD_ID, key + "_off");
            panel.litLocation = new ResourceLocation(TheatricalExtraLights.MOD_ID, key + "_lit");
            panel.offTexture = new DynamicTexture(res, res, false);
            panel.litTexture = new DynamicTexture(res, res, false);
            Minecraft.getInstance().getTextureManager().register(panel.offLocation, panel.offTexture);
            Minecraft.getInstance().getTextureManager().register(panel.litLocation, panel.litTexture);
            panel.offSig = Integer.MIN_VALUE;
            panel.litSig = Integer.MIN_VALUE;
            panels.put(pos, panel);
        }

        int offSig = be.getResolution() * 31 + be.getActivePixels().hashCode();
        if (offSig != panel.offSig) {
            writeOff(panel, be);
            panel.offTexture.upload();
            panel.offSig = offSig;
        }
        int litSig = offSig * 31 + be.getColorVersion();
        if (litSig != panel.litSig) {
            writeLit(panel, be);
            panel.litTexture.upload();
            panel.litSig = litSig;
        }
        panel.lastAccess = frame;
        return panel;
    }

    /** Texture éteinte : gris opaque sur tout pixel dessiné, transparent ailleurs (cutout). */
    private void writeOff(Panel panel, LedFacadeBlockEntity be) {
        NativeImage image = panel.offTexture.getPixels();
        if (image == null) {
            return;
        }
        int res = panel.resolution;
        for (int y = 0; y < res; y++) {
            for (int x = 0; x < res; x++) {
                image.setPixelRGBA(x, y, be.isPixelActive(y * res + x) ? OFF_GREY : 0);
            }
        }
    }

    /** Texture allumée : couleur DMX sur les pixels éclairés, transparent ailleurs. */
    private void writeLit(Panel panel, LedFacadeBlockEntity be) {
        NativeImage image = panel.litTexture.getPixels();
        if (image == null) {
            return;
        }
        int res = panel.resolution;
        for (int y = 0; y < res; y++) {
            for (int x = 0; x < res; x++) {
                image.setPixelRGBA(x, y, litNativeColor(be, y * res + x));
            }
        }
    }

    private int litNativeColor(LedFacadeBlockEntity be, int index) {
        if (!be.isPixelActive(index)) {
            return 0;
        }
        int argb = be.getPixelArgb(index);
        if ((argb & 0xFFFFFF) == 0) {
            return 0; // éteint → transparent dans la passe allumée
        }
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        return 0xFF000000 | (b << 16) | (g << 8) | r; // ARGB → ABGR natif
    }

    private void sweep() {
        frame++;
        if (frame % 200 != 0 || panels.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<BlockPos, Panel>> it = panels.entrySet().iterator();
        while (it.hasNext()) {
            Panel panel = it.next().getValue();
            if (frame - panel.lastAccess > STALE_FRAMES) {
                release(panel);
                it.remove();
            }
        }
    }

    private void release(Panel panel) {
        var tm = Minecraft.getInstance().getTextureManager();
        if (panel.offLocation != null) tm.release(panel.offLocation);
        if (panel.litLocation != null) tm.release(panel.litLocation);
        if (panel.offTexture != null) panel.offTexture.close();
        if (panel.litTexture != null) panel.litTexture.close();
    }

    private static final class Panel {
        DynamicTexture offTexture, litTexture;
        ResourceLocation offLocation, litLocation;
        int resolution;
        int offSig, litSig;
        long lastAccess;
    }
}
