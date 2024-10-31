package com.chailotl.fbombs.block.entity;

import com.chailotl.fbombs.init.FBombsBlockEntities;
import com.chailotl.fbombs.init.FBombsTags;
import com.chailotl.fbombs.util.NbtKeys;
import com.chailotl.fbombs.util.SirenPoleWalker;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Optional;

public class SirenBlockEntity extends BlockEntity {
    public static final int MIN_POLE_LENGTH = 6;    // size at which power reduction starts
    public static final int MAX_POLE_LENGTH = 30;

    private int tick;
    private float normalizedRedstoneStrength;

    public SirenBlockEntity(BlockPos pos, BlockState state) {
        super(FBombsBlockEntities.SIREN, pos, state);
        this.tick = 0;
        this.normalizedRedstoneStrength = 0f;
    }

    public static void tick(World world, BlockPos pos, BlockState state, SirenBlockEntity blockEntity) {
        blockEntity.tick++;
        if (blockEntity.tick % 40 != 0) return;

        float normalizedRedstonePower = (float) getStrengthFromStructure(world, pos) / 15;
        float normalizedPoleSizePower = (float) (MAX_POLE_LENGTH - Math.max(0, getPoleCountBelow(world, pos) - MIN_POLE_LENGTH)) / MAX_POLE_LENGTH;
        blockEntity.normalizedRedstoneStrength = normalizedRedstonePower * normalizedPoleSizePower;
    }

    private static int getStrengthFromStructure(World world, BlockPos pos) {
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

    public static boolean isPartOfPole(World world, BlockPos pos) {
        return world.getBlockState(pos).isIn(FBombsTags.Blocks.TRANSMITS_REDSTONE_POWER)
                || world.getBlockState(pos).getBlock() instanceof SirenPoleWalker;
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains(NbtKeys.TICK)) {
            this.tick = nbt.getInt(NbtKeys.TICK);
        }
        if (nbt.contains(NbtKeys.REDSTONE_STRENGTH)) {
            this.normalizedRedstoneStrength = nbt.getFloat(NbtKeys.REDSTONE_STRENGTH);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt(NbtKeys.TICK, this.tick);
        nbt.putFloat(NbtKeys.REDSTONE_STRENGTH, this.normalizedRedstoneStrength);
    }
}
