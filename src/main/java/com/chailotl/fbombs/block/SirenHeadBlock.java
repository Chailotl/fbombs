package com.chailotl.fbombs.block;

import com.chailotl.fbombs.block.entity.AbstractSirenBlock;
import com.chailotl.fbombs.block.entity.SirenBlockEntity;
import com.chailotl.fbombs.init.FBombsBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class SirenHeadBlock extends AbstractSirenBlock implements BlockEntityProvider {
    public SirenHeadBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(POWERED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(POWERED);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        world.scheduleBlockTick(pos, this, 5);
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SirenBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return FBombsBlockEntities.SIREN == type ? (world1, pos, state1, blockEntity) -> SirenBlockEntity.tick(world1, pos, state1, (SirenBlockEntity) blockEntity) : null;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape pole = Block.createCuboidShape(5, 0, 5, 11, 16, 11);
        VoxelShape head = Block.createCuboidShape(2, 2, 2, 14, 14, 14);
        return VoxelShapes.union(pole, head);
    }
}
