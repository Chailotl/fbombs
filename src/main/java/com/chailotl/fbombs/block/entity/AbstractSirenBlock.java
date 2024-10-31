package com.chailotl.fbombs.block.entity;

import com.chailotl.fbombs.block.SirenHeadBlock;
import com.chailotl.fbombs.block.SirenPoleBlock;
import com.chailotl.fbombs.util.SirenPoleWalker;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractSirenBlock extends Block implements Waterloggable, SirenPoleWalker {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public AbstractSirenBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            if (blockItem.getBlock() instanceof SirenPoleBlock || blockItem.getBlock() instanceof SirenHeadBlock) {
                BlockPos.Mutable posWalker = hit.getBlockPos().mutableCopy();
                do {
                    posWalker.move(Direction.UP);
                } while (SirenBlockEntity.isPartOfPole(world, posWalker));
                if (world.canSetBlock(posWalker) && world.getBlockState(posWalker).isReplaceable()) {
                    stack.decrementUnlessCreative(1, player);
                    world.setBlockState(posWalker, blockItem.getBlock().getDefaultState());
                    return ItemActionResult.SUCCESS;
                }
            }
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).isOf(Fluids.WATER));
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        /*
        BlockState stateBelow = world.getBlockState(pos.down());
        if (!(stateBelow.getBlock() instanceof SirenPoleWalker) || !stateBelow.isIn(FBombsTags.Blocks.TRANSMITS_REDSTONE_POWER)) {
            return Blocks.AIR.getDefaultState();
        }*/
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }
}
