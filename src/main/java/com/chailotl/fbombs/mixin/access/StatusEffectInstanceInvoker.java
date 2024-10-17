package com.chailotl.fbombs.mixin.access;

import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(StatusEffectInstance.class)
public interface StatusEffectInstanceInvoker {
    @Invoker("updateDuration")
    int invokeUpdateDuration();
}
