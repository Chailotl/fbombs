package com.chailotl.fbombs.block;

import com.chailotl.fbombs.block.entity.AbstractSirenBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class SirenPoleBlock extends AbstractSirenBlock {
    public SirenPoleBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(5, 0, 5, 11, 16, 11);
    }
}
