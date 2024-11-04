package com.chailotl.fbombs.data;

import com.chailotl.fbombs.util.NbtKeys;
import com.google.common.primitives.Doubles;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public record LocatableBlock(BlockPos pos, BlockState state,
                             double sqDistanceToOrigin) implements Comparable<LocatableBlock> {
    public static final Codec<LocatableBlock> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf(NbtKeys.BLOCK_POS).forGetter(LocatableBlock::pos),
            BlockState.CODEC.fieldOf(NbtKeys.BLOCK_STATE).forGetter(LocatableBlock::state),
            Codec.DOUBLE.fieldOf(NbtKeys.DISTANCE_TO_ORIGIN).forGetter(LocatableBlock::sqDistanceToOrigin)
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

    @Override
    public int compareTo(@NotNull LocatableBlock external) {
        return Doubles.compare(this.sqDistanceToOrigin, external.sqDistanceToOrigin);
    }
}
