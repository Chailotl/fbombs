package com.chailotl.fbombs.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;

public class ItemStackHelper {
    public static void decrementOrDamageInNonCreative(ItemStack stack, int amount, @NotNull PlayerEntity player) {
        if (player.isCreative()) return;
        if (!(player.getWorld() instanceof ServerWorld serverWorld)) return;
        if (!(player instanceof ServerPlayerEntity serverPlayer)) return;

        if (stack.isDamageable()) {
            stack.damage(amount, serverWorld, serverPlayer, item -> {
            });
        } else {
            stack.decrement(amount);
        }
    }
}
