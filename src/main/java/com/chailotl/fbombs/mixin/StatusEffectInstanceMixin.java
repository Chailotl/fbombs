package com.chailotl.fbombs.mixin;

import com.chailotl.fbombs.data.RadiationCategory;
import com.chailotl.fbombs.init.FBombsStatusEffects;
import com.chailotl.fbombs.item.HazmatArmor;
import com.chailotl.fbombs.mixin.access.StatusEffectInstanceAccessor;
import com.chailotl.fbombs.mixin.access.StatusEffectInstanceInvoker;
import com.chailotl.fbombs.util.cast.Contaminatable;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

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
        Identifier identifier = Registries.STATUS_EFFECT.getId(FBombsStatusEffects.RADIATION_POISONING.value());
        if (identifier == null || !getEffectType().matchesId(identifier)) {
            return original.call(instance);
        }

        // if (!(entityRef.get().getWorld() instanceof ServerWorld serverWorld)) return original.call(instance);
        if (HazmatArmor.hasFullSetEquipped(entityRef.get())) return original.call(instance);
        if (!(entityRef.get() instanceof Contaminatable contaminatable)) return original.call(instance);
        if (contaminatable.fbombs$getCps() <= RadiationCategory.SAFE.getMaxCps()) return original.call(instance);

        if (this.hiddenEffect != null) {
            ((StatusEffectInstanceInvoker) this.hiddenEffect).invokeUpdateDuration();
        }

        int durationIncrease = RadiationCategory.getRadiationCategory(contaminatable.fbombs$getCps()).getDurationIncrease();
        int newDuration = mapDuration(duration -> Math.max(1, duration + durationIncrease));

        var durationAccessor = (StatusEffectInstanceAccessor) this;
        durationAccessor.setDuration(newDuration);
        return newDuration;
    }
}
