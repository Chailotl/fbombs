package com.chailotl.fbombs.entity;

import com.chailotl.fbombs.init.FBombsBlocks;
import com.chailotl.fbombs.init.FBombsEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TntSlabEntity extends AbstractTntEntity {
    public TntSlabEntity(EntityType<TntSlabEntity> entityType, World world) {
        super(entityType, world);
    }

    public TntSlabEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter, BlockState state) {
        super(FBombsEntityTypes.TNT_SLAB, world, x, y, z, igniter, state);
    }

    @Override
    protected Block getDefaultBlock() {
        return FBombsBlocks.SPLIT_TNT;
    }

    @Override
    protected float getPower() {
        BlockState state = getBlockState();
        if (state.contains(Properties.WATERLOGGED) && state.get(Properties.WATERLOGGED)) return 0;
        if (!state.contains(Properties.SLAB_TYPE)) return 0;
        float power = 2;
        if (state.get(Properties.SLAB_TYPE).equals(SlabType.DOUBLE)) power *= 2;
        return power;
    }
}