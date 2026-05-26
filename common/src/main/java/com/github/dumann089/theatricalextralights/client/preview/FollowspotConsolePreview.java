package com.github.dumann089.theatricalextralights.client.preview;

import dev.imabad.theatrical.blockentities.light.BaseLightBlockEntity;
import dev.imabad.theatrical.blocks.HangableBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Viseur followspot — raycast depuis la tête optique + réticule.
 * Un rendu POV vidéo complet (RenderTarget) pourra être ajouté ultérieurement.
 */
public final class FollowspotConsolePreview {

    private FollowspotConsolePreview() {
    }

    public static void render(GuiGraphics graphics, int x, int y, int width, int height, BaseLightBlockEntity fixture) {
        if (fixture.getLevel() == null) {
            return;
        }

        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, 0xFF101018);

        Vec3 origin = getBeamOrigin(fixture);
        Vec3 direction = getBeamDirection(fixture);
        double reach = fixture.getFixture().getLightRadius() * 4.0;
        Vec3 end = origin.add(direction.scale(reach));

        Minecraft minecraft = Minecraft.getInstance();
        BlockHitResult hit = fixture.getLevel().clip(new ClipContext(
                origin,
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                minecraft.player
        ));

        int cx = x + width / 2;
        int cy = y + height / 2;
        int cross = 6;
        int crossColor = 0xCCFFFFFF;
        graphics.hLine(cx - cross, cx + cross, cy, crossColor);
        graphics.vLine(cx, cy - cross, cy + cross, crossColor);
        graphics.fill(cx - 1, cy - 1, cx + 2, cy + 2, 0xFFFF4040);

        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockPos hitPos = hit.getBlockPos();
            BlockState state = fixture.getLevel().getBlockState(hitPos);
            double distance = origin.distanceTo(hit.getLocation());
            graphics.drawCenteredString(minecraft.font, state.getBlock().getName(), cx, y + height - 24, 0xFFE0E0E8);
            graphics.drawCenteredString(minecraft.font,
                    Component.translatable("screen.followspot_console.preview_distance",
                            String.format("%.1f", distance)),
                    cx, y + height - 12, 0xFF9090A0);
        } else {
            graphics.drawCenteredString(minecraft.font,
                    Component.translatable("screen.followspot_console.preview_open"),
                    cx, y + height - 16, 0xFF707080);
        }
    }

    private static Vec3 getBeamOrigin(BaseLightBlockEntity fixture) {
        BlockPos pos = fixture.getBlockPos();
        float[] beam = fixture.getFixture().getBeamStartPosition();
        double ox = pos.getX() + beam[0];
        double oy = pos.getY() + beam[1];
        double oz = pos.getZ() + beam[2];
        return new Vec3(ox, oy, oz);
    }

    private static Vec3 getBeamDirection(BaseLightBlockEntity fixture) {
        float panRad = (float) Math.toRadians(fixture.getPan());
        float tiltRad = (float) Math.toRadians(-fixture.getTilt());
        Direction facing = fixture.getBlockState().getValue(HangableBlock.FACING);
        float baseYaw = facing.toYRot();

        float yaw = (float) Math.toRadians(-baseYaw + panRad);
        float pitch = tiltRad;

        double dx = -Math.sin(yaw) * Math.cos(pitch);
        double dy = -Math.sin(pitch);
        double dz = Math.cos(yaw) * Math.cos(pitch);
        return new Vec3(dx, dy, dz).normalize();
    }
}
