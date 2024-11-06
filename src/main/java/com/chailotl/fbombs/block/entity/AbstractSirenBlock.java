package com.chailotl.fbombs.block.entity;

import com.chailotl.fbombs.block.SirenBaseBlock;
import com.chailotl.fbombs.block.SirenHeadBlock;
import com.chailotl.fbombs.block.SirenPoleBlock;
import com.chailotl.fbombs.init.FBombsTags;
import com.chailotl.fbombs.util.SirenPoleWalker;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
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

import java.util.Optional;

public abstract class AbstractSirenBlock extends Block implements Waterloggable, SirenPoleWalker {
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public AbstractSirenBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(POWERED, false).with(WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(POWERED).add(WATERLOGGED);
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
                    if (world instanceof ServerWorld serverWorld) {
                        serverWorld.playSound(null, pos, this.soundGroup.getPlaceSound(), SoundCategory.BLOCKS, 1f, 1f);
                    }
                    world.setBlockState(posWalker, blockItem.getBlock().getPlacementState(new ItemPlacementContext(player, hand, stack, hit)), NOTIFY_LISTENERS);

                    stack.decrementUnlessCreative(1, player);
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
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState
            neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    protected static void updatePowerState(WorldAccess world, BlockPos pos, BlockState state) {
        BlockPos.Mutable offsetPos = pos.offset(Direction.DOWN).mutableCopy();
        BlockState offsetState = world.getBlockState(offsetPos);

        while (world.getBlockState(offsetPos).isIn(FBombsTags.Blocks.TRANSMITS_REDSTONE_POWER)) {
            offsetPos.move(Direction.DOWN);
        }

        boolean isPowered = state.contains(POWERED) && state.get(POWERED);
        boolean isOffsetPowered = offsetState.contains(POWERED) && offsetState.get(POWERED);
        isOffsetPowered = offsetState.getBlock() instanceof SirenBaseBlock baseBlock
                ? Optional.ofNullable(baseBlock.getPower(world, offsetPos)).map(integer -> integer > 0).orElse(false)
                : isOffsetPowered;

        if (offsetState.getBlock() instanceof SirenPoleWalker) {
            if (isPowered != isOffsetPowered) {
                world.setBlockState(pos, state.with(POWERED, isOffsetPowered), NOTIFY_LISTENERS);
            }
        }
    }
}
