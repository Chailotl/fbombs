package com.chailotl.fbombs.util;

import com.chailotl.fbombs.block.entity.SirenBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface SirenPoleWalker {
    @Nullable
    default Integer getPower(World world, BlockPos pos) {
        if (!(world.isChunkLoaded(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()))))
            return null;
        BlockPos.Mutable posWalker = pos.mutableCopy();

        do {
            posWalker.move(Direction.DOWN);
        }
        while (SirenBlockEntity.isPartOfPole(world, posWalker));

        BlockState bottomState = world.getBlockState(posWalker.move(Direction.UP));
        if (bottomState.getBlock() instanceof SirenPoleWalker walker && walker.canReceivePower()) {
            return walker.getPower(world, posWalker);
        }
        return null;
    }

    @Nullable
    default Integer getPoleCountBelow(World world, BlockPos pos) {
        BlockPos.Mutable posWalker = pos.down().mutableCopy();
        int currentCount = 0;

        while (SirenBlockEntity.isPartOfPole(world, posWalker)) {
            currentCount++;
            posWalker.move(Direction.DOWN);
        }
        if (!SirenBlockEntity.isPartOfPole(world, posWalker)) currentCount--;
        return currentCount;
    }

    default boolean canReceivePower() {
        return false;
    }
}
