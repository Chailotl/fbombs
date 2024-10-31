package com.chailotl.fbombs.block;

import com.chailotl.fbombs.entity.util.TntEntityType;
import com.chailotl.fbombs.init.FBombsGamerules;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DetonatorBlock extends GenericTntBlock {
    public static final BooleanProperty IS_PRESSED = BooleanProperty.of("is_pressed");
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public static final float SELF_DETONATION_CHANCE = 0.2f;

    public DetonatorBlock(TntEntityType tntEntityType, Settings settings) {
        super(tntEntityType, settings);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(IS_PRESSED, FACING);
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
            serverWorld.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 1f, 1f);
        }
        if (world.getRandom().nextFloat() <= SELF_DETONATION_CHANCE && world.getGameRules().getBoolean(FBombsGamerules.SELF_DESTRUCTING_DETONATOR)) {
            if (world.isClient()) return ActionResult.SUCCESS;
            activateExplosion(world, pos, player);
        } else {
            if (world.isClient()) return ActionResult.SUCCESS;
            activateConnectedGunPowderTrails(world, pos);
            world.setBlockState(pos, state.with(IS_PRESSED, true));
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
}
