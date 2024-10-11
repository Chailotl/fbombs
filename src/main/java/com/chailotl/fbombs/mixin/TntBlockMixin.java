package com.chailotl.fbombs.mixin;

import com.chailotl.fbombs.init.FBombsBlocks;
import com.chailotl.fbombs.init.FBombsTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.TntBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TntBlock.class)
public class TntBlockMixin {
    @Inject(method = "onUseWithItem", at = @At("HEAD"), cancellable = true)
    private void splitTntBlock(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player,
                               Hand hand, BlockHitResult hit, CallbackInfoReturnable<ItemActionResult> cir) {
        if (stack.isIn(FBombsTags.SPLITS_TNT)) {
            if (world instanceof ServerWorld serverWorld && player instanceof ServerPlayerEntity serverPlayer) {
                if (stack.isDamageable()) {
                    stack.damage(1, serverWorld, serverPlayer, item -> {
                    });
                }
                world.setBlockState(pos, FBombsBlocks.SPLIT_TNT.getFirstSplitState(player.getHorizontalFacing().getOpposite()));
                world.playSound(null, pos, SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.BLOCKS, 1f, 1f);
            }
            //TODO: [ShiroJR] add advancements / stats ?
        }
        cir.setReturnValue(ItemActionResult.SUCCESS);
    }
}
