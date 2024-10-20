package com.chailotl.fbombs.entity;

import com.chailotl.fbombs.init.FBombsBlockEntities;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;

public class AcmeBedBlockEntity extends BlockEntity {
    private DyeColor color;

    public AcmeBedBlockEntity(BlockPos pos, BlockState state) {
        super(FBombsBlockEntities.ACME_BED, pos, state);
        this.color = ((BedBlock)state.getBlock()).getColor();
    }

    public AcmeBedBlockEntity(BlockPos pos, BlockState state, DyeColor color) {
        super(FBombsBlockEntities.ACME_BED, pos, state);
        this.color = color;
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public DyeColor getColor() {
        return this.color;
    }

    public void setColor(DyeColor color) {
        this.color = color;
    }
}