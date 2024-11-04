package com.chailotl.fbombs.mixin;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.data.RadiationCategory;
import com.chailotl.fbombs.data.RadiationData;
import com.chailotl.fbombs.init.FBombsPersistentState;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.block.BlockState;
import net.minecraft.block.SpreadableBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SpreadableBlock.class)
public class SpreadableBlockMixin {
    @WrapOperation(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/SpreadableBlock;canSurvive(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Z"))
    private boolean canSurviveContamination(BlockState state, WorldView world, BlockPos pos, Operation<Boolean> original,
                                            @Local(argsOnly = true) LocalRef<ServerWorld> serverWorldRef) {
        if (isContaminated(serverWorldRef.get(), pos)) {
            return false;
        }
        return original.call(state, world, pos);
    }

    @WrapOperation(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/SpreadableBlock;canSpread(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Z"))
    private boolean canSSpreadInContamination(BlockState state, WorldView world, BlockPos pos, Operation<Boolean> original,
                                              @Local(argsOnly = true) LocalRef<ServerWorld> serverWorldRef) {
        if (isContaminated(serverWorldRef.get(), pos)) {
            return false;
        }
        return original.call(state, world, pos);
    }

    @Unique
    private static boolean isContaminated(ServerWorld world, BlockPos pos) {
        FBombsPersistentState state = FBombs.getCachedPersistentState(world);
        if (state != null) {
            return RadiationData.getRadiationLevel(state.getRadiationSources(), pos) > RadiationCategory.SAFE.getMaxCps();
        }
        return false;
    }
}
