package com.chailotl.fbombs.block;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SplitTntBlock extends TntBlock implements Waterloggable {
    public static final int MAX_SPLITS = 4;

    public static final IntProperty TNT_SPLITS = IntProperty.of("splits", 0, MAX_SPLITS);

    public SplitTntBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(TNT_SPLITS, 4)
                .with(Properties.HORIZONTAL_FACING, Direction.NORTH)
                .with(Properties.WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(TNT_SPLITS, Properties.HORIZONTAL_FACING, Properties.WATERLOGGED);
    }

    public BlockState getFirstSplitState(Direction direction) {
        return this.getDefaultState()
                .with(TNT_SPLITS, MAX_SPLITS - 1)
                .with(Properties.HORIZONTAL_FACING, direction);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        //TODO: [ShiroJR] based on 4 sliced tnt state VoxelShapes
        return super.getOutlineShape(state, world, pos, context);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        //TODO: [ShiroJR] split more into small pieces
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }
}
