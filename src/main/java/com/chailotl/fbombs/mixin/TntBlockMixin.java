package com.chailotl.fbombs.mixin;

import com.chailotl.fbombs.block.SplitTntBlock;
import com.chailotl.fbombs.init.FBombsTags;
import com.chailotl.fbombs.util.ItemStackHelper;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.TntBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TntBlock.class)
public class TntBlockMixin {
    @Inject(method = "onUseWithItem", at = @At("HEAD"), cancellable = true)
    private void splitTntBlock(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player,
                               Hand hand, BlockHitResult hit, CallbackInfoReturnable<ItemActionResult> cir) {
        if (stack.isIn(FBombsTags.Items.SPLITS_TNT)) {
            if (player instanceof ServerPlayerEntity serverPlayer && world instanceof ServerWorld serverWorld) {
                ItemStackHelper.decrementOrDamageInNonCreative(stack, 1, serverPlayer);
                SplitTntBlock.removeSplit(serverWorld, pos, state, hit);
            }
            cir.setReturnValue(ItemActionResult.SUCCESS);
        }
    }

    @ModifyExpressionValue(method = "onUseWithItem", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE", ordinal = 0,
                            target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"

                    ),
                    to = @At(
                            value = "INVOKE", ordinal = 1,
                            target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z",
                            shift = At.Shift.AFTER)
            )
    )
    private boolean replaceIgniteItems(boolean original, @Local(argsOnly = true) ItemStack stack) {
        return stack.isIn(FBombsTags.Items.IGNITES_TNT);
    }
}
