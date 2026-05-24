package com.github.dumann089.theatricalextralights.client.blockentities;

import com.github.dumann089.theatricalextralights.blockentities.interfaces.HasGobo;
import com.github.dumann089.theatricalextralights.config.TheatricalExtraLightsConfig;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.imabad.theatrical.blocks.HangableBlock;
import dev.imabad.theatrical.client.LazyRenderers;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GoboGPUProjector {

    private static final float RAYCAST_SKIP_RADIUS = 2.0f;
    private static final Direction[] DIRECTIONS = Direction.values();

    // ── SINGLE ALLOCATION STRUCTURES (ZERO ALLOCATIONS IN DYNAMIC BUFFERS) ──
    private final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
    private final org.joml.Vector3d lensOrigin = new org.joml.Vector3d();
    private final org.joml.Vector3d beamDir    = new org.joml.Vector3d();
    private final org.joml.Vector3d axisU      = new org.joml.Vector3d();
    private final org.joml.Vector3d axisV      = new org.joml.Vector3d();

    private final float[] vertexUV_U = new float[4];
    private final float[] vertexUV_V = new float[4];

    public <T extends BlockEntity & HasGobo> void render(
            T be, MultiBufferSource multiBufferSource, Direction facing, float partialTicks,
            boolean isFlipped, BlockState blockState, boolean isHanging, Vec3 localLensOffset,
            float[] panPivot, float[] tiltPivot, float[] structuralTransform) {

        float intensity01 = be.getPartialIntensity(partialTicks) / 255f;
        if (intensity01 <= 0f) return;

        int colour = be.getColour() == 0 ? 0xFFFFFF : be.getColour();
        float panDeg  = be.getPartialPanDeg(partialTicks);
        float tiltDeg = be.getPartialTiltDeg(partialTicks);
        float zoom    = be.getPartialZoom(partialTicks);
        float coneHalfAngle = 2.5f + (zoom / 255f) * 12.5f;

        RaycastResult ray = performRaycast(be, panDeg, tiltDeg, facing, blockState, isHanging, isFlipped, localLensOffset, panPivot, tiltPivot, structuralTransform);
        if (!ray.hadHit || ray.hitPos == null || ray.hitNormal == null || ray.beamDir == null) return;

        final int finalColour = colour;
        final float finalIntensity = intensity01;
        final float finalHalfAngle = coneHalfAngle;
        final float finalGoboRot = be.getGoboRotation();
        final int finalGoboSlot = be.getGobo();
        final RaycastResult finalRay = ray;
        final net.minecraft.world.level.Level level = be.getLevel();
        final BlockPos bePos = be.getBlockPos();

        // Initialization of the projector base in analytical global space
        this.lensOrigin.set(finalRay.origin.x, finalRay.origin.y, finalRay.origin.z);
        this.beamDir.set(finalRay.beamDir.x, finalRay.beamDir.y, finalRay.beamDir.z).normalize();

        this.axisU.set(this.beamDir).cross(Math.abs(this.beamDir.y) > 0.9 ? 1.0 : 0.0, Math.abs(this.beamDir.y) > 0.9 ? 0.0 : 1.0, 0.0).normalize();
        this.axisV.set(this.beamDir).cross(this.axisU).normalize();

        if (Math.abs(finalGoboRot) > 0.001f) {
            double rad = Math.toRadians(finalGoboRot);
            double cos = Math.cos(rad); double sin = Math.sin(rad);
            double ux = this.axisU.x; double uy = this.axisU.y; double uz = this.axisU.z;
            double vx = this.axisV.x; double vy = this.axisV.y; double vz = this.axisV.z;

            this.axisU.set(ux * cos + vx * sin, uy * cos + vy * sin, uz * cos + vz * sin);
            this.axisV.set(-ux * sin + vx * cos, -uy * sin + vy * cos, -uz * sin + vz * cos);
        }

        final double lOx = this.lensOrigin.x; final double lOy = this.lensOrigin.y; final double lOz = this.lensOrigin.z;
        final double bDx = this.beamDir.x;    final double bDy = this.beamDir.y;    final double bDz = this.beamDir.z;
        final double aUx = this.axisU.x;      final double aUy = this.axisU.y;      final double aUz = this.axisU.z;
        final double aVx = this.axisV.x;      final double aVy = this.axisV.y;      final double aVz = this.axisV.z;

        LazyRenderers.addLazyRender(new LazyRenderers.LazyRenderer() {
            @Override
            public void render(MultiBufferSource.BufferSource bufferSource, PoseStack poseStack, Camera camera, float partialTick) {
                poseStack.pushPose();
                poseStack.translate(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z);
                Matrix4f matrix = poseStack.last().pose();

                ResourceLocation texture = getGoboTexture(finalGoboSlot);
                VertexConsumer vc = bufferSource.getBuffer(getVanillaGoboRenderType(texture));

                double hitRadius = Math.tan(Math.toRadians(finalHalfAngle)) * finalRay.length;

                // ── OPTIMIZATION 1: ULTRA-SMART DYNAMIC SCAN RADIUS ADJUSTMENT ──
                int blockRadius;
                if (zoom < 40f)       blockRadius = Math.min(Mth.ceil(hitRadius) + 1, 3);
                else if (zoom < 110f) blockRadius = Math.min(Mth.ceil(hitRadius) + 1, 5);
                else                  blockRadius = Math.min(Mth.ceil(hitRadius) + 1, 7);

                int r = (finalColour >> 16) & 0xFF; int g = (finalColour >> 8) & 0xFF; int b = finalColour & 0xFF;
                int a = (int) (Math.min(1f, finalIntensity * 1.5f) * 255f);

                BlockPos centerBlock = BlockPos.containing(finalRay.hitPos);
                double tanHalfAngle = Math.tan(Math.toRadians(finalHalfAngle));
                float maxLen = TheatricalExtraLightsConfig.getLaserBeamLength();

                for (int x = -blockRadius; x <= blockRadius; x++) {
                    for (int y = -blockRadius; y <= blockRadius; y++) {
                        for (int z = -blockRadius; z <= blockRadius; z++) {
                            mutablePos.set(centerBlock.getX() + x, centerBlock.getY() + y, centerBlock.getZ() + z);

                            // Center coordinates of the evaluated block
                            double bX = mutablePos.getX(); double bY = mutablePos.getY(); double bZ = mutablePos.getZ();
                            double blockCenterX = bX + 0.5; double blockCenterY = bY + 0.5; double blockCenterZ = bZ + 0.5;

                            // ── OPTIMIZATION 2: CONE INTERSECTION SPATIAL PRUNING ON CPU (AVOIDS SCANNING A FULL CUBE) ──
                            double vcx = blockCenterX - lOx; double vcy = blockCenterY - lOy; double vcz = blockCenterZ - lOz;
                            double tProj = vcx * bDx + vcy * bDy + vcz * bDz;

                            if (tProj <= 0.05 || tProj > maxLen + 1.0) continue;

                            double perpDistSq = (vcx * vcx + vcy * vcy + vcz * vcz) - (tProj * tProj);
                            double coneRadAtBlock = tanHalfAngle * tProj;
                            double exactTolerance = coneRadAtBlock + 0.866; // 0.866 is the exact radius of the block's inscribed sphere

                            if (perpDistSq > exactTolerance * exactTolerance) continue; // Immediate mathematical discarding without touching the world

                            BlockState state = level.getBlockState(mutablePos);
                            if (state.isAir()) continue;

                            for (Direction dir : DIRECTIONS) {
                                int sX = dir.getStepX(); int sY = dir.getStepY(); int sZ = dir.getStepZ();

                                // Analytical Backface Culling via direct scalar dot product
                                double dotNormal = sX * bDx + sY * bDy + sZ * bDz;
                                if (dotNormal >= -0.01) continue;

                                processAndRenderContinuousFace(vc, matrix, bX, bY, bZ, dir, sX, sY, sZ, lOx, lOy, lOz, bDx, bDy, bDz, aUx, aUy, aUz, aVx, aVy, aVz, tanHalfAngle, r, g, b, a);
                            }
                        }
                    }
                }
                poseStack.popPose();
            }

            @Override
            public Vec3 getPos(float partialTick) { return bePos.getCenter(); }
        });
    }

    private void processAndRenderContinuousFace(
            VertexConsumer vc, Matrix4f matrix, double bx, double by, double bz, Direction face, int sx, int sy, int sz,
            double lOx, double lOy, double lOz, double bDx, double bDy, double bDz,
            double aUx, double aUy, double aUz, double aVx, double aVy, double aVz,
            double tanHalfAngle, int r, int g, int b, int a) {

        double minX = bx, minY = by, minZ = bz;
        double maxX = bx + 1.0, maxY = by + 1.0, maxZ = bz + 1.0;

        double x0, y0, z0, x1, y1, z1, x2, y2, z2, x3, y3, z3;

        switch (face) {
            case DOWN -> { x0=minX; y0=minY; z0=minZ; x1=maxX; y1=minY; z1=minZ; x2=maxX; y2=minY; z2=maxZ; x3=minX; y3=minY; z3=maxZ; }
            case UP   -> { x0=minX; y0=maxY; z0=maxZ; x1=maxX; y1=maxY; z1=maxZ; x2=maxX; y2=maxY; z2=minZ; x3=minX; y3=maxY; z3=minZ; }
            case NORTH-> { x0=maxX; y0=maxY; z0=minZ; x1=maxX; y1=minY; z1=minZ; x2=minX; y2=minY; z2=minZ; x3=minX; y3=maxY; z3=minZ; }
            case SOUTH-> { x0=minX; y0=maxY; z0=maxZ; x1=minX; y1=minY; z1=maxZ; x2=maxX; y2=minY; z2=maxZ; x3=maxX; y3=maxY; z3=maxZ; }
            case WEST -> { x0=minX; y0=maxY; z0=minZ; x1=minX; y1=minY; z1=minZ; x2=minX; y2=minY; z2=maxZ; x3=minX; y3=maxY; z3=maxZ; }
            default   -> { x0=maxX; y0=maxY; z0=maxZ; x1=maxX; y1=minY; z1=maxZ; x2=maxX; y2=minY; z2=minZ; x3=maxX; y3=maxY; z3=minZ; } // EAST
        }

        // ── OPTIMIZATION 3: NUMERICAL STABILIZATION FIXING THE APERTURE FLOOR AT 0.15 ──

        // Vertex 0
        double t0x = x0 - lOx; double t0y = y0 - lOy; double t0z = z0 - lOz; double d0 = t0x * bDx + t0y * bDy + t0z * bDz;
        double rad0 = tanHalfAngle * d0; if (rad0 < 0.15) rad0 = 0.15;
        double o0x = t0x - bDx * d0; double o0y = t0y - bDy * d0; double o0z = t0z - bDz * d0;
        vertexUV_U[0] = (float) (0.5 + (o0x * aUx + o0y * aUy + o0z * aUz) / (2.0 * rad0));
        vertexUV_V[0] = (float) (0.5 + (o0x * aVx + o0y * aVy + o0z * aVz) / (2.0 * rad0));

        // Vertex 1
        double t1x = x1 - lOx; double t1y = y1 - lOy; double t1z = z1 - lOz; double d1 = t1x * bDx + t1y * bDy + t1z * bDz;
        double rad1 = tanHalfAngle * d1; if (rad1 < 0.15) rad1 = 0.15;
        double o1x = t1x - bDx * d1; double o1y = t1y - bDy * d1; double o1z = t1z - bDz * d1;
        vertexUV_U[1] = (float) (0.5 + (o1x * aUx + o1y * aUy + o1z * aUz) / (2.0 * rad1));
        vertexUV_V[1] = (float) (0.5 + (o1x * aVx + o1y * aVy + o1z * aVz) / (2.0 * rad1));

        // Vertex 2
        double t2x = x2 - lOx; double t2y = y2 - lOy; double t2z = z2 - lOz; double d2 = t2x * bDx + t2y * bDy + t2z * bDz;
        double rad2 = tanHalfAngle * d2; if (rad2 < 0.15) rad2 = 0.15;
        double o2x = t2x - bDx * d2; double o2y = t2y - bDy * d2; double o2z = t2z - bDz * d2;
        vertexUV_U[2] = (float) (0.5 + (o2x * aUx + o2y * aUy + o2z * aUz) / (2.0 * rad2));
        vertexUV_V[2] = (float) (0.5 + (o2x * aVx + o2y * aVy + o2z * aVz) / (2.0 * rad2));

        // Vertex 3
        double t3x = x3 - lOx; double t3y = y3 - lOy; double t3z = z3 - lOz; double d3 = t3x * bDx + t3y * bDy + t3z * bDz;
        double rad3 = tanHalfAngle * d3; if (rad3 < 0.15) rad3 = 0.15;
        double o3x = t3x - bDx * d3; double o3y = t3y - bDy * d3; double o3z = t3z - bDz * d3;
        vertexUV_U[3] = (float) (0.5 + (o3x * aUx + o3y * aUy + o3z * aUz) / (2.0 * rad3));
        vertexUV_V[3] = (float) (0.5 + (o3x * aVx + o3y * aVy + o3z * aVz) / (2.0 * rad3));

        // ── CORRECTION: REMOVED FULL CULLING BLOCK RETURN (AVOIDS HARD CLIPPING ON EDGES) ──

        // ── MANDATORY: MANUAL CLAMPING REQUIRED PER VERTEX TO STOP TEXTURE BLEEDING ACROSS MULTIPLE BLOCKS ──
        float u0 = Mth.clamp(vertexUV_U[0], 0.0f, 1.0f); float v0 = Mth.clamp(vertexUV_V[0], 0.0f, 1.0f);
        float u1 = Mth.clamp(vertexUV_U[1], 0.0f, 1.0f); float v1 = Mth.clamp(vertexUV_V[1], 0.0f, 1.0f);
        float u2 = Mth.clamp(vertexUV_U[2], 0.0f, 1.0f); float v2 = Mth.clamp(vertexUV_V[2], 0.0f, 1.0f);
        float u3 = Mth.clamp(vertexUV_U[3], 0.0f, 1.0f); float v3 = Mth.clamp(vertexUV_V[3], 0.0f, 1.0f);

        // Stable micrometric anti Z-Fighting offset
        double offX = sx * 0.007; double offY = sy * 0.007; double offZ = sz * 0.007;

        vc.vertex(matrix, (float)(x0 + offX), (float)(y0 + offY), (float)(z0 + offZ)).color(r, g, b, a).uv(u0, v0).endVertex();
        vc.vertex(matrix, (float)(x1 + offX), (float)(y1 + offY), (float)(z1 + offZ)).color(r, g, b, a).uv(u1, v1).endVertex();
        vc.vertex(matrix, (float)(x2 + offX), (float)(y2 + offY), (float)(z2 + offZ)).color(r, g, b, a).uv(u2, v2).endVertex();
        vc.vertex(matrix, (float)(x3 + offX), (float)(y3 + offY), (float)(z3 + offZ)).color(r, g, b, a).uv(u3, v3).endVertex();
    }

    private static class ClampedGoboTextureStateShard extends RenderStateShard.TextureStateShard {
        public ClampedGoboTextureStateShard(ResourceLocation resourceLocation, boolean blur, boolean mipmap) {
            super(resourceLocation, blur, mipmap);
        }

        @Override
        public void setupRenderState() {
            super.setupRenderState();
            // Ensure sampler behavior at the hardware level on the GPU
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        }
    }

    private static RenderType getVanillaGoboRenderType(ResourceLocation texture) {
        return RenderType.create("gobo_decal_industrial_clamped",
                DefaultVertexFormat.POSITION_COLOR_TEX,
                VertexFormat.Mode.QUADS,
                256,
                false,
                true,
                RenderType.CompositeState.builder()
                        .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorTexShader))
                        .setTextureState(new ClampedGoboTextureStateShard(texture, false, false))
                        .setTransparencyState(RenderStateShard.LIGHTNING_TRANSPARENCY)
                        .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                        .setCullState(RenderStateShard.NO_CULL)
                        .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                        .createCompositeState(false)
        );
    }

    private static ResourceLocation getGoboTexture(int slot) {
        if (slot <= 25) return new ResourceLocation("theatricalextralights", "textures/gobos/open.png");
        int idx = ((slot - 26) / 26) + 1; idx = Math.min(idx, 9);
        return new ResourceLocation("theatricalextralights", "textures/gobos/gobo_" + idx + ".png");
    }

    private <T extends BlockEntity & HasGobo> RaycastResult performRaycast(
            T be, float panDeg, float tiltDeg, Direction facing, BlockState blockState, boolean isHanging, boolean isFlipped,
            Vec3 localLensOffset, float[] panPivot, float[] tiltPivot, float[] structuralTransform) {

        float maxLen = TheatricalExtraLightsConfig.getLaserBeamLength();
        if (be.getLevel() == null) return new RaycastResult(maxLen, false, null, null, Vec3.ZERO, be.getBlockPos().getCenter());

        PoseStack temp = new PoseStack();
        applyFixtureOrientation(temp, facing, isFlipped, blockState, isHanging, panDeg, tiltDeg, panPivot, tiltPivot, structuralTransform);
        temp.translate(localLensOffset.x, localLensOffset.y, localLensOffset.z);

        Matrix4f m = temp.last().pose();
        org.joml.Vector4f localOrigin = new org.joml.Vector4f(0f, 0f, 0f, 1f);
        localOrigin.mul(m);

        Vec3 origin = Vec3.atLowerCornerOf(be.getBlockPos()).add(localOrigin.x, localOrigin.y, localOrigin.z);

        org.joml.Vector4f localDir = new org.joml.Vector4f(0f, 0f, -1f, 0f);
        localDir.mul(m);
        Vec3 dir = new Vec3(localDir.x, localDir.y, localDir.z).normalize();

        Vec3 end = origin.add(dir.scale(maxLen));
        Vec3 rayStart = origin;
        int safety = 24;

        while (safety-- > 0) {
            BlockHitResult hit = be.getLevel().clip(new ClipContext(rayStart, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
            if (hit.getType() == HitResult.Type.MISS) return new RaycastResult(maxLen, false, null, null, dir, origin);

            BlockState hitState = be.getLevel().getBlockState(hit.getBlockPos());
            ResourceLocation key = BuiltInRegistries.BLOCK.getKey(hitState.getBlock());

            boolean isModBlock = key != null && (key.getNamespace().equals("theatrical") || key.getNamespace().equals("theatricalextralights"));
            boolean isPassThrough = key != null && TheatricalExtraLightsConfig.isLaserPassThrough(key.toString());
            float dist = (float) hit.getLocation().distanceTo(origin);

            if (!isModBlock && !isPassThrough && dist >= RAYCAST_SKIP_RADIUS) {
                Vec3 norm = Vec3.atLowerCornerOf(hit.getDirection().getNormal());
                return new RaycastResult(Math.min(maxLen, dist), true, hit.getLocation(), norm, dir, origin);
            }
            rayStart = hit.getLocation().add(dir.scale(0.03));
            if (rayStart.distanceToSqr(end) < 1e-4) return new RaycastResult(maxLen, false, null, null, dir, origin);
        }
        return new RaycastResult(maxLen, false, null, null, dir, origin);
    }

    private static void applyFixtureOrientation(PoseStack ps, Direction facing, boolean isFlipped, BlockState blockState, boolean isHanging,
                                                float panDeg, float tiltDeg, float[] pans, float[] tilts, float[] structuralTransform) {
        ps.translate(0.5f, 0f, 0.5f);
        if (isHanging) {
            Direction hangDir = Direction.UP;
            try { hangDir = blockState.getValue(HangableBlock.HANG_DIRECTION); } catch (Exception ignored) {}
            ps.translate(0, 0.5, 0);
            if (hangDir.getAxis() != Direction.Axis.Y) {
                if (hangDir.getAxis() == Direction.Axis.Z) {
                    ps.mulPose(Axis.ZP.rotationDegrees(90));
                    ps.mulPose(hangDir == Direction.SOUTH ? Axis.XP.rotationDegrees(-90) : Axis.XP.rotationDegrees(90));
                } else {
                    ps.mulPose(Axis.ZN.rotationDegrees(-90));
                }
            }
            ps.translate(0, -0.5, 0);
        }
        ps.mulPose(Axis.YP.rotationDegrees(facing.toYRot()));
        ps.translate(-0.5f, 0f, -0.5f);

        if (isHanging) {
            ps.translate(structuralTransform[0], structuralTransform[1], structuralTransform[2]);
            ps.translate(0, -0.08, 0);
        }

        if (isFlipped) {
            ps.translate(0.5f, 0.5f, 0.5f);
            ps.mulPose(Axis.ZP.rotationDegrees(180));
            ps.translate(-0.5f, -0.5f, -0.5f);
        }

        ps.translate(pans[0], pans[1], pans[2]);
        ps.mulPose(Axis.YP.rotationDegrees(panDeg));
        ps.translate(-pans[0], -pans[1], -pans[2]);

        ps.translate(tilts[0], tilts[1], tilts[2]);
        if (isFlipped) ps.mulPose(Axis.XP.rotationDegrees(-180));
        else           ps.mulPose(Axis.XP.rotationDegrees(180));
        ps.mulPose(Axis.XP.rotationDegrees(tiltDeg));
        ps.translate(-tilts[0], -tilts[1], -tilts[2]);
    }

    static class RaycastResult {
        final float length; final boolean hadHit; final Vec3 hitPos; final Vec3 hitNormal; final Vec3 beamDir; final Vec3 origin;
        RaycastResult(float l, boolean h, Vec3 p, Vec3 n, Vec3 d, Vec3 o) {
            length = l; hadHit = h; hitPos = p; hitNormal = n; beamDir = d; origin = o;
        }
    }
}