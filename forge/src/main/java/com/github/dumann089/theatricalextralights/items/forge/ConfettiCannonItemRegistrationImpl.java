package com.github.dumann089.theatricalextralights.items.forge;

import com.github.dumann089.theatricalextralights.blocks.Blocks;
import com.github.dumann089.theatricalextralights.client.ConfettiCannonItemRenderer;
import com.github.dumann089.theatricalextralights.items.ConfettiCannonItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public class ConfettiCannonItemRegistrationImpl {
    private ConfettiCannonItemRegistrationImpl() {
    }

    public static Item create() {
        return new ConfettiCannonForgeItem(Blocks.CONFETTI_CANNON_BLOCK.get());
    }

    private static final class ConfettiCannonForgeItem extends ConfettiCannonItem {
        private ConfettiCannonForgeItem(net.minecraft.world.level.block.Block block) {
            super(block);
        }

        @Override
        public void initializeClient(Consumer<IClientItemExtensions> consumer) {
            consumer.accept(new IClientItemExtensions() {
                private ConfettiCannonItemRenderer renderer;

                @Override
                public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    if (renderer == null) {
                        Minecraft minecraft = Minecraft.getInstance();
                        renderer = new ConfettiCannonItemRenderer(
                                minecraft.getBlockEntityRenderDispatcher(),
                                minecraft.getEntityModels()
                        );
                    }
                    return renderer;
                }
            });
        }
    }
}
