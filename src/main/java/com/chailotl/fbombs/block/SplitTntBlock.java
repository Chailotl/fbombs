package com.chailotl.fbombs.block;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.entity.AbstractTntEntity;
import com.chailotl.fbombs.init.FBombsBlocks;
import com.chailotl.fbombs.init.FBombsItems;
import com.chailotl.fbombs.init.FBombsTags;
import com.chailotl.fbombs.util.ItemStackHelper;
import com.chailotl.fbombs.util.TntEntityType;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SplitTntBlock extends GenericTntBlock implements Waterloggable {
    private final TntEntityType tntEntityType;

    public SplitTntBlock(TntEntityType tntEntityType, AbstractBlock.Settings settings) {
        super(tntEntityType, settings);
        this.tntEntityType = tntEntityType;
        this.setDefaultState(this.getDefaultState()
                .with(UNSTABLE, false)
                .with(Split.NE.getProperty(), true)
                .with(Split.SE.getProperty(), true)
                .with(Split.SW.getProperty(), true)
                .with(Split.NW.getProperty(), true)
                .with(Properties.WATERLOGGED, false)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(Properties.WATERLOGGED);
        for (Split entry : Split.values()) {
            builder.add(entry.getProperty());
        }
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = VoxelShapes.empty();
        for (Split entry : getExistingSplits(state)) {
            shape = VoxelShapes.union(shape, entry.getShape());
        }
        return shape;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.isIn(FBombsTags.Items.SPLITS_TNT)) {
            if (!removeSplit(world, pos, state, hit)) return ItemActionResult.FAIL;
            ItemStackHelper.decrementOrDamageInNonCreative(stack, 1, player);
            return ItemActionResult.SUCCESS;
        }
        if (stack.isIn(FBombsTags.Items.IGNITES_TNT)) {
            if (world.isClient()) return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
            primeTnt(world, pos);
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), NOTIFY_ALL);
            ItemStackHelper.decrementOrDamageInNonCreative(stack, 1, player);
            return ItemActionResult.SUCCESS;
        }

        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean removeSplit(World world, BlockPos pos, BlockState state, @NotNull BlockHitResult hitResult) {
        Split split = SplitTntBlock.Split.get(hitResult, hitResult.getSide(), state, true);
        if (split == null) return false;
        if (world instanceof ServerWorld serverWorld) {
            if (getExistingSplits(state).size() <= 1) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), NOTIFY_ALL);
            } else {
                if (state.getBlock() instanceof TntBlock) {
                    state = FBombsBlocks.SPLIT_TNT.getDefaultState().with(Properties.WATERLOGGED, world.getFluidState(pos).isOf(Fluids.WATER));
                }
                serverWorld.setBlockState(pos, state.with(split.getProperty(), false), NOTIFY_ALL);
            }
            ItemScatterer.spawn(serverWorld, pos.getX(), pos.getY() + 1, pos.getZ(), FBombsItems.DYNAMITE_STICK.getDefaultStack());
            serverWorld.playSound(null, pos, SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.BLOCKS, 1f, 1f);
        }
        return true;
    }

    public static List<Split> getExistingSplits(BlockState state) {
        if (state.getBlock() instanceof TntBlock) {
            return List.of(Split.values());
        }
        List<Split> splits = new ArrayList<>();
        if (state.get(Split.NE.getProperty())) splits.add(Split.NE);
        if (state.get(Split.SE.getProperty())) splits.add(Split.SE);
        if (state.get(Split.SW.getProperty())) splits.add(Split.SW);
        if (state.get(Split.NW.getProperty())) splits.add(Split.NW);
        return splits;
    }

    @Override
    public void primeTnt(World world, BlockPos pos) {
        if (!world.isClient) {
            BlockState state = world.getBlockState(pos);
            AbstractTntEntity tntEntity = tntEntityType.tntEntityProvider().spawn(world, (double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5, null, state);
            world.spawnEntity(tntEntity);
            if (tntEntity.getFuse() >= 10) {
                world.playSound(null, tntEntity.getX(), tntEntity.getY(), tntEntity.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
            world.emitGameEvent(null, GameEvent.PRIME_FUSE, pos);
        }
    }

    public static boolean containsSplitStates(BlockState state) {
        for (Split entry : Split.values()) {
            if (!state.contains(entry.getProperty())) return false;
        }
        return true;
    }

    public enum Split {
        NE("north_east_split", Direction.NORTH, Direction.EAST,
                Block.createCuboidShape(8, 0, 0, 16, 16, 8)),
        SE("south_east_split", Direction.SOUTH, Direction.EAST,
                Block.createCuboidShape(8, 0, 8, 16, 16, 16)),
        SW("south_west_split", Direction.SOUTH, Direction.WEST,
                Block.createCuboidShape(0, 0, 8, 8, 16, 16)),
        NW("north_west_split", Direction.NORTH, Direction.WEST,
                Block.createCuboidShape(0, 0, 0, 8, 16, 8));

        private final BooleanProperty property;
        private final Pair<Direction, Direction> exposedSides;
        private final VoxelShape shape;

        Split(String name, Direction direction1, Direction direction2, VoxelShape shape) {
            this.property = BooleanProperty.of(name);
            this.exposedSides = new Pair<>(direction1, direction2);
            this.shape = shape;
        }

        @NotNull
        public BooleanProperty getProperty() {
            return property;
        }

        public Pair<Direction, Direction> getExposedSides() {
            return exposedSides;
        }

        public VoxelShape getShape() {
            return shape;
        }

        @Nullable
        public static Split get(BlockHitResult hit, Direction testDirection, BlockState state, boolean testOpposite) {
            if (testDirection.equals(Direction.UP) || testDirection.equals(Direction.DOWN)) {
                Throwable error = new UnsupportedOperationException("Interacted with [%s] side of TntSplitBlock".formatted(testDirection));
                FBombs.LOGGER.error("Interacted with invalid side", error);
                return null;
            }
            BlockPos blockPos = hit.getBlockPos();
            Pair<Split, Split> possibleSides = getSplitsFromCardinalDirection(testDirection);
            Vec3d vec3d = hit.getPos().subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            Vec2f sideCoordinates = getSideCoordinates(vec3d, testDirection);
            Split split = sideCoordinates.x < 0.5 ? possibleSides.getLeft() : possibleSides.getRight();
            if ((split == null || (state.contains(split.getProperty()) && !state.get(split.getProperty()))) && testOpposite) {
                return get(hit, testDirection.getOpposite(), state, false);
            }
            return split;
        }

        private static Vec2f getSideCoordinates(Vec3d vec3d, Direction hitDirection) {
            float x = (float) vec3d.getX();
            float y = (float) vec3d.getY();
            float z = (float) vec3d.getZ();
            return switch (hitDirection) {
                case NORTH -> new Vec2f(1.0f - x, y);
                case EAST -> new Vec2f(1.0f - z, y);
                case SOUTH -> new Vec2f(x, y);
                case WEST -> new Vec2f(z, y);
                default ->
                        throw new UnsupportedOperationException("Interacted with [%s] side of TntSplitBlock".formatted(hitDirection));
            };
        }

        public static Pair<Split, Split> getSplitsFromCardinalDirection(Direction hitDirection) {
            return switch (hitDirection) {
                case NORTH -> new Pair<>(NE, NW);
                case EAST -> new Pair<>(SE, NE);
                case SOUTH -> new Pair<>(SW, SE);
                case WEST -> new Pair<>(NW, SW);
                default ->
                        throw new UnsupportedOperationException("Interacted with [%s] side of TntSplitBlock".formatted(hitDirection));
            };
        }
    }
}
