package com.chailotl.fbombs.data;

import com.chailotl.fbombs.util.Locatable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public record LocatableBlock(BlockPos pos, BlockState state) implements Locatable {
    @Override
    public Vec3d getPos() {
        return pos().toCenterPos();
    }

    // for hashmap key equality checks
    @Override
    public int hashCode() {
        return pos().hashCode();
    }

    // for hashmap key equality checks
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof LocatableBlock locatableBlock)) return false;
        return locatableBlock.getPos().equals(this.getPos());
    }
}
