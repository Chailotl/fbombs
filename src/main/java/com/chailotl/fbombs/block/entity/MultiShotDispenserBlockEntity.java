package com.chailotl.fbombs.block.entity;

import com.chailotl.fbombs.init.FBombsBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class MultiShotDispenserBlockEntity extends DispenserBlockEntity {
    protected MultiShotDispenserBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public MultiShotDispenserBlockEntity(BlockPos pos, BlockState state) {
        this(FBombsBlockEntities.MULTI_SHOT_DISPENSER, pos, state);
    }

    // TODO: add translation string
    @Override
    protected Text getContainerName() {
        return Text.translatable("container.dispenser");
    }
}