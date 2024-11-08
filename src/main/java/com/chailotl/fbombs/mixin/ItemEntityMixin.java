package com.chailotl.fbombs.mixin;

import com.chailotl.fbombs.contamination.ContaminationHandler;
import com.chailotl.fbombs.data.RadiationCategory;
import com.chailotl.fbombs.init.FBombsItemComponents;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @WrapOperation(method = "onPlayerCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;insertStack(Lnet/minecraft/item/ItemStack;)Z"))
    private boolean applyContaminatedEffect(PlayerInventory instance, ItemStack stack, Operation<Boolean> original) {
        ItemStack stackHolder = stack.copy();
        boolean insertedStack = original.call(instance, stack);
        float contamination = stackHolder.getOrDefault(FBombsItemComponents.CONTAMINATION, 0f);
        boolean isContaminated = contamination > RadiationCategory.SAFE.getMaxCps();
        if (insertedStack && isContaminated && !instance.player.getWorld().isClient()) {
            ContaminationHandler.applyEffectIfMissing(instance.player, contamination);
        }
        return insertedStack;
    }
}
