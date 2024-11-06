package com.chailotl.fbombs.block;

import com.chailotl.fbombs.entity.util.TntEntityType;
import com.chailotl.fbombs.init.FBombsGamerules;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class DetonatorBlock extends GenericTntBlock implements Waterloggable {
    public static final BooleanProperty IS_PRESSED = BooleanProperty.of("is_pressed");
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public static final float SELF_DETONATION_CHANCE = 0.2f;

    public DetonatorBlock(TntEntityType tntEntityType, Settings settings) {
        super(tntEntityType, settings);
        this.setDefaultState(this.getDefaultState().with(IS_PRESSED, false)
                .with(FACING, Direction.NORTH)
                .with(Properties.WATERLOGGED, false));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(IS_PRESSED, false)
                .with(FACING, ctx.getHorizontalPlayerFacing())
                .with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).isOf(Fluids.WATER));
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

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(IS_PRESSED, FACING, WATERLOGGED);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
    }


    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world instanceof ServerWorld serverWorld) {
            if (state.get(WATERLOGGED)) {
                serverWorld.playSound(null, pos, SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.BLOCKS, 0.6f, 1f);
            } else {
                serverWorld.playSound(null, pos, SoundEvents.ENTITY_CREEPER_PRIMED, SoundCategory.BLOCKS, 0.6f, 1f);
            }
            serverWorld.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 1f, 1f);
        }
        if (world.getRandom().nextFloat() <= SELF_DETONATION_CHANCE && world.getGameRules().getBoolean(FBombsGamerules.SELF_DESTRUCTING_DETONATOR)) {
            if (world.isClient()) return ActionResult.SUCCESS;
            activateExplosion(world, pos, player);
        } else {
            world.setBlockState(pos, state.with(IS_PRESSED, !state.get(IS_PRESSED)));
            if (world.isClient()) return ActionResult.SUCCESS;
            activateConnectedGunPowderTrails(world, pos);
        }
        return ActionResult.SUCCESS;
    }

    public static void activateExplosion(World world, BlockPos pos, LivingEntity igniter) {
        if (!(world instanceof ServerWorld) || !(world.getBlockState(pos).getBlock() instanceof GenericTntBlock tntBlock))
            return;
        tntBlock.primeTnt(world, pos, igniter);
        world.removeBlock(pos, false);
    }

    public static void activateConnectedGunPowderTrails(World world, BlockPos pos) {
        if (world.isClient()) return;
        Direction.Type.HORIZONTAL.stream().forEach(direction -> {
            BlockState offsetState = world.getBlockState(pos.offset(direction));
            if (offsetState.getBlock() instanceof GunpowderTrailBlock) {
                GunpowderTrailBlock.lightGunpowder(world, pos.offset(direction));
            }
        });
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(3, 0, 3, 13, 8, 13);
    }
}
