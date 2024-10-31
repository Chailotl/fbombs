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

    // used for reducing power over long vertical poles
    private static final float POLE_POWER_SIZE_FACTOR = 0.8f;

    private int tick = 0;
    private float redstoneStrength = 0;

    private int angle = 0;

    public SirenBlockEntity(BlockPos pos, BlockState state) {
        super(FBombsBlockEntities.SIREN, pos, state);
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }


    public static void tick(World world, BlockPos pos, BlockState state, SirenBlockEntity blockEntity) {
        blockEntity.tick++;
        blockEntity.angle = blockEntity.tick % 360;
        if (blockEntity.tick % 40 != 0) return;
        float calculatedStrength = getStrengthFromStructure(world, pos);
        calculatedStrength *= getPoleCountBelow(world, pos) * POLE_POWER_SIZE_FACTOR;
        blockEntity.redstoneStrength = calculatedStrength;
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

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains(NbtKeys.TICK)) {
            this.tick = nbt.getInt(NbtKeys.TICK);
        }
        if (nbt.contains(NbtKeys.REDSTONE_STRENGTH)) {
            this.redstoneStrength = nbt.getFloat(NbtKeys.REDSTONE_STRENGTH);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt(NbtKeys.TICK, this.tick);
        nbt.putFloat(NbtKeys.REDSTONE_STRENGTH, this.redstoneStrength);
    }
}
