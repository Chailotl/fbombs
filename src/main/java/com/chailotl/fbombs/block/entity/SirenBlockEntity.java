package com.chailotl.fbombs.block.entity;

import com.chailotl.fbombs.block.SirenHeadBlock;
import com.chailotl.fbombs.block.SirenPoleBlock;
import com.chailotl.fbombs.init.FBombsBlockEntities;
import com.chailotl.fbombs.init.FBombsTags;
import com.chailotl.fbombs.util.SirenPoleWalker;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.Optional;

public class SirenBlockEntity extends BlockEntity {
    public static final int MIN_POLE_LENGTH = 6;    // size at which power reduction starts
    public static final int MAX_POLE_LENGTH = 30;

    private float angle = 0;
    private float prevAngle = 0;

    public SirenBlockEntity(BlockPos pos, BlockState state) {
        super(FBombsBlockEntities.SIREN, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, SirenBlockEntity blockEntity) {
        AbstractSirenBlock.updatePowerState(world, pos, state);

        float normalizedRedstonePower = (float) getStrengthFromStructure(world, pos) / 15;
        float normalizedPoleSizePower = (float) (MAX_POLE_LENGTH - Math.max(0, getPoleCountBelow(world, pos) - MIN_POLE_LENGTH)) / MAX_POLE_LENGTH;
        float normalizedRedstoneStrength = normalizedRedstonePower * normalizedPoleSizePower;
        blockEntity.prevAngle = blockEntity.angle;
        if (normalizedRedstoneStrength >= 0 && state.get(SirenPoleBlock.POWERED)) {
            blockEntity.angle += (float) (12.0 / 20.0 * (normalizedRedstoneStrength * 3 + 1));
        }
    }

    public static int getStrengthFromStructure(World world, BlockPos pos) {
        BlockPos.Mutable posWalker = pos.down().mutableCopy();
        while (world.getBlockState(posWalker).isIn(FBombsTags.Blocks.TRANSMITS_REDSTONE_POWER)) {
            if (world.getBlockState(posWalker).getBlock() instanceof SirenPoleWalker) break;
            posWalker.move(Direction.DOWN);
        }
        if (world.getBlockState(posWalker).getBlock() instanceof SirenPoleWalker sirenPoleWalker) {
            return Optional.ofNullable(sirenPoleWalker.getPower(world, posWalker)).orElse(0);
        }
        return 0;
    }

    private static int getPoleCountBelow(World world, BlockPos pos) {
        if (world.getBlockState(pos).getBlock() instanceof SirenPoleWalker poleWalker) {
            return Optional.ofNullable(poleWalker.getPoleCountBelow(world, pos)).orElse(0);
        }
        return 0;
    }

    public float getAngle(float tickDelta) {
        return MathHelper.lerp(tickDelta, prevAngle, angle);
    }

    public static boolean isPartOfPole(WorldAccess world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return world.getBlockState(pos).isIn(FBombsTags.Blocks.TRANSMITS_REDSTONE_POWER)
            || state.getBlock() instanceof SirenPoleBlock
            || state.getBlock() instanceof SirenHeadBlock;
    }
}
