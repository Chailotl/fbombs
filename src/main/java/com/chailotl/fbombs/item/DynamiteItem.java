package com.chailotl.fbombs.item;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.entity.DynamiteEntity;
import com.chailotl.fbombs.entity.util.DynamiteEntityProvider;
import com.chailotl.fbombs.init.FBombsCriteria;
import com.chailotl.fbombs.init.FBombsTags;
import com.chailotl.fbombs.util.ItemStackHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class DynamiteItem extends Item {
    protected final DynamiteEntityProvider dynamiteEntityProvider;

    public DynamiteItem(DynamiteEntityProvider dynamiteEntityProvider, Item.Settings settings) {
        super(settings);
        this.dynamiteEntityProvider = dynamiteEntityProvider;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(Text.translatable("item.fbombs.dynamite_stick.tooltip"));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getMainHandStack();
        ItemStack offStack = user.getOffHandStack();

        //TODO: [ShiroJR] make it switchable in hands

        if (!offStack.isIn(FBombsTags.Items.IGNITES_TNT)) {
            return super.use(world, user, hand);
        }

        if (!world.isClient) {
            DynamiteEntity dynamiteEntity = dynamiteEntityProvider.spawn(world, user);
            dynamiteEntity.setItem(stack);
            dynamiteEntity.setVelocity(user, user.getPitch(), user.getYaw(), -10.0f, 0.75f, 1.0f);
            world.spawnEntity(dynamiteEntity);

            FBombsCriteria.USED_DYNAMITE.trigger((ServerPlayerEntity) user);
        }

        FBombs.streamEntries(Registries.ITEM).forEach(item -> {
            if (item instanceof DynamiteItem) {
                user.getItemCooldownManager().set(item, 10);
            }
        });
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        ItemStackHelper.decrementOrDamageInNonCreative(stack, 1, user);
        ItemStackHelper.decrementOrDamageInNonCreative(offStack, 1, user);
        return TypedActionResult.success(user.getStackInHand(hand), world.isClient());
    }
}
