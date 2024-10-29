package com.chailotl.fbombs.data;

import com.chailotl.fbombs.util.NbtKeys;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public record LocatableBlock(BlockPos pos, BlockState state) {
    public static final Codec<LocatableBlock> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf(NbtKeys.BLOCK_POS).forGetter(LocatableBlock::pos),
            BlockState.CODEC.fieldOf(NbtKeys.BLOCK_STATE).forGetter(LocatableBlock::state)
    ).apply(instance, LocatableBlock::new));

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
        return locatableBlock.pos.equals(this.pos);
    }
}
