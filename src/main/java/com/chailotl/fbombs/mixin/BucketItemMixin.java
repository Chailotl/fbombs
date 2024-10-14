package com.chailotl.fbombs.mixin;

import com.chailotl.fbombs.init.FBombsCriteria;
import com.chailotl.fbombs.util.LoggerUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidFillable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BucketItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BucketItem.class)
public class BucketItemMixin {
    @WrapOperation(method = "placeFluid",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/block/FluidFillable;tryFillWithFluid(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/fluid/FluidState;)Z")
    )
    private boolean addWaterloggableTntCriterionTrigger(FluidFillable instance, WorldAccess worldAccess, BlockPos pos,
                                                        BlockState state, FluidState fluidState, Operation<Boolean> original,
                                                        @Local(argsOnly = true) LocalRef<PlayerEntity> player) {
        boolean waterlogged = original.call(instance, worldAccess, pos, state, fluidState);
        if (!waterlogged) return false;
        if (player.get() instanceof ServerPlayerEntity serverPlayer) {
            LoggerUtil.devLogger("isServer");
            FBombsCriteria.WATERLOGGED_TNT_BLOCK.trigger(serverPlayer);
        }
        return true;
    }
}
