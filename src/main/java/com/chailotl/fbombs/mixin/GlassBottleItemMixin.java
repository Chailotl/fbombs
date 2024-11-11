package com.chailotl.fbombs.mixin;

import com.chailotl.fbombs.init.FBombsItems;
import com.chailotl.fbombs.init.FBombsTags;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GlassBottleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GlassBottleItem.class)
public abstract class GlassBottleItemMixin {
    @Shadow protected abstract ItemStack fill(ItemStack stack, PlayerEntity player, ItemStack outputStack);

    @Inject(
        method = "use",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;"
        ),
        cancellable = true
    )
    private void getJuiceThatMakesYouExplode(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir, @Local ItemStack itemStack, @Local BlockPos blockPos) {
        if (world.getFluidState(blockPos).isIn(FBombsTags.Fluids.JUICE_THAT_MAKES_YOU_EXPLODE)) {
            world.playSound(user, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
            world.emitGameEvent(user, GameEvent.FLUID_PICKUP, blockPos);
            cir.setReturnValue(TypedActionResult.success(fill(itemStack, user, new ItemStack(FBombsItems.JUICE_THAT_MAKES_YOU_EXPLODE_BOTTLE)), world.isClient()));
        }
    }
}