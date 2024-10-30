package com.chailotl.fbombs.util;

import com.chailotl.fbombs.init.FBombsBlocks;
import com.chailotl.fbombs.init.FBombsTags;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface SirenPoleWalker {
    @Nullable
    default Integer getPower(World world, BlockPos pos) {
        if (!(world.isChunkLoaded(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ())))) return null;
        BlockPos.Mutable posWalker = pos.mutableCopy();
        while (world.getBlockState(posWalker).isIn(FBombsTags.Blocks.TRANSMITS_REDSTONE_POWER) && !(world.getBlockState(posWalker).getBlock() instanceof SirenPoleWalker)) {
            posWalker.move(Direction.DOWN);
        }
        BlockState stateBelow = world.getBlockState(posWalker);
        if (stateBelow instanceof SirenPoleWalker walker) return walker.getPower(world, posWalker);
        return null;
    }

    @Nullable
    default Integer getPoleCountBelow(World world, BlockPos pos) {
        BlockPos.Mutable posWalker = pos.mutableCopy();
        int currentCount = 0;
        while (world.getBlockState(posWalker).isIn(FBombsTags.Blocks.TRANSMITS_REDSTONE_POWER)
                || world.getBlockState(posWalker).getBlock() instanceof SirenPoleWalker) {
            currentCount++;
        }
        if (world.getBlockState(posWalker).isOf(FBombsBlocks.SIREN_BASE)) currentCount--;
        return currentCount;
    }
}
