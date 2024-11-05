package com.chailotl.fbombs.block;

import com.chailotl.fbombs.block.entity.AbstractSirenBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class SirenBaseBlock extends AbstractSirenBlock {
    public SirenBaseBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public Integer getPower(WorldAccess world, BlockPos pos) {
        if (!(world.isChunkLoaded(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()))))
            return null;
        BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof SirenBaseBlock)) return null;
        return Math.max(world.getReceivedRedstonePower(pos), world.getReceivedRedstonePower(pos.down()));
    }

    @Override
    public boolean canReceivePower() {
        return true;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(3, 0, 3, 13, 16, 13);
    }
}
