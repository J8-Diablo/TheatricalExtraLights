package com.github.dumann089.theatricalextralights.blocks;

import com.github.dumann089.theatricalextralights.util.ConfigurationCardHelper;
import dev.imabad.theatrical.blockentities.light.BaseDMXConsumerLightBlockEntity;
import dev.imabad.theatrical.blocks.light.BaseLightBlock;
import dev.imabad.theatrical.items.Items;
import dev.imabad.theatrical.util.UUIDUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Base des fixtures Extra Lights — configuration card avec wrap automatique d'univers DMX.
 */
public abstract class ExtraLightsLightBlock extends BaseLightBlock {

    protected ExtraLightsLightBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!level.isClientSide() && be instanceof BaseDMXConsumerLightBlockEntity consumerLightBlockEntity) {
            if (player.getItemInHand(hand).getItem() == Items.CONFIGURATION_CARD.get()) {
                ItemStack itemInHand = player.getItemInHand(hand);
                CompoundTag tagData = itemInHand.getOrCreateTag();
                consumerLightBlockEntity.setNetworkId(tagData.getUUID("network"));
                ConfigurationCardHelper.applyToFixture(tagData, consumerLightBlockEntity);
                itemInHand.save(tagData);
                sendConfiguredMessage(player, consumerLightBlockEntity, tagData);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    private static void sendConfiguredMessage(Player player, BaseDMXConsumerLightBlockEntity consumer,
                                              CompoundTag tagData) {
        String networkLabel = consumer.getNetworkId().equals(UUIDUtil.NULL)
                ? "—"
                : consumer.getNetworkId().toString();
        player.sendSystemMessage(Component.translatable(
                "item.configurationcard.success",
                networkLabel,
                Integer.toString(consumer.getUniverse()),
                Integer.toString(consumer.getChannelStart()),
                Integer.toString(tagData.getInt("dmxAddress"))
        ));
    }
}
