package com.chailotl.fbombs.block;

import com.chailotl.fbombs.init.FBombsBlocks;
import com.chailotl.fbombs.init.FBombsTags;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import java.util.Map;

import net.minecraft.block.*;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class GunpowderTrailBlock extends Block {
    public static final MapCodec<GunpowderTrailBlock> CODEC = createCodec(GunpowderTrailBlock::new);
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_NORTH = Properties.NORTH_WIRE_CONNECTION;
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_EAST = Properties.EAST_WIRE_CONNECTION;
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_SOUTH = Properties.SOUTH_WIRE_CONNECTION;
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_WEST = Properties.WEST_WIRE_CONNECTION;
    public static final BooleanProperty LIT = Properties.LIT;
    public static final Map<Direction, EnumProperty<WireConnection>> DIRECTION_TO_WIRE_CONNECTION_PROPERTY = Maps.newEnumMap(
        ImmutableMap.of(
            Direction.NORTH, WIRE_CONNECTION_NORTH, Direction.EAST, WIRE_CONNECTION_EAST, Direction.SOUTH, WIRE_CONNECTION_SOUTH, Direction.WEST, WIRE_CONNECTION_WEST
        )
    );
    private static final VoxelShape DOT_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 13.0);
    private static final Map<Direction, VoxelShape> DIRECTION_TO_SIDE_SHAPE = Maps.newEnumMap(
        ImmutableMap.of(
            Direction.NORTH,
            Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 1.0, 13.0),
            Direction.SOUTH,
            Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 16.0),
            Direction.EAST,
            Block.createCuboidShape(3.0, 0.0, 3.0, 16.0, 1.0, 13.0),
            Direction.WEST,
            Block.createCuboidShape(0.0, 0.0, 3.0, 13.0, 1.0, 13.0)
        )
    );
    private static final Map<Direction, VoxelShape> DIRECTION_TO_UP_SHAPE = Maps.newEnumMap(
        ImmutableMap.of(
            Direction.NORTH,
            VoxelShapes.union(DIRECTION_TO_SIDE_SHAPE.get(Direction.NORTH), Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 16.0, 1.0)),
            Direction.SOUTH,
            VoxelShapes.union(DIRECTION_TO_SIDE_SHAPE.get(Direction.SOUTH), Block.createCuboidShape(3.0, 0.0, 15.0, 13.0, 16.0, 16.0)),
            Direction.EAST,
            VoxelShapes.union(DIRECTION_TO_SIDE_SHAPE.get(Direction.EAST), Block.createCuboidShape(15.0, 0.0, 3.0, 16.0, 16.0, 13.0)),
            Direction.WEST,
            VoxelShapes.union(DIRECTION_TO_SIDE_SHAPE.get(Direction.WEST), Block.createCuboidShape(0.0, 0.0, 3.0, 1.0, 16.0, 13.0))
        )
    );
    private static final Map<BlockState, VoxelShape> SHAPES = Maps.newHashMap();

    @Override
    public MapCodec<GunpowderTrailBlock> getCodec() {
        return CODEC;
    }

    public GunpowderTrailBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(
            this.stateManager
                .getDefaultState()
                .with(WIRE_CONNECTION_NORTH, WireConnection.NONE)
                .with(WIRE_CONNECTION_EAST, WireConnection.NONE)
                .with(WIRE_CONNECTION_SOUTH, WireConnection.NONE)
                .with(WIRE_CONNECTION_WEST, WireConnection.NONE)
                .with(LIT, false)
        );

        for (BlockState blockState : this.getStateManager().getStates()) {
            if (!blockState.get(LIT)) {
                SHAPES.put(blockState, this.getShapeForState(blockState));
            }
        }
    }

    private VoxelShape getShapeForState(BlockState state) {
        VoxelShape voxelShape = DOT_SHAPE;

        for (Direction direction : Direction.Type.HORIZONTAL) {
            WireConnection wireConnection = state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
            if (wireConnection == WireConnection.SIDE) {
                voxelShape = VoxelShapes.union(voxelShape, DIRECTION_TO_SIDE_SHAPE.get(direction));
            } else if (wireConnection == WireConnection.UP) {
                voxelShape = VoxelShapes.union(voxelShape, DIRECTION_TO_UP_SHAPE.get(direction));
            }
        }

        return voxelShape;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES.get(state.with(LIT, false));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getPlacementState(ctx.getWorld(), this.getDefaultState(), ctx.getBlockPos());
    }

    private BlockState getPlacementState(BlockView world, BlockState state, BlockPos pos) {
        return this.getDefaultWireState(world, this.getDefaultState().with(LIT, state.get(LIT)), pos);
    }

    private BlockState getDefaultWireState(BlockView world, BlockState state, BlockPos pos) {
        boolean bl = !world.getBlockState(pos.up()).isSolidBlock(world, pos);

        for (Direction direction : Direction.Type.HORIZONTAL) {
            if (!((WireConnection)state.get((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction))).isConnected()) {
                WireConnection wireConnection = this.getRenderConnectionType(world, pos, direction, bl);
                state = state.with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), wireConnection);
            }
        }

        return state;
    }

    @Override
    protected BlockState getStateForNeighborUpdate(
        BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
    ) {
        if (direction == Direction.DOWN) {
            return !this.canRunOnTop(world, neighborPos, neighborState) ? Blocks.AIR.getDefaultState() : state;
        } else if (direction == Direction.UP) {
            return this.getPlacementState(world, state, pos);
        } else {
            WireConnection wireConnection = this.getRenderConnectionType(world, pos, direction);
            return wireConnection.isConnected() == ((WireConnection)state.get((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction))).isConnected()
                && !isFullyConnected(state)
                ? state.with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), wireConnection)
                : this.getPlacementState(
                world, this.getDefaultState().with(LIT, state.get(LIT)).with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), wireConnection), pos
            );
        }
    }

    private static boolean isFullyConnected(BlockState state) {
        return state.get(WIRE_CONNECTION_NORTH).isConnected()
            && state.get(WIRE_CONNECTION_SOUTH).isConnected()
            && state.get(WIRE_CONNECTION_EAST).isConnected()
            && state.get(WIRE_CONNECTION_WEST).isConnected();
    }

    @Override
    protected void prepare(BlockState state, WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (Direction direction : Direction.Type.HORIZONTAL) {
            WireConnection wireConnection = state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
            if (wireConnection != WireConnection.NONE && !world.getBlockState(mutable.set(pos, direction)).isOf(this)) {
                mutable.move(Direction.DOWN);
                BlockState blockState = world.getBlockState(mutable);
                if (blockState.isOf(this)) {
                    BlockPos blockPos = mutable.offset(direction.getOpposite());
                    world.replaceWithStateForNeighborUpdate(direction.getOpposite(), world.getBlockState(blockPos), mutable, blockPos, flags, maxUpdateDepth);
                }

                mutable.set(pos, direction).move(Direction.UP);
                BlockState blockState2 = world.getBlockState(mutable);
                if (blockState2.isOf(this)) {
                    BlockPos blockPos2 = mutable.offset(direction.getOpposite());
                    world.replaceWithStateForNeighborUpdate(direction.getOpposite(), world.getBlockState(blockPos2), mutable, blockPos2, flags, maxUpdateDepth);
                }
            }
        }
    }

    private WireConnection getRenderConnectionType(BlockView world, BlockPos pos, Direction direction) {
        return this.getRenderConnectionType(world, pos, direction, !world.getBlockState(pos.up()).isSolidBlock(world, pos));
    }

    private WireConnection getRenderConnectionType(BlockView world, BlockPos pos, Direction direction, boolean bl) {
        BlockPos blockPos = pos.offset(direction);
        BlockState blockState = world.getBlockState(blockPos);
        if (bl) {
            boolean bl2 = blockState.getBlock() instanceof TrapdoorBlock || this.canRunOnTop(world, blockPos, blockState);
            if (bl2 && connectsTo(world.getBlockState(blockPos.up()))) {
                if (blockState.isSideSolidFullSquare(world, blockPos, direction.getOpposite())) {
                    return WireConnection.UP;
                }

                return WireConnection.SIDE;
            }
        }

        return !connectsTo(blockState, direction) && (blockState.isSolidBlock(world, blockPos) || !connectsTo(world.getBlockState(blockPos.down())))
            ? WireConnection.NONE
            : WireConnection.SIDE;
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        return this.canRunOnTop(world, blockPos, blockState);
    }

    private boolean canRunOnTop(BlockView world, BlockPos pos, BlockState floor) {
        return floor.isSideSolidFullSquare(world, pos, Direction.UP);
    }

    private void updateNeighbors(World world, BlockPos pos) {
        if (world.getBlockState(pos).isOf(this)) {
            world.updateNeighborsAlways(pos, this);

            for (Direction direction : Direction.values()) {
                world.updateNeighborsAlways(pos.offset(direction), this);
            }
        }
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock()) && !world.isClient) {
            for (Direction direction : Direction.Type.VERTICAL) {
                world.updateNeighborsAlways(pos.offset(direction), this);
            }

            this.updateOffsetNeighbors(world, pos);
        }
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!moved && !state.isOf(newState.getBlock())) {
            super.onStateReplaced(state, world, pos, newState, moved);
            if (!world.isClient) {
                for (Direction direction : Direction.values()) {
                    world.updateNeighborsAlways(pos.offset(direction), this);
                }

                this.updateOffsetNeighbors(world, pos);
            }
        }
    }

    private void updateOffsetNeighbors(World world, BlockPos pos) {
        for (Direction direction : Direction.Type.HORIZONTAL) {
            this.updateNeighbors(world, pos.offset(direction));
        }

        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos blockPos = pos.offset(direction);
            if (world.getBlockState(blockPos).isSolidBlock(world, blockPos)) {
                this.updateNeighbors(world, blockPos.up());
            } else {
                this.updateNeighbors(world, blockPos.down());
            }
        }
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient &&!state.canPlaceAt(world, pos)) {
            dropStacks(state, world, pos);
            world.removeBlock(pos, false);
        }
    }

    protected static boolean connectsTo(BlockState state) {
        return connectsTo(state, null);
    }

    protected static boolean connectsTo(BlockState state, @Nullable Direction dir) {
        return state.isOf(FBombsBlocks.GUNPOWDER_TRAIL_BLOCK)
            || state.isIn(FBombsTags.Blocks.TNT_VARIANTS);
    }

    private void addPoweredParticles(World world, Random random, BlockPos pos, Direction direction, Direction direction2, float f, float g) {
        float j = f + (g - f) * random.nextFloat();
        double d = 0.5 + (double)(0.4375F * (float)direction.getOffsetX()) + (double)(j * (float)direction2.getOffsetX());
        double e = 0.5 + (double)(0.4375F * (float)direction.getOffsetY()) + (double)(j * (float)direction2.getOffsetY());
        double k = 0.5 + (double)(0.4375F * (float)direction.getOffsetZ()) + (double)(j * (float)direction2.getOffsetZ());
        world.addParticle(ParticleTypes.FLAME, (double)pos.getX() + d, (double)pos.getY() + e, (double)pos.getZ() + k, 0.0, 0.0, 0.0);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(LIT)) {
            for (Direction direction : Direction.Type.HORIZONTAL) {
                WireConnection wireConnection = state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
                switch (wireConnection) {
                    case UP:
                        this.addPoweredParticles(world, random, pos, direction, Direction.UP, -0.5F, 0.5F);
                    case SIDE:
                        this.addPoweredParticles(world, random, pos, Direction.DOWN, direction, 0.0F, 0.5F);
                        break;
                    case NONE:
                    default:
                        this.addPoweredParticles(world, random, pos, Direction.DOWN, direction, 0.0F, 0.3F);
                }
            }
        }
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return switch (rotation) {
            case CLOCKWISE_180 -> state.with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_SOUTH))
                .with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_WEST))
                .with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_NORTH))
                .with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_EAST));
            case COUNTERCLOCKWISE_90 -> state.with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_EAST))
                .with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_SOUTH))
                .with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_WEST))
                .with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_NORTH));
            case CLOCKWISE_90 -> state.with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_WEST))
                .with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_NORTH))
                .with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_EAST))
                .with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_SOUTH));
            default -> state;
        };
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return switch (mirror) {
            case LEFT_RIGHT -> state.with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_SOUTH))
                .with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_NORTH));
            case FRONT_BACK -> state.with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_WEST))
                .with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_EAST));
            default -> super.mirror(state, mirror);
        };
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WIRE_CONNECTION_NORTH, WIRE_CONNECTION_EAST, WIRE_CONNECTION_SOUTH, WIRE_CONNECTION_WEST, LIT);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!stack.isOf(Items.FLINT_AND_STEEL) && !stack.isOf(Items.FIRE_CHARGE)) {
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        } else {
            lightGunpowder(world, pos);
            Item item = stack.getItem();
            if (stack.isOf(Items.FLINT_AND_STEEL)) {
                stack.damage(1, player, LivingEntity.getSlotForHand(hand));
            } else {
                stack.decrementUnlessCreative(1, player);
            }

            player.incrementStat(Stats.USED.getOrCreateStat(item));
            return ItemActionResult.success(world.isClient);
        }
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(LIT)) {
            for (Direction direction : Direction.Type.HORIZONTAL) {
                WireConnection wire = state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));

                switch (wire) {
                    case SIDE:
                        BlockPos pos2 = pos.offset(direction);
                        lightGunpowder(world, pos2);
                        lightGunpowder(world, pos2.down());
                        primeTnt(world, pos2);
                        break;
                    case UP:
                        lightGunpowder(world, pos.offset(direction).up());
                        break;
                }
            }
            primeTnt(world, pos.down());

            world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL_AND_REDRAW);
            this.updateNeighbors(world, pos);
        }
    }

    public static void lightGunpowder(World world, BlockPos pos) {
        if (!world.isClient) {
            BlockState state = world.getBlockState(pos);

            if (state.isOf(FBombsBlocks.GUNPOWDER_TRAIL_BLOCK)) {
                world.setBlockState(pos, state.with(LIT, true), Block.NOTIFY_LISTENERS);
                world.scheduleBlockTick(pos, FBombsBlocks.GUNPOWDER_TRAIL_BLOCK, 4);
            }
        }
    }

    private void primeTnt(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        if (block == Blocks.TNT) {
            TntBlock.primeTnt(world, pos);
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL_AND_REDRAW);
        } else if (block instanceof GenericTntBlock genericTntBlock) {
            genericTntBlock.primeTnt(world, pos);
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL_AND_REDRAW);
        }
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state)
    {
        return new ItemStack(Items.GUNPOWDER);
    }
}
