package com.github.dumann089.theatricalextralights.client.blockentities;

import com.github.dumann089.theatricalextralights.client.WaterJetClientEffects;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.imabad.theatrical.blockentities.light.BaseLightBlockEntity;
import dev.imabad.theatrical.client.LazyRenderers;
import dev.imabad.theatrical.client.blockentities.FixtureRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/** Renderer Extra Lights sans faisceau parent Theatrical (évite clignotement à intensité max). */
public abstract class ExtraLightsRenderer<T extends BaseLightBlockEntity> extends FixtureRenderer<T> {

    public ExtraLightsRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRenderBeam(T blockEntity) {
        return false;
    }

    @Override
    public void beforeRenderBeam(T blockEntity, PoseStack poseStack, VertexConsumer vertexConsumer,
                                 MultiBufferSource multiBufferSource, Direction facing, float partialTicks,
                                 boolean isFlipped, BlockState blockState, boolean isHanging, int packedLight,
                                 int packedOverlay) {
        if (blockEntity.getIntensity() <= 0 || !WaterJetClientEffects.isWaterJetFixture(blockEntity)) {
            return;
        }
        LazyRenderers.addLazyRender(new LazyRenderers.LazyRenderer() {
            @Override
            public void render(MultiBufferSource.BufferSource bufferSource, PoseStack lazyPoseStack,
                               Camera camera, float partialTick) {
                if (Minecraft.getInstance().isPaused()) {
                    return;
                }
                WaterJetClientEffects.spawnFromBeam(blockEntity, partialTick);
            }

            @Override
            public Vec3 getPos(float partialTick) {
                return blockEntity.getBlockPos().getCenter();
            }
        });
    }
}
