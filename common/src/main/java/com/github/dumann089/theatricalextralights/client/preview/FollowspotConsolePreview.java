package com.github.dumann089.theatricalextralights.client.preview;

import com.github.dumann089.theatricalextralights.util.FollowspotBeamHelper;
import dev.imabad.theatrical.blockentities.light.BaseLightBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Viseur followspot — raycast depuis la tête optique + réticule.
 */
public final class FollowspotConsolePreview {

    private FollowspotConsolePreview() {
    }

    public static void render(GuiGraphics graphics, int x, int y, int width, int height, BaseLightBlockEntity fixture) {
        if (fixture.getLevel() == null) {
            return;
        }

        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, 0xFF101018);

        Vec3 origin = FollowspotBeamHelper.getBeamOrigin(fixture);
        Vec3 direction = FollowspotBeamHelper.getBeamDirection(fixture, fixture.getPan(), fixture.getTilt());
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
}
