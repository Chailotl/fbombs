package com.chailotl.fbombs.item;

import com.chailotl.fbombs.entity.DynamiteStickEntity;
import com.chailotl.fbombs.init.FBombsCriteria;
import com.chailotl.fbombs.init.FBombsTags;
import com.chailotl.fbombs.util.ItemStackHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class DynamiteStickItem extends Item {
    public DynamiteStickItem(Item.Settings settings) {
        super(settings);
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

        if (offStack == null || !(offStack.isIn(FBombsTags.Items.IGNITES_TNT))) {
            return super.use(world, user, hand);
        }

        if (!(world instanceof ServerWorld serverWorld) || !(user instanceof ServerPlayerEntity serverPlayer)) {
            return TypedActionResult.success(user.getStackInHand(hand), true);
        }
        DynamiteStickEntity dynamiteStickEntity = new DynamiteStickEntity(world, user);
        dynamiteStickEntity.setItem(stack);
        dynamiteStickEntity.setTick(60);
        dynamiteStickEntity.setMaxBounces(5);   // remove if only ticks should apply
        dynamiteStickEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 1.2f, 1.0f);
        world.spawnEntity(dynamiteStickEntity);
        FBombsCriteria.USED_DYNAMITE_STICK.trigger(serverPlayer);

        ItemStackHelper.decrementOrDamageInNonCreative(stack, 1, serverPlayer);
        ItemStackHelper.decrementOrDamageInNonCreative(offStack, 1, serverPlayer);

        return TypedActionResult.success(user.getStackInHand(hand), true);
    }
}
