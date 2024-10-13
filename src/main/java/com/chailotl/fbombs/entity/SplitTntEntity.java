package com.chailotl.fbombs.entity;

import com.chailotl.fbombs.init.FBombsBlocks;
import com.chailotl.fbombs.init.FBombsEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SplitTntEntity extends AbstractTntEntity {
    private int splits = - 1;

    public SplitTntEntity(EntityType<SplitTntEntity> entityType, World world) {
        super(entityType, world);
    }

    public SplitTntEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter) {
        super(FBombsEntityTypes.SPLIT_TNT, world, x, y, z, igniter);
    }

    public SplitTntEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter, int splits) {
        this(world, x, y, z, igniter);
        this.splits = splits;
    }

    @Override
    protected Block getDefaultBlock() {
        return FBombsBlocks.SPLIT_TNT;
    }

    @Override
    protected float getPower() {
        return splits * 2;
    }
}