package com.chailotl.fbombs.mixin;

import com.chailotl.fbombs.data.RadiationData;
import com.chailotl.fbombs.init.FBombsItemComponents;
import com.chailotl.fbombs.init.FBombsStatusEffects;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @WrapOperation(method = "onPlayerCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;insertStack(Lnet/minecraft/item/ItemStack;)Z"))
    private boolean applyContaminatedEffect(PlayerInventory instance, ItemStack stack, Operation<Boolean> original) {
        boolean insertedStack = original.call(instance, stack);
        boolean isContaminated = stack.getOrDefault(FBombsItemComponents.CONTAMINATION, 0f) > RadiationData.SAFE_CPS_LEVEL;
        if (insertedStack && isContaminated) {
            instance.player.addStatusEffect(new StatusEffectInstance(FBombsStatusEffects.RADIATION_POISONING, 5), (ItemEntity) (Object) this);
        }
        return insertedStack;
    }
}
