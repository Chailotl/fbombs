package com.chailotl.fbombs.block;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.entity.AbstractTntEntity;
import com.chailotl.fbombs.entity.util.TntEntityType;
import com.chailotl.fbombs.init.FBombsBlocks;
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
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class DetonatorBlock extends Block implements Waterloggable {
    public static final BooleanProperty IS_PRESSED = BooleanProperty.of("is_pressed");
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    protected final TntEntityType tntEntityType;
    public static final float SELF_DETONATION_CHANCE = 0.2f;

    public DetonatorBlock(TntEntityType tntEntityType, Settings settings) {
        super(settings);
        this.tntEntityType = tntEntityType;
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
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        state = state.cycle(IS_PRESSED);
        world.setBlockState(pos, state);

        if (world instanceof ServerWorld serverWorld) {
            /*if (state.get(WATERLOGGED)) {
                serverWorld.playSound(null, pos, SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.BLOCKS, 0.6f, 1f);
            } else {
                serverWorld.playSound(null, pos, SoundEvents.ENTITY_CREEPER_PRIMED, SoundCategory.BLOCKS, 0.6f, 1f);
            }
            serverWorld.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 1f, 1f);*/

            float f = state.get(IS_PRESSED) ? 0.6F : 0.5F;
            serverWorld.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, f);
        }
        if (world.isClient() || !state.get(IS_PRESSED)) return ActionResult.SUCCESS;

        if (world.getRandom().nextFloat() <= SELF_DETONATION_CHANCE && world.getGameRules().getBoolean(FBombsGamerules.SELF_DESTRUCTING_DETONATOR)) {
            primeTnt(world, pos, player);
            world.removeBlock(pos, false);
        } else {
            activateConnectedGunPowderTrails(world, pos);
        }
        return ActionResult.SUCCESS;
    }

    public void primeTnt(World world, BlockPos pos) {
        primeTnt(world, pos, null);
    }

    protected void primeTnt(World world, BlockPos pos, @Nullable LivingEntity igniter) {
        if (!world.isClient) {
            BlockState state = world.getBlockState(pos);
            AbstractTntEntity tntEntity = tntEntityType.tntEntityProvider().spawn(world, (double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5, igniter, state);
            world.spawnEntity(tntEntity);
            if (tntEntity.getFuse() >= 10) {
                world.playSound(null, tntEntity.getX(), tntEntity.getY(), tntEntity.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
            world.emitGameEvent(igniter, GameEvent.PRIME_FUSE, pos);
        }
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
