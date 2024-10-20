package com.chailotl.fbombs.block;

import com.chailotl.fbombs.entity.AbstractTntEntity;
import com.chailotl.fbombs.entity.MiningChargeEntity;
import com.chailotl.fbombs.entity.util.TntEntityType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class ShapedChargeBlock extends GenericTntBlock {
    public static final DirectionProperty FACING = FacingBlock.FACING;

    public ShapedChargeBlock(TntEntityType tntEntityType, Settings settings) {
        super(tntEntityType, settings);
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void primeTnt(World world, BlockPos pos, @Nullable LivingEntity igniter) {
        if (!world.isClient) {
            BlockState state = world.getBlockState(pos);
            AbstractTntEntity tntEntity = tntEntityType.tntEntityProvider().spawn(world, (double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5, igniter, state);
            ((MiningChargeEntity) tntEntity).setFacing(state.get(FACING));
            world.spawnEntity(tntEntity);
            if (tntEntity.getFuse() >= 10) {
                world.playSound(null, tntEntity.getX(), tntEntity.getY(), tntEntity.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
            world.emitGameEvent(igniter, GameEvent.PRIME_FUSE, pos);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
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
        builder.add(FACING);
    }
}