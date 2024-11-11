package com.chailotl.fbombs.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @ModifyExpressionValue(
        method = "sendToPlayerIfNearby",
        at = @At(
            value = "CONSTANT",
            args = "doubleValue=512.0"
        )
    )
    private double extendDistance(double original) {
        return original * 2;
    }
}