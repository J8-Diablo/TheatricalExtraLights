package com.github.dumann089.theatricalextralights.blockentities;

import dev.imabad.theatrical.api.DynamicLightProvider;
import dev.imabad.theatrical.lighting.LightManager;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

import java.util.function.IntSupplier;

/**
 * Dynamic light emitter for one zone of the {@link AtomicStrobeBlockEntity}.
 *
 * Theatrical's light system tracks {@link DynamicLightProvider} instances and
 * each instance contributes its own luminance / position to the world light
 * calculation. By registering 17 of these (one per RGB zone + one per bar
 * segment), each zone becomes its own physical light source even though all
 * 17 share a parent block entity.
 */
public class AtomicZoneLight implements DynamicLightProvider {

    private final AtomicStrobeBlockEntity parent;
    private final float offsetX, offsetY, offsetZ;
    private final IntSupplier luminanceSupplier;
    private final IntSupplier colourSupplier;

    // Chunk-rebuild tracking: when luminance or spread changes we need to
    // invalidate the cached chunk lightmaps around this emitter, otherwise the
    // world stays dark until something else triggers a rebuild.
    private int prevLuminance = 0;
    private float prevSpread = 0;
    private LongOpenHashSet trackedLitChunkPos = new LongOpenHashSet();

    public AtomicZoneLight(AtomicStrobeBlockEntity parent,
                           float offsetX, float offsetY, float offsetZ,
                           IntSupplier luminanceSupplier,
                           IntSupplier colourSupplier) {
        this.parent = parent;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.luminanceSupplier = luminanceSupplier;
        this.colourSupplier = colourSupplier;
    }

    @Override
    public BlockPos getOwnerPos() {
        return parent.getBlockPos();
    }

    @Override
    public Vector3f getLightPos() {
        BlockPos p = parent.getBlockPos();
        return new Vector3f(p.getX() + offsetX, p.getY() + offsetY, p.getZ() + offsetZ);
    }

    @Override
    public Level getLightWorld() {
        return parent.getLevel();
    }

    @Override
    public int getLightLuminance() {
        // 0–255 brightness → 0–15 Minecraft light level
        int b = luminanceSupplier.getAsInt();
        return (int) ((Math.min(255, Math.max(0, b)) / 255f) * 15f);
    }

    @Override
    public int getLightColour() {
        int luminance = getLightLuminance();
        return (luminance << 24) | (colourSupplier.getAsInt() & 0xFFFFFF);
    }

    @Override
    public float getLightSpread() {
        // Each zone is a small point-source LED, not a focused beam. Keeping
        // the per-zone spread tight (≈1 block) prevents the 17 emitters from
        // collectively flooding light far beyond the fixture's own panel area.
        return 1.5f;
    }

    @Override
    public void resetLight() {
        // No per-emitter state to reset; the parent BE handles trace state.
    }

    @Override
    public void lightTick() {
        // Stateless emitter — luminance/colour are recomputed on each query.
    }

    @Override
    public boolean shouldUpdateLight() {
        return LightManager.shouldUpdateDynamicLight()
                && parent.emitsLight()
                && getLightWorld() != null
                && getLightWorld().isClientSide;
    }

    @Override
    public boolean updateDynamicLight(LevelRenderer renderer) {
        int luminance = getLightLuminance();
        float spread = getLightSpread();
        if (luminance == prevLuminance && spread == prevSpread) {
            return false;
        }
        prevLuminance = luminance;
        prevSpread = spread;
        // Mark surrounding chunks dirty so they re-bake lightmaps with this
        // emitter's contribution. Replicates the 2×2×2 pattern from the base
        // BE's theatricalLightHandler around our own position.
        LongOpenHashSet newPos = new LongOpenHashSet();
        Vector3f lp = getLightPos();
        BlockPos here = BlockPos.containing(lp.x, lp.y, lp.z);
        if (luminance > 0) {
            ChunkPos entityChunkPos = new ChunkPos(here);
            BlockPos.MutableBlockPos chunkPos = new BlockPos.MutableBlockPos(
                    entityChunkPos.x,
                    SectionPos.blockToSectionCoord(here.getY()),
                    entityChunkPos.z);
            LightManager.scheduleChunkRebuild(renderer, chunkPos);
            LightManager.updateTrackedChunks(chunkPos, trackedLitChunkPos, newPos);
            net.minecraft.core.Direction dirX = (here.getX() & 15) >= 8
                    ? net.minecraft.core.Direction.EAST : net.minecraft.core.Direction.WEST;
            net.minecraft.core.Direction dirY = (here.getY() & 15) >= 8
                    ? net.minecraft.core.Direction.UP : net.minecraft.core.Direction.DOWN;
            net.minecraft.core.Direction dirZ = (here.getZ() & 15) >= 8
                    ? net.minecraft.core.Direction.SOUTH : net.minecraft.core.Direction.NORTH;
            for (int i = 0; i < 7; i++) {
                if (i % 4 == 0) {
                    chunkPos.move(dirX);
                } else if (i % 4 == 1) {
                    chunkPos.move(dirZ);
                } else if (i % 4 == 2) {
                    chunkPos.move(dirX.getOpposite());
                } else {
                    chunkPos.move(dirZ.getOpposite());
                    chunkPos.move(dirY);
                }
                LightManager.scheduleChunkRebuild(renderer, chunkPos);
                LightManager.updateTrackedChunks(chunkPos, trackedLitChunkPos, newPos);
            }
        }
        scheduleTrackedChunksRebuild(renderer);
        trackedLitChunkPos = newPos;
        return true;
    }

    @Override
    public void scheduleTrackedChunksRebuild(LevelRenderer renderer) {
        if (trackedLitChunkPos == null) return;
        for (long pos : trackedLitChunkPos) {
            LightManager.scheduleChunkRebuild(renderer, pos);
        }
    }
}
