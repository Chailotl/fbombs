package com.chailotl.fbombs.block;

import com.chailotl.fbombs.block.entity.AbstractSirenBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SirenBaseBlock extends AbstractSirenBlock {
    public SirenBaseBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public Integer getPower(World world, BlockPos pos) {
        if (!(world.isChunkLoaded(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()))))
            return null;
        BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof SirenBaseBlock)) return null;
        return world.getReceivedRedstonePower(pos);
    }

    @Override
    public boolean canReceivePower() {
        return true;
    }
}
