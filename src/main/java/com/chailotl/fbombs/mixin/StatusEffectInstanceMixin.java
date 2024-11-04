package com.chailotl.fbombs.mixin;

import com.chailotl.fbombs.data.RadiationCategory;
import com.chailotl.fbombs.init.FBombsItemComponents;
import com.chailotl.fbombs.init.FBombsStatusEffects;
import com.chailotl.fbombs.item.HazmatArmor;
import com.chailotl.fbombs.mixin.access.StatusEffectInstanceAccessor;
import com.chailotl.fbombs.mixin.access.StatusEffectInstanceInvoker;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(StatusEffectInstance.class)
public abstract class StatusEffectInstanceMixin {
    @Shadow
    public abstract RegistryEntry<StatusEffect> getEffectType();

    @Shadow
    public abstract int mapDuration(Int2IntFunction mapper);

    @Shadow
    private @Nullable StatusEffectInstance hiddenEffect;

    @WrapOperation(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;updateDuration()I"))
    private int increaseTimer(StatusEffectInstance instance, Operation<Integer> original, @Local(argsOnly = true) LocalRef<LivingEntity> entityRef) {
        Identifier identifier = Optional.ofNullable(Registries.STATUS_EFFECT.getId(FBombsStatusEffects.RADIATION_POISONING.value())).orElseThrow();
        if (!getEffectType().matchesId(identifier)) {
            return original.call(instance);
        }
        //TODO: [ShiroJR] consider other types of LivingEntities too?
        if (!(entityRef.get() instanceof PlayerEntity player)) return original.call(instance);
        if (HazmatArmor.hasFullSetEquipped(player)) return original.call(instance);
        if (getMaxContaminationInInventory(player) <= RadiationCategory.SAFE.getMaxCps())
            return original.call(instance);

        if (this.hiddenEffect != null) {
            ((StatusEffectInstanceInvoker) this.hiddenEffect).invokeUpdateDuration();
        }
        int newDuration = mapDuration(duration -> duration + 1);
        var durationAccessor = (StatusEffectInstanceAccessor) this;
        durationAccessor.setDuration(newDuration);
        return newDuration;
    }

    @Unique
    private static float getMaxContaminationInInventory(PlayerEntity player) {
        float contamination = 0f;
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack entry = player.getInventory().getStack(i);
            if (!entry.contains(FBombsItemComponents.CONTAMINATION)) continue;
            contamination = Math.max(contamination, entry.getOrDefault(FBombsItemComponents.CONTAMINATION, 0f));
        }
        return contamination;
    }
}
